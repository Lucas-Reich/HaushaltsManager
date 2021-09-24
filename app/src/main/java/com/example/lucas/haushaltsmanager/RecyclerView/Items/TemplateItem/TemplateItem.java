package com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IParentRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.TemplateBooking;

public class TemplateItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 8;

    private final TemplateBooking templateBooking;

    public TemplateItem(TemplateBooking templateBooking) {
        this.templateBooking = templateBooking;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public TemplateBooking getContent() {
        return templateBooking;
    }

    @Override
    public IParentRecyclerItem getParent() {
        return null;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @NonNull
    @Override
    public String toString() {
        return templateBooking.toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof TemplateItem)) {
            return false;
        }

        TemplateItem other = (TemplateItem) obj;

        return other.getContent().equals(getContent());
    }
}
