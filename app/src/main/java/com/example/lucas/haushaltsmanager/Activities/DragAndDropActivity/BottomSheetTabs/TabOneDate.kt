package com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.BottomSheetTabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andrewjapar.rangedatepicker.CalendarPicker
import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.BookingFilter
import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.BookingQueryBuilder
import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.ConfigurationObject
import com.example.lucas.haushaltsmanager.R
import java.util.*

class TabOneDate : Fragment() {
    private lateinit var configurationListener: OnConfigurationChangeListener
    private lateinit var filter: BookingFilter // TODO: I probably need to inject the filter into the class to be able to share it between the tabs

    companion object {
        @JvmStatic
        var title: Int = R.string.dnd_tab_one_date_title
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.dnd_tab_one_date, container, false)
        filter = BookingFilter()

        val datePicker = rootView.findViewById<CalendarPicker>(R.id.calendar_picker)
        datePicker.showDayOfWeekTitle(true)
        datePicker.setRangeDate(Date(946684800), Date())
        datePicker.setOnStartSelectedListener { startDate, _ ->
            filter.fromDate = startDate.time
            filter.toDate = null

            val configuration = ConfigurationObject()
            configuration.addQuery(BookingQueryBuilder(filter).build())

            configurationListener.onConfigurationChange(configuration)
        }

        datePicker.setOnRangeSelectedListener { startDate, endDate, _, _ ->
            filter.fromDate = startDate.time
            filter.toDate = endDate.time

            val configuration = ConfigurationObject()
            configuration.addQuery(BookingQueryBuilder(filter).build())

            configurationListener.onConfigurationChange(configuration)
        }

        return rootView
    }

    fun setOnConfigurationChangedListener(listener: OnConfigurationChangeListener) {
        this.configurationListener = listener
    }
}