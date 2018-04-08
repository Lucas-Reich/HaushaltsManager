package com.example.lucas.haushaltsmanager.Views.PieChart.Legend;


import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Views.PieChart.PieChart;

public class LegendItem {

    private Rect bounds;
    private @ColorInt
    int color;
    private String label;
    private PieChart.NumeratorStyles style;

    public LegendItem(@NonNull String label, @ColorInt int color, @NonNull PieChart.NumeratorStyles style) {

        bounds = new Rect();
        this.color = color;
        this.label = label;
        this.style = style;
    }

    public void setBounds(Rect bounds) {
        this.bounds.set(bounds);
    }

    public int getWidth() {
        return bounds.width();
    }

    public int getHeight() {
        return bounds.height();
    }

    @ColorInt
    public int getColor() {
        return color;
    }

    @NonNull
    public String getLabel() {
        return label;
    }

    @NonNull
    public PieChart.NumeratorStyles getStyle() {
        return style;
    }
}
