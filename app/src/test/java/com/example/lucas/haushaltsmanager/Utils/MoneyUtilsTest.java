package com.example.lucas.haushaltsmanager.Utils;

import static org.junit.Assert.assertEquals;

import com.example.lucas.haushaltsmanager.entities.Price;

import org.junit.Test;

import java.util.Locale;

public class MoneyUtilsTest {
    @Test
    public void testWithNullPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(null, Locale.GERMANY);

        assertEquals("-,--", actualValue);
    }

    @Test
    public void testWithEmptyPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(new Price(0), Locale.GERMANY);

        assertEquals("-,--", actualValue);
    }

    @Test
    public void testWithPositiveOneDigitPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(1),
                Locale.GERMANY
        );

        assertEquals("1,00", actualValue);
    }

    @Test
    public void testWithPositiveFourDigitPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(1000),
                Locale.GERMANY
        );

        assertEquals("1.000,00", actualValue);
    }

    @Test
    public void testWithPositiveFourDigitAndCentPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(1000.77),
                Locale.GERMANY
        );

        assertEquals("1.000,77", actualValue);
    }

    @Test
    public void testWithNegativeOneDigitPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(-1),
                Locale.GERMANY
        );

        assertEquals("-1,00", actualValue);
    }

    @Test
    public void testWithNegativeFourDigitPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(-1000),
                Locale.GERMANY
        );

        assertEquals("-1.000,00", actualValue);
    }

    @Test
    public void testWithNegativeFourDigitAndCentPrice() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(-1000.98),
                Locale.GERMANY
        );

        assertEquals("-1.000,98", actualValue);
    }

    @Test
    public void testWithUSLocale() {
        String actualValue = MoneyUtils.formatHumanReadable(
                new Price(1000.33),
                Locale.US
        );

        assertEquals("1,000.33", actualValue);
    }
}
