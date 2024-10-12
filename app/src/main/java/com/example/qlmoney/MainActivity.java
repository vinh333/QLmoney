package com.example.qlmoney;

import static com.example.qlmoney.DatabaseHelper.COLUMN_AMOUNT;
import static com.example.qlmoney.DatabaseHelper.COLUMN_DATE;
import static com.example.qlmoney.DatabaseHelper.COLUMN_ID_ITEM;
import static com.example.qlmoney.DatabaseHelper.COLUMN_ID_PHANLOAINGANH;
import static com.example.qlmoney.DatabaseHelper.COLUMN_IMG_PHANLOAINGANH;
import static com.example.qlmoney.DatabaseHelper.COLUMN_KIEU;
import static com.example.qlmoney.DatabaseHelper.COLUMN_KIEU_PHANLOAINGANH;
import static com.example.qlmoney.DatabaseHelper.COLUMN_NAME_PHANLOAINGANH;
import static com.example.qlmoney.DatabaseHelper.COLUMN_NOTE;
import static com.example.qlmoney.DatabaseHelper.COLUMN_TIME;
import static com.example.qlmoney.DatabaseHelper.COLUMN_TONG_NGANH;
import static com.example.qlmoney.DatabaseHelper.TABLE_ITEM;
import static com.example.qlmoney.DatabaseHelper.TABLE_PHANLOAINGANH;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlmoney.Adapter.Items_Adapter;
import com.example.qlmoney.Chi_tieu.NhapthuchiActivity;
import com.example.qlmoney.Chi_tieu.Sua_Xoa_Chitieu;
import com.example.qlmoney.Database.Item;
import com.example.qlmoney.Database.Phanloai_Nganh;
import com.example.qlmoney.Phan_loai.ThemphanloaiActivity;
import com.example.qlmoney.User.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Items_Adapter userAdapter;
    private ImageView oneClickButton;

    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference;
    private DatabaseHelper databaseHelper;
    private  String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
             userID = firebaseUser.getUid();
            // Sử dụng loggedInUserId ở đây cho các mục đích tiếp theo
            // ...
        }





//        ////        // Xóa toàn bộ dữ liệu trong bảng ITEM ( nguy hiểm dùng khi lỗi)
//                DatabaseHelper databaseHelper = new DatabaseHelper(this);
//                SQLiteDatabase db = databaseHelper.getWritableDatabase();
//                db.delete(DatabaseHelper.TABLE_ITEM, null, null);
//                db.delete(DatabaseHelper.TABLE_PHANLOAINGANH, null, null);

        // Khởi tạo cơ sở dữ liệu và lưu trữ dữ liệu vào đó
        initializeDatabase();

        // Lấy danh sách người dùng từ cơ sở dữ liệu
        List<Item> itemListFromDB = getUserListFromDB();


        // Khởi tạo và cập nhật UserAdapter với danh sách người dùng
        userAdapter = new Items_Adapter();
        userAdapter.setItemList(itemListFromDB);

        // Khởi tạo RecyclerView và cài đặt Adapter
        recyclerView = findViewById(R.id.recyclerView_menuphanloai);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        // Xử lý sự kiện khi người dùng click vào ImageView
        oneClickButton = findViewById(R.id.imageView_add);
        oneClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performOneClickAction("NhapthuchiActivity");
            }
        });

        // Xử lý sự kiện khi người dùng click vào imageView_phanloai
        oneClickButton = findViewById(R.id.imageView_phanloai);
        oneClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performOneClickAction("ThemphanloaiActivity");
            }
        });
        // chuyển sang trang sua xoa
        userAdapter.setOnItemClickListener(new Items_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int itemId) {
                // Chuyển đến trang sửa/xoá và truyền ID cần thiết qua Intent
                Intent intent = new Intent(MainActivity.this, Sua_Xoa_Chitieu.class);
                intent.putExtra("itemId", itemId);
                startActivity(intent);
            }
        });

        // Tính toán và hiển thị tổng số tiền
        Sum_Money(itemListFromDB);

        calculateAndSetTongAmount();

        // Gọi hàm setupNavigationDrawer để thiết lập sự kiện cho ImageView và NavigationView
        setupNavigationDrawer();

        // Xử lý sự kiện khi người dùng click vào imageView_bieudo
        oneClickButton = findViewById(R.id.imageView_bieudo);
        oneClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BieudoActivity.class);
                startActivity(intent);
            }
        });


        // upload databse realtime
