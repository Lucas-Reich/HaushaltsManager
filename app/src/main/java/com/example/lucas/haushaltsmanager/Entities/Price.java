package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

public class Price implements Parcelable {
    private static final String TAG = Price.class.getSimpleName();

    private double value;
    private boolean isNegative;
    private Currency currency;

    public Price(double value, boolean isNegative, @NonNull Currency currency) {
        setPrice(value, isNegative);
        setCurrency(currency);
    }

    public Price(double price, @NonNull Currency currency) {
        setPrice(price);
        setCurrency(currency);
    }

    private Price(Parcel source) {
        Log.v(TAG, "ParcelData (Parcel Source): time to put back parcel data");

        value = source.readDouble();
        isNegative = source.readByte() != 0;
        currency = source.readParcelable(Currency.class.getClassLoader());
    }

    public double getUnsignedValue() {
        return value;
    }

    public double getSignedValue() {
        if (isNegative) {
            return -value;
        }

        return value;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Price)) {
            return false;
        }

        Price other = (Price) obj;

        return other.getSignedValue() == getSignedValue()
                && other.getCurrency().equals(getCurrency());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.v(TAG, " write to Parcel..." + flags);

        dest.writeDouble(value);
        dest.writeByte((byte) (isNegative ? 1 : 0));
        dest.writeParcelable(currency, flags);
    }

    public static final Parcelable.Creator<Price> CREATOR = new Parcelable.Creator<Price>() {

        @Override
        public Price createFromParcel(Parcel in) {

            return new Price(in);
        }

        @Override
        public Price[] newArray(int size) {

            return new Price[size];
        }
    };

    private void setPrice(double price) {
        if (price >= 0) {
            this.value = price;
            isNegative = false;

            return;
        }

        value = Math.abs(price);
        isNegative = true;
    }

    private void setPrice(double price, boolean isNegative) {
        this.isNegative = isNegative;
        this.value = price;
    }

    private void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
