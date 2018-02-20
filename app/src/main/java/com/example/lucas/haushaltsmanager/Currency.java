package com.example.lucas.haushaltsmanager;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

class Currency implements Parcelable {

    private long index;
    private String currencyName;
    private String currencyShortName;
    private String currencySymbol;
    private double rateToBase;

    private static String TAG = Currency.class.getSimpleName();

    Currency(long index, @NonNull String currencyName, @NonNull String currencyShortName, @NonNull String currencySymbol, Double rateToBase) {

        this.index = index;
        this.currencyName = currencyName;
        this.currencyShortName = currencyShortName;
        this.currencySymbol = currencySymbol;
        this.rateToBase = rateToBase != null ? rateToBase : 0;
    }

    Currency(long index, @NonNull String currencyName, @NonNull String currencyShortName, @NonNull String currencySymbol) {

        this(index, currencyName, currencyShortName, currencySymbol, null);
    }

    Currency(@NonNull String currencyName, @NonNull String currencyShortName, @NonNull String currencySymbol) {

        this(-1, currencyName, currencyShortName, currencySymbol, null);
    }

    /**
     * Parcelable Constructor
     *
     * @param source Parceldata
     */
    Currency(Parcel source) {

        Log.v(TAG, "Recreating Currency from parcel data");
        index = source.readLong();
        currencyName = source.readString();
        currencyShortName = source.readString();
        currencySymbol = source.readString();
        rateToBase = source.readDouble();
    }

    /**
     * Methode um eine DummyCategory zu erstellen die keine Werte entält
     *
     * @return dummy Category
     */
    static Currency createDummyCurrency() {

        return new Currency("", "", "");
    }

    public long getIndex() {

        return index;
    }

    @NonNull
    String getCurrencyName() {

        return currencyName;
    }

    @NonNull
    String getCurrencyShortName() {

        return currencyShortName;
    }

    @NonNull
    String getCurrencySymbol() {

        return currencySymbol != null ? currencySymbol : currencyShortName;
    }

    double getRateToBase() {

        return rateToBase;
    }

    void setRateToBase(double rate) {

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
