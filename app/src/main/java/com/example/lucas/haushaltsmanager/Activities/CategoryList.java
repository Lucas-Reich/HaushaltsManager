package com.example.lucas.haushaltsmanager.Activities;

import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_CATEGORY;
import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_MODE;
import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_MODE_UPDATE;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryDAO;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.DeleteCategoryMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.IMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.FABToolbarWithActionHandler;
import com.example.lucas.haushaltsmanager.FABToolbar.OnFABToolbarFABClickListener;
import com.example.lucas.haushaltsmanager.FABToolbar.OnFABToolbarItemClickListener;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerItemClickListener;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.ItemCreator;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.CategoryItem.CategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.CategoryListRecyclerViewAdapter;

import java.util.List;

public class CategoryList extends AbstractAppCompatActivity implements
        RecyclerItemClickListener.OnRecyclerItemClickListener,
        OnFABToolbarItemClickListener,
        OnFABToolbarFABClickListener {
    private RecyclerView mRecyclerView;
    private FABToolbarWithActionHandler mFabToolbar;
    private CategoryListRecyclerViewAdapter mRecyclerViewAdapter;
    private CategoryDAO categoryRepo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        mRecyclerView = findViewById(R.id.category_list_recycler_view);
        mRecyclerView.setLayoutManager(LayoutManagerFactory.vertical(this));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, this));

        categoryRepo = Room.databaseBuilder(this, AppDatabase.class, "expenses")
                .allowMainThreadQueries() // TODO: Remove
                .build().categoryDAO();

        mFabToolbar = new FABToolbarWithActionHandler(findViewById(R.id.category_list_fab_toolbar));
        mFabToolbar.setOnFabClickListener(this);

        setActionHandler();

        initializeToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateListView();
    }

    @Override
    public void onClick(View v, IRecyclerItem item, int position) {
        if (!mRecyclerViewAdapter.isInSelectionMode()) {
            CategoryItem categoryItem = (CategoryItem) item;

            handleOpenChildClick(categoryItem.getContent());

            return;
        }

        if (mRecyclerViewAdapter.isItemSelected(item)) {
            mRecyclerViewAdapter.unselectItem(item, position);
        } else {
            mRecyclerViewAdapter.selectItem(item, position);
        }

        updateFABToolbar();
    }

    @Override
    public void onLongClick(View v, IRecyclerItem item, int position) {
        mRecyclerViewAdapter.selectItem(item, position);

        updateFABToolbar();
    }

    @Override
    public void onFabClick() {
        Intent createCategoryIntent = new Intent(this, CreateCategory.class);
        createCategoryIntent.putExtra(CreateCategory.INTENT_MODE, CreateCategory.INTENT_MODE_CREATE);

        startActivity(createCategoryIntent);
    }

    @Override
    public void onFABMenuItemClick(IMenuItem actionHandler) {
        if (!actionHandler.getActionKey().toString().equals(DeleteCategoryMenuItem.ACTION_KEY)) {
            return;
        }

        ActionPayload payload = new ActionPayload();
        payload.setPayload(mRecyclerViewAdapter.getSelectedItems());

        actionHandler.handleClick(payload, this);

        mFabToolbar.toggleToolbarVisibility(false);

        updateListView();
    }

    private void updateFABToolbar() {
        boolean itemsSelected = mRecyclerViewAdapter.getSelectedItemsCount() != 0;

        mFabToolbar.toggleToolbarVisibility(itemsSelected);
    }

    private void handleOpenChildClick(Category clickedCategory) {

        if (getCallingActivity() != null) {

            Intent returnCategoryIntent = new Intent();
            returnCategoryIntent.putExtra("categoryObj", clickedCategory);
            setResult(Activity.RESULT_OK, returnCategoryIntent);
            finish();
        } else {

            Intent updateCategoryIntent = new Intent(CategoryList.this, CreateCategory.class);
            updateCategoryIntent.putExtra(INTENT_MODE, INTENT_MODE_UPDATE);
            updateCategoryIntent.putExtra(INTENT_CATEGORY, clickedCategory);
            startActivity(updateCategoryIntent);
        }
    }

    private void updateListView() {
        mRecyclerViewAdapter = new CategoryListRecyclerViewAdapter(
                loadData()
        );

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    private List<IRecyclerItem> loadData() {
        List<Category> categories = categoryRepo.getAll();

        return ItemCreator.createCategoryItems(categories);
    }

    private void setActionHandler() {
        mFabToolbar.addMenuItem(new DeleteCategoryMenuItem(deletedItem -> mRecyclerViewAdapter.removeItem(deletedItem), categoryRepo), this);
    }
}
