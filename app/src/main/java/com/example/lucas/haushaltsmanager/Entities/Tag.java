package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.R;

public class Tag implements Parcelable {
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
        setIndex(source.readLong());
        setName(source.readString());
    }

    /**
     * Methode um einen dummy Tag zu erstellen
     *
     * @return dummy tag
     */
    public static Tag createDummyTag() {

        return new Tag(-1, app.getContext().getString(R.string.no_name));
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Tag) {

            Tag otherTag = (Tag) obj;

            return getName().equals(otherTag.getName());
        } else {

            return false;
        }
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
        dest.writeLong(this.index);
        dest.writeString(this.name);
    }

    public long getIndex() {

        return this.index;
    }

    /**
     * Methode um den index des Tags zu manipulieren
     *
     * @param index neuer index
     */
    private void setIndex(long index) {

        this.index = index;
    }

    @NonNull
    public String getName() {

        return this.name;
    }

    public void setName(@NonNull String name) {

        this.name = name;
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
}
