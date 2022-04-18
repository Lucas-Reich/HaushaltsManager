package com.example.lucas.haushaltsmanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.ColorRes;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.entities.category.Category;

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

    private final double value;

    public Price(String value, Locale locale) {
        this.value = localeAwareDoubleParser(value, locale);
    }

    public Price(double price) {
        this.value = price;
    }

    public Price(double absPrice, boolean isNegative) {
        this.value = isNegative ? -absPrice : absPrice;
    }

    private Price(Parcel source) {
        value = source.readDouble();
    }

    public static Price fromValueWithCategory(double price, Category category) {
        if (category.getDefaultExpenseType().getType()) {
            return new Price(price);
        }

        return new Price(-price);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Price)) {
            return false;
        }

        Price other = (Price) obj;

        return other.getPrice() == getPrice();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(value);
    }

    public double getAbsoluteValue() {
        return Math.abs(value);
    }

    public double getPrice() {
        return value;
    }

    public boolean isNegative() {
        return value < 0;
    }

    @ColorRes
    public int getColor() {
        if (getPrice() > 0) {
            return R.color.booking_income;
        }

        if (getPrice() < 0) {
            return R.color.booking_expense;
        }

        return R.color.primary_text_color;
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
