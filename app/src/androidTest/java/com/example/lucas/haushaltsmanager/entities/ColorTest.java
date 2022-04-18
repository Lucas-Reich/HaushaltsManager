package com.example.lucas.haushaltsmanager.entities;

import android.os.Parcel;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ColorTest {
    private static final String WHITE = "#ffffffff";

    @Test
    public void colorCanBeCreatedFromParcel() {
        // Arrange
        Color color = new Color(WHITE);

        // Act
        Parcel parcel = writeToParcel(color);
        Object createdFromParcel = Color.CREATOR.createFromParcel(parcel);

        // Assert
        assertTrue(createdFromParcel instanceof Color);
        assertEquals(color, createdFromParcel);
    }

    private Parcel writeToParcel(Color color) {
        Parcel parcel = Parcel.obtain();
        color.writeToParcel(parcel, color.describeContents());
        parcel.setDataPosition(0);

        return parcel;
    }
}
