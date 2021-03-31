package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.UUID;

// TODO: Should I add all bookings for one account into a parameters within the Account class?
public class Account implements Parcelable {
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
    private final UUID id;
    private String name;
    private Price balance;

    public Account(
            @NonNull UUID id,
            @NonNull String name,
            @NonNull Price balance
    ) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

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
    public Account(Parcel source) {
        this.id = UUID.fromString(source.readString());
        setName(source.readString());
        setBalance((Price) source.readParcelable(Price.class.getClassLoader()));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Account)) {
            return false;
        }

        Account otherAccount = (Account) obj;

        return name.equals(otherAccount.getTitle())
                && id.equals(otherAccount.getId());
    }

    @Override
    @NonNull
    public String toString() {

        return getTitle();
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
        dest.writeParcelable(balance, flags);
    }

    public UUID getId() {
        return id;
    }

    @NonNull
    public String getTitle() {

        return name;
    }

    public void setName(@NonNull String accountName) {

        this.name = accountName;
    }

    public Price getBalance() {
        return balance;
    }

    public void setBalance(Price balance) {
        this.balance = balance;
    }

    /**
     * Methode die die Felder des Kontos checkt ob diese gesetzt sind oder nicht.
     * Sind alle Felder gesetzt, dann kann das Konto ohne Probleme in die Datenbank geschrieben werden.
     *
     * @return Ob das Konto in die Datenbank geschrieben werden kann
     */
    public boolean isSet() {

        return !getTitle().equals("");
    }
}
