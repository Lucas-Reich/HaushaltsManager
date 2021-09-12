package com.example.lucas.haushaltsmanager.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository
import com.example.lucas.haushaltsmanager.R
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerItemClickListener
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.ItemCreator
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem
import com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem.TemplateItem
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.TemplateListRecyclerViewAdapter

class TemplatesActivityKt : AbstractAppCompatActivity(),
    RecyclerItemClickListener.OnRecyclerItemClickListener {
    private lateinit var templateRepo: TemplateRepository
    private lateinit var recyclerView: RecyclerView

    override fun onStart() {
        super.onStart()

        templateRepo = TemplateRepository(this)

        updateListView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_list)

        recyclerView = findViewById(R.id.template_list_recycler_view)
        recyclerView.layoutManager = LayoutManagerFactory.vertical(this)
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(this, recyclerView, this))

        initializeToolbar()
    }

    private fun updateListView() {
        recyclerView.adapter = TemplateListRecyclerViewAdapter(loadData())
    }

    private fun loadData(): MutableList<IRecyclerItem>? {
        val templateBookings = templateRepo.all

        return ItemCreator.createTemplateItems(templateBookings)
    }

    override fun onClick(v: View?, item: IRecyclerItem?, position: Int) {
        if (callingActivity == null) {
            return
        }

        if (item !is TemplateItem) {
            return
        }

        val returnTemplateIntent = Intent()
        returnTemplateIntent.putExtra("templateObj", item.content.template)
        setResult(Activity.RESULT_OK, returnTemplateIntent)

        finish()
    }

    override fun onLongClick(v: View?, item: IRecyclerItem?, position: Int) {
        // Do nothing
    }
}