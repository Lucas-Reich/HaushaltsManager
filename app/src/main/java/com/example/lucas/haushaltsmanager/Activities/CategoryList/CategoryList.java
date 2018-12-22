package com.example.lucas.haushaltsmanager.Activities.CategoryList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.AbstractAppCompatActivity;
import com.example.lucas.haushaltsmanager.Activities.CreateCategory;
import com.example.lucas.haushaltsmanager.ListAdapter.CategoryAdapter;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.CannotDeleteChildCategoryException;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.R;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import java.util.List;

import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_CATEGORY;
import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_MODE;
import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_MODE_UPDATE;
import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_PARENT;

public class CategoryList extends AbstractAppCompatActivity implements CategoryFABToolbar.OnCategoryFABToolbarMenuItemClicked {
    private static final String TAG = CategoryList.class.getSimpleName();

    private CategoryFABToolbar mFabToolbar;
    private ExpandableListView mExpListView;
    private CategoryAdapter mCategoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        mExpListView = findViewById(R.id.expandable_list_view);

        mFabToolbar = new CategoryFABToolbar(
                (FABToolbarLayout) findViewById(R.id.category_list_fab_toolbar),
                this,
                this
        );

        initializeToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateListView();

        mExpListView.setBackgroundColor(Color.WHITE);
        mExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
                Category category = (Category) mCategoryAdapter.getChild(groupPosition, childPosition);

                if (noChildrenSelected()) {
                    handleOpenChildClick(category, (Category) mCategoryAdapter.getGroup(groupPosition));
                    return false;
                }

                if (mCategoryAdapter.isChildSelected(category))
                    unselectCategory(category, view);
                else
                    selectCategory(category, view);

                return true;
            }
        });

        mExpListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCategoryAdapter.isGroup(id))
                    return true;

                selectCategory(getCategory(id), view);

                mFabToolbar.showToolbar();

                setListLongClickable(false);

                return true;
            }
        });
    }

    private void handleOpenChildClick(Category clickedCategory, Category parent) {

        if (getCallingActivity() != null) {

            Intent returnCategoryIntent = new Intent();
            returnCategoryIntent.putExtra("categoryObj", clickedCategory);
            setResult(Activity.RESULT_OK, returnCategoryIntent);
            finish();
        } else {

            Intent updateCategoryIntent = new Intent(CategoryList.this, CreateCategory.class);
            updateCategoryIntent.putExtra(INTENT_MODE, INTENT_MODE_UPDATE);
            updateCategoryIntent.putExtra(INTENT_CATEGORY, clickedCategory);
            updateCategoryIntent.putExtra(INTENT_PARENT, parent);
            startActivity(updateCategoryIntent);
        }
    }

    private void unselectCategory(Category category, View categoryView) {
        mCategoryAdapter.deselectChild(category);
        categoryView.setBackgroundColor(Color.WHITE);

        if (noChildrenSelected()) {
            mFabToolbar.hideToolbar();
            setListLongClickable(true);
        }
    }

    private void selectCategory(Category category, View categoryView) {
        mCategoryAdapter.selectChild(category);
        categoryView.setBackgroundColor(getResources().getColor(R.color.list_item_highlighted));
    }

    private Category getCategory(long id) {
        return (Category) mCategoryAdapter.getChild(
                ExpandableListView.getPackedPositionGroup(id),
                ExpandableListView.getPackedPositionChild(id)
        );
    }

    private boolean noChildrenSelected() {
        return mCategoryAdapter.getSelectedChildItemCount() == 0;
    }

    private void setListLongClickable(boolean isLongClickable) {
        mExpListView.setLongClickable(isLongClickable);
    }

    /**
     * Methode um die ListView nach einer Änderung anzuzeigen
     */
    private void updateListView() {

        mCategoryAdapter = new CategoryAdapter(getAllCategories(), this);

        mExpListView.setAdapter(mCategoryAdapter);

        mCategoryAdapter.notifyDataSetChanged();
    }

    private List<Category> getAllCategories() {
        CategoryRepository categoryRepo = new CategoryRepository(this);
        return categoryRepo.getAll();
    }

    @Override
    public void onToolbarMenuItemClicked(String tag) {
        switch (tag) {
            case CategoryFABToolbar.MENU_DELETE_ACTION:
                ChildCategoryRepository childRepo = new ChildCategoryRepository(this);

                try {
                    for (Category childCategory : mCategoryAdapter.getSelectedChildData())
                        childRepo.delete(childCategory);

                    mFabToolbar.hideToolbar();

                    updateListView();
                } catch (CannotDeleteChildCategoryException e) {

                    // TODO: Ich sollte den Try-Catch Block nur um die for schleife machen und die Kategorien die nicht gelöscht werden konnten speichern und etwas mit ihnen machen
                    Toast.makeText(this, getString(R.string.failed_to_delete_category), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Could not delete ChildCategory: " + e.getMessage());
                }
                break;
            default:
                throw new UnsupportedOperationException(tag + " Action does not exist.");
        }
    }

    @Override
    public void onFabClicked() {
        Intent createCategoryIntent = new Intent(this, CreateCategory.class);
        createCategoryIntent.putExtra(CreateCategory.INTENT_MODE, CreateCategory.INTENT_MODE_CREATE);

        startActivity(createCategoryIntent);
    }
}
