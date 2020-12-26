package com.example.lucas.haushaltsmanager.ReportBuilder;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.Widget;

public class DropZoneCard {
    private int zoneCount;
    private final CardView cardView;

    public DropZoneCard(@NonNull CardView cardView) {
        this.cardView = cardView;
        this.zoneCount = 3;
    }

    public void addDroppedView(Widget widget, float x, float y) {
        // TODO: Disable on click behaviour of widgets in Builder
        int droppedZone = determineDropZone(new Point(x, y));

        removeExistingViewFromZone(droppedZone);

        View widgetView = widget.getWidgetView();

        addLayoutConstraintsToChild(widgetView, droppedZone);

        addView(widgetView, droppedZone);
    }

    public void setDropZoneCount(int dropZoneCount) {
        zoneCount = dropZoneCount;

        cardView.removeAllViews();
        cardView.invalidate();
    }

    private void addView(View view, int droppedZone) {
        view.setId(droppedZone);
        cardView.addView(view);

        cardView.invalidate();
    }

    private void addLayoutConstraintsToChild(View widgetView, int droppedZone) {
        widgetView.setLayoutParams(new LinearLayout.LayoutParams(
                cardView.getWidth() / zoneCount,
                cardView.getHeight()
        ));

        int x = cardView.getWidth() / zoneCount;

        widgetView.setX(droppedZone * x);
    }

    private void removeExistingViewFromZone(int droppedZone) {
        View oldView = cardView.findViewById(droppedZone);

        if (null == oldView) {
            return;
        }

        cardView.removeView(oldView);
    }

    private int determineDropZone(Point dropPoint) {
        int zoneWidth = this.cardView.getWidth() / zoneCount;

        for (int i = 0; i < zoneCount; i++) {
            int zoneStart = i * zoneWidth;
            int zoneEnd = zoneStart + zoneWidth;

            if (isPointInRange(dropPoint, zoneStart, zoneEnd)) {
                return i;
            }
        }

        throw new RuntimeException("Registered Drop outside of DropZone");
    }

    private boolean isPointInRange(Point point, int startX, int endX) {
        return point.getX() > startX && point.getX() < endX;
    }
}
