package com.example.lucas.haushaltsmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Categories extends AppCompatActivity {

    ExpensesDataSource expensesDataSource;
    ListView listView;
    ArrayList<Category> categories;
    private static CategroyAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        expensesDataSource = new ExpensesDataSource(this);
/*
        String[] categoriesArray = {
                "Mittagessen",
                "Computer",
                "Kategorie 3",
                "Kategorie 4",
                "Kategorie 5",
                "Kategorie 6",
                "Kategorie 7"
        };


        List<String> categoryList = new ArrayList<>(Arrays.asList(categoriesArray));

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this, R.layout.category_item, R.id.category_item, categoryList);

        ListView categoriesListView = (ListView) findViewById(R.id.categories_listview);
        categoriesListView.setAdapter(categoriesAdapter);*/











        listView = (ListView) findViewById(R.id.categories_listview);

        expensesDataSource.open();

        categories = expensesDataSource.getAllCategories();

        adapter = new CategroyAdapter(categories, getApplicationContext());

        listView.setAdapter(adapter);

    }
}
