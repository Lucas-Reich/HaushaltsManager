package com.example.lucas.haushaltsmanager.Utils;

import com.example.lucas.haushaltsmanager.entities.Price;

import java.text.NumberFormat;
import java.util.Locale;

import javax.annotation.Nullable;

public class MoneyUtils {
    private static final String DEFAULT_PRICE = "-,--";

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
