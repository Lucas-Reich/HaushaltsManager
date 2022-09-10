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

class DropZoneCardKt @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    private val maxDropZoneCount = 3
    private var widgetMap: HashMap<Int, Widget> = HashMap()
    private var configuration: ConfigurationObject
    private val bookingRepository: BookingDAO

    init {
        configuration = ConfigurationObject.createWithDefaults()
        bookingRepository = AppDatabase.getDatabase(context).bookingDAO()
    }

    // TODO: Overwrite OnDragListener
    // TODO: Dynamically reassign size of current widgets if a view is added or removed
    // TODO: Remove widget on long click

    fun addWidgetAutoAssignDropzone(newWidget: Widget, point: Point) {
        newWidget.updateView(getBookingsForConfiguration())

        // get dropzone of new widget, dropzone is now current count + 1 if less than maxCount
        if (widgetMap.size < maxDropZoneCount) { // Max widget count is not yet reached, new widget can simply be added
            val tempWidgetMap = HashMap<Int, Widget>()

            // get dropZone for new widget
            val dropZoneIdOfNewWidget = translateCoordinatesToDropzone(point, widgetMap.size + 1)

            // add new widget to tempWidgetMap
            tempWidgetMap[dropZoneIdOfNewWidget] = newWidget

            // add currently added widgets to tempWidgetMap all widgets with dropZoneId >= newWidgetDropZone need to be increase by 1
            widgetMap.forEach { (dropZone, widget) ->
                if (dropZone >= dropZoneIdOfNewWidget) {
                    tempWidgetMap[dropZone + 1] = widget
                } else {
                    tempWidgetMap[dropZone] = widget
                }
            }

            // overwrite current widgetMap with updatedWidgetMap
            widgetMap = tempWidgetMap
        } else { // Max widget count is already reached => currently added widget at dropZone needs to be replaced with new widget
            // get dropZone for new widget
            val dropZoneId = translateCoordinatesToDropzone(point, widgetMap.size)

            // replace current widget at dropZone
            widgetMap[dropZoneId] = newWidget
        }

        // remove current child views
        removeAllViews()

        // redraw card
        widgetMap.forEach { (dropZone, widget) ->
            val widgetView = widget.view

            configureChildView(widgetView, dropZone)

            addView(widgetView)
        }
    }

    fun updateConfiguration(configuration: ConfigurationObject) {
        this.configuration = configuration

        val bookings = getBookingsForConfiguration()

        widgetMap.forEach { (dropZone, widget) -> widget.updateView(bookings) }
    }

    private fun configureChildView(view: View, dropZoneId: Int) {
        view.setOnTouchListener { v: View, event: MotionEvent? -> (v.parent as View).onTouchEvent(event) } // Propagates child click to parent

        addLayoutConstraintsToChild(view, dropZoneId)
    }

    private fun addLayoutConstraintsToChild(view: View, dropZoneId: Int) {
        val widgetWidth = width / widgetMap.size

        view.layoutParams = LinearLayout.LayoutParams(
            widgetWidth,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        view.x = dropZoneId.toFloat() * widgetWidth.toFloat()
    }

    private fun translateCoordinatesToDropzone(point: Point, dropZoneCount: Int): Int {
        val zoneWidth = width / dropZoneCount

        for (zone in 0 until dropZoneCount) {
            val zoneStart = zone * zoneWidth
            val zoneEnd = zoneStart + zoneWidth

            if (isPointInRange(point, zoneStart, zoneEnd)) {
                return zone
            }
        }

        throw RuntimeException("Registered Drop outside of DropZone")
    }

    private fun isPointInRange(point: Point, startX: Int, endX: Int): Boolean {
        return point.x >= startX && point.x < endX
    }

    private fun getBookingsForConfiguration(): List<Booking> {
        if (null == this.configuration.getQuery()) {
            return ConfigurationObject.createWithDefaults().getBookings()
        }

        return bookingRepository.getFilteredList(this.configuration.getQuery()!!)
    }
}