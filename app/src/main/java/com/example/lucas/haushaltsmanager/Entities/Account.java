package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "accounts")
public class Account implements Parcelable {
    @Ignore
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {

        @Override
        public Account createFromParcel(Parcel in) {

            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {

            return new Account[size];
        }
    };

    @PrimaryKey
    @NonNull
    private UUID id;
    private String name;
    private Price price;

    public Account(
            @NonNull UUID id,
            @NonNull String name,
            @NonNull Price price
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    @Ignore
    public Account(@NonNull String accountName, Price price) {
        this(UUID.randomUUID(), accountName, price);
    }

    /**
     * see: http://prasanta-paul.blogspot.de/2010/06/android-parcelable-example.html (Parcelable ArrayList)
     * and: https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents for further explanations (Parcelable Object)
     * <p>
     * this constructor converts our parcelable object back into an Category object
     *
     * @param source .
     */
    @Ignore
    public Account(Parcel source) {
        this.id = UUID.fromString(source.readString());
        setName(source.readString());
        setPrice(source.readParcelable(Price.class.getClassLoader()));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Account)) {
            return false;
        }

        Account otherAccount = (Account) obj;

        return name.equals(otherAccount.getName())
                && id.equals(otherAccount.getId());
    }

    @Override
    @NonNull
    public String toString() {

        return getName();
    }

    @Override
    public int describeContents() {

        return 0;
    }

    /**
     * converting the custom object into an parcelable object
     *
     * @param dest  destination Parcel
     * @param flags flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id.toString());
        dest.writeString(name);
        dest.writeParcelable(price, flags);
    }

    @NonNull
    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String accountName) {
        this.name = accountName;
    }

    @NonNull
    public Price getPrice() {
        return price;
    }

    public void setPrice(@NonNull Price price) {
        this.price = price;
    }

    /**
     * Methode die die Felder des Kontos checkt ob diese gesetzt sind oder nicht.
     * Sind alle Felder gesetzt, dann kann das Konto ohne Probleme in die Datenbank geschrieben werden.
     *
     * @return Ob das Konto in die Datenbank geschrieben werden kann
     */
    public boolean isSet() {

        return !getName().equals("");
    }
}