//        Upload_DatabseRealtme(itemListFromDB);
        insertDataToFirebase();
//
        loadDataFromFirebase(this, "phanloai_nganh");
        loadDataFromFirebase(this,DatabaseHelper.TABLE_ITEM);


        if (isTableEmpty(DatabaseHelper.TABLE_PHANLOAINGANH)) {
            loadDataFromFirebase(this, "phanloai_nganh");
        }

        if (isTableEmpty(DatabaseHelper.TABLE_ITEM)) {
            loadDataFromFirebase(this,DatabaseHelper.TABLE_ITEM);
        }

        // Đăng ký BroadcastReceiver để lắng nghe sự kiện Broadcast
        IntentFilter filter = new IntentFilter("UPDATE_DATA_ACTION");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Cập nhật lại dữ liệu trong RecyclerView
                userAdapter.setItemList(itemListFromDB);
            }
        };
        registerReceiver(receiver, filter);
    }

    private void loadDataIntoRecyclerView() {

        recyclerView = findViewById(R.id.recyclerView_menuphanloai);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

    }



    // Thanh menu
    private void setupNavigationDrawer() {
        ImageView imageViewMenu = findViewById(R.id.imageView_menu);
        imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);

                // Xử lý logic tương ứng với mục được chọn
                int itemId = menuItem.getItemId();
                switch (itemId) {
                    case R.id.menu_dark_mode:
                        // Kiểm tra trạng thái hiện tại của Dark Mode
                        int currentMode = AppCompatDelegate.getDefaultNightMode();
                        if (currentMode == AppCompatDelegate.MODE_NIGHT_NO) {
                            // Đang ở chế độ Light Mode, chuyển sang Dark Mode
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        } else {
                            // Đang ở chế độ Dark Mode, chuyển sang Light Mode
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }
                        // Tạo lại Activity để áp dụng thay đổi Dark Mode
                        recreate();
                        break;

                    case R.id.menu_logout:
                        // Thực hiện đăng xuất ở đây
                        // Xóa toàn bộ dữ liệu trong bảng  ( nguy hiểm dùng khi lỗi)
                        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        db.delete(DatabaseHelper.TABLE_ITEM, null, null);
                        db.delete(DatabaseHelper.TABLE_PHANLOAINGANH, null, null);
                        // đăng xuất
                        performLogout();
                        break;
                    case R.id.menu_backup_restore:
                        // Thực hiện chuene trang sang backup
                        Intent intent = new Intent(MainActivity.this, Connect_Google.class);
                        startActivity(intent);
                        break;

                    // Các case khác tương ứng với các mục khác trong NavigationView
                }
                return true;

            }
        });
    }

    // Phương thức để khởi tạo cơ sở dữ liệu và lưu trữ dữ liệu vào đó
    private void initializeDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Xóa toàn bộ dữ liệu trong bảng ITEM
        //        db.delete(DatabaseHelper.TABLE_ITEM, null, null);

        // Tạo danh sách dữ liệu mẫu
        //        List<Item> itemList = createDummyData();

        // Lưu danh sách người dùng vào cơ sở dữ liệu
        //        for (Item item : itemList) {
        //            ContentValues values = new ContentValues();
        //            values.put(DatabaseHelper.COLUMN_NAME, item.getNameItem());
        //            values.put(DatabaseHelper.COLUMN_AMOUNT, item.getTextViewGia());
        //            values.put(DatabaseHelper.COLUMN_KIEU, item.getNameKieu());
        //            long itemId = db.insert(DatabaseHelper.TABLE_ITEM, null, values);
        //
        //            // Thêm khoá ngoại
        //            ContentValues foreignKeyValues = new ContentValues();
        //            foreignKeyValues.put(DatabaseHelper.COLUMN_ID_PHANLOAINGANH, 1); // Giá trị khoá ngoại tương ứng từ bảng TABLE_PHANLOAINGANH
        //            db.update(DatabaseHelper.TABLE_ITEM, foreignKeyValues, DatabaseHelper.COLUMN_ID + " = " + itemId, null);
        //        }
        //        db.close();
    }

    // Phương thức để lấy danh sách người dùng từ cơ sở dữ liệu
    private List<Item> getUserListFromDB() {
        List<Item> itemListFromDB = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String selectQuery = "SELECT " +
                TABLE_ITEM + "." + COLUMN_ID_ITEM + ", " +
                TABLE_ITEM + "." + COLUMN_AMOUNT + ", " +
                TABLE_ITEM + "." + COLUMN_KIEU + ", " +
                TABLE_PHANLOAINGANH + "." + COLUMN_NAME_PHANLOAINGANH + ", " +
                TABLE_PHANLOAINGANH + "." + COLUMN_IMG_PHANLOAINGANH + ", " +
                TABLE_ITEM + "." + COLUMN_DATE + ", " +
                TABLE_ITEM + "." + COLUMN_TIME + ", " +
                TABLE_ITEM + "." + COLUMN_NOTE +
                " FROM " + TABLE_ITEM +
                " INNER JOIN " + TABLE_PHANLOAINGANH +
                " ON " + TABLE_ITEM + "." + COLUMN_ID_PHANLOAINGANH +
                " = " + TABLE_PHANLOAINGANH + "." + COLUMN_ID_PHANLOAINGANH +
                " ORDER BY " + TABLE_ITEM + "." + COLUMN_DATE + " DESC, " +
                TABLE_ITEM + "." + COLUMN_TIME + " DESC";


        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_ITEM));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PHANLOAINGANH));
                @SuppressLint("Range") String amount = cursor.getString(cursor.getColumnIndex(COLUMN_AMOUNT));
                @SuppressLint("Range") String kieu = cursor.getString(cursor.getColumnIndex(COLUMN_KIEU));
                @SuppressLint("Range") int img = cursor.getInt(cursor.getColumnIndex(COLUMN_IMG_PHANLOAINGANH));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
                @SuppressLint("Range") String note = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE));

                // In giá trị COLUMN_IMG_PHANLOAINGANH bằng biến 'img'
                System.out.println("Img Phanloainganh: " + img);

                itemListFromDB.add(new Item(id, amount, kieu,0, date, time, note, name, img));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return itemListFromDB;
    }

    // Phương thức để xử lý hành động khi người dùng click vào ImageView
    private void performOneClickAction(String targetPage) {
        Intent intent = null;
        if (targetPage.equals("NhapthuchiActivity")) {
            intent = new Intent(this, NhapthuchiActivity.class);
        } else if (targetPage.equals("ThemphanloaiActivity")) {
            intent = new Intent(this, ThemphanloaiActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    // tính tiền và in chúng ra _ lưu thì chưa làm
    private void Sum_Money(List<Item> itemListFromDB) {
        // Tính tổng amount
        int tongthunhap = 0;
        int tongchitieu = 0;
        int tong = 0;

        for (Item item : itemListFromDB) {
            String kieu = item.getNameKieu();
            String amountString = item.getValue();
            if (amountString != null && !amountString.isEmpty()) {
                amountString = amountString.replaceAll("[^0-9]", ""); // Loại bỏ các ký tự không phải số
                int amount = Integer.parseInt(amountString);
                if (kieu != null) {
                    if (kieu.equals("thunhap")) {
                        tongthunhap += amount;
                    } else if (kieu.equals("chitieu")) {
                        tongchitieu += amount;
                    }
                }
            }
        }

        tong = tongthunhap - tongchitieu;

        Log.d("TongThuNhap", String.valueOf(tongthunhap));
        Log.d("TongChiTieu", String.valueOf(tongchitieu));
        Log.d("Tong", String.valueOf(tong));

        /// Gắn các giá trị tính toán vào TextView
        TextView textViewTong = findViewById(R.id.textView_tong);
        TextView textViewTongThuNhap = findViewById(R.id.textView_tongthunhap2);
        TextView textViewTongChiTieu = findViewById(R.id.textView_tongchitieu2);

        textViewTong.setText(String.valueOf(tong) + " VND");
        textViewTongThuNhap.setText(String.valueOf(tongthunhap) + " VND");
        textViewTongChiTieu.setText(String.valueOf(tongchitieu) + " VND");

        // Thiết lập màu đỏ cho textViewTong khi tong là số âm
        if (tong < 0) {
            textViewTong.setTextColor(Color.RED);
        } else {
            // Đặt lại màu văn bản mặc định nếu tong không là số âm
            textViewTong.setTextColor(Color.GREEN);
        }
    }
    // Tính tổng tiền theo phân loại
    private void calculateAndSetTongAmount() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

//        // Thực hiện câu truy vấn để tính tổng amount theo idphanloai và cập nhật vào cột tongnganh
        String query = "UPDATE " + DatabaseHelper.TABLE_PHANLOAINGANH +
                " SET " + COLUMN_TONG_NGANH + " = (" +
                "SELECT SUM(CAST(" + DatabaseHelper.COLUMN_AMOUNT + " AS INTEGER)) " +
                "FROM " + DatabaseHelper.TABLE_ITEM +
                " WHERE " + DatabaseHelper.COLUMN_ID_PHANLOAINGANH + " = " + DatabaseHelper.TABLE_PHANLOAINGANH + "." + DatabaseHelper.COLUMN_ID_PHANLOAINGANH +
                ")";

        db.execSQL(query);
        db.close();
    }
// đăng xuất
    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
//        clearLoginStatus();
//        updateUI(null);

        // Chuyển đến màn hình đăng nhập (LoginActivity) hoặc màn hình chính khác
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();;
    }


    public void insertDataToFirebase() {
        // Mở database SQLite
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Truy vấn dữ liệu từ bảng "Phanloai_nganh"
        Cursor phanloaiCursor = db.query(TABLE_PHANLOAINGANH, null, null, null, null, null, null);

        // Duyệt qua các hàng dữ liệu của bảng "Phanloai_nganh"
        while (phanloaiCursor.moveToNext()) {
            int phanloaiId = phanloaiCursor.getInt(phanloaiCursor.getColumnIndex(COLUMN_ID_PHANLOAINGANH));
            int imgPhanLoai = phanloaiCursor.getInt(phanloaiCursor.getColumnIndex(COLUMN_IMG_PHANLOAINGANH));
            String tenPhanLoai = phanloaiCursor.getString(phanloaiCursor.getColumnIndex(COLUMN_NAME_PHANLOAINGANH));
            int tongNganh = phanloaiCursor.getInt(phanloaiCursor.getColumnIndex(COLUMN_TONG_NGANH));
            String kieuPhanLoai = phanloaiCursor.getString(phanloaiCursor.getColumnIndex(COLUMN_KIEU_PHANLOAINGANH));

            // Tạo đối tượng PhanLoaiNganh từ dữ liệu lấy được
            Phanloai_Nganh phanLoaiNganh = new Phanloai_Nganh(phanloaiId, imgPhanLoai, tenPhanLoai, tongNganh, kieuPhanLoai);

            // Ghi dữ liệu lên Firebase Database
            mDatabase.child("users").child(userID).child("phanloai_nganh").child(String.valueOf(phanloaiId)).setValue(phanLoaiNganh);
        }

        // Đóng cursor của bảng "Phanloai_nganh"
        phanloaiCursor.close();

        // Truy vấn dữ liệu từ bảng "item"
        Cursor itemCursor = db.query(TABLE_ITEM, null, null, null, null, null, null);

        // Duyệt qua các hàng dữ liệu của bảng "item"
        while (itemCursor.moveToNext()) {

            int itemId = itemCursor.getInt(itemCursor.getColumnIndex(COLUMN_ID_ITEM));
            String amount = itemCursor.getString(itemCursor.getColumnIndex(COLUMN_AMOUNT));
            String kieu = itemCursor.getString(itemCursor.getColumnIndex(COLUMN_KIEU));

            int phanloaiId = itemCursor.getInt(itemCursor.getColumnIndex(COLUMN_ID_PHANLOAINGANH));
            String date = itemCursor.getString(itemCursor.getColumnIndex(COLUMN_DATE));
            String time = itemCursor.getString(itemCursor.getColumnIndex(COLUMN_TIME));
            String note = itemCursor.getString(itemCursor.getColumnIndex(COLUMN_NOTE));

            // Tạo đối tượng Item từ dữ liệu lấy được
            Item item = new Item(itemId, amount, kieu, phanloaiId, date, time, note,null , 0);

            // Ghi dữ liệu lên Firebase Database
            mDatabase.child("users").child(userID).child("item").child(String.valueOf(itemId)).setValue(item);
        }

        // Đóng cursor của bảng "item"
        itemCursor.close();

        // Đóng database
        db.close();
    }




    private void loadDataFromFirebase(Context context, String tableName) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("users").child(userID).child(tableName);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                // Xóa dữ liệu hiện có trong bảng
                db.delete(tableName, null, null);

                // Lặp qua dữ liệu đã lấy
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ContentValues values = new ContentValues();

                    // Thêm các cặp khóa-giá trị vào ContentValues tùy theo từng bảng
                    if (tableName.equals(DatabaseHelper.TABLE_ITEM)) {
                        int idItem = snapshot.child("id_Item").getValue(Integer.class);
                        String kieu = snapshot.child("nameKieu").getValue(String.class);
                        String amount = snapshot.child("value").getValue(String.class);
                        int phanloaiId = snapshot.child("idPhanLoai").getValue(Integer.class);
                        String date = snapshot.child("date").getValue(String.class);
                        String time = snapshot.child("time").getValue(String.class);
                        String note = snapshot.child("note").getValue(String.class);

                        values.put(DatabaseHelper.COLUMN_ID_ITEM, idItem);
                        values.put(DatabaseHelper.COLUMN_KIEU, kieu);
                        values.put(DatabaseHelper.COLUMN_AMOUNT, amount);
                        values.put(DatabaseHelper.COLUMN_ID_PHANLOAINGANH, phanloaiId);
                        values.put(DatabaseHelper.COLUMN_DATE, date);
                        values.put(DatabaseHelper.COLUMN_TIME, time);
                        values.put(DatabaseHelper.COLUMN_NOTE, note);
                    } else if (tableName.equals("phanloai_nganh")) {
                        int idPhanLoaiNganh = snapshot.child("idphanloainganh").getValue(Integer.class);
                        int imgPhanLoaiNganh = snapshot.child("imgphanloainganh").getValue(Integer.class);
                        String tenPhanLoaiNganh = snapshot.child("tenphanloainganh").getValue(String.class);
                        String kieuPhanLoaiNganh = snapshot.child("kieuphanloainganh").getValue(String.class);
                        int tongNganh = snapshot.child("tong").getValue(Integer.class);

                        values.put(DatabaseHelper.COLUMN_ID_PHANLOAINGANH, idPhanLoaiNganh);
                        values.put(DatabaseHelper.COLUMN_IMG_PHANLOAINGANH, imgPhanLoaiNganh);
                        values.put(DatabaseHelper.COLUMN_NAME_PHANLOAINGANH, tenPhanLoaiNganh);
                        values.put(DatabaseHelper.COLUMN_KIEU_PHANLOAINGANH, kieuPhanLoaiNganh);
                        values.put(DatabaseHelper.COLUMN_TONG_NGANH, tongNganh);
                    }

                    db.insert(tableName, null, values);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Phương thức này được gọi khi việc lấy dữ liệu bị hủy bỏ hoặc thất bại
            }
        });
    }



    public boolean isTableEmpty(String tableName) {
        databaseHelper = new DatabaseHelper(this);

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + tableName;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }




}
