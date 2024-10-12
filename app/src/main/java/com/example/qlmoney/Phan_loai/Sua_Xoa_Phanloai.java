package com.example.qlmoney.Phan_loai;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlmoney.Adapter.IconAdapter;
import com.example.qlmoney.DatabaseHelper;
import com.example.qlmoney.MainActivity;
import com.example.qlmoney.R;

import java.util.ArrayList;
import java.util.List;

public class Sua_Xoa_Phanloai extends AppCompatActivity implements IconAdapter.OnItemClickListener {
    private RecyclerView iconRecyclerView;
    private IconAdapter iconAdapter;
    private List<Integer> iconIds;
    private ImageView selectedIconImageView;
    private DatabaseHelper databaseHelper;
    private String targetPage;
    private String kieu;
    private int phanloaiId;

    @Override
    public void onItemClick(int position) {
        iconAdapter.setSelectedIconPosition(position);
    }

    private void deletePhanloaiFromDatabase(int phanloaiId) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        int rowsDeleted = db.delete(DatabaseHelper.TABLE_PHANLOAINGANH,
                DatabaseHelper.COLUMN_ID_PHANLOAINGANH + " = ?",
                new String[]{String.valueOf(phanloaiId)});
        // xoá  các item có cùng id ( tránh tính tiền sai )
        int rowsDeleted2 = db.delete(DatabaseHelper.TABLE_ITEM,
                DatabaseHelper.COLUMN_ID_PHANLOAINGANH + " = ?",
                new String[]{String.valueOf(phanloaiId)});

        db.close();

        if (rowsDeleted > 0 && rowsDeleted > 0) {
            Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sua_xoa_phanloai);

        Intent intent = getIntent();
        phanloaiId = intent.getIntExtra("phanloaiId", 0);
        targetPage = intent.getStringExtra("targetPage");
        kieu = intent.getStringExtra("kieu");
        Button btnXoa = findViewById(R.id.btnXoa);
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePhanloaiFromDatabase(phanloaiId);
            }
        });

        databaseHelper = new DatabaseHelper(this);
        TypedArray iconArray = getResources().obtainTypedArray(R.array.icon_ids);

        iconIds = new ArrayList<>();
        for (int i = 0; i < iconArray.length(); i++) {
            int resourceId = iconArray.getResourceId(i, 0);
            iconIds.add(resourceId);
        }

        selectedIconImageView = findViewById(R.id.selectedIconImageView);

        iconRecyclerView = findViewById(R.id.iconRecyclerView);
        iconAdapter = new IconAdapter(iconIds, this);
        iconRecyclerView.setAdapter(iconAdapter);
        int spanCount = 4;
        iconRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        EditText editTextNhap = findViewById(R.id.editText_Nhap);
        final TextView countText = findViewById(R.id.count_text);
        editTextNhap.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int count = charSequence.length();
                countText.setText(count + "/15");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        loadPhanloaiDetails();
    }

    private void loadPhanloaiDetails() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PHANLOAINGANH,
                new String[]{DatabaseHelper.COLUMN_IMG_PHANLOAINGANH, DatabaseHelper.COLUMN_NAME_PHANLOAINGANH},
                DatabaseHelper.COLUMN_ID_PHANLOAINGANH + " = ?",
                new String[]{String.valueOf(phanloaiId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int iconId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMG_PHANLOAINGANH));
            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME_PHANLOAINGANH));

            int position = iconIds.indexOf(iconId);
            if (position != -1) {
                iconAdapter.setSelectedIconPosition(position);
            }
            selectedIconImageView.setImageResource(iconId);
            EditText editTextNhap = findViewById(R.id.editText_Nhap);
            editTextNhap.setText(name);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }

    public void onSaveClick(View view) {
        int selectedIconId = iconIds.get(iconAdapter.getSelectedIconPosition());

        EditText editTextNhap = findViewById(R.id.editText_Nhap);
        String text = editTextNhap.getText().toString();

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IMG_PHANLOAINGANH, selectedIconId);
        values.put(DatabaseHelper.COLUMN_NAME_PHANLOAINGANH, text);
        values.put(DatabaseHelper.COLUMN_KIEU_PHANLOAINGANH, kieu);

        int rowsUpdated = db.update(DatabaseHelper.TABLE_PHANLOAINGANH, values,
                DatabaseHelper.COLUMN_ID_PHANLOAINGANH + " = ?",
                new String[]{String.valueOf(phanloaiId)});

        db.close();

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Đã cập nhật dữ liệu vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật dữ liệu vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCancelClick(View view) {
        Toast.makeText(this, "Đã hủy", Toast.LENGTH_SHORT).show();
    }
}
