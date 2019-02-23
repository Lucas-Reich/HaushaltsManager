package com.example.lucas.haushaltsmanager.Utils;

import com.example.lucas.haushaltsmanager.Entities.Currency;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyUtils {
    public static String toHumanReadablePrice(double price) {
        // REFACTOR: Die andere toHumandReadablePrice benutzen, wenn sie nicht mehr Deprecated ist.
        return String.format(Locale.getDefault(), "%.2f", price);
    }

    public static String toHumanReadablePriceWithCurrency(double price, Currency currency) {
        return String.format(Locale.getDefault(), "%.2f %s", price, currency.getSymbol());
    }

    @Deprecated
    public static String toHumanReadablePriceDeprecated(double price) {
        // IMPROVEMENT: NumberFormater hängt an die Zahl auch noch das Währungszeichen.
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

        return formatter.format(price);
    }
}
