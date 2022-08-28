package com.example.lucas.haushaltsmanager.ReportBuilder.Widgets;

import android.content.Context;
import android.view.View;

import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.ConfigurationObject;

public interface Widget {
    View getView();

    int getIcon();

    void updateView(ConfigurationObject configuration);

    Widget cloneWidget(Context context);
}
