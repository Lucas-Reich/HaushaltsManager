package com.example.lucas.haushaltsmanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorRes;

import com.example.lucas.haushaltsmanager.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Price implements Parcelable {
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

    private double value;
    private boolean isNegative;

    public Price(double value, boolean isNegative) {
        setPrice(value, isNegative);
    }

    public Price(String value, Locale locale) {
        double parsedPrice = localeAwareDoubleParser(value, locale);

        setPrice(parsedPrice);
    }

    public Price(double price) {
        setPrice(price);
    }

    private Price(Parcel source) {
        value = source.readDouble();
        isNegative = source.readByte() != 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Price)) {
            return false;
        }

        Price other = (Price) obj;

        return other.getSignedValue() == getSignedValue();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(value);
        dest.writeByte((byte) (isNegative ? 1 : 0));
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

    @ColorRes
    public int getColor() {
        if (getSignedValue() > 0) {
            return R.color.booking_income;
        }

        if (getSignedValue() < 0) {
            return R.color.booking_expense;
        }

        return R.color.primary_text_color;
    }

    private void setPrice(double price) {
        if (price >= 0) {
            setPrice(price, false);

            return;
        }

        setPrice(Math.abs(price), true);
    }

    private void setPrice(double price, boolean isNegative) {
        this.isNegative = isNegative;
        this.value = price;
    }

    private double localeAwareDoubleParser(String doubleString, Locale locale) {
        try {
            return NumberFormat.getInstance(locale)
                    .parse(doubleString)
                    .doubleValue();
        } catch (ParseException | NullPointerException e) {
            return 0D;
        }
    }
}
