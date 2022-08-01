package com.example.lucas.haushaltsmanager.worker.periodicWorker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.lucas.haushaltsmanager.Database.AppDatabase
import com.example.lucas.haushaltsmanager.entities.RecurringBooking
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import java.util.*

class RecurringBookingWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    companion object {
        private const val WORKER_TAG = "RecurringBookingWorker"

        private const val INPUT_DATA_RECURRING_BOOKING_ID = "recurringBookingId"

        @JvmStatic
        fun createWorkRequest(recurringBooking: RecurringBooking): PeriodicWorkRequest {
            val inputData = workDataOf(
                INPUT_DATA_RECURRING_BOOKING_ID to recurringBooking.id.toString()
            )

            return PeriodicWorkRequestBuilder<RecurringBookingWorker>(recurringBooking.getDelayUntilNextExecution().duration, recurringBooking.getDelayUntilNextExecution().timeUnit)
                .setInputData(inputData)
                .setInitialDelay(recurringBooking.getDelayUntilNextExecution().duration, recurringBooking.getDelayUntilNextExecution().timeUnit)
                .addTag(WORKER_TAG)
                .build()
        }

        fun stopWorker(context: Context) {
            Log.i(WORKER_TAG, "Stopping RecurringBookingWorker")

            WorkManager.getInstance(context).cancelAllWorkByTag(WORKER_TAG)
        }
    }

    override fun doWork(): Result {
        val recurringBookingRepository = AppDatabase.getDatabase(applicationContext).recurringBookingDAO()

        val recurringBookingId = UUID.fromString(inputData.getString(INPUT_DATA_RECURRING_BOOKING_ID))
        val recurringBooking = recurringBookingRepository.get(recurringBookingId)

        saveBooking(recurringBooking)

        val nextRecurringBooking = RecurringBooking.createNextRecurringBooking(recurringBooking)
        if (null == nextRecurringBooking) {
            recurringBookingRepository.delete(recurringBooking)
            stopWorker(applicationContext)
        }

        return Result.success()
    }

    private fun saveBooking(recurringBooking: RecurringBooking) {
        AppDatabase.getDatabase(applicationContext).bookingDAO().insert(
            Booking(
                UUID.randomUUID(),
                recurringBooking.title,
                recurringBooking.price,
                recurringBooking.date,
                recurringBooking.categoryId,
                recurringBooking.accountId,
                null
            )
        )
    }
}