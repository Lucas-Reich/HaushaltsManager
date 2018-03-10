package com.example.lucas.haushaltsmanager.Entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.haushaltsmanager.R;

public class Currency implements Parcelable {

    private long index;
    private String currencyName;
    private String currencyShortName;
    private String currencySymbol;
    private double rateToBase;

    private static String TAG = Currency.class.getSimpleName();

    public Currency(long index, @NonNull String currencyName, @NonNull String currencyShortName, @NonNull String currencySymbol, Double rateToBase) {

        this.index = index;
        this.currencyName = currencyName;
        this.currencyShortName = currencyShortName;
        this.currencySymbol = currencySymbol;
        this.rateToBase = rateToBase != null ? rateToBase : 0;
    }

    public Currency(long index, @NonNull String currencyName, @NonNull String currencyShortName, @NonNull String currencySymbol) {

        this(index, currencyName, currencyShortName, currencySymbol, null);
    }

    public Currency(@NonNull String currencyName, @NonNull String currencyShortName, @NonNull String currencySymbol) {

        this(-1, currencyName, currencyShortName, currencySymbol, null);
    }

    /**
     * Parcelable Constructor
     *
     * @param source Parceldata
     */
    public Currency(Parcel source) {

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
    public static Currency createDummyCurrency(Context context) {

        return new Currency(-1, context.getResources().getString(R.string.no_name), "NON", "");
    }

    public long getIndex() {

        return index;
    }

    @NonNull
    public String getCurrencyName() {

        return currencyName;
    }

    @NonNull
    public String getCurrencyShortName() {

        return currencyShortName;
    }

    @NonNull
    public String getCurrencySymbol() {

        return currencySymbol != null ? currencySymbol : currencyShortName;
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
