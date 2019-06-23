package com.example.lucas.haushaltsmanager.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.DeleteCategoryMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.IMenuItem;
import com.example.lucas.haushaltsmanager.FABToolbar.FABToolbarWithActionHandler;
import com.example.lucas.haushaltsmanager.FABToolbar.OnFABToolbarFABClickListener;
import com.example.lucas.haushaltsmanager.FABToolbar.OnFABToolbarItemClickListener;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerItemClickListener;
import com.example.lucas.haushaltsmanager.RecyclerView.CategoryListRecyclerViewAdapter;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.ItemCreator;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentCategoryItem;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import java.util.List;

import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_CATEGORY;
import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_MODE;
import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_MODE_UPDATE;
import static com.example.lucas.haushaltsmanager.Activities.CreateCategory.INTENT_PARENT;

public class CategoryList extends AbstractAppCompatActivity implements
        RecyclerItemClickListener.OnItemClickListener,
        OnFABToolbarItemClickListener,
        OnFABToolbarFABClickListener {
    private RecyclerView mRecyclerView;
    private FABToolbarWithActionHandler mFabToolbar;
    private CategoryListRecyclerViewAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        mRecyclerView = findViewById(R.id.category_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, this));

        mFabToolbar = new FABToolbarWithActionHandler(
                (FABToolbarLayout) findViewById(R.id.category_list_fab_toolbar)
        );
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
    public void onItemClick(IRecyclerItem item, int position) {
        if (item instanceof ParentCategoryItem) {
            mRecyclerViewAdapter.toggleExpansion(position);

            return;
        }

        if (mRecyclerViewAdapter.getSelectedItemsCount() == 0) {
            handleOpenChildClick((Category) item.getContent(), (Category) item.getParent().getContent());

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
    public void onItemLongClick(IRecyclerItem item, int position) {
        if (item instanceof ParentCategoryItem || mRecyclerViewAdapter.getSelectedItemsCount() > 0) {

            mRecyclerViewAdapter.toggleExpansion(position);
            return;
        }

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

    private void updateListView() {
        mRecyclerViewAdapter = new CategoryListRecyclerViewAdapter(
                loadData()
        );

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

    private List<IRecyclerItem> loadData() {
        List<Category> categories = getAllCategories();

        return ItemCreator.createCategoryItems(categories);
    }

    private List<Category> getAllCategories() {
        CategoryRepository categoryRepo = new CategoryRepository(this);

        return categoryRepo.getAll();
    }

    private void setActionHandler() {
        mFabToolbar.addMenuItem(new DeleteCategoryMenuItem(new DeleteCategoryMenuItem.OnSuccessCallback() {
            @Override
            public void onSuccess(IRecyclerItem deletedItem) {

                mRecyclerViewAdapter.removeItem(deletedItem);
            }
        }), this);
    }
}
