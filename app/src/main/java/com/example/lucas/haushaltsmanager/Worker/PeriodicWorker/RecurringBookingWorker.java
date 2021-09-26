package com.example.lucas.haushaltsmanager.Worker.PeriodicWorker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookingDAO;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.RecurringBooking;

import java.util.UUID;

public class RecurringBookingWorker extends AbstractRecurringWorker {
    public static final String RECURRING_BOOKING = "recurringBookingId";
    private static final String TAG = RecurringBookingWorker.class.getSimpleName();

    private final RecurringBookingDAO recurringBookingRepository;

    private final RecurringBooking mRecurringBooking;

    public RecurringBookingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        recurringBookingRepository = AppDatabase.getDatabase(context).recurringBookingDAO();

        UUID recurringBookingId = extractIdFromParameters(workerParams);
        mRecurringBooking = recurringBookingRepository.get(recurringBookingId);
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
        if (null != nextRecurringBooking) {
            recurringBookingRepository.update(nextRecurringBooking);

            scheduleNextWorker(nextRecurringBooking);
        } else {
            recurringBookingRepository.delete(mRecurringBooking);
        }

        return Result.success();
    }

    @Override
    String getTag() {
        return "RecurringBookingWorker";
    }

    private void saveBooking(RecurringBooking recurringBooking) {
        new ExpenseRepository(getApplicationContext()).insert(new Booking(
                UUID.randomUUID(),
                recurringBooking.getTitle(),
                recurringBooking.getPrice(),
                recurringBooking.getDate(),
                getCategory(recurringBooking.getCategoryId()),
                recurringBooking.getNotice(),
                recurringBooking.getAccountId(),
                recurringBooking.getExpenseType()
        ));
    }

    private Category getCategory(UUID categoryId) {
        return AppDatabase.getDatabase(app.getContext()).categoryDAO().get(categoryId);
    }

    private UUID extractIdFromParameters(WorkerParameters workerParams) {
        String rawId = workerParams.getInputData().getString(RECURRING_BOOKING);

        return UUID.fromString(rawId);
    }
}
