package com.example.lucas.haushaltsmanager.Worker;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkRequest;

import com.example.lucas.haushaltsmanager.entities.Backup;
import com.example.lucas.haushaltsmanager.entities.Delay;
import com.example.lucas.haushaltsmanager.entities.Notification;
import com.example.lucas.haushaltsmanager.entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.Worker.PeriodicWorker.BackupWorker;
import com.example.lucas.haushaltsmanager.Worker.PeriodicWorker.NotificationWorker;
import com.example.lucas.haushaltsmanager.Worker.PeriodicWorker.RecurringBookingWorker;

public class WorkRequestBuilder {
    public static WorkRequest from(Object object) {
        WorkRequestBuilder workRequestBuilder = new WorkRequestBuilder();

        if (object instanceof RecurringBooking) {
            return workRequestBuilder.fromRecurringBooking((RecurringBooking) object);
        }

        if (object instanceof Notification) {
            return workRequestBuilder.fromNotification((Notification) object);
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

    private WorkRequest fromNotification(Notification notification) {
        Delay delay = notification.getDelay();

        return new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delay.getDuration(), delay.getTimeUnit())
                .setInputData(buildInputData(notification))
                .addTag(NotificationWorker.WORKER_TAG)
                .build();
    }

    private Data buildInputData(Notification notification) {
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
