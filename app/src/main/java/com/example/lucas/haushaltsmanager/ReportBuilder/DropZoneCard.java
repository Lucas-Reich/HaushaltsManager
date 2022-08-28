package com.example.lucas.haushaltsmanager.ReportBuilder;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.ConfigurationObject;
import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.Widget;

import java.util.HashMap;
import java.util.Map;

public class DropZoneCard {
    private int dropZoneCount;
    private final CardView cardView;
    private final HashMap<Integer, Widget> widgetMap;
    private ConfigurationObject configuration; // TODO: I could pre-populate the configuration here to not do it in the Widget implementation

    public DropZoneCard(@NonNull CardView cardView) {
        this.cardView = cardView;
        this.dropZoneCount = 3;
        widgetMap = new HashMap<>();
        configuration = new ConfigurationObject();
    }

    public void addDroppedView(Widget widget, Point point) {
        int dropZoneId = translateCoordinatesToDropzone(point);

        removeExistingViewFromZone(dropZoneId);

        widget.updateView(configuration);
        View widgetView = widget.getView();

        configureChildView(widgetView, dropZoneId);

        addView(widgetView, dropZoneId);
        widgetMap.put(dropZoneId, widget);
    }

    public void setDropZoneCount(int dropZoneCount) {
        this.dropZoneCount = dropZoneCount;

        cardView.removeAllViews();
        cardView.invalidate();
    }

    public void updateConfiguration(ConfigurationObject configuration) {
        this.configuration = configuration;

        for (Map.Entry<Integer, Widget> entry : widgetMap.entrySet()) {
            Widget value = entry.getValue();
            value.updateView(this.configuration);
        }
    }

    private void addView(View view, int zoneId) {
        view.setId(zoneId);
        cardView.addView(view);

        cardView.invalidate();
    }

    private void configureChildView(View view, int dropZoneId) {
        view.setOnTouchListener((v, event) -> ((View) v.getParent()).onTouchEvent(event)); // Propagates child click to parent

        addLayoutConstraintsToChild(view, dropZoneId);
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
        widgetMap.remove(dropZoneId);
    }

    private int translateCoordinatesToDropzone(Point dropPoint) {
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
