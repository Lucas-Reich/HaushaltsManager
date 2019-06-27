package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.TemplateItem;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;

public class TemplateListSelectionRulesTest {
    private TemplateListSelectionRules selectionRules = new TemplateListSelectionRules();

    @Test
    public void noItemCanBeSelected() {
        IRecyclerItem item = new TemplateItem(null);

        boolean itemCanBeSelected = selectionRules.canBeSelected(item, new ArrayList<IRecyclerItem>());

        assertFalse(itemCanBeSelected);
    }
}
