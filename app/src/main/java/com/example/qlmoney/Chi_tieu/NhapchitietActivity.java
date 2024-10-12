package com.example.qlmoney.Chi_tieu;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlmoney.DatabaseHelper;
import com.example.qlmoney.MainActivity;
import com.example.qlmoney.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NhapchitietActivity extends AppCompatActivity {
    private  TextView editTextValue;
    private TextView editTextDate;
    private TextView editTextNote;
    private TextView editTextTime;
    private ImageView editTextImg;

    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    private ImageButton imageButtonExit;

    private String itemImg; // Biến để lưu ID của mục đã chọn

    private TextView oneClickButton_Add;

    private DatabaseHelper databaseHelper; // Đối tượng DatabaseHelper

    private int ID_Phan_loai;


    public NhapchitietActivity() {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chitiet);

        // Ánh xạ TextView và thiết lập định dạng ngày tháng và thời gian
        editTextDate = findViewById(R.id.editTextDate_2);
        editTextTime = findViewById(R.id.editTextTime_2);
        editTextNote = findViewById(R.id.editTextNote);
        editTextValue = findViewById(R.id.editText_Value);
        // Tạo một đối tượng Date đại diện cho thời gian hiện tại
        Date currentTime = new Date();

        // Định dạng thời gian theo định dạng HH:mm
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Chuyển đổi thời gian hiện tại thành chuỗi theo định dạng HH:mm
        String currentTimeString = timeFormat.format(currentTime);

        // Gán giá trị của currentTimeString cho trường editTextTime
        editTextTime.setText(currentTimeString);

        editTextImg = findViewById(R.id.imageView_chon_phanloai);
        ImageButton imageButtonExit = findViewById(R.id.imageButton_exit);
        ImageView imageViewNganh2 = findViewById(R.id.imageView_chon_phanloai);

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Xử lý khi nhấn vào tableRow_date
        TableRow tableRowDate = findViewById(R.id.tableRow_date);
        tableRowDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Xử lý khi nhấn vào tableRow_time
        TableRow tableRowTime = findViewById(R.id.tableRow_time);
        tableRowTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        int itemImg = intent.getIntExtra("image", 0);
        ID_Phan_loai = intent.getIntExtra("ID", 0);


        if (itemImg != 0) {
            // Kiểu dữ liệu của itemImg là String (đường dẫn tới tệp hình ảnh)
            // Sử dụng thư viện Picasso để tải và hiển thị hình ảnh từ URL
            Picasso.get().load(itemImg).into(imageViewNganh2);
        } else {
            // Nếu itemImg không phải hoặc không có giá trị, bạn có thể đặt hình ảnh mặc định
            imageViewNganh2.setImageResource(R.drawable.cham_hoi);
        }
        // Khởi tạo DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Đặt lệnh lắng nghe sự kiện cho nút Exit
        imageButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToDatabase(); // Lưu dữ liệu vào cơ sở dữ liệu
                onBackPressed(); // Gọi phương thức onBackPressed khi nút được nhấn
            }
        });

        // Xử lý sự kiện khi người dùng nhấp vào TextView Add
        oneClickButton_Add = findViewById(R.id.textView_add1);
        oneClickButton_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToDatabase(); // Lưu dữ liệu vào cơ sở dữ liệu


            }

        });

    }

    // Hiển thị DatePickerDialog để chọn ngày
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(NhapchitietActivity.this,
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    calendar.set(yearSelected, monthOfYear, dayOfMonth);
                    String selectedDate = dateFormatter.format(calendar.getTime());
                    editTextDate.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    // Hiển thị TimePickerDialog để chọn thời gian
    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(NhapchitietActivity.this,
                (view, hourOfDay, minuteOfDay) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minuteOfDay);
                    String selectedTime = timeFormatter.format(calendar.getTime());
                    editTextTime.setText(selectedTime);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    // Lưu dữ liệu vào cơ sở dữ liệu
    private void saveDataToDatabase() {
        String selectedDate = editTextDate.getText().toString();
        String selectedTime = editTextTime.getText().toString();
        String note = editTextNote.getText().toString();
        String amount = editTextValue.getText().toString();
        int idphanloai =  ID_Phan_loai;


        // Kiểm tra xem dữ liệu đã nhập đầy đủ chưa
        if (selectedDate.isEmpty() || selectedTime.isEmpty() || note=="" || amount.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDate.equals("Hôm nay")) {
            // Lấy ngày hôm nay và gán cho amountValue
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            selectedDate = dateFormat.format(new Date());
        }


        // Mở cơ sở dữ liệu để ghi dữ liệu
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Tạo đối tượng ContentValues để chứa dữ liệu
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DATE, selectedDate);
        values.put(DatabaseHelper.COLUMN_TIME, selectedTime);
        values.put(DatabaseHelper.COLUMN_AMOUNT, amount);
        values.put(DatabaseHelper.COLUMN_ID_PHANLOAINGANH, idphanloai);
        values.put(DatabaseHelper.COLUMN_NOTE, note);
        // nguy hiem vkl ditme loi hoai cay vl
        String kieuphanloai = getKieuPhanLoai(db, idphanloai);

        values.put(DatabaseHelper.COLUMN_KIEU, kieuphanloai);






        // Thêm dữ liệu vào cơ sở dữ liệu
        long result = db.insert(DatabaseHelper.TABLE_ITEM, null, values);

        if (result != -1 ) {
            Toast.makeText(this, "Đã lưu dữ liệu vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            // Chuyển sang MainActivity
            Intent intent = new Intent(NhapchitietActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Lỗi khi lưu dữ liệu vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
        }

        // Đóng cơ sở dữ liệu
        db.close();
    }
    //lấy giá trị của cột "kieu" từ bảng "Phanloai_nganh" dựa trên "idphanloai"
    private String getKieuPhanLoai(SQLiteDatabase db, int idPhanLoai) {
        String[] columns = {DatabaseHelper.COLUMN_KIEU_PHANLOAINGANH};
        String selection = DatabaseHelper.COLUMN_ID_PHANLOAINGANH + " = ?";
        String[] selectionArgs = {String.valueOf(idPhanLoai)};
        Cursor cursor = db.query(DatabaseHelper.TABLE_PHANLOAINGANH, columns, selection, selectionArgs, null, null, null);

        String kieuPhanLoai = "";

        if (cursor != null && cursor.moveToFirst()) {
            kieuPhanLoai = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_KIEU_PHANLOAINGANH));
            cursor.close();
        }

        return kieuPhanLoai;
    }





}

