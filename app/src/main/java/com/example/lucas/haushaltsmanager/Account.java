package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

class Account implements Parcelable {

    /**
     * is set to 9999 if the expense with this account has children
     * is set to 8888 if the expense with this account is an date exp_listview_separator
     */
    private long index;
    private String accountName;
    private int balance;
    private Currency currency;

    private String TAG = Account.class.getSimpleName();

    Account(long index, @NonNull String accountName, Integer balance,@NonNull Currency currency) {

        this.index = index;
        this.accountName = accountName;
        this.balance = balance != null ? balance : 0;
        this.currency = currency;
    }

    Account(@NonNull String accountName, Integer balance, @NonNull Currency currency) {

        this(-1, accountName, balance != null ? balance : 0, currency);
    }

    /**
     * this constructor converts our parcelable object back into an Category object
     *
     * see: http://prasanta-paul.blogspot.de/2010/06/android-parcelable-example.html (Parcelable ArrayList)
     * and: https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents for further explanations (Parcelable Object)
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
     * Methode um ein dummy Konto zu erzeugen
     *
     * @param context System context
     * @return dummy Konto
     */
    public static Account createDummyAccount(Context context) {

        return new Account(-1, context.getResources().getString(R.string.no_name), 0, Currency.createDummyCurrency());
    }

    public long getIndex() {

        return index;
    }

    @NonNull
    String getAccountName() {

        return accountName;
    }

    void setAccountName(@NonNull String accountName) {

        this.accountName = accountName;
    }

    int getBalance() {

        return balance;
    }

    void setBalance(int balance) {

        this.balance = balance;
    }

    @NonNull
    Currency getCurrency() {

        return currency;
    }

    void setCurrency(@NonNull Currency currency) {

        this.currency = currency;
    }


    //make class Parcelable

    /**
     * can be ignored mostly
     *
     * @return int
     */
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
