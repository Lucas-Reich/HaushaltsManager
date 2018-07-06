package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.example.lucas.haushaltsmanager.CategoryAdapter;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryListActivity extends AppCompatActivity {
    private static final String TAG = CategoryListActivity.class.getSimpleName();

    private ExpensesDataSource mDatabase;
    private ArrayList<Category> mCategories;
    private FloatingActionButton mAddCategoryFab;
    private ImageButton mBackArrow;
    private ExpandableListView mExpListView;
    private CategoryAdapter mListAdapter;

    private List<String> mListDataHeader;
    private HashMap<String, Category> mListDataChild;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_ver2);

        mDatabase = new ExpensesDataSource(this);
        mDatabase.open();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);

        mExpListView = (ExpandableListView) findViewById(R.id.categories_exp_list_view);

        mAddCategoryFab = (FloatingActionButton) findViewById(R.id.categories_fab);

        mListDataHeader = new ArrayList<>();
        mListDataChild = new HashMap<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateListView();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBackArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        mExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                Category clickedCategory = (Category) mListAdapter.getChild(groupPosition, childPosition);
                if (getCallingActivity() != null) {

                    Intent returnCategoryIntent = new Intent();
                    returnCategoryIntent.putExtra("categoryObj", clickedCategory);
                    setResult(Activity.RESULT_OK, returnCategoryIntent);
                    finish();
                } else {

                    Intent updateCategoryIntent = new Intent(CategoryListActivity.this, CreateCategoryActivity.class);
                    updateCategoryIntent.putExtra("mode", "updateCategory");
                    updateCategoryIntent.putExtra("updateCategory", clickedCategory);
                    CategoryListActivity.this.startActivity(updateCategoryIntent);
                }

                return true;
            }
        });

        mAddCategoryFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent createCategoryIntent = new Intent(CategoryListActivity.this, CreateCategoryActivity.class);
                createCategoryIntent.putExtra("mode", "createCategory");
                CategoryListActivity.this.startActivity(createCategoryIntent);
            }
        });
    }

    /**
     * Methode um die ListView nach einer Änderung anzuzeigen
     */
    private void updateListView() {

        prepareDataSources();

        mListAdapter = new CategoryAdapter(mCategories, this);

        mExpListView.setAdapter(mListAdapter);

        mListAdapter.notifyDataSetChanged();
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
