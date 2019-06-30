package com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.Entities.Template;

public class TemplateItem implements IRecyclerItem {
    public static final int VIEW_TYPE = 8;

    private Template template;

    public TemplateItem(Template template) {
        this.template = template;
    }

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public Object getContent() {
        return template;
    }

    @Override
    public IParentRecyclerItem getParent() {
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return template.toString();
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
