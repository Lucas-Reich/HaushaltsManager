package com.example.lucas.haushaltsmanager.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Locale;

public class PriceTest {

    @Test
    public void testCreatePrice() {
        // Arrange
        double expectedValue = 100;

        // Act
        Price price = new Price(expectedValue);

        // Assert
        assertEquals(expectedValue, price.getPrice(), 0);
    }

    @Test
    public void testInferredPositiveValue() {
        // Arrange
        double expectedValue = 100;

        // Act
        Price price = new Price(expectedValue);

        // Assert
        assertEquals(expectedValue, price.getPrice(), 0);
        assertFalse(price.isNegative());
    }

    @Test
    public void testInferredNegativeValue() {
        // Arrange
        double expectedValue = -100;

        // Act
        Price price = new Price(expectedValue);

        // Assert
        assertEquals(expectedValue, price.getPrice(), 0);
        assertTrue(price.isNegative());
    }

    @Test
    public void testCreatePriceFromEmptyString() {
        // Act
        Price price = new Price("", Locale.GERMANY);

        // Assert
        assertEquals(0, price.getAbsoluteValue(), 0);
    }

    @Test
    public void testInferredPositiveValueFromString() {
        // Act
        Price price = new Price("10.000", Locale.GERMANY);

        // Assert
        assertEquals(10000, price.getAbsoluteValue(), 0);
        assertFalse(price.isNegative());
    }

    @Test
    public void testInferredNegativeValueFromString() {
        // Act
        Price price = new Price("-10.000", Locale.GERMANY);

        // Assert
        assertEquals(10000, price.getAbsoluteValue(), 0);
        assertTrue(price.isNegative());
    }

    @Test
    public void testInvalidValueStringDefaultsToZero() {
        // Act
        Price price = new Price("I am a not valid Price", Locale.GERMANY);

        // Assert
        assertEquals(0, price.getAbsoluteValue(), 0);
    }
}
