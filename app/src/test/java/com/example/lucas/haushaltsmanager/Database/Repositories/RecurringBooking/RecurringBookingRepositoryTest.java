package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBooking;

import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions.RecurringBookingCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions.RecurringBookingNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.IBooking;
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

import java.util.Calendar;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class RecurringBookingRepositoryTest {
    private RecurringBookingRepository mRepo;

    @Before
    public void setup() {
        mRepo = new RecurringBookingRepository(RuntimeEnvironment.application);
    }

    @Test
    public void testCreateWithValidRecurringBookingShouldSucceed() throws RecurringBookingCouldNotBeCreatedException {
        RecurringBooking recurringBooking = createSimpleRecurringBooking();
        mRepo.insert(recurringBooking);

        try {

            RecurringBooking actualRecurringBooking = mRepo.get(recurringBooking.getId());
            assertEquals(recurringBooking, actualRecurringBooking);
        } catch (RecurringBookingNotFoundException e) {

            Assert.fail("Could not find RecurringBooking");
        }
    }

    private RecurringBooking createSimpleRecurringBooking() {
        return new RecurringBooking(
                createDate(1, Calendar.JANUARY, 2019),
                createDate(1, Calendar.JANUARY, 2020),
                new Frequency(Calendar.MONTH, 1),
                createBooking()
        );
    }

    private IBooking createBooking() {
        return new ExpenseObject(
                "Ausgabe",
                new Price(150, true),
                new Category("Kategorie", Color.black(), ExpenseType.expense()),
                UUID.randomUUID()
        );
    }

    private Calendar createDate(int day, int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);

        return date;
    }
}

