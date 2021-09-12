package com.example.lucas.haushaltsmanager.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Assert;
import org.junit.Test;

public class ColorTest {
    private static final String WHITE = "#ffffffff";
    private static final String BLACK = "#00000000";

    @Test
    public void colorCanBeCreatedFromValidStringInput() {
        Color color = new Color(WHITE);

        assertEquals(WHITE, color.getColor());
    }

    @Test
    public void colorCanBeCreatedFromValidIntInput() {
        new Color(Color.WHITE);

        // Cannot check if color int is the same as input since the method "getColorInt" uses Color.parseColor which cannot be mocked
    }

    @Test
    public void intColorCanBeTransformerToString() {
        Color color = new Color(Color.WHITE);

        assertEquals(WHITE, color.getColor());
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
