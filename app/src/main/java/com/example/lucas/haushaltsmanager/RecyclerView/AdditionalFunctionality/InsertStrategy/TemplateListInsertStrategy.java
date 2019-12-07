package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem.TemplateItem;

import java.util.List;

/**
 * Neue Templates werden einfach an das Ende der Liste angehängt.
 * Die TemplateList hat somit keine eigenständige sortierung.
 */
public class TemplateListInsertStrategy implements InsertStrategy {
    @Override
    public int insert(IRecyclerItem item, List<IRecyclerItem> items) {
        assertCorrectInstance(item);

        items.add(item);

        return items.size() - 1;
    }

    private void assertCorrectInstance(IRecyclerItem item) {
        if (!(item instanceof TemplateItem)) {
            throw new IllegalArgumentException(String.format("TemplateListInsertStrategy requires TemplateItems. Class given: %s", item.getClass().getSimpleName()));
        }
    }
}
