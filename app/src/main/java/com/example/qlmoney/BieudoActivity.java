package com.example.qlmoney;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
//import android.support.v7.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class BieudoActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SwitchCompat switchOnOff;
    private TextView tvSwitchYes;
    private TextView tvSwitchNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bieudo);

        PieChart pieChart = findViewById(R.id.chart);
        SwitchCompat switchChartType = findViewById(R.id.switch_chart_type);
        tvSwitchYes = findViewById(R.id.tvSwitchYes);
        tvSwitchNo = findViewById(R.id.tvSwitchNo);

        databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

//        switchChartType.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            loadChartData(db, pieChart, isChecked);
//        });

        // Load dữ liệu ban đầu cho biểu đồ
        loadChartData(db, pieChart, switchChartType.isChecked());


        switchChartType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loadChartData(db, pieChart, isChecked);

                tvSwitchYes.setTextColor(ContextCompat.getColor(this, R.color.blue_color));
                tvSwitchNo.setTextColor(ContextCompat.getColor(this, R.color.white));

            } else {
                loadChartData(db, pieChart, isChecked);

                tvSwitchYes.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSwitchNo.setTextColor(ContextCompat.getColor(this, R.color.blue_color));

            }
        });
    }

    private void loadChartData(SQLiteDatabase db, PieChart pieChart, boolean isIncomeChart) {
        ArrayList<PieEntry> entries = getDataFromDatabase(db, isIncomeChart);
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK); // Thiết lập màu chữ cho giá trị
        dataSet.setValueTextSize(15f); // Thiết lập kích thước chữ cho giá trị
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(false); // Vô hiệu hóa hiển thị phần trăm
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.invalidate(); // Cập nhật biểu đồ

        if (isIncomeChart) {

            pieChart.setCenterText("Tổng chi tiêu");

        } else {
            pieChart.setCenterText("Tổng thu nhập");
        }
    }


    private ArrayList<PieEntry> getDataFromDatabase(SQLiteDatabase db, boolean isIncomeChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        String[] columns = {DatabaseHelper.COLUMN_TONG_NGANH, DatabaseHelper.COLUMN_NAME_PHANLOAINGANH};
        String selection = DatabaseHelper.COLUMN_KIEU_PHANLOAINGANH + " = ?";
        String[] selectionArgs = {isIncomeChart ? "chitieu" : "thunhap"};
        Cursor cursor = db.query(DatabaseHelper.TABLE_PHANLOAINGANH, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int tongnganh = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TONG_NGANH));
                String tenphanloainganh = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME_PHANLOAINGANH));

                entries.add(new PieEntry(tongnganh, tenphanloainganh));
            }
            cursor.close();
        }

        return entries;
    }

}
