package com.example.qlmoney.Chi_tieu;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
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
import com.example.qlmoney.MainActivity;
import com.example.qlmoney.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Sua_Xoa_Chitieu extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private int itemId;
    private int itemIdPhanLoai;
    private TextView editTextValue;
    private TextView editTextDate;
    private TextView editTextNote;
    private TextView editTextTime;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    private boolean isPhanloaiViewVisible = false;
    private View inflatedView;
    private RecyclerView recyclerView;
    private Phanloai_Adapter userAdapter;
    private List<Phanloai_Nganh> yourDataList;
    private SwitchCompat switchOnOff;
    private TextView tvSwitchYes;
    private TextView tvSwitchNo;
    private boolean isSwitchChecked = false;
    private boolean check_kieu_narmal = false;
    private  int idPhanloai;

    private  String kieuphanloai;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sua_xoa_chitieu);

        // Khởi tạo đối tượng databaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ TextView và thiết lập định dạng ngày tháng và thời gian
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
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
        // nhận intenr
        Intent intent = getIntent();
        itemId = intent.getIntExtra("itemId", 0);

        Button btnXoa = findViewById(R.id.btnXoa);
        loadItemDetails();
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemFromDatabase(itemId);
            }
        });
        // chọn ảnh và phân loại

        ImageView button = findViewById(R.id.imageView_chon_phanloai);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPhanloaiViewVisible) {
                    // Nếu inflatedView đã được hiển thị, hãy xóa nó khỏi giao diện người dùng
                    ViewGroup mainLayout = findViewById(R.id.main_layout);
                    mainLayout.removeView(inflatedView);
                    isPhanloaiViewVisible = false;
                } else {

                    // Nếu inflatedView chưa được hiển thị, hãy hiển thị nó
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    inflatedView = inflater.inflate(R.layout.activity_nhapthuchi, null);

                    ViewGroup mainLayout = findViewById(R.id.main_layout);
                    mainLayout.addView(inflatedView);
                    isPhanloaiViewVisible = true;
                    mainLayout.setBackgroundColor(Color.parseColor("#4CAF74")); // Sử dụng mã màu hexa
                    switchOnOff = findViewById(R.id.switch_chart_type);
                    tvSwitchYes = findViewById(R.id.tvSwitchYes);
                    tvSwitchNo = findViewById(R.id.tvSwitchNo);
                    // biêết viết thôi giải thích bó tay
                    switchOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {

                            tvSwitchYes.setTextColor(ContextCompat.getColor(Sua_Xoa_Chitieu.this, R.color.blue_color));
                            tvSwitchNo.setTextColor(ContextCompat.getColor(Sua_Xoa_Chitieu.this, R.color.white));
                            loadDataFromDatabase("chitieu");
                        } else {
                            tvSwitchYes.setTextColor(ContextCompat.getColor(Sua_Xoa_Chitieu.this, R.color.white));
                            tvSwitchNo.setTextColor(ContextCompat.getColor(Sua_Xoa_Chitieu.this, R.color.blue_color));
                            loadDataFromDatabase("thunhap");
                        }
                    });

                    recyclerView = findViewById(R.id.recyclerView_menuphanloai);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Sua_Xoa_Chitieu.this));
                    yourDataList = new ArrayList<>();
                    userAdapter = new Phanloai_Adapter();
                    userAdapter.setPhanloaiList(yourDataList);
                    recyclerView.setAdapter(userAdapter);
                    loadDataFromDatabase("chitieu");
                    userAdapter.notifyDataSetChanged();

                    userAdapter.setOnPhanloaiClickListener(new Phanloai_Adapter.OnPhanloaiClickListener() {
                        @Override
                        public void onPhanloaiClick(int position) {
                            Phanloai_Nganh phanloai = yourDataList.get(position);
                            idPhanloai = phanloai.getIDphanloainganh();
                            kieuphanloai = phanloai.getKieuphanloainganh();
                            Log.d("TAG", "Mục đã nhấp: " + phanloai.getIDphanloainganh()); // Thay "TAG" bằng tag log của bạn
                            // đổi màu
                            mainLayout.setBackgroundColor(Color.TRANSPARENT);
                            //đóng
                            ViewGroup mainLayout = findViewById(R.id.main_layout);
                            mainLayout.removeView(inflatedView);
                            isPhanloaiViewVisible = false;
                            check_kieu_narmal = true;
                            if (check_kieu_narmal) {
                                loadImageById(idPhanloai);
                                imageViewNganh2.setImageResource(loadImageById(idPhanloai));
                            } else{
                                loadImageById(itemId);
                                imageViewNganh2.setImageResource(loadImageById(itemId));
                            }
                        }
                    });



                }




            }
        });

        if (check_kieu_narmal) {
            loadImageById(idPhanloai);

            imageViewNganh2.setImageResource(loadImageById(idPhanloai));
        } else{
            loadImageById(itemId);
            imageViewNganh2.setImageResource(loadImageById(itemId));
        }

    }


    private void deleteItemFromDatabase(int itemId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        int rowsDeleted = db.delete(DatabaseHelper.TABLE_ITEM,
                DatabaseHelper.COLUMN_ID_ITEM + " = ?",
                new String[]{String.valueOf(itemId)});

        db.close();

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
        }



        // Kết thúc activity
        finish();
        Intent intent = new Intent(Sua_Xoa_Chitieu.this, MainActivity.class);
        startActivity(intent);
    }

    @SuppressLint("Range")
    private void loadItemDetails() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_ITEM, null,
                DatabaseHelper.COLUMN_ID_ITEM + " = ?", new String[]{String.valueOf(itemId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            String itemAmount = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AMOUNT));
            String itemDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
            String itemTime = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME));
            String itemNote = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTE));

            Toast.makeText(this, "itemAmount: " + itemAmount + "\n" +
                    "itemDate: " + itemDate + "\n" +
                    "itemTime: " + itemTime + "\n" +
                    "itemNote: " + itemNote, Toast.LENGTH_SHORT).show();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = dateFormat.parse(itemDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            TextView editTextDate = findViewById(R.id.editTextDate);
            editTextDate.setText(itemDate);
//
            TextView editTextTime = findViewById(R.id.editTextTime);
            editTextTime.setText(itemTime);

            EditText editTextNote = findViewById(R.id.editTextNote);
            editTextNote.setText(itemNote);


            EditText editTextAmount = findViewById(R.id.editText_Value);
            editTextAmount.setText(itemAmount);


        } else {
            // Không tìm thấy dữ liệu cho itemId
            // Xử lý tại đây nếu cần thiết
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }


    public void onSaveClick(View view) {

        String selectedDate = editTextDate.getText().toString();
        String selectedTime = editTextTime.getText().toString();
        String note = editTextNote.getText().toString();
        String amount = editTextValue.getText().toString();
        int id =  itemId;


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


        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Tạo đối tượng ContentValues để chứa dữ liệu
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DATE, selectedDate);
        values.put(DatabaseHelper.COLUMN_TIME, selectedTime);
        values.put(DatabaseHelper.COLUMN_AMOUNT, amount);


        if (check_kieu_narmal) {
            values.put(DatabaseHelper.COLUMN_KIEU, kieuphanloai);
            values.put(DatabaseHelper.COLUMN_ID_PHANLOAINGANH, idPhanloai);
        }





        values.put(DatabaseHelper.COLUMN_NOTE, note);

        int rowsUpdated = db.update(DatabaseHelper.TABLE_ITEM, values,
                DatabaseHelper.COLUMN_ID_ITEM + " = ?",
                new String[]{String.valueOf(itemId)});

        db.close();

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Đã cập nhật dữ liệu vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật dữ liệu vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }

    // Hiển thị DatePickerDialog để chọn ngày
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Sua_Xoa_Chitieu.this,
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(Sua_Xoa_Chitieu.this,
                (view, hourOfDay, minuteOfDay) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minuteOfDay);
                    String selectedTime = timeFormatter.format(calendar.getTime());
                    editTextTime.setText(selectedTime);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    // hàm lấy data pahnloai để chọn phan lịa và hình
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
                yourDataList.add(new Phanloai_Nganh(id, image, name,0, kieu));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        userAdapter.notifyDataSetChanged();
    }
    private int loadImageById(int id) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String selectQuery;
        if (check_kieu_narmal) {

             selectQuery = "SELECT " + DatabaseHelper.COLUMN_IMG_PHANLOAINGANH +
                    " FROM " + DatabaseHelper.TABLE_PHANLOAINGANH +
                    " JOIN " + DatabaseHelper.TABLE_ITEM +
                    " ON " + DatabaseHelper.TABLE_PHANLOAINGANH + "." + DatabaseHelper.COLUMN_ID_PHANLOAINGANH +
                    " = " + DatabaseHelper.TABLE_ITEM + "." + DatabaseHelper.COLUMN_ID_PHANLOAINGANH +
                    " WHERE " + DatabaseHelper.TABLE_ITEM + "." + DatabaseHelper.COLUMN_ID_PHANLOAINGANH + " = " + id;
        } else{
             selectQuery = "SELECT " + DatabaseHelper.COLUMN_IMG_PHANLOAINGANH +
                    " FROM " + DatabaseHelper.TABLE_PHANLOAINGANH +
                    " JOIN " + DatabaseHelper.TABLE_ITEM +
                    " ON " + DatabaseHelper.TABLE_PHANLOAINGANH + "." + DatabaseHelper.COLUMN_ID_PHANLOAINGANH +
                    " = " + DatabaseHelper.TABLE_ITEM + "." + DatabaseHelper.COLUMN_ID_PHANLOAINGANH +
                    " WHERE " + DatabaseHelper.TABLE_ITEM + "." + DatabaseHelper.COLUMN_ID_ITEM + " = " + id;
        }



        Cursor cursor = db.rawQuery(selectQuery, null);
        int imageId = 0;

        if (cursor.moveToFirst()) {
            imageId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMG_PHANLOAINGANH));
        }

        cursor.close();
        db.close();

        return imageId;
    }

}
