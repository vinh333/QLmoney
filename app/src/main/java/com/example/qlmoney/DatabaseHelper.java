package com.example.qlmoney;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_KIEU = "kieu";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String TABLE_ITEM = "item";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_ID_ITEM  = "id_item";


    private static final int DATABASE_VERSION = 11;
    // database phan loai
    public static final String COLUMN_ID_PHANLOAINGANH = "IDphanloainganh";

    public static final String TABLE_PHANLOAINGANH = "Phanloai_nganh";
    public static final String COLUMN_IMG_PHANLOAINGANH = "Imgphanloainganh";
    public static final String COLUMN_NAME_PHANLOAINGANH = "Tenhanloainganh";
    public static final String COLUMN_KIEU_PHANLOAINGANH = "Kieuphanloainganh";
    private static final String DATABASE_NAME = "qlmoney.db";
    public static final String COLUMN_TONG_NGANH = "tongnganh";






    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tạo lại cả bảng TABLE_PHANLOAINGANH
        String createPhanLoaiNganhTableQuery = "CREATE TABLE " + TABLE_PHANLOAINGANH + " (" +
                COLUMN_ID_PHANLOAINGANH + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_IMG_PHANLOAINGANH + " INTEGER, " +
                COLUMN_NAME_PHANLOAINGANH + " TEXT, " +
                COLUMN_TONG_NGANH + " INTEGER DEFAULT 0 , " +

                COLUMN_KIEU_PHANLOAINGANH + " TEXT)";
//        db.execSQL("ALTER TABLE " + TABLE_PHANLOAINGANH + " ADD COLUMN " + COLUMN_TONG_NGANH + " INTEGER DEFAULT 0");

        db.execSQL(createPhanLoaiNganhTableQuery);

        // Tạo lại bảng TABLE_ITEM với cấu trúc mới
        String createItemTableQuery = "CREATE TABLE " + TABLE_ITEM + " (" +
                COLUMN_ID_ITEM + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AMOUNT + " TEXT, " +
                COLUMN_KIEU + " TEXT, " +
                COLUMN_ID_PHANLOAINGANH + " INTEGER, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_NOTE + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_ID_PHANLOAINGANH + ") REFERENCES " +
                TABLE_PHANLOAINGANH + "(" + COLUMN_ID_PHANLOAINGANH + "))";
        db.execSQL(createItemTableQuery);



    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng TABLE_ITEM hiện tại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);

        // Xóa bảng TABLE_PHANLOAINGANH hiện tại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHANLOAINGANH);
        // Xóa bảng User nếu tồn tại
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Tạo lại cả bảng TABLE_PHANLOAINGANH
        String createPhanLoaiNganhTableQuery = "CREATE TABLE " + TABLE_PHANLOAINGANH + " (" +
                COLUMN_ID_PHANLOAINGANH + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_IMG_PHANLOAINGANH + " INTEGER, " +
                COLUMN_NAME_PHANLOAINGANH + " TEXT, " +
                COLUMN_TONG_NGANH + " INTEGER DEFAULT 0 , " +

                COLUMN_KIEU_PHANLOAINGANH + " TEXT)";
        db.execSQL(createPhanLoaiNganhTableQuery);


        // Tạo lại bảng TABLE_ITEM với cấu trúc mới
        String createItemTableQuery = "CREATE TABLE " + TABLE_ITEM + " (" +
                COLUMN_ID_ITEM + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AMOUNT + " TEXT, " +
                COLUMN_KIEU + " TEXT, " +
                COLUMN_ID_PHANLOAINGANH + " INTEGER, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_NOTE + " TEXT, " +
                "FOREIGN KEY (" + COLUMN_ID_PHANLOAINGANH + ") REFERENCES " +
                TABLE_PHANLOAINGANH + "(" + COLUMN_ID_PHANLOAINGANH + "))";
        db.execSQL(createItemTableQuery);



    }

}






