package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.lucas.haushaltsmanager.BookingAdapter;
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
    private ImageButton mBackArrow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_bookings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);

        mListView = (ListView) findViewById(R.id.booking_listview);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBackArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (getCallingActivity() != null) {

                    Intent returnTemplateIntent = new Intent();
                    returnTemplateIntent.putExtra("templateObj", mTemplates.get(position));
                    setResult(Activity.RESULT_OK, returnTemplateIntent);
                    finish();
                } else {

                    //todo template soll bearbeitet werden können
                }
            }
        });
        updateListView();
    }

    private void updateListView() {
        prepareListData();

        BookingAdapter bookingAdapter = new BookingAdapter(mTemplates, this);

        mListView.setAdapter(bookingAdapter);

        bookingAdapter.notifyDataSetChanged();
    }

    private void prepareListData() {
        List<Template> templates = TemplateRepository.getAll();
        for (Template template : templates) {
            mTemplates.add(template.getTemplate());
        }
    }
}
