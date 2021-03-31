package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.ActionKey;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.CategoryItem.CategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.sql.SQLException;

public class DeleteCategoryMenuItem implements IMenuItem {
    public static final String ACTION_KEY = "deleteCategoryAction";
    private static final String TAG = DeleteCategoryMenuItem.class.getSimpleName();

    private final OnSuccessCallback mCallback;
    private final IActionKey mActionKey = new ActionKey(ACTION_KEY);
    private final CategoryRepositoryInterface categoryRepository;

    public DeleteCategoryMenuItem(
            OnSuccessCallback callback,
            CategoryRepositoryInterface categoryRepository
    ) {
        mCallback = callback;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public int getIconRes() {
        return R.drawable.ic_delete_white;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public int getHintRes() {
        return R.string.fab_menu_item_delete_category_hint;
    }

    @Override
    public IActionKey getActionKey() {
        return mActionKey;
    }

    @Override
    public void handleClick(ActionPayload actionPayload, Context context) {
        for (IRecyclerItem selectedItem : actionPayload.getItems()) {
            if (!(selectedItem instanceof CategoryItem)) {
                continue;
            }

            deleteCategory((CategoryItem) selectedItem);
        }
    }

    private void deleteCategory(CategoryItem categoryItem) {
        Category category = categoryItem.getContent();

        try {
            categoryRepository.delete(category);

            if (null != mCallback) {
                mCallback.onSuccess(categoryItem);
            }
        } catch (SQLException e) {

            Toast.makeText(app.getContext(), R.string.could_not_delete_child_category, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Could not delete Category " + category.getTitle(), e);
        }
    }

    public interface OnSuccessCallback {
        void onSuccess(IRecyclerItem deletedItem);
    }
}
