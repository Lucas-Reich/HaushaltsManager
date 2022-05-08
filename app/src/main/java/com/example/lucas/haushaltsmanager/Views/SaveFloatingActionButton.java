package com.example.lucas.haushaltsmanager.Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;

import com.example.lucas.haushaltsmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SaveFloatingActionButton extends FloatingActionButton {
    private boolean mEnabled = false;

    public SaveFloatingActionButton(Context context) {
        super(context);
    }

    public SaveFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SaveFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnClickListener(final OnClickListener listener) {
        setOnClickListener(v -> {
            if (mEnabled)
                listener.onCheckClick();
            else
                listener.onCrossClick();
        });
    }

    /**
     * TODO Der Übergang von dem Häkchen zum Kreuz soll animiert sein
     * How to animate cross to check can be found here: https://www.nickmillward.com/android/2016/7/5/fab-utilizing-animated-vector-drawable-animations
     */
    public void enable() {
        if (!mEnabled) {
            setImageDrawable(getDrawableRes(R.drawable.ic_check_white_24dp));
            mEnabled = true;
        }
    }

    /**
     * TODO Der Übergang von dem Häkchen zum Kreuz soll animiert sein
     * How to animate cross to check can be found here: https://www.nickmillward.com/android/2016/7/5/fab-utilizing-animated-vector-drawable-animations
     */
    public void disable() {
        // TODO: sollte ich auch den FAB disablen (setClickable(false?))
        if (mEnabled) {
            setImageDrawable(getDrawableRes(R.drawable.ic_cross_white));
            mEnabled = false;
        }
    }

    private Drawable getDrawableRes(@DrawableRes int drawable) {
        return ResourcesCompat.getDrawable(getContext().getResources(), drawable, null);
    }

    public interface OnClickListener {
        void onCrossClick();

        void onCheckClick();
    }
}
