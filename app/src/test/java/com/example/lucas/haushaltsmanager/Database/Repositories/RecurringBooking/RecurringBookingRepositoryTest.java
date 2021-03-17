package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBooking;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions.RecurringBookingNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.Booking;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.Entities.Frequency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class RecurringBookingRepositoryTest {
    private RecurringBookingRepository mRepo;

    @Before
    public void setup() {
        mRepo = new RecurringBookingRepository(RuntimeEnvironment.application);
    }

    @Test
    public void testCreateWithValidRecurringBookingShouldSucceed() {
        RecurringBooking recurringBooking = mRepo.create(createSimpleRecurringBooking());

        try {

            RecurringBooking actualRecurringBooking = mRepo.get(recurringBooking.getIndex());
            assertEquals(recurringBooking, actualRecurringBooking);
        } catch (RecurringBookingNotFoundException e) {

            Assert.fail("Could not find RecurringBooking");
        }
    }

    private RecurringBooking createSimpleRecurringBooking() {
        return RecurringBooking.create(
                createDate(1, Calendar.JANUARY, 2019),
                createDate(1, Calendar.JANUARY, 2020),
                new Frequency(Calendar.MONTH, 1),
                createBooking()
        );
    }

    private Booking createBooking() {
        Currency currency = new Currency("Euro", "EUR", "€");

        return new ExpenseObject(
                "Ausgabe",
                new Price(150, true, currency),
                new Category("Kategorie", Color.black(), ExpenseType.expense(), new ArrayList<Category>()),
                ExpensesDbHelper.INVALID_INDEX,
                currency
        );
    }

    private Calendar createDate(int day, int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);

        return date;
    }
}

