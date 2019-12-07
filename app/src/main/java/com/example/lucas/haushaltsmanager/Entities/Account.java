package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.R;

public class Account implements Parcelable {
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
    private long index;
    private String name;
    private Price balance;

    public Account(long index, @NonNull String accountName, double balance, @NonNull Currency currency) {

        setIndex(index);
        setName(accountName);
        setBalance(new Price(balance, currency));
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
        setIndex(source.readLong());
        setName(source.readString());
        setBalance((Price) source.readParcelable(Price.class.getClassLoader()));
    }

    /**
     * Methode um ein dummy Konto zu erstellen
     *
     * @return dummy Konto
     */
    public static Account createDummyAccount() {

        return new Account(-1, app.getContext().getString(R.string.no_name), 0, Currency.createDummyCurrency());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Account)) {
            return false;
        }

        Account otherAccount = (Account) obj;

        return name.equals(otherAccount.getTitle())
                && index == otherAccount.getIndex();
    }

    @Override
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

        dest.writeLong(index);
        dest.writeString(name);
        dest.writeParcelable(balance, flags);
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

        return !getTitle().equals(app.getContext().getString(R.string.no_name))
                && !getTitle().equals("")
                && balance.getCurrency().isSet();
    }
}
