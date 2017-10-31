package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Categories extends AppCompatActivity {

    ExpensesDataSource expensesDataSource;
    ListView listView;
    ArrayList<Category> categories;
    CategoryAdapter adapter;

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

                //Toast.makeText(Categories.this, categories.get(position).getCategoryName(), Toast.LENGTH_SHORT).show();
/*
                ArrayList<Category> test = new ArrayList<>();
                test.add(categories.get(position));
                Intent returnIntent = new Intent();
                returnIntent.putParcelableArrayListExtra("categoryObj", test);
                returnIntent.putExtra("category", categories.get(position).getCategoryName());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                */

                Intent returnIntent = new Intent();
                returnIntent.putExtra("categoryObj", categories.get(position));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }
}
