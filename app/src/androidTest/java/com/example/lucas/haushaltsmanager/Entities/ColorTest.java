package com.example.lucas.haushaltsmanager.Entities;

import android.os.Parcel;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ColorTest {
    private static final String WHITE = "#ffffffff";

    @Test
    public void colorCanBeCreatedFromParcel() {
        Color color = new Color(WHITE);

        Parcel parcel = writeToParcel(color);
        Color createdFromParcel = Color.CREATOR.createFromParcel(parcel);

        assertEquals(color, createdFromParcel);
    }

    private Parcel writeToParcel(Color color) {
        Parcel parcel = Parcel.obtain();
        color.writeToParcel(parcel, color.describeContents());
        parcel.setDataPosition(0);

        return parcel;
    }
}
