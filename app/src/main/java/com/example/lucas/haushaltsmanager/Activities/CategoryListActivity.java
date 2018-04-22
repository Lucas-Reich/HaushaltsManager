package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.lucas.haushaltsmanager.CategoryAdapter;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;

public class CategoryListActivity extends AppCompatActivity {
    private String TAG = CategoryListActivity.class.getSimpleName();

    ExpensesDataSource mDatabase;
    ListView mListView;
    ArrayList<Category> mCategories;
    FloatingActionButton mAddCategoryFab;
    ImageButton mBackArrow;
    Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);

        mListView = (ListView) findViewById(R.id.categories_listview);

        mAddCategoryFab = (FloatingActionButton) findViewById(R.id.categories_add_category);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateListView();

        mBackArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        mListView.setDivider(null);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TODO wenn der nutzer durch das menü in die activity gekommen ist den intent verbieten/ ausschalten
                Intent returnIntent = new Intent();
                returnIntent.putExtra("categoryObj", mCategories.get(position));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        mAddCategoryFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent createCategoryIntent = new Intent(CategoryListActivity.this, CreateCategoryActivity.class);
                CategoryListActivity.this.startActivity(createCategoryIntent);
            }
        });
    }

    /**
     * Methode um die ListView nach einer Änderung anzuzeigen
     */
    private void updateListView() {

        prepareDataSources();

        CategoryAdapter categoryAdapter = new CategoryAdapter(mCategories, this);

        mListView.setAdapter(categoryAdapter);

        categoryAdapter.notifyDataSetChanged();
    }

    /**
     * Methode um die Liste der Kategorien zu initilisieren.
     */
    private void prepareDataSources() {

        Log.d(TAG, "prepareDataSources: Initialisiere Kategorienliste");
        mCategories = mDatabase.getAllCategories();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDatabase.close();
    }
}
