package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.SelectionRules;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.AdItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ChildCategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.ParentCategoryItem;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CategoryListSelectionRulesTest {
    private CategoryListSelectionRules selectionRules;

    @Before
    public void setUp() {
        selectionRules = new CategoryListSelectionRules();
    }

    @Test
    public void parentCategoryItemCannotBeSelected() {
        ParentCategoryItem parentCategoryItem = new ParentCategoryItem(getDummyCategory());

        boolean parentCanBeSelected = selectionRules.canBeSelected(parentCategoryItem, new ArrayList<IRecyclerItem>());

        assertFalse(parentCanBeSelected);
    }

    @Test
    public void adItemCannotBeSelected() {
        AdItem adItem = new AdItem();

        boolean parentCanBeSelected = selectionRules.canBeSelected(adItem, new ArrayList<IRecyclerItem>());

        assertFalse(parentCanBeSelected);
    }

    @Test
    public void alreadySelectedItemCannotBeSelected() {
        List<IRecyclerItem> selectedItems = new ArrayList<>();

        ChildCategoryItem selectedItem = new ChildCategoryItem(getDummyCategory(), getDummyParentCategoryItem());
        selectedItems.add(selectedItem);

        boolean canBeSelected = selectionRules.canBeSelected(selectedItem, selectedItems);

        assertFalse(canBeSelected);
    }

    @Test
    public void childCategoryItemCanBeSelected() {
        ChildCategoryItem childCategoryItem = new ChildCategoryItem(getDummyCategory(), null);

        boolean childCanBeSelected = selectionRules.canBeSelected(childCategoryItem, new ArrayList<IRecyclerItem>());

        assertTrue(childCanBeSelected);
    }

    @Test
    public void childCategoryItemWithADifferentParentCannotBeSelected() {
        List<IRecyclerItem> selectedItems = new ArrayList<>();
        selectedItems.add(new ChildCategoryItem(getDummyCategory(), getDummyParentCategoryItem()));

        ChildCategoryItem child = new ChildCategoryItem(getDummyCategory(), getDummyParentCategoryItem());
        boolean canBeSelected = selectionRules.canBeSelected(child, selectedItems);

        assertFalse(canBeSelected);
    }

    private ParentCategoryItem getDummyParentCategoryItem() {
        return new ParentCategoryItem(getDummyCategory());
    }

    private Category getDummyCategory() {
        return new Category(
                "Kategorie",
                new Color(Color.BLACK),
                true,
                new ArrayList<Category>()
        );
    }
}
