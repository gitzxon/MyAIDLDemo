package com.zxon.myaidldemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by leon on 16/4/14.
 */
public class CustomBean implements Parcelable{

    private String mName;

    private int mAge;

    public CustomBean() {

    }

    public CustomBean(Parcel in) {
        mName = in.readString();
        mAge = in.readInt();
    }

    public static final Creator<CustomBean> CREATOR = new Creator<CustomBean>() {
        @Override
        public CustomBean createFromParcel(Parcel in) {
            return new CustomBean(in);
        }

        @Override
        public CustomBean[] newArray(int size) {
            return new CustomBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mAge);
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        this.mAge = age;
    }
}
