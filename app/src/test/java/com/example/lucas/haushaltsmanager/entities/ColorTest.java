package com.example.lucas.haushaltsmanager.entities;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ColorTest {
    private static final String WHITE = "#ffffffff";
    private static final String BLACK = "#00000000";

    @Test
    public void colorCanBeCreatedFromValidStringInput() {
        Color color = new Color(WHITE);

        assertEquals(WHITE, color.getColorString());
    }

    @Test
    public void colorCanBeCreatedFromValidIntInput() {
        new Color(Color.WHITE);

        // Cannot check if color int is the same as input since the method "getColorInt" uses Color.parseColor which cannot be mocked
    }

    // Cannot menuItemHasCorrectActionKey Int to String conversion, since the Color.parseColor() method cannot be mocked

    @Test
    public void colorCanBeRandomlyCreated() {
        try {
            Color randomColor = Color.random();

            assertTrue(randomColor.getColorString().matches(Color.VALID_COLOR_PATTERN));
        } catch (IllegalArgumentException e) {

            Assert.fail("Could not create random color.");
        }
    }

    @Test
    public void intColorCanBeTransformerToString() {
        Color color = new Color(Color.WHITE);

        assertEquals(WHITE, color.getColorString());
    }

    @Test
    public void colorsWithSameColorStringShouldBeEqual() {
        Color color1 = new Color(WHITE);
        Color color2 = new Color(WHITE);

        assertEquals(color1, color2);
    }

    @Test
    public void colorsWithDifferentColorStringsShouldNotBeEqual() {
        Color color1 = new Color(WHITE);
        Color color2 = new Color(BLACK);

        assertNotEquals(color1, color2);
    }

    @Test
    public void colorCannotBeCreatedFromInvalidColorString() {
        String invalidColorString = "IAmInvalid";

        try {
            new Color(invalidColorString);

            Assert.fail(String.format("Could created Color from invalid String %s", invalidColorString));
        } catch (IllegalArgumentException e) {
            assertEquals("Could not create Color from: 'IAmInvalid'", e.getMessage());
        }
    }
}
