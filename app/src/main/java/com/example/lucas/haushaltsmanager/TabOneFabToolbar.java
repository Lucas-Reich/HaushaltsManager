package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

public class TabOneFabToolbar {
    public static final String MENU_ITEM_ONE = "one";
    public static final String MENU_ITEM_TWO = "two";
    public static final String MENU_ITEM_THREE = "three";
    public static final String MENU_ITEM_FOUR = "four";

    private FABToolbarLayout mFabToolbar;
    private LinearLayout mFabToolbarToolbar;
    private OnFabToolbarMenuItemClicked mListener;
    private Context mContext;

    public TabOneFabToolbar(FABToolbarLayout fabToolbarLayout, Context context, OnFabToolbarMenuItemClicked listener) {
        mFabToolbar = fabToolbarLayout;
        mFabToolbarToolbar = mFabToolbar.findViewById(R.id.fabtoolbar_toolbar);
        mContext = context;
        mListener = listener;

        fillToolbar();

        FloatingActionButton fab = mFabToolbar.findViewById(R.id.fabtoolbar_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onFabClick();
            }
        });
    }

    public void changeToolbarMenuItem(ImageView newImageView) {
        int index = mFabToolbar.indexOfChild(mFabToolbar.findViewWithTag(MENU_ITEM_ONE));

        mFabToolbar.removeViewAt(index);
        mFabToolbar.addView(newImageView, index);
    }


    private void fillToolbar() {
        mFabToolbarToolbar.removeAllViews();

        ImageView addChild = getToolbarMenuItem(R.drawable.ic_add_child_white, "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onFabToolbarMenuItemClicked((String) v.getTag());
            }
        });
        addChild.setTag(MENU_ITEM_ONE);
        mFabToolbarToolbar.addView(addChild);

        ImageView saveAsTemplate = getToolbarMenuItem(R.drawable.ic_template_white, "", new View.OnClickListener() {//todo besseres icon für template suchen
            @Override
            public void onClick(View v) {

                mListener.onFabToolbarMenuItemClicked((String) v.getTag());
            }
        });
        saveAsTemplate.setTag(MENU_ITEM_TWO);
        mFabToolbarToolbar.addView(saveAsTemplate);

        ImageView saveAsRecurring = getToolbarMenuItem(R.drawable.ic_repeat_white, "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onFabToolbarMenuItemClicked((String) v.getTag());
            }
        });
        saveAsRecurring.setTag(MENU_ITEM_THREE);
        mFabToolbarToolbar.addView(saveAsRecurring);

        ImageView delete = getToolbarMenuItem(R.drawable.ic_delete_white, "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onFabToolbarMenuItemClicked((String) v.getTag());
            }
        });
        delete.setTag(MENU_ITEM_FOUR);
        mFabToolbarToolbar.addView(delete);
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

    public void show() {
        if (mFabToolbar.isFab())
            mFabToolbar.show();
    }

    public void hide() {
        if (!mFabToolbar.isToolbar())
            mFabToolbar.hide();
    }

    public interface OnFabToolbarMenuItemClicked {
        void onFabToolbarMenuItemClicked(String tag);

        void onFabClick();
    }
}
