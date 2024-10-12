package com.example.qlmoney.Phan_loai;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
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

public class ThemphanloaiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Phanloai_Adapter phanloaiAdapter;
    private List<Phanloai_Nganh> yourDataList;

    private SwitchCompat switchOnOff;
    private TextView tvSwitchYes;
    private TextView tvSwitchNo;
    private boolean isSwitchChecked ;
    private TextView oneClickButton;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themphanloai);

        switchOnOff = findViewById(R.id.switch_chart_type);
        tvSwitchYes = findViewById(R.id.tvSwitchYes);
        tvSwitchNo = findViewById(R.id.tvSwitchNo);

        oneClickButton = findViewById(R.id.textView_add1);

        // Khởi tạo cơ sở dữ liệu và lưu trữ dữ liệu vào đó
        initializeDatabase();
        // Lấy danh sách người dùng từ cơ sở dữ liệu
        yourDataList = new ArrayList<>();

        phanloaiAdapter = new Phanloai_Adapter();
        phanloaiAdapter.setPhanloaiList(yourDataList);

        recyclerView = findViewById(R.id.recyclerView_menuphanloai);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(phanloaiAdapter);

        loadDataFromDatabase("chitieu");

        switchOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                tvSwitchYes.setTextColor(ContextCompat.getColor(this, R.color.blue_color));
                tvSwitchNo.setTextColor(ContextCompat.getColor(this, R.color.white));
                loadDataFromDatabase("chitieu");
                isSwitchChecked = false;

            } else {

                tvSwitchYes.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSwitchNo.setTextColor(ContextCompat.getColor(this, R.color.blue_color));
                loadDataFromDatabase("thunhap");
                isSwitchChecked = true;
            }
        });

        phanloaiAdapter.setOnPhanloaiClickListener(new Phanloai_Adapter.OnPhanloaiClickListener() {
            @Override
            public void onPhanloaiClick(int position) {
                Phanloai_Nganh phanloai = yourDataList.get(position);

                Intent intent = new Intent(ThemphanloaiActivity.this, Sua_Xoa_Phanloai.class);
                intent.putExtra("phanloaiId", phanloai.getIDphanloainganh());
                intent.putExtra("phanloaiName", phanloai.getTenphanloainganh());
                intent.putExtra("kieu", phanloai.getKieuphanloainganh());
                startActivity(intent);
            }
        });

        // Xử lý sự kiện khi người dùng click vào themphanloai
        oneClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performOneClickAction("Them_chitiet_phan_loai");
            }
        });
    }

    private void initializeDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Xóa toàn bộ dữ liệu trong bảng PHANLOAI_NGANH (nếu cần)
//         db.delete(DatabaseHelper.TABLE_PHANLOAINGANH, null, null);

        db.close();
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
                @SuppressLint("Range")  int image = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMG_PHANLOAINGANH));
                @SuppressLint("Range")  String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME_PHANLOAINGANH));
                @SuppressLint("Range")  String kieu = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_KIEU_PHANLOAINGANH));
                yourDataList.add(new Phanloai_Nganh(id, image, name,0, kieu));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        phanloaiAdapter.notifyDataSetChanged();
    }

    private void performOneClickAction(String targetPage) {
        Intent intent = null;
        if (targetPage.equals("Them_chitiet_phan_loai")) {
            intent = new Intent(this, Them_chitiet_phan_loai.class);
            if (isSwitchChecked) {
//                intent.putExtra("targetPage", "chitieu");
                intent.putExtra("targetPage", "thunhap");
            } else {
                intent.putExtra("targetPage", "chitieu");
//                intent.putExtra("targetPage", "thunhap");
            }
        } else if (targetPage.equals("ThemphanloaiActivity")) {
            intent = new Intent(this, ThemphanloaiActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
