package com.example.lucas.haushaltsmanager.Utils;

import com.example.lucas.haushaltsmanager.Entities.Price;

import java.text.NumberFormat;
import java.util.Locale;

import javax.annotation.Nullable;

public class MoneyUtils {
    private static final String DEFAULT_PRICE = "-,--";

    // Falls ich mal eine Formatierung brauche mit dem Währungszeichen, kann ich die NumberFormat.getCurrencyInstance dafür nutzen.

    public static String formatHumanReadableOmitCents(Price price, Locale locale) {
        String priceString = formatHumanReadable(price, locale);

        return priceString.substring(0, priceString.length() - 3);
    }

    public static String formatHumanReadable(@Nullable Price price, Locale locale) {
        if (isNullOrEmpty(price)) {
            return DEFAULT_PRICE;
        }

        NumberFormat formatter = getDefaultFormatter(locale);

        return formatter.format(price.getSignedValue());
    }

    private static NumberFormat getDefaultFormatter(Locale locale) {
        NumberFormat formatter = NumberFormat.getNumberInstance(locale);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter;
    }

    private static boolean isNullOrEmpty(Price price) {
        if (null == price) {
            return true;
        }

        return 0 == price.getUnsignedValue();
    }
}
