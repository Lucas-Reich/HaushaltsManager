package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.TemplateBookingDAO;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerItemClickListener;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.ItemCreator;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem.TemplateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.TemplateListRecyclerViewAdapter;
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBooking;

import java.util.List;

public class TemplatesActivity extends AbstractAppCompatActivity implements RecyclerItemClickListener.OnRecyclerItemClickListener {
    private TemplateBookingDAO templateBookingsRepository;
    private RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();

        templateBookingsRepository = AppDatabase.getDatabase(this).templateBookingDAO();

        updateListView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_list);

        recyclerView = findViewById(R.id.template_list_recycler_view);
        recyclerView.setLayoutManager(LayoutManagerFactory.vertical(this));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));

        initializeToolbar();
    }

    @Override
    public void onClick(View v, IRecyclerItem item, int position) {
        if (getCallingActivity() == null) {
            return;
        }

        if (!(item instanceof TemplateItem)) {
            return;
        }

        // TODO: Ich sollte Templates bearbeitbar machen.
        //  Außerdem sollte man Templates auch löschen können.

        Intent returnTemplateIntent = new Intent();
        returnTemplateIntent.putExtra("templateObj", (TemplateBooking) item.getContent());
        setResult(Activity.RESULT_OK, returnTemplateIntent);
        finish();
    }

    @Override
    public void onLongClick(View v, IRecyclerItem item, int position) {
        // Do nothing
    }

    private void updateListView() {
        TemplateListRecyclerViewAdapter adapter = new TemplateListRecyclerViewAdapter(
                loadData()
        );

        recyclerView.setAdapter(adapter);
    }

    private List<IRecyclerItem> loadData() {
        List<TemplateBooking> templateBookings = templateBookingsRepository.getAll();

        return ItemCreator.createTemplateItems(templateBookings);
    }
}
