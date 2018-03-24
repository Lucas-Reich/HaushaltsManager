package com.example.lucas.haushaltsmanager.Entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.haushaltsmanager.R;

public class Tag implements Parcelable {

    private String TAG = Tag.class.getSimpleName();

    private long index;
    private String name;

    public Tag(long index, @NonNull String name) {

        setIndex(index);
        setName(name);
    }

    public Tag(@NonNull String name) {

        this(-1, name);
    }

    public Tag(Parcel source) {

        Log.v(TAG, "ParcelData (Parcel Source): time to put back parcel data");
        setIndex(source.readLong());
        setName(source.readString());
    }

    /**
     * Methode um einen dummy Tag zu erstellen
     *
     * @param context context
     * @return dummy tag
     */
    public static Tag createDummyTag(Context context) {

        return new Tag(-1, context.getResources().getString(R.string.no_name));
    }

    /**
     * Methode um den index des Tags zu manipulieren
     *
     * @param index neuer index
     */
    private void setIndex(long index) {

        this.index = index;
    }

    public long getIndex() {

        return this.index;
    }

    public void setName(@NonNull String name) {

        this.name = name;
    }

    @NonNull
    public String getName() {

        return this.name;
    }

    /**
     * Methode die die Felder des Tags checkt ob diese gesetzt sind oder nicht.
     * Sind alle Felder gesetzt, dann kann das Tag ohne Probleme in die Datenbank geschrieben werden.
     *
     * @return Ob das Tag in die Datenbank geschrieben werden kann
     */
    public boolean isSet() {

        return !this.name.isEmpty();
    }

    /**
     * Wenn der index des Tags größer als null ist, dann gibt es das Tag bereits in der Datenbank
     * und man kann sie sicher verwenden.
     *
     * @return boolean
     */
    public boolean isValid() {

        return getIndex() > -1;
    }

    /**
     * Methode um zu überprüfen, ob das angegebene Tag das gleiche ist wie dieses.
     *
     * @param otherTag Anderes Tag
     * @return boolean
     */
    public boolean equals(Tag otherTag) {

        return getName().equals(otherTag.getName());
    }

    public String toString() {

        return getIndex() + " " + getName();
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
