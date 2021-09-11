package com.example.lucas.haushaltsmanager.Utils

import android.content.Context
import com.example.lucas.haushaltsmanager.R

class WeekdayUtils(var context: Context) {
    val weekdays: Array<String> = context.resources.getStringArray(R.array.weekdays)

    fun getWeekday(weekdayIndex: Int): String {
        return weekdays[weekdayIndex];
    }

    fun getWeekdayIndex(weekday: String): Int {
        return weekdays.indexOf(weekday);
    }
}