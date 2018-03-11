package com.example.lucas.haushaltsmanager.Entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.haushaltsmanager.R;

public class Account implements Parcelable {

    /**
     * is set to 9999 if the expense with this account has children
     * is set to 8888 if the expense with this account is an date exp_listview_separator
     */
    private long index;
    private String accountName;
    private int balance;
    private Currency currency;

    private String TAG = Account.class.getSimpleName();

    public Account(long index, @NonNull String accountName, Integer balance,@NonNull Currency currency) {

        this.index = index;
        this.accountName = accountName;
        this.balance = balance != null ? balance : 0;
        this.currency = currency;
    }

    public Account(@NonNull String accountName, Integer balance,@NonNull Currency currency) {

        this(-1, accountName, balance != null ? balance : 0, currency);
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
        index = source.readLong();
        accountName = source.readString();
        balance = source.readInt();
        currency = source.readParcelable(Account.class.getClassLoader());
    }

    /**
     * Methode um ein dummy Konto zu erstellen
     *
     * @param accountIndex Index des Kontos
     * @param context Context
     * @return dummy Konto
     */
    public static Account createDummyAccount(Context context,@Nullable Long accountIndex) {

        return new Account(accountIndex != null ? accountIndex : -1, context.getResources().getString(R.string.no_name), 0, Currency.createDummyCurrency(context));
    }

    public long getIndex() {

        return index;
    }

    /**
     * Damit man eine DummyExpense erstellen kann und im nachinein den Platzhalter setzen kann
     * @param index Database index
     */
    public void setIndex(long index) {

        this.index = index;
    }

    @NonNull
    public String getName() {

        return accountName;
    }

    public void setAccountName(@NonNull String accountName) {

        this.accountName = accountName;
    }

    public int getBalance() {

        return balance;
    }

    public void setBalance(int balance) {

        this.balance = balance;
    }

    @NonNull
    public Currency getCurrency() {

        return currency;
    }

    public void setCurrency(@NonNull Currency currency) {

        this.currency = currency;
    }

    @Override
    public String toString() {

        return this.index + " " + this.accountName;
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
        dest.writeString(accountName);
        dest.writeInt(balance);
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
