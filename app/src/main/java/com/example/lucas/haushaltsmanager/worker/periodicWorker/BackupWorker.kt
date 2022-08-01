package com.example.lucas.haushaltsmanager.worker.periodicWorker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.lucas.haushaltsmanager.Backup.BackupUtils
import com.example.lucas.haushaltsmanager.Backup.Handler.Decorator.DatabaseBackupHandler
import com.example.lucas.haushaltsmanager.Backup.Handler.FileBackupHandler
import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences
import com.example.lucas.haushaltsmanager.entities.Backup
import com.example.lucas.haushaltsmanager.entities.Directory

class BackupWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    companion object {
        const val WORKER_TAG = "BackupWorker"

        private const val INPUT_DATA_BACKUP_TITLE = "title"

        @JvmStatic
        fun createWorkRequest(backup: Backup): PeriodicWorkRequest {
            val inputData = workDataOf(
                INPUT_DATA_BACKUP_TITLE to backup.title
            )

            return PeriodicWorkRequestBuilder<BackupWorker>(backup.delay.duration, backup.delay.timeUnit)
                .setInitialDelay(backup.delay.duration, backup.delay.timeUnit)
                .setInputData(inputData)
                .addTag(WORKER_TAG)
                .build()
        }

        @JvmStatic
        fun stopWorker(context: Context) {
            Log.i(WORKER_TAG, "Stopping BackupWorker")

            WorkManager.getInstance(context).cancelAllWorkByTag(WORKER_TAG)
        }
    }

    override fun doWork(): Result {
        val successful = createNewBackup()

        BackupUtils.deleteBackupsAboveThreshold(getBackupDir(), getBackupThreshold())

        return if (successful) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun createNewBackup(): Boolean {
        val backupHandler = DatabaseBackupHandler(applicationContext, FileBackupHandler())

        return backupHandler.backup(
            null,
            inputData.getString(INPUT_DATA_BACKUP_TITLE)
        )
    }

    private fun getBackupDir(): Directory {
        return AppInternalPreferences(applicationContext).backupDirectory
    }

    private fun getBackupThreshold(): Int {
        return UserSettingsPreferences(applicationContext).maxBackupCount
    }
}