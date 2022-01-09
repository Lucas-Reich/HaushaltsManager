package com.example.lucas.haushaltsmanager.Worker

import android.content.Context
import androidx.work.*
import com.example.lucas.haushaltsmanager.CsvBookingExporter
import com.example.lucas.haushaltsmanager.Database.AppDatabase
import com.example.lucas.haushaltsmanager.entities.Directory
import com.example.lucas.haushaltsmanager.entities.booking.Booking

class BookingExportWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    companion object {
        const val TAG = "booking_exporter"

        const val INPUT_DATA_TARGET_DIRECTORY = "target_directory"
        const val OUTPUT_DATA_CREATED_FILE = "created_file"

        @JvmStatic
        fun createWorkRequest(targetDirectory: String): WorkRequest {
            val constraints = Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .build()

            val inputData = workDataOf(
                INPUT_DATA_TARGET_DIRECTORY to targetDirectory
            )

            return OneTimeWorkRequestBuilder<BookingExportWorker>()
                .setConstraints(constraints)
                .setInputData(inputData)
                .addTag(TAG)
                .build()
        }
    }

    override fun doWork(): Result {
        val targetDirectory = inputData.getString(INPUT_DATA_TARGET_DIRECTORY) ?: return Result.failure()

        val exporter = buildExporter(Directory(targetDirectory))

        val bookings = getExpenses()
        val outputFile = exporter.writeToFile(bookings)

        if (null != outputFile) {
            val outputData = workDataOf(
                OUTPUT_DATA_CREATED_FILE to outputFile.absolutePath
            )

            return Result.success(outputData)
        }

        return Result.failure()
    }

    private fun buildExporter(targetDirectory: Directory): CsvBookingExporter {
        return CsvBookingExporter(
            targetDirectory,
            AppDatabase.getDatabase(applicationContext).accountDAO(),
            AppDatabase.getDatabase(applicationContext).categoryDAO()
        )
    }

    private fun getExpenses(): List<Booking> {
        val db = AppDatabase.getDatabase(applicationContext).bookingDAO()

        return db.getAll()
    }
}