package com.example.lucas.haushaltsmanager.Utils;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceUtils {
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
}
