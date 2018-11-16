package com.example.lucas.haushaltsmanager.Utils;

import java.text.NumberFormat;
import java.util.Locale;

public class PriceUtils {
    public static String toHumanReadablePrice(double price) {
        // TODO: NumberFormater hängt an die Zahl auch noch das Währungszeichen.
        // Wenn ich herausfinde wie ich das abstellen kann, sollte ich wieder NumberFormat benutzen
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        String formattedPrice = formatter.format(price);
        return String.format(Locale.getDefault(), "%.2f", price);
    }
}
