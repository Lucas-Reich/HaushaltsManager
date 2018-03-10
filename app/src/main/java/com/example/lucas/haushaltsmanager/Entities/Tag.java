package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Tag implements Parcelable {

    private Long index;
    private String name;
    private String TAG = "TAG";

    public Tag(Long index, String name) {

        this.index = index;
        this.name = name;
    }

    public Tag(Parcel source) {

        Log.v(TAG, "ParcelData (Parcel Source): time to put back parcel data");
        this.index = source.readLong();
        this.name = source.readString();
    }

    public Tag(String name) {

        this(null, name);
    }

    public void setIndex(long index) {

        this.index = index;
    }

    public Long getIndex() {

        return this.index;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getName() {

        return this.name;
    }


    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        Log.v(TAG, " write to Parcel..." + flags);
        dest.writeLong(this.index);
        dest.writeString(this.name);
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {

        @Override
        public Tag createFromParcel(Parcel in) {

            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {

            return new Tag[size];
        }
    };
}
