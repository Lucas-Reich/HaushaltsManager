package com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class BookingTagsRepositoryTest {

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.application;
        ExpensesDbHelper dbHelper = new ExpensesDbHelper(context);
        DatabaseManager.initializeInstance(dbHelper);
    }

    public void testGetWithExistingBookingTagsShouldSucceed() {

        //todo
    }

    public void testGetWithExistingBookingIdButNotExistingExpenseTypeShouldSucceed() {

        //todo
    }

    public void testGetWithNotExistingBookingIdAndExistingExpenseTypeShouldSucceed() {

        //todo
    }
}
