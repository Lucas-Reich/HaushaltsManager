package com.example.lucas.haushaltsmanager.Views.PieChart;


import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

/**
 * Klasse um die Datensets des Kreisdiagramms zu sortieren
 */
public class PieSlice {

    /**
     * Originaler vom User gegebener Wert
     */
    private float mAbsValue;

    /**
     * Basiert auf mAbsValue und ist zum animieren gedacht
     */
    private float mAnimValue;

    /**
     * Prozentualer Anteil am Kreis
     */
    private float mPercentValue;

    /**
     * Winkel ab dem der Bereich des Segments beginnt
     */
    private float mStartAngle;

    /**
     * Winkel ab dem der Bereich des Segments endet
     */
    private float mEndAngle;

    /**
     * Farbe des Kreissegments
     */
    private int mSliceColor;

    /**
     * Bezeichnung des Kreissegments
     */
    private String mSliceLabel;

    /**
     * Platz den das Kreissegment einnimt unterteilt in gewichte (Default 1).
     */
    private int mSliceWeight;

    PieSlice(float absValue, float percentValue, float startAngle) {

        constructor(absValue, percentValue, startAngle);
    }

    private void constructor(float absValue, float percentValue, float startAngle) {

        this.mAbsValue = absValue;
        this.mAnimValue = absValue;
        this.mPercentValue = percentValue;
        this.mStartAngle = startAngle;
        this.mEndAngle = mStartAngle + percentValue;
        this.mSliceColor = Color.WHITE;
        mSliceWeight = 0;
    }

    public void setWeight(int weight) {

        this.mSliceWeight = weight;
    }

    public int getWeight() {

        return this.mSliceWeight;
    }

    public float getAbsValue() {

        return mAbsValue;
    }

    public float getAnimValue() {

        return this.mAnimValue;
    }

    public void setAnimValue(float animValue) {

        this.mAnimValue = animValue >= 0 ? animValue : 0f;
    }

    public int getColor() {

        return this.mSliceColor;
    }

    public void setSliceColor(@ColorInt int color) {

        this.mSliceColor = color;
    }

    @NonNull
    public String getLabel() {

        return this.mSliceLabel != null ? mSliceLabel : "";
    }

    public void setSliceLabel(@NonNull String label) {

        this.mSliceLabel = label;
    }

    public float getPercentValue() {

        return mPercentValue;
    }

    public void setPercentValue(float mCalcValue) {

        this.mPercentValue = mCalcValue;
        this.mEndAngle = this.mStartAngle + mCalcValue;
    }

    public float getStartAngle() {

        return mStartAngle;
    }

    public void setStartAngle(float mStartAngle) {

        this.mStartAngle = mStartAngle;
    }

    public float getEndAngle() {

        return mEndAngle;
    }
}
