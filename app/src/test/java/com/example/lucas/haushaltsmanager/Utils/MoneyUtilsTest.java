package com.example.lucas.haushaltsmanager.Utils;

import com.example.lucas.haushaltsmanager.Entities.Price;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class MoneyUtilsTest {
    @Test
    public void testWithNullPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(null, Locale.GERMANY);

        assertEquals("-,--", actualValue);
    }

    @Test
    public void testWithEmptyPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(new Price(0, true), Locale.GERMANY);

        assertEquals("-,--", actualValue);
    }

    @Test
    public void testWithPositiveOneDigitPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(1, false),
                Locale.GERMANY
        );

        assertEquals("1,00", actualValue);
    }

    @Test
    public void testWithPositiveFourDigitPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(1000, false),
                Locale.GERMANY
        );

        assertEquals("1.000,00", actualValue);
    }

    @Test
    public void testWithPositiveFourDigitAndCentPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(1000.77, false),
                Locale.GERMANY
        );

        assertEquals("1.000,77", actualValue);
    }

    @Test
    public void testWithNegativeOneDigitPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(1, true),
                Locale.GERMANY
        );

        assertEquals("-1,00", actualValue);
    }

    @Test
    public void testWithNegativeFourDigitPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(1000, true),
                Locale.GERMANY
        );

        assertEquals("-1.000,00", actualValue);
    }

    @Test
    public void testWithNegativeFourDigitAndCentPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(1000.98, true),
                Locale.GERMANY
        );

        assertEquals("-1.000,98", actualValue);
    }

    @Test
    public void testWithUSLocale() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(1000.33, false),
                Locale.US
        );

        assertEquals("1,000.33", actualValue);
    }
}
