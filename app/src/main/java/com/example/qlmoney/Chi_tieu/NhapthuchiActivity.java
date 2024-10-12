package com.example.qlmoney.Chi_tieu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlmoney.Adapter.Phanloai_Adapter;
import com.example.qlmoney.Database.Phanloai_Nganh;
import com.example.qlmoney.DatabaseHelper;
import com.example.qlmoney.R;

import java.util.ArrayList;
import java.util.List;

public class NhapthuchiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Phanloai_Adapter userAdapter;
    private List<Phanloai_Nganh> yourDataList;

    private SwitchCompat switchOnOff;
    private TextView tvSwitchYes;
    private TextView tvSwitchNo;
    private boolean isSwitchChecked ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhapthuchi);

        switchOnOff = findViewById(R.id.switch_chart_type);
        tvSwitchYes = findViewById(R.id.tvSwitchYes);
        tvSwitchNo = findViewById(R.id.tvSwitchNo);

        switchOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isSwitchChecked = true;
                tvSwitchYes.setTextColor(ContextCompat.getColor(this, R.color.blue_color));
                tvSwitchNo.setTextColor(ContextCompat.getColor(this, R.color.white));
                loadDataFromDatabase("chitieu");
            } else {
                isSwitchChecked = false;
                tvSwitchYes.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSwitchNo.setTextColor(ContextCompat.getColor(this, R.color.blue_color));
                loadDataFromDatabase("thunhap");
            }
        });

        recyclerView = findViewById(R.id.recyclerView_menuphanloai);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        yourDataList = new ArrayList<>();

        userAdapter = new Phanloai_Adapter();
        userAdapter.setPhanloaiList(yourDataList);
        recyclerView.setAdapter(userAdapter);

        loadDataFromDatabase("chitieu");

        userAdapter.setOnPhanloaiClickListener(new Phanloai_Adapter.OnPhanloaiClickListener() {
            @Override
            public void onPhanloaiClick(int position) {
                Phanloai_Nganh item = yourDataList.get(position);
                Intent intent = new Intent(NhapthuchiActivity.this, NhapchitietActivity.class);
                intent.putExtra("image", item.getImgphanloainganh());
                intent.putExtra("ID", item.getIDphanloainganh());
                    if (isSwitchChecked) {
                        intent.putExtra("targetPage", "chitieu");
                    } else {
                        intent.putExtra("targetPage", "thunhap");
                    }
                // Thêm các dữ liệu khác cần truyền
                startActivity(intent);
            }


        });

    }

    private void loadDataFromDatabase(String filter) {
        yourDataList.clear();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_PHANLOAINGANH;

        if (filter != null) {
            selectQuery += " WHERE " + DatabaseHelper.COLUMN_KIEU_PHANLOAINGANH + "='" + filter + "'";
        }

        Toast.makeText(getApplicationContext(), "myVariable = " + selectQuery, Toast.LENGTH_SHORT).show();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_PHANLOAINGANH));

                @SuppressLint("Range") int image = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMG_PHANLOAINGANH));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME_PHANLOAINGANH));
                @SuppressLint("Range") String kieu = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_KIEU_PHANLOAINGANH));
                yourDataList.add(new Phanloai_Nganh(id, image, name, 0,kieu));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        userAdapter.notifyDataSetChanged();
    }


}
