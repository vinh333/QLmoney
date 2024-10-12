package com.example.qlmoney.Database;

import android.os.Parcel;
import android.os.Parcelable;

public class Phanloai_Nganh implements Parcelable {
    private int IDphanloainganh;
    private int imgphanloainganh;
    private int tongnganh;
    private String tenphanloainganh;
    private String kieuphanloainganh;

    public Phanloai_Nganh(int phanloaiId, int imgPhanLoai, String tenPhanLoai, int tongnganh, String kieuPhanLoai) {
        this.IDphanloainganh = phanloaiId;
        this.imgphanloainganh = imgPhanLoai;
        this.tenphanloainganh = tenPhanLoai;
        this.tongnganh = tongnganh;
        this.kieuphanloainganh = kieuPhanLoai;
    }


    protected Phanloai_Nganh(Parcel in) {
        IDphanloainganh = in.readInt();
        imgphanloainganh = in.readInt();
        tenphanloainganh = in.readString();
        kieuphanloainganh = in.readString();
    }

    public static final Creator<Phanloai_Nganh> CREATOR = new Creator<Phanloai_Nganh>() {
        @Override
        public Phanloai_Nganh createFromParcel(Parcel in) {
            return new Phanloai_Nganh(in);
        }

        @Override
        public Phanloai_Nganh[] newArray(int size) {
            return new Phanloai_Nganh[size];
        }
    };

    public int getTong() {
        return tongnganh;
    }

    public void setTong(int tongnganh) {
        this.tongnganh = tongnganh;
    }


    public int getIDphanloainganh() {
        return IDphanloainganh;
    }
    public int getImgphanloainganh() {
        return imgphanloainganh;
    }

    public String getTenphanloainganh() {
        return tenphanloainganh;
    }

    public String getKieuphanloainganh() {
        return kieuphanloainganh;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(IDphanloainganh);
        dest.writeInt(imgphanloainganh);
        dest.writeString(tenphanloainganh);
        dest.writeString(kieuphanloainganh);
    }
}
