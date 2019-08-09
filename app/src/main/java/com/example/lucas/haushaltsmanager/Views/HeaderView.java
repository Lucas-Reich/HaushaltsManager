package com.example.lucas.haushaltsmanager.Views;

import android.content.Context;
import android.util.AttributeSet;

import com.example.lucas.haushaltsmanager.ExpenseImporter.HeaderHolder;

public class HeaderView extends android.support.v7.widget.AppCompatTextView {
    private HeaderHolder currentVisibleHeader;

    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindHeader(HeaderHolder holder) {
        setText(holder.getHeaderField());

        currentVisibleHeader = holder;
    }

    public HeaderHolder getCurrentlyVisibleHeader() {
        return currentVisibleHeader;
    }
}