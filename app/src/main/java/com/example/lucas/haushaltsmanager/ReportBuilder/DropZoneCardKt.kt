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
    private var dropZoneCount = 3
    private var maxDropZoneCount = 3
    private val widgetMap: HashMap<Int, Widget> = HashMap()
    private var configuration: ConfigurationObject
    private val bookingRepository: BookingDAO

    init {
        configuration = ConfigurationObject.createWithDefaults()
        bookingRepository = AppDatabase.getDatabase(context).bookingDAO()
    }

    // TODO: Overwrite OnDragListener
    // TODO: Dynamically reassign size of current widgets if a view is added or removed
    // TODO: Remove widget on long click

    fun addDroppedView(widget: Widget, point: Point) {
        val dropZoneId = translateCoordinatedToDropzone(point)

        removeExistingViewFromZone(dropZoneId)

        widget.updateView(getBookingsForConfiguration())
        val widgetView = widget.view

        configureChildView(widgetView, dropZoneId)

        addViewInternal(widgetView, dropZoneId)
        widgetMap.put(dropZoneId, widget)
    }

    fun setDropzoneCount(dropZoneCount: Int) {
        this.dropZoneCount = dropZoneCount

        removeAllViews()
        invalidate()
    }

    fun updateConfiguration(configuration: ConfigurationObject) {
        this.configuration = configuration

        val bookings = getBookingsForConfiguration()

        widgetMap.forEach { (dropZone, widget) -> widget.updateView(bookings) }
    }

    private fun addViewInternal(view: View, dropZoneId: Int) {
        view.id = dropZoneId
        addView(view)

        invalidate()
    }

    private fun configureChildView(view: View, dropZoneId: Int) {
        view.setOnTouchListener { v: View, event: MotionEvent? -> (v.parent as View).onTouchEvent(event) } // Propagates child click to parent

        addLayoutConstraintsToChild(view, dropZoneId)
    }

    private fun addLayoutConstraintsToChild(view: View, dropZoneId: Int) {
        val widgetWidth = width / dropZoneCount

        view.layoutParams = LinearLayout.LayoutParams(
            widgetWidth,
            height
        )

        view.x = dropZoneId.toFloat() * widgetWidth.toFloat()
    }

    private fun removeExistingViewFromZone(dropZoneId: Int) {
        val oldView = findViewById<View>(dropZoneId) ?: return

        removeView(oldView)
        widgetMap.remove(dropZoneId)
    }

    private fun translateCoordinatedToDropzone(point: Point): Int {
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