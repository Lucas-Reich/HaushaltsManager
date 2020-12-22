package com.example.lucas.haushaltsmanager.Activities;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class LayoutManagerFactory {
    public static LinearLayoutManager horizontal(Context context) {
        return createManager(context, LinearLayoutManager.HORIZONTAL);
    }

    public static LinearLayoutManager vertical(Context context) {
        return createManager(context, LinearLayoutManager.VERTICAL);
    }

    private static LinearLayoutManager createManager(Context context, int orientation) {
        return new LinearLayoutManager(
                context,
                orientation,
                false
        );
    }
}
