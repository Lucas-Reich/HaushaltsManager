package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

public class Directory extends File implements Parcelable {
    public static final Parcelable.Creator<Directory> CREATOR = new Parcelable.Creator<Directory>() {
        @Override
        public Directory createFromParcel(Parcel source) {

            return new Directory(source);
        }

        @Override
        public Directory[] newArray(int size) {

            return new Directory[size];
        }
    };

    public Directory(String path) {
        super(path);

        if (!isDirectory())
            throw new IllegalArgumentException(String.format("%s not a directory", path));
    }

    public Directory(Parcel source) {
        super(source.readString());
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toString());
    }
}
