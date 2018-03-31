package com.example.lucas.haushaltsmanager;

import android.support.annotation.ColorInt;

public class DataSet {

    private float value;
    private @ColorInt
    int color;
    private String label;

    public DataSet(float dataValue, @ColorInt int dataColor, String dataLabel) {

        this.value = dataValue;
        this.color = dataColor;
        this.label = dataLabel;
    }

    public float getValue() {
        return value;
    }

    public int getColor() {
        return color;
    }

    public String getLabel() {
        return label;
    }
}
