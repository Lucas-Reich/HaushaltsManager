package com.example.lucas.haushaltsmanager.OneTimeWorkRequest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import androidx.work.Configuration;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.testing.SynchronousExecutor;
import androidx.work.testing.TestDriver;
import androidx.work.testing.WorkManagerTestInitHelper;

import com.example.lucas.haushaltsmanager.Database.DatabaseTest;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.Booking;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Frequency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.Worker.PeriodicWorker.RecurringBookingWorker;
import com.example.lucas.haushaltsmanager.Worker.WorkRequestBuilder;
import com.google.common.util.concurrent.ListenableFuture;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RecurringBookingWorkerTest extends DatabaseTest {
    private ExpenseRepository mExpenseRepo;
    private RecurringBookingRepository mRecurringBookingRepo;

    @Override
    public Context getContext() {
        return InstrumentationRegistry.getContext();
    }

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getTargetContext();

        Configuration config = new Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG) // Set log level to Log.DEBUG to make it easier to see why tests failed
                .setExecutor(new SynchronousExecutor()) // Use a SynchronousExecutor to make it easier to write tests
                .build();

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(
                context,
                config
        );

        mExpenseRepo = new ExpenseRepository(context);
        mRecurringBookingRepo = new RecurringBookingRepository(context);
    }

    @Test
    public void testWorkerWithNoInputData() throws Exception {
        OneTimeWorkRequest.Builder request = new OneTimeWorkRequest.Builder(RecurringBookingWorker.class)
                .setInitialDelay(100, TimeUnit.SECONDS);

        WorkInfo.State workState = runRecurringBookingWorker(request.build());

        assertEquals(workState, WorkInfo.State.FAILED);
    }

    @Test
    public void testWorkerCreatesBookingAndUpdatesRecurringBooking() throws Exception {
        RecurringBooking recurringBooking = createRecurringBooking();
        WorkRequest recurringBookingWorkRequest = WorkRequestBuilder.from(recurringBooking);

        WorkInfo.State workState = runRecurringBookingWorker(recurringBookingWorkRequest);


        assertEquals(workState, WorkInfo.State.SUCCEEDED);

        assertTrue(mExpenseRepo.exists(recurringBooking.getBooking()));

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking);
        assertTrue(mRecurringBookingRepo.exists(nextRecurringBooking));
    }

    @Test
    public void testWorkerSchedulesNewWorkerAfterRun() throws Exception {
        RecurringBooking recurringBooking = createRecurringBooking();
        WorkRequest recurringBookingWorkRequest = WorkRequestBuilder.from(recurringBooking);

        WorkInfo.State workState = runRecurringBookingWorker(recurringBookingWorkRequest);

        assertEquals(workState, WorkInfo.State.SUCCEEDED);
        assertTrue(isWorkerWithTagScheduled(String.format("%s", recurringBooking.getIndex())));
    }

    @Test
    public void testWorkerCreatesLastBookingAndRemovesRecurringBooking() throws Exception {
        RecurringBooking oneTimeRecurringBooking = RecurringBooking.create(
                createDate(1, Calendar.JANUARY, 2019),
                createDate(2, Calendar.JANUARY, 2019),
                new Frequency(Calendar.DATE, 1),
                createBooking()
        );

        // TODO: Funktioniert noch nicht ganz -> irgendwie bekomme ich immer AccountNotFoundException weil das Konto nicht richtig erzeugt wurde

        WorkRequest recurringBookingWorkRequest = WorkRequestBuilder.from(oneTimeRecurringBooking);
        WorkInfo.State actualWorkState = runRecurringBookingWorker(recurringBookingWorkRequest);

        assertEquals(WorkInfo.State.SUCCEEDED, actualWorkState);
        assertTrue(mExpenseRepo.exists(oneTimeRecurringBooking.getBooking()));

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(oneTimeRecurringBooking);
        assertFalse(mRecurringBookingRepo.exists(nextRecurringBooking));
    }

    private WorkInfo.State runRecurringBookingWorker(WorkRequest request) throws Exception {
        WorkManager workManager = WorkManager.getInstance();
        workManager.enqueue(request).getResult().get();

        TestDriver testDriver = WorkManagerTestInitHelper.getTestDriver();
        testDriver.setInitialDelayMet(request.getId());

        return workManager
                .getWorkInfoById(request.getId())
                .get()
                .getState();
    }

    private RecurringBooking createRecurringBooking() {
        RecurringBooking recurringBooking = RecurringBooking.create(
                createDate(1, Calendar.JANUARY, 2019),
                createDate(1, Calendar.JANUARY, 2020),
                new Frequency(Calendar.MONTH, 1),
                createBooking()
        );

        return mRecurringBookingRepo.create(recurringBooking);
    }

    private Account createDefaultAccount(Context context) {
        return new AccountRepository(context).create(new Account(
                "Konto",
                1000,
                new Currency("Euro", "EUR", "€")
        ));
    }

    private Category createDefaultCategory(Context context) {
        Category category = new Category(
                "Kategorie",
                new Color(Color.BLACK),
                true,
                new ArrayList<Category>() {{
                    add(new Category(
                            "Kategorie",
                            new Color(Color.WHITE),
                            true,
                            new ArrayList<Category>()
                    ));
                }}
        );

        return new CategoryRepository(context).insert(category).getChildren().get(0);
    }

    private Booking createBooking() {
        Currency currency = new Currency("Euro", "EUR", "€");

        ExpenseObject booking = new ExpenseObject(
                "Ich bin eine Ausgabe",
                new Price(150, true, currency),
                createDefaultCategory(InstrumentationRegistry.getTargetContext()),
                createDefaultAccount(InstrumentationRegistry.getTargetContext()).getIndex(),
                currency
        );

        return mExpenseRepo.insert(booking);
    }

    private Calendar createDate(int day, int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);

        return date;
    }

    /**
     * Quelle: https://stackoverflow.com/a/51613101
     */
    private boolean isWorkerWithTagScheduled(String tag) {
        ListenableFuture<List<WorkInfo>> statuses = WorkManager
                .getInstance()
                .getWorkInfosByTag(tag);

        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }

            return running;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
