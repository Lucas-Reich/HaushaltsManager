package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.R;

public class Account implements Parcelable {
    private static final String TAG = Account.class.getSimpleName();

    private long mIndex;
    private String mName;
    private double mBalance;
    private Currency mCurrency;

    public Account(long index, @NonNull String accountName, double balance, @NonNull Currency currency) {

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
     * @return dummy Konto
     */
    public static Account createDummyAccount() {

        return new Account(-1, app.getContext().getString(R.string.no_name), 0, Currency.createDummyCurrency());
    }

    public long getIndex() {

        return mIndex;
    }

    private void setIndex(long index) {

        this.mIndex = index;
    }

    @NonNull
    public String getTitle() {

        return mName;
    }

    public void setName(@NonNull String accountName) {

        this.mName = accountName;
    }

    public double getBalance() {

        return mBalance;
    }

    public void setBalance(double balance) {

        this.mBalance = balance;
    }

    @NonNull
    public Currency getCurrency() {

        return mCurrency;
    }

    public void setCurrency(@NonNull Currency currency) {

        this.mCurrency = currency;
    }

    /**
     * Methode die die Felder des Kontos checkt ob diese gesetzt sind oder nicht.
     * Sind alle Felder gesetzt, dann kann das Konto ohne Probleme in die Datenbank geschrieben werden.
     *
     * @return Ob das Konto in die Datenbank geschrieben werden kann
     */
    public boolean isSet() {

        return !this.mName.isEmpty() && this.mCurrency.isSet();
    }

    /**
     * Wenn der mIndex des Kontos größer als null ist, dann gibt es das Konto bereits in der Datenbank
     * und man kann es sicher verwenden.
     *
     * @return boolean
     */
    public boolean isValid() {

        return getIndex() > -1 && getCurrency().isValid();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Account) {

            Account otherAccount = (Account) obj;

            boolean result;
            result = this.mName.equals(otherAccount.getTitle());
            result = result && (this.mIndex == otherAccount.getIndex());

            return result;
        } else {

            return false;
        }
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
        dest.writeLong(mIndex);
        dest.writeString(mName);
        dest.writeDouble(mBalance);
        dest.writeParcelable(mCurrency, flags);
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
