package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

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
    private Currency currency;

    public Price(double value, boolean isNegative, @NonNull Currency currency) {
        setPrice(value, isNegative);
        setCurrency(currency);
    }

    public Price(String value, boolean isNegative, @NonNull Currency currency, Locale locale) {
        double parsedPrice = localeAwareDoubleParser(value, locale);

        setPrice(parsedPrice, isNegative);
        setCurrency(currency);
    }

    public Price(String value, @NonNull Currency currency, Locale locale) {
        double parsedPrice = localeAwareDoubleParser(value, locale);

        setPrice(parsedPrice);
        setCurrency(currency);
    }

    public Price(double price, @NonNull Currency currency) {
        setPrice(price);
        setCurrency(currency);
    }

    private Price(Parcel source) {
        value = source.readDouble();
        isNegative = source.readByte() != 0;
        currency = source.readParcelable(Currency.class.getClassLoader());
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
        dest.writeDouble(value);
        dest.writeByte((byte) (isNegative ? 1 : 0));
        dest.writeParcelable(currency, flags);
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

    private void setCurrency(Currency currency) {
        this.currency = currency;
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
        } catch (ParseException e) {
            return 0D;
        }
    }
}
