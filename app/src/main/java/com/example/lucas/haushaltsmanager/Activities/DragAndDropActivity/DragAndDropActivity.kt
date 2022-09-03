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
import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.BottomSheetTabs.OnConfigurationChangeListener
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
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                val tabOne = TabOneDate()
                tabOne.setOnConfigurationChangedListener(object : OnConfigurationChangeListener {
                    override fun onConfigurationChange(configurationObject: ConfigurationObject) {
                        dropZoneCard.updateConfiguration(configurationObject)
                    }
                })

                return when (position) {
                    1 -> TabTwoConfiguration()
                    else -> tabOne
                }
            }
        }

        TabLayoutMediator(configurationTabView, viewPager) { tab, position ->
            tab.text = "Tab ${position + 1}"
        }.attach()
    }

    override fun onDrag(targetView: View?, event: DragEvent?): Boolean {
        // TODO: Can I show a preview when the user hovers the widget over an area but hasn't dropped it yet?
        //  This can also be used instead of the buttons selecting the amount of widget within the card.
        //  If the User hovers a new widget over the card and and there is still a zone free the widget currently in the dropzone becomes smaller.
        //  If there is no zone left the dropped view would replace the one currently below it.
        //  .
        //  The only question would be how to remove widgets from the card without directly setting a new one.
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

