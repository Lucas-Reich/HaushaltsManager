package com.example.lucas.haushaltsmanager.Utils;

import com.example.lucas.haushaltsmanager.Entities.Price;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyUtils {
    public static String toHumanReadablePrice(double price) {
        // REFACTOR: Die andere toHumandReadablePrice benutzen, wenn sie nicht mehr Deprecated ist.
        return String.format(Locale.getDefault(), "%.2f", price);
    }

    @Deprecated
    public static String toHumanReadablePriceDeprecated(double price) {
        // IMPROVEMENT: NumberFormater hängt an die Zahl auch noch das Währungszeichen.
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

        return formatter.format(price);
    }

    public static String formatHumanReadable(Price price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(price.getSignedValue());
    }
}
