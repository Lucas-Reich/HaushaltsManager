package com.example.lucas.haushaltsmanager.ReportBuilder;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.Widget;

public class DropZoneCard {
    private int dropZoneCount;
    private final CardView cardView;

    public DropZoneCard(@NonNull CardView cardView) {
        this.cardView = cardView;
        this.dropZoneCount = 3;
    }

    public void addDroppedView(Widget widget, float x, float y) {
        // TODO: Disable on click behaviour of widgets in Builder
        int dropZoneId = translateCoordsToDropzone(new Point(x, y));

        removeExistingViewFromZone(dropZoneId);

        View widgetView = widget.getView();

        addLayoutConstraintsToChild(widgetView, dropZoneId);

        addView(widgetView, dropZoneId);
    }

    public void setDropZoneCount(int dropZoneCount) {
        this.dropZoneCount = dropZoneCount;

        cardView.removeAllViews();
        cardView.invalidate();
    }

    private void addView(View view, int zoneId) {
        view.setId(zoneId);
        cardView.addView(view);

        cardView.invalidate();
    }

    private void addLayoutConstraintsToChild(View widgetView, int dropZoneId) {
        widgetView.setLayoutParams(new LinearLayout.LayoutParams(
                cardView.getWidth() / dropZoneCount,
                cardView.getHeight()
        ));

        int widgetWidth = cardView.getWidth() / dropZoneCount;

        widgetView.setX(dropZoneId * widgetWidth);
    }

    private void removeExistingViewFromZone(int dropZoneId) {
        View oldView = cardView.findViewById(dropZoneId);

        if (null == oldView) {
            return;
        }

        cardView.removeView(oldView);
    }

    private int translateCoordsToDropzone(Point dropPoint) {
        int zoneWidth = this.cardView.getWidth() / dropZoneCount;

        for (int zone = 0; zone < dropZoneCount; zone++) {
            int zoneStart = zone * zoneWidth;
            int zoneEnd = zoneStart + zoneWidth;

            if (isPointInRange(dropPoint, zoneStart, zoneEnd)) {
                return zone;
            }
        }

        throw new RuntimeException("Registered Drop outside of DropZone");
    }

    private boolean isPointInRange(Point point, int startX, int endX) {
        return point.getX() > startX && point.getX() < endX;
    }
}
