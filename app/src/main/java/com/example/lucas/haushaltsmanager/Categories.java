package com.example.lucas.haushaltsmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

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

    }
}
