package com.example.lucas.haushaltsmanager.ReportBuilder

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
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
    private var overriddenWidget: Widget? = null

    init {
        configuration = ConfigurationObject.createWithDefaults()
        bookingRepository = AppDatabase.getDatabase(context).bookingDAO()
    }

    // TODO: Remove widget on long click or long click and remove view by dragging

    override fun onDragEvent(event: DragEvent?): Boolean {
        when (event?.action) {
            DragEvent.ACTION_DRAG_STARTED -> return true
            DragEvent.ACTION_DRAG_ENTERED -> return true
            DragEvent.ACTION_DRAG_LOCATION -> {
                addWidgetWithPreview(event.localState as Widget, Point.fromDragEvent(event))
                return true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                removeWidget(event.localState as Widget)
                return true
            }
            DragEvent.ACTION_DRAG_ENDED -> return true
            DragEvent.ACTION_DROP -> {
                overriddenWidget = null
                return true
            }
            else -> Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
        }

        return false
    }

    fun addWidgetAutoAssignDropzone(newWidget: Widget, point: Point) {
        newWidget.updateView(getBookingsForConfiguration())

        if (widgetList.size < maxDropZoneCount) {
            val dropZoneIdOfNewWidget = translateCoordinatesToDropzone(point, widgetList.size + 1)

            widgetList.add(dropZoneIdOfNewWidget, newWidget)
        } else {
            val dropZoneId = translateCoordinatesToDropzone(point, widgetList.size)

            widgetList[dropZoneId] = newWidget
        }

        drawWidgets()
    }

    fun addWidgetWithPreview(newWidget: Widget, point: Point) {
        val dropZoneId = translateCoordinatesToDropzone(point, widgetList.size)

        if (widgetList.indexOf(newWidget) == dropZoneId) {
            return // Widget is already added to dropZone, no need to add it again
        }

        if (widgetList.contains(newWidget)) {
            removeWidget(newWidget) // Widget was already added but to another dropZone, therefore we need to remove it from the other zone and add it to the new one
        }

        if (widgetList.size == maxDropZoneCount) {
            overriddenWidget = widgetList.get(dropZoneId)
        }

        addWidgetAutoAssignDropzone(newWidget, point)
    }

    fun removeWidget(widget: Widget) {
        if (null != overriddenWidget) {
            val indexOfPreviewWidget = widgetList.indexOf(widget)

            widgetList.removeAt(indexOfPreviewWidget)
            widgetList.add(indexOfPreviewWidget, overriddenWidget!!)

            overriddenWidget = null
        } else {
            widgetList.remove(widget)
        }

        removeView(widget.view)
        drawWidgets()
    }

    fun updateConfiguration(configuration: ConfigurationObject) {
        this.configuration = configuration

        val bookings = getBookingsForConfiguration()

        widgetList.forEach { widget -> widget.updateView(bookings) }
    }

    private fun configureChildView(view: View, dropZoneId: Int) {
        view.setOnTouchListener { v: View, event: MotionEvent? -> (v.parent as View).onTouchEvent(event); performClick() } // Propagates child click to parent

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

    private fun drawWidgets() {
        removeAllViews()

        widgetList.forEachIndexed { dropZone, widget ->
            val widgetView = widget.view

            configureChildView(widgetView, dropZone)

            addView(widgetView)
        }
    }

    private fun translateCoordinatesToDropzone(point: Point, dropZoneCount: Int): Int {
        if (widgetList.size == 0) {
            return 0
        }

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