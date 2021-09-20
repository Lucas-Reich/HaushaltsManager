package com.example.lucas.haushaltsmanager.entities

import androidx.annotation.DrawableRes
import com.example.lucas.haushaltsmanager.App.app
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences
import java.util.concurrent.TimeUnit

data class Notification(
    val title: String,
    val content: String,
    @DrawableRes val icon: Int
) {
    fun getDelay(): Delay {
        return Delay(
            TimeUnit.MILLISECONDS,
            timeUntilExecution()
        )
    }

    private fun timeUntilExecution(): Long {
        val currentReminderTime = getReminderTime().toMillis()
        val now = System.currentTimeMillis()

        if (notificationOccursToday(currentReminderTime)) {
            return currentReminderTime - now
        }

        val nextReminderTime = getNextReminderTime(currentReminderTime)
        return nextReminderTime - now
    }

    private fun getNextReminderTime(reminderTime: Long): Long {
        return reminderTime + TimeUnit.MILLISECONDS.convert(24, TimeUnit.HOURS)
    }

    private fun notificationOccursToday(reminderTime: Long): Boolean {
        return System.currentTimeMillis() < reminderTime
    }

    private fun getReminderTime(): Time {
        return UserSettingsPreferences(app.getContext()).reminderTime
    }
}