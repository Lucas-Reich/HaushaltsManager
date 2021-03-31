package com.example.lucas.haushaltsmanager.Worker;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkRequest;

import com.example.lucas.haushaltsmanager.Entities.Backup;
import com.example.lucas.haushaltsmanager.Entities.Delay;
import com.example.lucas.haushaltsmanager.Entities.NotificationVO;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.Worker.PeriodicWorker.BackupWorker;
import com.example.lucas.haushaltsmanager.Worker.PeriodicWorker.NotificationWorker;
import com.example.lucas.haushaltsmanager.Worker.PeriodicWorker.RecurringBookingWorker;

public class WorkRequestBuilder {
    public static WorkRequest from(Object object) {
        WorkRequestBuilder workRequestBuilder = new WorkRequestBuilder();

        if (object instanceof RecurringBooking) {
            return workRequestBuilder.fromRecurringBooking((RecurringBooking) object);
        }

        if (object instanceof NotificationVO) {
            return workRequestBuilder.fromNotification((NotificationVO) object);
        }

        if (object instanceof Backup) {
            return workRequestBuilder.fromBackup((Backup) object);
        }

        throw new IllegalArgumentException(String.format("No mapping defined for class '%s'", object.getClass()));
    }

    private WorkRequest fromBackup(Backup backup) {
        Delay delay = backup.getDelay();

        return new OneTimeWorkRequest.Builder(BackupWorker.class)
                .setInitialDelay(delay.getDuration(), delay.getTimeUnit())
                .setInputData(buildInputData(backup))
                .addTag(BackupWorker.WORKER_TAG)
                .build();
    }

    private Data buildInputData(Backup backup) {
        return new Data.Builder()
                .putString(BackupWorker.TITLE, backup.getTitle())
                .build();
    }

    private WorkRequest fromNotification(NotificationVO notification) {
        Delay delay = notification.getDelay();

        return new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delay.getDuration(), delay.getTimeUnit())
                .setInputData(buildInputData(notification))
                .addTag(NotificationWorker.WORKER_TAG)
                .build();
    }

    private Data buildInputData(NotificationVO notification) {
        return new Data.Builder()
                .putString(NotificationWorker.TITLE, notification.getTitle())
                .putString(NotificationWorker.CONTENT, notification.getContent())
                .putInt(NotificationWorker.ICON, notification.getIcon())
                .build();
    }

    private WorkRequest fromRecurringBooking(RecurringBooking recurringBooking) {
        Delay delay = recurringBooking.getDelayUntilNextExecution();

        return new OneTimeWorkRequest.Builder(RecurringBookingWorker.class)
                .setInitialDelay(delay.getDuration(), delay.getTimeUnit())
                .setInputData(buildInputData(recurringBooking))
                .addTag(recurringBooking.getId().toString())
                .build();
    }

    private Data buildInputData(RecurringBooking recurringBooking) {
        return new Data.Builder()
                .putString(RecurringBookingWorker.RECURRING_BOOKING, recurringBooking.getId().toString())
                .build();
    }
}
