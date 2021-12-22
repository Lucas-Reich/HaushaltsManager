package com.example.lucas.haushaltsmanager.ReportBuilder;

import android.view.DragEvent;

public class Point {
    private final float x;
    private final float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static Point fromDragEvent(DragEvent event) {
        return new Point(event.getX(), event.getY());
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
