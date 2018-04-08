package com.example.lucas.haushaltsmanager.Views.PieChart.Legend;


import android.content.res.Resources;
import android.graphics.Rect;

import com.example.lucas.haushaltsmanager.Views.PieChart.PieChart;

import java.util.ArrayList;
import java.util.List;

public class Legend {

    private PieChart.LegendDirections mLegendDirection;
    private Rect bounds;
    private List<LegendItem> items;
    private int mNumeratorItemOffset = dpToPx(5);
    private int mNumeratorChartPadding = dpToPx(10);
    private int mNumeratorSize = dpToPx(20);

    public Legend(PieChart.LegendDirections legendDirection) {

        mLegendDirection = legendDirection;
        bounds = new Rect();
        items = new ArrayList<>();
    }

    public void addItem(LegendItem item) {
        items.add(item);
    }

    /**
     * Methode um die Breite der Legende zu ermitteln.
     * Dabei wird die Ausrichtung der Legende mit einbezogen.
     *
     * @return Legendenbreite
     */
    public int getWidth() {
        int width = 0;
        if (mLegendDirection == PieChart.LegendDirections.LEFT_TO_RIGHT) {

            for (LegendItem item : items) {
                width += item.getWidth() + mNumeratorItemOffset;
            }
        } else {

            for (LegendItem item : items) {
                width = Math.max(width, item.getWidth()) + mNumeratorChartPadding;
            }
        }

        return width;
    }

    /**
     * Methode um die Höhe der Legende zu ermitteln.
     * Dabei wird die Auscrichtung der Legende mit einbezogen.
     *
     * @return Legendenhöhe
     */
    public int getHeight() {
        int height = 0;
        if (mLegendDirection == PieChart.LegendDirections.LEFT_TO_RIGHT) {

            for (LegendItem item : items) {
                height = Math.max(height, item.getHeight()) + mNumeratorChartPadding;
            }
        } else {

            for (LegendItem item : items) {
                height += item.getHeight() + mNumeratorItemOffset;
            }
        }

        return height;
    }

    /**
     * Methode um die Breite der Legend zu ermitteln, wenn die Legendenelemente keine Labels haben.
     * Dabei wird die Ausrichtung der Legende mit einbezogen.
     *
     * @return Legendenbreite ohne Labels
     */
    public int getShrinkedWidth() {

        if (mLegendDirection == PieChart.LegendDirections.LEFT_TO_RIGHT)
            return items.size() * ((int) mNumeratorSize + mNumeratorItemOffset);
        else
            return (int) mNumeratorSize + mNumeratorChartPadding;
    }

    /**
     * Methode um die Höhe der Legend zu ermitteln, wenn die Legendenelemente keine Labels haben.
     * Dabei wird die Ausrichtung der Legende mit einbezogen.
     *
     * @return Legendenhöhe ohne Labels
     */
    public int getShrinkedHeight() {

        if (mLegendDirection == PieChart.LegendDirections.LEFT_TO_RIGHT)
            return (int) mNumeratorSize + mNumeratorChartPadding;
        else
            return items.size() * (int) (mNumeratorSize + mNumeratorItemOffset);
    }

    /**
     * Methode um DensityPixel in Pixel umzuwandeln.
     * source: https://stackoverflow.com/a/19953871/9376633
     *
     * @param dp Zu konvertierende dp
     * @return In px konvertierte dp
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}