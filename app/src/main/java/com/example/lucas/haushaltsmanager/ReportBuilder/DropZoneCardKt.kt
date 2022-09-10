package com.example.lucas.haushaltsmanager.ReportBuilder

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.ConfigurationObject
import com.example.lucas.haushaltsmanager.Database.AppDatabase
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingDAO
import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.Widget
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import kotlin.math.floor

class DropZoneCardKt @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    private val maxDropZoneCount = 3
    private val widgetList: ArrayList<Widget> = ArrayList()
    private var configuration: ConfigurationObject
    private val bookingRepository: BookingDAO

    init {
        configuration = ConfigurationObject.createWithDefaults()
        bookingRepository = AppDatabase.getDatabase(context).bookingDAO()
    }

    // TODO: Overwrite OnDragListener
    // TODO: Remove widget on long click

    fun addWidgetAutoAssignDropzone(newWidget: Widget, point: Point) {
        newWidget.updateView(getBookingsForConfiguration())

        if (widgetList.size < maxDropZoneCount) {
            val dropZoneIdOfNewWidget = translateCoordinatesToDropzone(point, widgetList.size + 1)

            widgetList.add(dropZoneIdOfNewWidget, newWidget)
        } else {
            val dropZoneId = translateCoordinatesToDropzone(point, widgetList.size)

            widgetList[dropZoneId] = newWidget
        }

        removeAllViews()

        widgetList.forEachIndexed { dropZone, widget ->
            val widgetView = widget.view

            configureChildView(widgetView, dropZone)

            addView(widgetView)
        }
    }

    fun updateConfiguration(configuration: ConfigurationObject) {
        this.configuration = configuration

        val bookings = getBookingsForConfiguration()

        widgetList.forEach { widget -> widget.updateView(bookings) }
    }

    private fun configureChildView(view: View, dropZoneId: Int) {
        view.setOnTouchListener { v: View, event: MotionEvent? -> (v.parent as View).onTouchEvent(event) } // Propagates child click to parent

        addLayoutConstraintsToChild(view, dropZoneId)
    }

    private fun addLayoutConstraintsToChild(view: View, dropZoneId: Int) {
        val widgetWidth = width / widgetList.size

        view.layoutParams = LinearLayout.LayoutParams(
            widgetWidth,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        view.x = (dropZoneId * widgetWidth).toFloat()
    }

    private fun translateCoordinatesToDropzone(point: Point, dropZoneCount: Int): Int {
        val zoneWidth = width / dropZoneCount

        return floor(point.x / zoneWidth).toInt()
    }

    private fun getBookingsForConfiguration(): List<Booking> {
        if (null == this.configuration.getQuery()) {
            return ConfigurationObject.createWithDefaults().getBookings()
        }

        return bookingRepository.getFilteredList(this.configuration.getQuery()!!)
    }
}