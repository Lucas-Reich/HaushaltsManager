package com.example.lucas.haushaltsmanager.Entities;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;

public class Currency implements Parcelable {
    // TODO: Kann ich die Currency durch die Java.util implementierung der Currency austauschen?
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

    public static Currency getDefault(Context context) {
        return new UserSettingsPreferences(context).getMainCurrency();
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Currency)) {
            return false;
        }

        Currency other = (Currency) obj;

        return getName().equals(other.getName())
                && getShortName().equals(other.getShortName())
                && getSymbol().equals(other.getSymbol());
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
