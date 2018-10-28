package com.example.lucas.haushaltsmanager.Activities.CategoryList;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.lucas.haushaltsmanager.R;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

class CategoryFABToolbar {
    public static final String MENU_DELETE_ACTION = "delete_action";

    private FABToolbarLayout mFabToolbar;
    private LinearLayout mFabToolbarToolbar;
    private OnCategoryFABToolbarMenuItemClicked mLIstener;
    private Context mContext;

    CategoryFABToolbar(
            FABToolbarLayout fabToolbarLayout,
            Context context,
            OnCategoryFABToolbarMenuItemClicked listener
    ) {
        mFabToolbar = fabToolbarLayout;
        mFabToolbarToolbar = mFabToolbar.findViewById(R.id.category_list_fab_toolbar_toolbar);
        mContext = context;
        mLIstener = listener;

        populateToolbar();

        FloatingActionButton fab = mFabToolbar.findViewById(R.id.category_list_fab_toolbar_fab);
        fab.setOnClickListener(getOnFabClickListener());
    }

    void showToolbar() {
        if (mFabToolbar.isFab())
            mFabToolbar.show();
    }

    void hideToolbar() {
        if (mFabToolbar.isToolbar())
            mFabToolbar.hide();
    }

    private void populateToolbar() {
        mFabToolbarToolbar.removeAllViews();

        ImageView deleteCategory = getToolbarMenuItem(
                R.drawable.ic_delete_white,
                "",
                MENU_DELETE_ACTION,
                getOnMenuClickListener()
        );
        mFabToolbarToolbar.addView(deleteCategory);
    }

    private View.OnClickListener getOnFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLIstener.onFabClicked();
            }
        };
    }

    private View.OnClickListener getOnMenuClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLIstener.onToolbarMenuItemClicked((String) v.getTag());
            }
        };
    }

    private ImageView getToolbarMenuItem(
            @DrawableRes int icon,
            String iconDesc,
            String actionTag,
            View.OnClickListener onClickListener
    ) {
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(icon);
        imageView.setContentDescription(iconDesc);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setOnClickListener(onClickListener);
        imageView.setTag(actionTag);

        return imageView;
    }

    public interface OnCategoryFABToolbarMenuItemClicked {
        void onToolbarMenuItemClicked(String tag);

        void onFabClicked();
    }
}
