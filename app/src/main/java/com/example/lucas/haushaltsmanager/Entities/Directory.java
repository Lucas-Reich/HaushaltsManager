package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;

public class Directory extends File implements Parcelable {
    private static final String TAG = Directory.class.getSimpleName();

    public Directory(String path) {
        super(path);

        if (!isDirectory())
            throw new IllegalArgumentException(String.format("%s not a directory", path));
    }

    public Directory(Parcel source) {
        super(source.readString());

        Log.v(TAG, "Recreating Entity");
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.v(TAG, "writing to Parcel " + flags);

        dest.writeString(toString());
    }

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
}
