package com.example.lucas.haushaltsmanager.entities

import java.util.*

class Time(val hour: Int, val minute: Int) {
    companion object {
        @JvmStatic
        fun fromString(stringTime: String): Time {
            val hour = stringTime.substring(0, 2).toInt()
            val minute = stringTime.substring(3, 5).toInt()

            return Time(hour, minute)
        }
    }

    override fun toString(): String {
        return String.format(Locale.US, "%02d:%02d", hour, minute)
    }

    fun toMillis(): Long {
        val time = Calendar.getInstance()
        time[Calendar.HOUR_OF_DAY] = hour
        time[Calendar.MINUTE] = minute
        time[Calendar.SECOND] = 0

        return time.timeInMillis
    }
}