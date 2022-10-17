package com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity

import android.os.Bundle
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
import com.example.lucas.haushaltsmanager.ReportBuilder.DropZoneCardKt
import com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems.LineChartCardViewItem
import com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems.PieChartCardViewItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.collections.ArrayList

class DragAndDropActivity : AbstractAppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var configurationTabView: TabLayout
    private lateinit var dropZoneCardKt: DropZoneCardKt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag_and_drop)

        recyclerView = findViewById(R.id.recycler_view)
        setUpWidgetListRecyclerView()

        configurationTabView = findViewById(R.id.tab_layout)
        setUpTabView()

        dropZoneCardKt = findViewById(R.id.drop_zone_card)
    }

    private fun setUpTabView() {
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                val tabOne = TabOneDate()
                tabOne.setOnConfigurationChangedListener(object : OnConfigurationChangeListener {
                    override fun onConfigurationChange(configurationObject: ConfigurationObject) {
                        dropZoneCardKt.updateConfiguration(configurationObject)
                    }
                })

                return when (position) {
                    1 -> TabTwoConfiguration()
                    else -> tabOne
                }
            }
        }

        TabLayoutMediator(configurationTabView, viewPager) { tab, position ->
            tab.text = when (position) {
                1 -> this.getString(TabTwoConfiguration.title)
                else -> this.getString(TabOneDate.title)
            }
        }.attach()
    }

    private fun setUpWidgetListRecyclerView() {
        recyclerView.layoutManager = LayoutManagerFactory.horizontal(this)

        val items: MutableList<IRecyclerItem> = ArrayList()
        items.add(PieChartCardViewItem(this))
        items.add(LineChartCardViewItem(this))

        recyclerView.adapter = CardViewRecyclerViewAdapter(items, AppendInsertStrategy())
    }
}

