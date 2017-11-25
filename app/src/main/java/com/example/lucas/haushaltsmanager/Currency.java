package com.example.lucas.haushaltsmanager;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Currency implements Parcelable {

    private long index;
    private String currencyName;
    private String currencyShortName;
    private String currencySymbol;
    private double rateToBase;

    private static String TAG = "Currency";

    /*
        public Currency(long index, String currencyName, String currencyShortName, String currencySymbol, double rateToBase) {

            this.index = index;
            this.currencyName = currencyName;
            this.currencyShortName = currencyShortName;
            this.currencySymbol = currencySymbol;
            this.rateToBase = rateToBase;
        }
    */
    public Currency(Long index, String currencyName, String currencyShortName, String currencySymbol, Double rateToBase) {

        this.index = index != null ? index : -1;
        this.currencyName = currencyName;
        this.currencyShortName = currencyShortName;
        this.currencySymbol = currencySymbol;
        this.rateToBase = rateToBase != null ? rateToBase : 0;
    }

    public Currency(String currencyName, String currencyShortName, String currencySymbol) {

        this(null, currencyName, currencyShortName, currencySymbol, null);
    }

    public Currency(Parcel source) {

        Log.v(TAG, "ParcelData (Parcel source): time to put back parcel data");
        index = source.readLong();
        currencyName = source.readString();
        currencyShortName = source.readString();
        currencySymbol = source.readString();
        rateToBase = source.readDouble();
    }

    public void setIndex(long index) {

        this.index = index;
    }

    public long getIndex() {

        return index;
    }

    public String getCurrencyName() {

        return currencyName;
    }

    public String getCurrencyShortName() {

        return currencyShortName;
    }

    public String getCurrencySymbol() {

        return currencySymbol;
    }

    public double getRateToBase() {

        return rateToBase;
    }

    public void setRateToBase(double rate) {

        this.rateToBase = rate;
    }


    // making class parcelable

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
        dest.writeString(currencyName);
        dest.writeString(currencyShortName);
        dest.writeString(currencySymbol);
        dest.writeDouble(rateToBase);
    }

    /**
     * regenerating the parcelable object back into our Category object
     */
    public static final Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() {

        @Override
        public Currency createFromParcel(Parcel in) {

            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {

            return new Currency[size];
        }
    };
}
