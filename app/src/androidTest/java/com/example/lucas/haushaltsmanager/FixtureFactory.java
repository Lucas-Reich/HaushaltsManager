package com.example.lucas.haushaltsmanager;

import android.support.test.InstrumentationRegistry;

import com.example.lucas.haushaltsmanager.Fixtures.IFixtures;
import com.example.lucas.haushaltsmanager.Fixtures.RecurringBookingsFixtures;

import java.io.IOException;
import java.io.InputStream;

public class FixtureFactory {
    public static IFixtures create(String fixtures) {
        switch (fixtures) {
            case "recurring_bookings.json":

                return RecurringBookingsFixtures.create(getFromAssets(fixtures));
            default:
                throw new IllegalArgumentException(String.format("Could not find file %s", fixtures));
        }
    }

    private static InputStream getFromAssets(String fileName) {
        try {
            return InstrumentationRegistry
                    .getContext()
                    .getResources()
                    .getAssets()
                    .open("fixture.json");
        } catch (IOException e) {
            // do nothing
            return null;
        }
    }
}
