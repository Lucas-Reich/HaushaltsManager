package com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity

import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.lucas.haushaltsmanager.Activities.AbstractAppCompatActivity
import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.BottomSheetTabs.OnConfigurationChanged
import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.BottomSheetTabs.TabOneDate
import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.BottomSheetTabs.TabTwoConfiguration
import com.example.lucas.haushaltsmanager.Activities.LayoutManagerFactory
import com.example.lucas.haushaltsmanager.R
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy.AppendInsertStrategy
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.CardViewRecyclerViewAdapter
import com.example.lucas.haushaltsmanager.ReportBuilder.DropZoneCard
import com.example.lucas.haushaltsmanager.ReportBuilder.Point
import com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems.LineChartCardViewItem
import com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems.PieChartCardViewItem
import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.Widget
import com.example.lucas.haushaltsmanager.entities.booking.Booking
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DragAndDropActivity : AbstractAppCompatActivity(), View.OnDragListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dropZoneCard: DropZoneCard
    private lateinit var configurationTabView: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag_and_drop)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setOnDragListener(this)
        setUpRecyclerView()

        configurationTabView = findViewById(R.id.tab_layout)
        setUpTabView()

        findViewById<ConstraintLayout>(R.id.drop_zone_root).setOnDragListener(this)

        dropZoneCard = DropZoneCard(findViewById(R.id.drop_zone))

        findViewById<Button>(R.id.drop_zone_1).setOnClickListener { dropZoneCard.setDropZoneCount(1) }
        findViewById<Button>(R.id.drop_zone_2).setOnClickListener { dropZoneCard.setDropZoneCount(2) }
        findViewById<Button>(R.id.drop_zone_3).setOnClickListener { dropZoneCard.setDropZoneCount(3) }
    }

    private fun setUpTabView() {
        // TODO: Can I save the configuration so that I can add the configuration to whatever new widget is added to the dropzone?
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                val tabOne = TabOneDate()
                tabOne.setOnConfigurationChangedListener(object : OnConfigurationChanged {
                    override fun configurationChanged(configurationObject: ConfigurationObject) {
                        dropZoneCard.updateConfiguration(configurationObject)
                    }
                })

                return when (position) {
                    1 -> TabTwoConfiguration()
                    else -> tabOne
                }
            }
        }

        // TODO: How to properly set the title of the page
        TabLayoutMediator(configurationTabView, viewPager) { tab, position -> tab.text = "OBJECT ${{ position + 1 }}" }.attach()
    }

    override fun onDrag(targetView: View?, event: DragEvent?): Boolean {
        when (event?.action) {
            DragEvent.ACTION_DRAG_STARTED -> return true
            DragEvent.ACTION_DRAG_ENTERED -> return true
            DragEvent.ACTION_DRAG_LOCATION -> return true
            DragEvent.ACTION_DRAG_EXITED -> return true
            DragEvent.ACTION_DRAG_ENDED -> return true
            DragEvent.ACTION_DROP -> {
                if (!isAtDropZone(targetView)) {
                    return true
                }

                val widget: Widget = event.localState as Widget

                dropZoneCard.addDroppedView(widget, Point.fromDragEvent(event))
                return true
            }
            else -> Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
        }

        return false
    }

    private fun isAtDropZone(targetView: View?): Boolean {
        if (null == targetView) {
            return false
        }

        return R.id.drop_zone_root == targetView.id
    }

    private fun setUpRecyclerView() {
        recyclerView.layoutManager = LayoutManagerFactory.horizontal(this)

        val items: MutableList<IRecyclerItem> = ArrayList()
        items.add(PieChartCardViewItem(this))
        items.add(LineChartCardViewItem(this))

        recyclerView.adapter = CardViewRecyclerViewAdapter(items, AppendInsertStrategy())
    }
}

class ConfigurationObject {
    private var bookings: ArrayList<Booking> = ArrayList()

    fun addBookings(bookings: List<Booking>) {
        this.bookings.addAll(bookings)
    }

    fun getBookings(): List<Booking> {
        return this.bookings
    }
}