package com.example.qlmoney.Phan_loai;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlmoney.Adapter.IconAdapter;
import com.example.qlmoney.DatabaseHelper;
import com.example.qlmoney.MainActivity;
import com.example.qlmoney.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Them_chitiet_phan_loai extends AppCompatActivity implements IconAdapter.OnItemClickListener {
    private RecyclerView iconRecyclerView;
    private IconAdapter iconAdapter;
    private List<Integer> iconIds;
    private List<String> iconUrls;


    private ImageView selectedIconImageView;
    private DatabaseHelper databaseHelper;

    private  String targetPage ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chitiet_phanloai);

        Intent intent = getIntent();
        targetPage = intent.getStringExtra("targetPage");


        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Lấy danh sách địa chỉ tài nguyên của biểu tượng từ XML tài nguyên
        TypedArray iconArray = getResources().obtainTypedArray(R.array.icon_ids);

        // Chuyển đổi danh sách địa chỉ tài nguyên thành danh sách resource ID và URL
        iconIds = new ArrayList<>();
        iconUrls = new ArrayList<>();
        for (int i = 0; i < iconArray.length(); i++) {
            int resourceId = iconArray.getResourceId(i, 0);
            String url = "your_icon_url_here"; // Thay thế "your_icon_url_here" bằng địa chỉ URL của tấm ảnh tương ứng
            iconIds.add(resourceId);
            iconUrls.add(url);
        }


        // Tìm ImageView để hiển thị biểu tượng được chọn
        selectedIconImageView = findViewById(R.id.selectedIconImageView);

        // Khởi tạo RecyclerView và Adapter
        iconRecyclerView = findViewById(R.id.iconRecyclerView);
        iconAdapter = new IconAdapter(iconIds, this);
        iconRecyclerView.setAdapter(iconAdapter);
        int spanCount = 4; // Số cột bạn muốn hiển thị trong bảng
        iconRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        // Hàm đếm kí tự đã nhập ( làm màu )
        EditText editTextNhap = findViewById(R.id.editText_Nhap);
        final TextView countText = findViewById(R.id.count_text);
        editTextNhap.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Không cần thực hiện thay đổi trước khi nhập văn bản
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Đếm số ký tự và cập nhật TextView
                int count = charSequence.length();
                countText.setText(count + "/15");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Không cần thực hiện thay đổi sau khi nhập văn bản
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        iconAdapter.setSelectedIconPosition(position);
    }




    // Bước 6: Xử lý sự kiện khi người dùng nhấn nút "Lưu"
    public void onSaveClick(View view) {
        // Lấy thông tin biểu tượng được chọn
        int selectedIconId = iconIds.get(iconAdapter.getSelectedIconPosition());

        // Lấy nội dung từ EditText
        EditText editTextNhap = findViewById(R.id.editText_Nhap);
        String text = editTextNhap.getText().toString();

        // Tạo đối tượng DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Mở cơ sở dữ liệu để ghi dữ liệu
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Tạo đối tượng ContentValues để chứa dữ liệu
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IMG_PHANLOAINGANH, selectedIconId);
        values.put(DatabaseHelper.COLUMN_NAME_PHANLOAINGANH, text);
        values.put(DatabaseHelper.COLUMN_KIEU_PHANLOAINGANH, targetPage);

        // Thực hiện lưu dữ liệu vào cơ sở dữ liệu
        long result = db.insert(DatabaseHelper.TABLE_PHANLOAINGANH, null, values);

        if (result != -1) {
            Toast.makeText(this, "Đã lưu dữ liệu vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Lỗi khi lưu dữ liệu vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
        }

        // Đóng cơ sở dữ liệu
        db.close();
    }



    // Bước 7: Xử lý sự kiện khi người dùng nhấn nút "Hủy"
    public void onCancelClick(View view) {
        // Thực hiện các xử lý hủy bỏ hoặc quay trở lại màn hình trước, ví dụ:
        // setResult(RESULT_CANCELED);
        // finish();

        Toast.makeText(this, "Đã hủy", Toast.LENGTH_SHORT).show();
    }



}


