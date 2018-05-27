package com.example.lucas.haushaltsmanager.Entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.haushaltsmanager.R;

public class Currency implements Parcelable {

    private static String TAG = Currency.class.getSimpleName();

    private long index;
    private String name;
    private String shortName;
    private String symbol;
    private double rateToBase;

    public Currency(long index, @NonNull String currencyName, @NonNull String currencyShortName, @NonNull String currencySymbol, Double rateToBase) {

        setIndex(index);
        setName(currencyName);
        setShortName(currencyShortName);
        setSymbol(currencySymbol);
        setRateToBase(rateToBase != null ? rateToBase : 0);
    }

    public Currency(long index, @NonNull String currencyName, @NonNull String currencyShortName, @NonNull String currencySymbol) {

        this(index, currencyName, currencyShortName, currencySymbol, null);
    }

    /**
     * Parcelable Constructor
     *
     * @param source Parceldata
     */
    public Currency(Parcel source) {

        Log.v(TAG, "Recreating Currency from parcel data");
        setIndex(source.readLong());
        setName(source.readString());
        setShortName(source.readString());
        setSymbol(source.readString());
        setRateToBase(source.readDouble());
    }

    /**
     * Methode um eine DummyCategory zu erstellen die keine Werte entält
     *
     * @return dummy Category
     */
    public static Currency createDummyCurrency(Context context) {

        return new Currency(-1, context.getResources().getString(R.string.no_name), "NON", "", null);
    }

    public long getIndex() {

        return index;
    }

    private void setIndex(long index) {

        this.index = index;
    }

    @NonNull
    public String getName() {

        return name;
    }

    private void setName(@NonNull String name) {

        this.name = name;
    }

    @NonNull
    public String getShortName() {

        return shortName;
    }

    private void setShortName(@NonNull String shortName) {

        this.shortName = shortName;
    }

    public @NonNull String getSymbol() {

        return symbol != null ? symbol : shortName;
    }

    private void setSymbol(@NonNull String symbol) {

        this.symbol = symbol;
    }

    public double getRateToBase() {

        return rateToBase;
    }

    public void setRateToBase(double rate) {

        this.rateToBase = rate;
    }

    /**
     * Methode die die Felder der Währung checkt ob diese gesetzt sind oder nicht.
     * Sind alle Felder gesetzt, dann kann die Währung ohne Probleme in die Datenbank geschrieben werden.
     *
     * @return Ob die Währung in die Datenbank geschrieben werden kann
     */
    public boolean isSet() {

        return !this.name.isEmpty() && !this.shortName.isEmpty() && !this.symbol.isEmpty();
    }

    /**
     * Wenn der index der Währung größer als null ist, dann gibt es die Währung bereits in der Datenbank
     * und man kann sie sicher verwenden.
     *
     * @return boolean
     */
    public boolean isValid() {

        return getIndex() > -1;
    }
    public String toString() {

        return getIndex() + " " + getName() + " " + getRateToBase();
    }

    /**
     * Methode um zu überprüfen, ob die angegebene Währung die gleiche ist wie diese.
     *
     * @param otherCurrency Andere Währung
     * @return boolean
     */
    public boolean equals(Currency otherCurrency) {

        boolean result = getName().equals(otherCurrency.getName());
        result = result && getShortName().equals(otherCurrency.getShortName());
        result = result && getSymbol().equals(otherCurrency.getSymbol());
        result = result && (getRateToBase() == otherCurrency.getRateToBase());

        return result;
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
        dest.writeString(name);
        dest.writeString(shortName);
        dest.writeString(symbol);
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
