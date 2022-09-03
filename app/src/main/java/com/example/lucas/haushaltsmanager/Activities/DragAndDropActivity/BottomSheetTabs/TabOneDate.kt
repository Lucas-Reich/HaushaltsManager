package com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.BottomSheetTabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.BookingFilter
import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.BookingQueryBuilder
import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.ConfigurationObject
import com.example.lucas.haushaltsmanager.R
import com.example.lucas.haushaltsmanager.entities.Price
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import java.util.*

class TabOneDate : Fragment() {
    private lateinit var configurationListener: OnConfigurationChangeListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.dnd_tab_one_date, container, false)

        val button = rootView.findViewById<Button>(R.id.dnd_tab_one_button)
        button.setOnClickListener { invokeListener() }

        return rootView
    }

    fun setOnConfigurationChangedListener(listener: OnConfigurationChangeListener) {
        this.configurationListener = listener
    }

    private fun invokeListener() {
        val bookings = ArrayList<Booking>()
        bookings.add(Booking("Booking 1", Price(100.00), UUID.randomUUID(), UUID.randomUUID()))
        bookings.add(Booking("Booking 2", Price(500.00), UUID.randomUUID(), UUID.randomUUID()))
        bookings.add(Booking("Booking 3", Price(-200.00), UUID.randomUUID(), UUID.randomUUID()))
        bookings.add(Booking("Booking 4", Price(-700.00), UUID.randomUUID(), UUID.randomUUID()))
        bookings.add(Booking("Booking 5", Price(550.00), UUID.randomUUID(), UUID.randomUUID()))

        val configuration = ConfigurationObject()
        configuration.addBookings(bookings)

        val filter = BookingFilter()
        filter.accountId = "7C4F706EB81641A0B5B036708D0D4489"
        configuration.addQuery(BookingQueryBuilder(filter).build())

//        val query = SimpleSQLiteQuery("Select * from bookings where account_id = '6E68C2AD5C8E422BB3E986DD13BB5C67';")
//        configuration.addQuery(query)

        configurationListener.onConfigurationChange(configuration)
    }
}