package com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies;

import com.example.lucas.haushaltsmanager.Entities.Template;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem.TemplateItem;

import java.util.ArrayList;
import java.util.List;

public class CreateTemplateItemsStrategy implements RecyclerItemCreatorStrategyInterface<Template> {
    @Override
    public List<IRecyclerItem> create(List<Template> templates) {
        if (templates.isEmpty()) {
            return new ArrayList<>();
        }

        List<IRecyclerItem> templateItems = new ArrayList<>();
        for (Template template : templates) {
            templateItems.add(new TemplateItem(template));
        }

        return templateItems;
    }
}
