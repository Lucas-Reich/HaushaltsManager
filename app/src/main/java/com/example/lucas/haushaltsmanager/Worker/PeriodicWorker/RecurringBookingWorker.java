package com.example.lucas.haushaltsmanager.Worker.PeriodicWorker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions.RecurringBookingNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import java.util.UUID;

public class RecurringBookingWorker extends AbstractRecurringWorker {
    public static final String RECURRING_BOOKING = "recurringBookingId";
    private static final String TAG = RecurringBookingWorker.class.getSimpleName();

    private final RecurringBooking mRecurringBooking;

    public RecurringBookingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        UUID recurringBookingId = extractIdFromParameters(workerParams);
        mRecurringBooking = getRecurringBookingById(recurringBookingId);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (null == mRecurringBooking) {
            Log.e(TAG, "Terminating RecurringBookingWorker because of invalid RecurringBooking.");
            return Result.failure();
        }

        saveBooking(mRecurringBooking);

        RecurringBooking nextRecurringBooking = RecurringBooking.createNextRecurringBooking(mRecurringBooking);

        if (hasNextRecurringBooking(mRecurringBooking)) {
            updateExecutionDateOfRecurringBooking(nextRecurringBooking);

            scheduleNextWorker(nextRecurringBooking);
        } else {
            deleteFinishedRecurringBooking();
        }

        return Result.success();
    }

    @Override
    String getTag() {
        return "RecurringBookingWorker";
    }

    private boolean hasNextRecurringBooking(RecurringBooking recurringBooking) {
        return null != RecurringBooking.createNextRecurringBooking(recurringBooking);
    }

    private void deleteFinishedRecurringBooking() {
        new RecurringBookingRepository(getApplicationContext()).delete(mRecurringBooking);
    }

    private void updateExecutionDateOfRecurringBooking(RecurringBooking updatedRecurringBooking) {
        new RecurringBookingRepository(getApplicationContext()).update(updatedRecurringBooking);
    }

    private void saveBooking(RecurringBooking recurringBooking) {
        new ExpenseRepository(getApplicationContext()).insert(recurringBooking.getBooking());
    }

    private UUID extractIdFromParameters(WorkerParameters workerParams) {
        String rawId = workerParams.getInputData().getString(RECURRING_BOOKING);

        return UUID.fromString(rawId);
    }

    private RecurringBooking getRecurringBookingById(UUID recurringBookingId) {
        try {
            return new RecurringBookingRepository(getApplicationContext()).get(recurringBookingId);
        } catch (RecurringBookingNotFoundException e) {

            Log.e(TAG, String.format("Could not find referenced RecurringBooking '%s'.", recurringBookingId));
            return null;
        }
    }
}
