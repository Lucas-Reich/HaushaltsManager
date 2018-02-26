package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class Categories extends AppCompatActivity {

    ExpensesDataSource expensesDataSource;
    ListView listView;
    ArrayList<Category> categories;
    CategoryAdapter adapter;
    FloatingActionButton addCatFab;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        expensesDataSource = new ExpensesDataSource(this);

        listView = (ListView) findViewById(R.id.categories_listview);

        expensesDataSource.open();

        categories = expensesDataSource.getAllCategories();

        adapter = new CategoryAdapter(categories, getApplicationContext());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TODO wenn der nutzer durch das menü in die activity gekommen ist den intent verbieten/ ausschalten
                Intent returnIntent = new Intent();
                returnIntent.putExtra("categoryObj", categories.get(position));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        addCatFab = (FloatingActionButton) findViewById(R.id.categories_add_category);
        addCatFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent createCategoryIntent = new Intent(Categories.this, CreateNewCategoryActivity.class);
                Categories.this.startActivity(createCategoryIntent);
            }
        });

    }
}
