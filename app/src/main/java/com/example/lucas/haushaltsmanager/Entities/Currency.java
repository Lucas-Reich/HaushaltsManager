package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.R;

public class Currency implements Parcelable {
    private static final String TAG = Currency.class.getSimpleName();

    private long index;
    private String name;
    private String shortName;
    private String symbol;

    public Currency(long index, @NonNull String currencyName, @NonNull String currencyShortName, @NonNull String currencySymbol) {

        setIndex(index);
        setName(currencyName);
        setShortName(currencyShortName);
        setSymbol(currencySymbol);
    }

    public Currency(@NonNull String currencyName, @NonNull String shortName, @NonNull String symbol) {

        setName(currencyName);
        setShortName(shortName);
        setSymbol(symbol);
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
    }

    /**
     * Methode um eine DummyCategory zu erstellen die keine Werte entält
     *
     * @return dummy Category
     */
    public static Currency createDummyCurrency() {

        return new Currency(-1, app.getContext().getString(R.string.no_name), "NON", "");
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

    public void setName(@NonNull String name) {

        this.name = name;
    }

    @NonNull
    public String getShortName() {

        return shortName;
    }

    public void setShortName(@NonNull String shortName) {

        this.shortName = shortName;
    }

    public @NonNull
    String getSymbol() {

        return symbol != null ? symbol : shortName;
    }

    public void setSymbol(@NonNull String symbol) {

        this.symbol = symbol;
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

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Currency) {

            Currency otherCurrency = (Currency) obj;

            boolean result = getName().equals(otherCurrency.getName());
            result = result && getShortName().equals(otherCurrency.getShortName());
            result = result && getSymbol().equals(otherCurrency.getSymbol());

            return result;
        } else {

            return false;
        }
    }

    public String toString() {

        return getName();
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
