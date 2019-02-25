package com.example.lucas.haushaltsmanager.Worker;

import com.example.lucas.haushaltsmanager.Entities.Delay;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.Worker.PeriodicWorker.RecurringBookingWorker;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkRequest;

public class WorkRequestBuilder {
    public static WorkRequest from(Object object) {
        if (object instanceof RecurringBooking) {
            return fromRecurringBooking((RecurringBooking) object);
        }

        throw new IllegalArgumentException(String.format("No mapping defined for class '%s'", object.getClass()));
    }

    private static WorkRequest fromRecurringBooking(RecurringBooking recurringBooking) {
        OneTimeWorkRequest.Builder request = new OneTimeWorkRequest.Builder(RecurringBookingWorker.class);

        request.setInputData(buildInputDataForRecurringRequest(recurringBooking.getIndex()));

        Delay initDelay = recurringBooking.getDelayUntilNextExecution();
        request.setInitialDelay(initDelay.getDuration(), initDelay.getTimeUnit());

        request.addTag(createTag(recurringBooking.getIndex()));

        return request.build();
    }

    private static Data buildInputDataForRecurringRequest(long recurringBookingId) {
        Data.Builder dataBuilder = new Data.Builder();
        dataBuilder.putLong(RecurringBookingWorker.RECURRING_BOOKING, recurringBookingId);

        return dataBuilder.build();
    }

    private static String createTag(long value) {
        return String.format("%s", value);
    }
}
