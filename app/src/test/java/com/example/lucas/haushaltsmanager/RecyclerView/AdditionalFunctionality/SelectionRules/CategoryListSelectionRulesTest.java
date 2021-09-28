package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.CategoryItem.CategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.booking.ExpenseType;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Color;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CategoryListSelectionRulesTest {
    private CategoryListSelectionRules selectionRules;

    @Before
    public void setUp() {
        selectionRules = new CategoryListSelectionRules();
    }

    @Test
    public void parentCategoryItemCannotBeSelected() {
        CategoryItem categoryItem = new CategoryItem(getDummyCategory());

        boolean parentCanBeSelected = selectionRules.canBeSelected(categoryItem, new ArrayList<>());

        assertFalse(parentCanBeSelected);
    }

    @Test
    public void alreadySelectedItemCannotBeSelected() {
        List<IRecyclerItem> selectedItems = new ArrayList<>();

        CategoryItem selectedItem = new CategoryItem(getDummyCategory());
        selectedItems.add(selectedItem);

        boolean canBeSelected = selectionRules.canBeSelected(selectedItem, selectedItems);

        assertFalse(canBeSelected);
    }

    @Test
    public void childCategoryItemCanBeSelected() {
        CategoryItem childCategoryItem = new CategoryItem(getDummyCategory());

        boolean childCanBeSelected = selectionRules.canBeSelected(childCategoryItem, new ArrayList<>());

        assertTrue(childCanBeSelected);
    }

    private Category getDummyCategory() {
        return new Category(
                "Kategorie",
                new Color(Color.BLACK),
                ExpenseType.Companion.expense()
        );
    }
}
