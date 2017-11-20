package com.example.lucas.haushaltsmanager;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

class Account implements Parcelable {

    private long index;
    private String accountName;
    private int balance;
    private String currencySym;

    private String TAG = "Account: ";

    public Account(long index, String accountName, int balance) {

        this.index = index;
        this.accountName = accountName;
        this.balance = balance;
        this.currencySym = "€";
    }

    public Account(String accountName, int balance) {

        this(0, accountName, balance);
    }

    public Account() {

        this(0, "", 0);
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getCurrencySym() {
        return currencySym;
    }

    public void setCurrencySym(String currencySym) {
        this.currencySym = currencySym;
    }


    //make class Parcelable
    /**
     * This will be only used by ParcelableCategories
     * see: http://prasanta-paul.blogspot.de/2010/06/android-parcelable-example.html (Parcelable ArrayList)
     * and: https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents for further explanations (Parcelable Object)
     * <p>
     * this constructor converts our parcelable object back into an Category object
     *
     * @param source .
     */
    public Account(Parcel source) {

        Log.v(TAG, "ParcelData (Parcel source): time to put back parcel data");
        index = source.readLong();
        accountName = source.readString();
        balance = source.readInt();
        currencySym = source.readString();
    }

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
        dest.writeString(currencySym);
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
