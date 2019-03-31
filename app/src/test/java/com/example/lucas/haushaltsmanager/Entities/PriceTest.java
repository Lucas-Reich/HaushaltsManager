package com.example.lucas.haushaltsmanager.Entities;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PriceTest {

    @Test
    public void testCreatePrice() {
        double expectedValue = 100;
        boolean expectedIsNegative = false;
        Currency expectedCurrency = createCurrency();

        Price price = new Price(expectedValue, expectedIsNegative, expectedCurrency);

        assertEquals(expectedValue, price.getSignedValue(), 0);
        assertEquals(expectedIsNegative, price.isNegative());
        assertEquals(expectedCurrency, price.getCurrency());
    }

    @Test
    public void testInferredPositiveValue() {
        double expectedValue = 100;
        Currency expectedCurrency = createCurrency();

        Price price = new Price(expectedValue, expectedCurrency);

        assertEquals(expectedValue, price.getSignedValue(), 0);
        assertFalse(price.isNegative());
        assertEquals(expectedCurrency, price.getCurrency());
    }

    @Test
    public void testInferredNegativeValue() {
        double expectedValue = -100;
        Currency expectedCurrency = createCurrency();

        Price price = new Price(expectedValue, expectedCurrency);

        assertEquals(expectedValue, price.getSignedValue(), 0);
        assertTrue(price.isNegative());
        assertEquals(expectedCurrency, price.getCurrency());
    }

    @Test
    public void testCreatePriceFromPositiveFourDigitStringWithCents() {
        String expectedValue = "1.000,33";
        Currency expectedCurrency = createCurrency();

        Price price = new Price(expectedValue, false, expectedCurrency, Locale.GERMANY);

        assertEquals(1000.33, price.getSignedValue(), 0);
        assertFalse(price.isNegative());
        assertEquals(expectedCurrency, price.getCurrency());
    }

    @Test
    public void testCreatePriceFromEmptyString() {
        Price price = new Price("", createCurrency(), Locale.GERMANY);

        assertEquals(0, price.getUnsignedValue(), 0);
    }

    @Test
    public void testInferredPositiveValueFromString() {
        Currency expectedCurrency = createCurrency();

        Price price = new Price("10.000", expectedCurrency, Locale.GERMANY);

        assertEquals(10000, price.getUnsignedValue(), 0);
        assertFalse(price.isNegative());
        assertEquals(expectedCurrency, price.getCurrency());
    }

    @Test
    public void testInferredNegativeValueFromString() {
        Currency expectedCurrency = createCurrency();

        Price price = new Price("-10.000", expectedCurrency, Locale.GERMANY);

        assertEquals(10000, price.getUnsignedValue(), 0);
        assertTrue(price.isNegative());
        assertEquals(expectedCurrency, price.getCurrency());
    }

    @Test
    public void testInvalidValueStringDefaultsToZero() {
        Price price = new Price("I am a not valid Price", createCurrency(), Locale.GERMANY);

        assertEquals(0, price.getUnsignedValue(), 0);
    }

    private Currency createCurrency() {
        return new Currency("Euro", "EUR", "€");
    }
}
