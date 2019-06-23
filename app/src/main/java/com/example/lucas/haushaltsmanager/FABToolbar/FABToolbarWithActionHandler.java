package com.example.lucas.haushaltsmanager.FABToolbar;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.TooltipCompat;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.IMenuItem;
import com.example.lucas.haushaltsmanager.R;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import androidx.annotation.StringRes;

public class FABToolbarWithActionHandler {
    private FABToolbarLayout mRootLayout;
    private LinearLayout mItemBar;

    public FABToolbarWithActionHandler(FABToolbarLayout layout) {
        mRootLayout = layout;
        mItemBar = mRootLayout.findViewById(R.id.fabtoolbar_toolbar);
    }

    public void setOnFabClickListener(final OnFABToolbarFABClickListener listener) {
        FloatingActionButton fab = mRootLayout.findViewById(R.id.fabtoolbar_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFabClick();
            }
        });
    }

    public void addMenuItem(IMenuItem actionHandler, OnFABToolbarItemClickListener listener) {
        mItemBar.addView(
                createToolbarMenuItemFrom(actionHandler, listener)
        );
    }

    public void toggleToolbarVisibility(boolean isVisible) {
        if (isVisible) {
            mRootLayout.show();
        } else {
            mRootLayout.hide();
        }
    }

    public void toggleMenuItemVisibility(String actionKey, boolean isVisible) {
        ImageView imV = mRootLayout.findViewWithTag(actionKey);
        toggleVisibilityWithAnimation(imV, isVisible);
    }

    // TODO: Can I fill FABToolbar with MenuItems?
    private ImageView createToolbarMenuItemFrom(final IMenuItem actionHandler, final OnFABToolbarItemClickListener listener) {
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

        ImageView imageView = new ImageView(mRootLayout.getContext());
        imageView.setImageResource(actionHandler.getIconRes());
        imageView.setContentDescription("");
        imageView.setTag(actionHandler.getActionKey().toString());
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFABMenuItemClick(actionHandler);
            }
        });
        TooltipCompat.setTooltipText(imageView, getString(actionHandler.getHintRes()));

        return imageView;
    }

    private String getString(@StringRes int stringRes) {
        return app.getContext().getResources().getString(stringRes);
    }

    // TODO: Only show inflation animation when deflation animation is finished
    private void toggleVisibilityWithAnimation(View view, boolean isVisible) {
        TransitionSet set = new TransitionSet()
                .addTransition(new Fade())
                .setInterpolator(isVisible ? new LinearOutSlowInInterpolator() :
                        new FastOutLinearInInterpolator());

        TransitionManager.beginDelayedTransition(mRootLayout, set);
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
