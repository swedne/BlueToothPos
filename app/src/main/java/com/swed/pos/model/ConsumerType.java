package com.swed.pos.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ConsumerType
        implements Parcelable {
    public static final Creator CREATOR = new Creator() {
        public ConsumerType createFromParcel(Parcel paramAnonymousParcel) {
            return new ConsumerType(paramAnonymousParcel);
        }

        public ConsumerType[] newArray(int paramAnonymousInt) {
            return new ConsumerType[paramAnonymousInt];
        }
    };
    private String endtime;
    private String engname;
    private String exchangerate;
    private String id;
    private String listorder;
    private String memo;
    private String name;
    private String percentage;
    private String starttime;
    private String thumb;
    private String unit;

    public ConsumerType() {
    }

    protected ConsumerType(Parcel paramParcel) {
        this.percentage = paramParcel.readString();
        this.memo = paramParcel.readString();
        this.exchangerate = paramParcel.readString();
        this.id = paramParcel.readString();
        this.starttime = paramParcel.readString();
        this.name = paramParcel.readString();
        this.unit = paramParcel.readString();
        this.listorder = paramParcel.readString();
        this.engname = paramParcel.readString();
        this.endtime = paramParcel.readString();

        this.thumb = paramParcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public String getEndtime() {
        return this.endtime;
    }

    public String getEngname() {
        return this.engname;
    }

    public String getExchangerate() {
        return this.exchangerate;
    }

    public float getExchangerateFloat() {
        try {
            System.out.println("exchangerate----->" + this.exchangerate);
            float f = Float.parseFloat(this.exchangerate);
            return f;
        } catch (Exception localException) {
        }
        return 1.0F;
    }

    public String getId() {
        return this.id;
    }

    public String getListorder() {
        return this.listorder;
    }

    public String getMemo() {
        return this.memo;
    }

    public String getName() {
        return this.name;
    }

    public String getPercentage() {
        return this.percentage;
    }

    public String getStarttime() {
        return this.starttime;
    }

    public String getThumb() {
        return this.thumb;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setEndtime(String paramString) {
        this.endtime = paramString;
    }

    public void setEngname(String paramString) {
        this.engname = paramString;
    }

    public void setExchangerate(String paramString) {
        this.exchangerate = paramString;
    }

    public void setId(String paramString) {
        this.id = paramString;
    }

    public void setListorder(String paramString) {
        this.listorder = paramString;
    }

    public void setMemo(String paramString) {
        this.memo = paramString;
    }

    public void setName(String paramString) {
        this.name = paramString;
    }

    public void setPercentage(String paramString) {
        this.percentage = paramString;
    }

    public void setStarttime(String paramString) {
        this.starttime = paramString;
    }

    public void setThumb(String paramString) {
        this.thumb = paramString;
    }

    public void setUnit(String paramString) {
        this.unit = paramString;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt) {
        paramParcel.writeString(this.name);
        paramParcel.writeString(this.id);
        paramParcel.writeString(this.unit);
        paramParcel.writeString(this.listorder);
        paramParcel.writeString(this.memo);
        paramParcel.writeString(this.engname);
        paramParcel.writeString(this.exchangerate);
        paramParcel.writeString(this.percentage);
        paramParcel.writeString(this.starttime);
        paramParcel.writeString(this.endtime);
        paramParcel.writeString(this.thumb);
    }
}
