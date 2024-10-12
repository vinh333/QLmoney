package com.example.qlmoney.Database;

public class Item {
    private String value;
    private String namekieu;
    private String date;
    private String time;
    private String note;
    private int img;
    private String name;
    private int idphanloai;
    private int id;

    private int tong;


    public Item(int id, String value, String namekieu,int phanloaiId, String date, String time, String note, String name, int img) {
        this.id = id;
        this.value = value;
        this.namekieu = namekieu;
        this.date = date;
        this.time = time;
        this.note = note;
        this.name = name;
        this.img = img;
        this.idphanloai = phanloaiId;
    }



    public int getId_Item() {
        return id;
    }

    public void setId_Item(int id) {
        this.id = id;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNameKieu() {
        return namekieu;
    }

    public void setNameKieu(String namekieu) {
        this.namekieu = namekieu;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIdPhanLoai() {
        return idphanloai;
    }

    public void setIdPhanLoai(int idPhanLoai) {
        this.idphanloai = idPhanLoai;
    }



}
