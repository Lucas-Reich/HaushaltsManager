package com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.Widget;

public class CardViewContent {
    private final int icon;
    private final Widget widget;

    public CardViewContent(@DrawableRes int icon, Widget widget) {
        this.icon = icon;
        this.widget = widget;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof CardViewContent)) {
            return false;
        }

        CardViewContent other = (CardViewContent) obj;

        return other.icon == this.icon
                && other.widget == this.widget;
    }

    public Widget getWidget() {
        return widget;
    }

    public int getIcon() {
        return icon;
    }
}
