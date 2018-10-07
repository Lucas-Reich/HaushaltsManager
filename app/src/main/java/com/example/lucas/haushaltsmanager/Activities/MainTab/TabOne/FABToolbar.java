package com.example.lucas.haushaltsmanager.Activities.MainTab.TabOne;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.lucas.haushaltsmanager.R;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

class FABToolbar {
    static final String MENU_COMBINE_ACTION = "combineAction";
    static final String MENU_EXTRACT_ACTION = "extractAction";
    static final String MENU_ADD_CHILD_ACTION = "addChildAction";
    static final String MENU_TEMPLATE_ACTION = "templateAction";
    static final String MENU_RECURRING_ACTION = "recurringAction";
    static final String MENU_DELETE_ACTION = "deleteAction";

    private static final int CHANGEABLE_OPTION_INDEX = 0;

    private FABToolbarLayout mFabToolbar;
    private LinearLayout mFabToolbarToolbar;
    private OnFabToolbarMenuItemClicked mListener;
    private Context mContext;

    FABToolbar(FABToolbarLayout fabToolbarLayout, Context context, OnFabToolbarMenuItemClicked listener) {
        mFabToolbar = fabToolbarLayout;
        mFabToolbarToolbar = mFabToolbar.findViewById(R.id.fabtoolbar_toolbar);
        mContext = context;
        mListener = listener;

        populateToolbar();

        FloatingActionButton fab = mFabToolbar.findViewById(R.id.fabtoolbar_fab);
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

    void changeToolbarItemOne(@DrawableRes int icon, String contentDesc, String actionTag) {
        ImageView imV = getToolbarMenuItem(icon, contentDesc, getOnMenuItemClickListener());
        imV.setTag(actionTag);

        mFabToolbarToolbar.removeViewAt(CHANGEABLE_OPTION_INDEX);
        mFabToolbarToolbar.addView(imV, CHANGEABLE_OPTION_INDEX);
    }

    void hideAction(String actionTag) {
        mFabToolbarToolbar.findViewWithTag(actionTag).setVisibility(View.GONE);
    }

    void showAction(String actionTag) {
        mFabToolbarToolbar.findViewWithTag(actionTag).setVisibility(View.VISIBLE);
    }

    private void populateToolbar() {
        mFabToolbarToolbar.removeAllViews();

        ImageView addChild = getToolbarMenuItem(R.drawable.ic_add_child_white, "", getOnMenuItemClickListener());
        addChild.setTag(MENU_COMBINE_ACTION);
        mFabToolbarToolbar.addView(addChild, CHANGEABLE_OPTION_INDEX);

        ImageView saveAsTemplate = getToolbarMenuItem(R.drawable.ic_template_white, "", getOnMenuItemClickListener());
        saveAsTemplate.setTag(MENU_TEMPLATE_ACTION);
        mFabToolbarToolbar.addView(saveAsTemplate);

        ImageView saveAsRecurring = getToolbarMenuItem(R.drawable.ic_repeat_white, "", getOnMenuItemClickListener());
        saveAsRecurring.setTag(MENU_RECURRING_ACTION);
        mFabToolbarToolbar.addView(saveAsRecurring);

        ImageView delete = getToolbarMenuItem(R.drawable.ic_delete_white, "", getOnMenuItemClickListener());
        delete.setTag(MENU_DELETE_ACTION);
        mFabToolbarToolbar.addView(delete);
    }

    private View.OnClickListener getOnMenuItemClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFabToolbarMenuItemClicked((String) v.getTag());
            }
        };
    }

    private View.OnClickListener getOnFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFabClick();
            }
        };
    }

    private ImageView getToolbarMenuItem(@DrawableRes int icon, String iconDesc, View.OnClickListener onClickListener) {
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(icon);
        imageView.setContentDescription(iconDesc);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setOnClickListener(onClickListener);

        return imageView;
    }

    public interface OnFabToolbarMenuItemClicked {
        void onFabToolbarMenuItemClicked(String tag);

        void onFabClick();
    }
}