package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.ListAdapter.BookingAdapter;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Template;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class TemplatesActivity extends AppCompatActivity {
    private static final String TAG = TemplatesActivity.class.getSimpleName();

    private List<ExpenseObject> mTemplates = new ArrayList<>();
    private ListView mListView;
    private TemplateRepository mTemplateRepo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_bookings);

        initializeToolbar();

        mListView = findViewById(R.id.booking_list_view);

        mTemplateRepo = new TemplateRepository(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView emptyText = findViewById(R.id.empty_list_view);
        emptyText.setText(R.string.template_empty_list);
        mListView.setEmptyView(emptyText);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (getCallingActivity() != null) {

                    Intent returnTemplateIntent = new Intent();
                    returnTemplateIntent.putExtra("templateObj", mTemplates.get(position));
                    setResult(Activity.RESULT_OK, returnTemplateIntent);
                    finish();
                } else {

                    // IMPROVEMENT: Ein Template sollte bearbeitet werden können.
                }
            }
        });
        updateListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Methode um eine Toolbar anzuzeigen die den Titel und einen Zurückbutton enthält.
     */
    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        //schatten der toolbar
        if (Build.VERSION.SDK_INT >= 21)
            toolbar.setElevation(10.f);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateListView() {
        prepareListData();

        BookingAdapter bookingAdapter = new BookingAdapter(mTemplates, this);

        mListView.setAdapter(bookingAdapter);

        bookingAdapter.notifyDataSetChanged();
    }

    private void prepareListData() {
        List<Template> templates = mTemplateRepo.getAll();
        for (Template template : templates) {
            mTemplates.add(template.getTemplate());
        }
    }
}
