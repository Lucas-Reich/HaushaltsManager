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

    private static String TAG = Currency.class.getSimpleName();

    Currency(Long index, String currencyName, String currencyShortName, String currencySymbol, Double rateToBase) {

        this.index = index != null ? index : -1;
        this.currencyName = currencyName;
        this.currencyShortName = currencyShortName;
        this.currencySymbol = currencySymbol;
        this.rateToBase = rateToBase != null ? rateToBase : 0;
    }

    Currency(long index, String currencyName, String currencyShortName, String currencySymbol) {

        this(index, currencyName, currencyShortName, currencySymbol, null);
    }

    Currency(String currencyName, String currencyShortName, String currencySymbol) {

        this(null, currencyName, currencyShortName, currencySymbol, null);
    }

    Currency(Parcel source) {

        this(source.readLong(), source.readString(), source.readString(), source.readString(), source.readDouble());
        Log.v(TAG, "ParcelData (Parcel source): time to put back parcel data");
    }

    void setIndex(long index) {

        this.index = index;
    }

    public long getIndex() {

        return index;
    }

    String getCurrencyName() {

        return currencyName;
    }

    String getCurrencyShortName() {

        return currencyShortName;
    }

    String getCurrencySymbol() {

        return currencySymbol;
    }

    double getRateToBase() {

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


    @Override
    public void writeToParcel(Parcel dest, int flags) {

        Log.v(TAG, "write to parcel..." + flags);
        dest.writeLong(index);
        dest.writeString(currencyName);
        dest.writeString(currencyShortName);
        dest.writeString(currencySymbol);
        dest.writeDouble(rateToBase);
    }

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
