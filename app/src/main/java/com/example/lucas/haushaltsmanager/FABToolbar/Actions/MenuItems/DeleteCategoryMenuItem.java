package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.CannotDeleteChildCategoryException;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.ActionKey;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ChildCategoryItem.ChildCategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

public class DeleteCategoryMenuItem implements IMenuItem {
    public static final String ACTION_KEY = "deleteCategoryAction";
    private static final String TAG = DeleteCategoryMenuItem.class.getSimpleName();

    private IActionKey mActionKey;
    private OnSuccessCallback mCallback;
    private ChildCategoryRepository mChildRepo;

    public DeleteCategoryMenuItem(OnSuccessCallback callback) {
        mCallback = callback;
        mActionKey = new ActionKey(ACTION_KEY);
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
        initRepo(context);

        for (IRecyclerItem selectedItem : actionPayload.getItems()) {
            if (!(selectedItem instanceof ChildCategoryItem)) {
                continue;
            }

            deleteChildCategory((ChildCategoryItem) selectedItem);
        }
    }

    private void initRepo(Context context) {
        mChildRepo = new ChildCategoryRepository(context);
    }

    private void deleteChildCategory(ChildCategoryItem childCategory) {
        Category category = childCategory.getContent();

        try {
            mChildRepo.delete(category);

            if (null != mCallback) {
                mCallback.onSuccess(childCategory);
            }
        } catch (CannotDeleteChildCategoryException e) {

            Toast.makeText(app.getContext(), R.string.could_not_delete_child_category, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Could not delete ChildCategory " + category.getTitle(), e);
        }
    }

    public interface OnSuccessCallback {
        void onSuccess(IRecyclerItem deletedItem);
    }
}
