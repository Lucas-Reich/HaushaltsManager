package com.example.lucas.haushaltsmanager.Entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.haushaltsmanager.R;

public class Account implements Parcelable {

    private String TAG = Account.class.getSimpleName();

    private long index;
    private String name;
    private double balance;
    private Currency currency;

    public Account(long index, @NonNull String accountName,double balance, @NonNull Currency currency) {

        setIndex(index);
        setName(accountName);
        setBalance(balance);
        setCurrency(currency);
    }

    public Account(@NonNull String accountName, double balance, @NonNull Currency currency) {

        this(-1, accountName, balance, currency);
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

        Log.v(TAG, "Recreating Account from parcel data");
        setIndex(source.readLong());
        setName(source.readString());
        setBalance(source.readDouble());
        setCurrency((Currency) source.readParcelable(Currency.class.getClassLoader()));
    }

    /**
     * Methode um ein dummy Konto zu erstellen
     *
     * @param context      Context
     * @return dummy Konto
     */
    public static Account createDummyAccount(Context context) {

        return new Account(-1, context.getResources().getString(R.string.no_name), 0, Currency.createDummyCurrency(context));
    }

    public long getIndex() {

        return index;
    }

    private void setIndex(long index) {

        this.index = index;
    }

    @NonNull
    public String getTitle() {

        return name;
    }

    public void setName(@NonNull String accountName) {

        this.name = accountName;
    }

    public double getBalance() {

        return balance;
    }

    public void setBalance(double balance) {

        this.balance = balance;
    }

    @NonNull
    public Currency getCurrency() {

        return currency;
    }

    public void setCurrency(@NonNull Currency currency) {

        this.currency = currency;
    }

    /**
     * Methode die die Felder des Kontos checkt ob diese gesetzt sind oder nicht.
     * Sind alle Felder gesetzt, dann kann das Konto ohne Probleme in die Datenbank geschrieben werden.
     *
     * @return Ob das Konto in die Datenbank geschrieben werden kann
     */
    public boolean isSet() {

        return !this.name.isEmpty() && this.currency.isSet();
    }

    /**
     * Wenn der index des Kontos größer als null ist, dann gibt es das Konto bereits in der Datenbank
     * und man kann es sicher verwenden.
     *
     * @return boolean
     */
    public boolean isValid() {

        return getIndex() > -1 && getCurrency().isValid();
    }

    /**
     * Methode die überprüft ob das angegebe Konto das gleiche ist wie dieses
     *
     * @param otherAccount Anderes Konot
     * @return sind die Konten gleich
     */
    public boolean equals(Account otherAccount) {

        boolean result;

        result = this.name.equals(otherAccount.getTitle());
        result = result && (this.index == otherAccount.getIndex());

        return result;
    }

    @Override
    public String toString() {

        return getIndex() + " " + getTitle() + " " + getBalance();
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

        Log.v(TAG, "write to parcel..." + flags);
        dest.writeLong(index);
        dest.writeString(name);
        dest.writeDouble(balance);
        dest.writeParcelable(currency, flags);
    }

    /**
     * regenerating the parcelable object back into our Category object
     */
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
}
