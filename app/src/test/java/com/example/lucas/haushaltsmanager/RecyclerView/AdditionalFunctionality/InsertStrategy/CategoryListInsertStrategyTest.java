package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ChildCategoryItem.ChildCategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ParentCategoryItem.ParentCategoryItem;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CategoryListInsertStrategyTest {
    private CategoryListInsertStrategy insertStrategy = new CategoryListInsertStrategy();

    @Test
    public void newParentIsInsertedAtCorrectPosition() {
        List<IRecyclerItem> categoryItems = createListWithItems(10);

        ParentCategoryItem parentCategoryItem = new ParentCategoryItem(getDummyCategory());

        int insertIndex = insertStrategy.insert(parentCategoryItem, categoryItems);

        assertEquals(categoryItems.get(insertIndex), parentCategoryItem);
    }

    @Test
    public void cannotAddWrongClassAsParent() {
        try {
            insertStrategy.insert(new ChildCategoryItem(null, null), new ArrayList<IRecyclerItem>());

            Assert.fail("ChildCategoryItem could be registered as Parent");
        } catch (IllegalArgumentException e) {
            assertEquals("CategoryListInsertStrategy requires ParentCategoryItems as parents. Class given: ChildCategoryItem", e.getMessage());
        }
    }

    @Test
    public void addChildToExistingAndExpandedParent() {
        List<IRecyclerItem> items = new ArrayList<>();

        ParentCategoryItem parentCategory = new ParentCategoryItem(getDummyCategory());
        parentCategory.setExpanded(true);
        items.add(parentCategory);


        ChildCategoryItem childCategory = new ChildCategoryItem(getDummyCategory(), parentCategory);
        int insertIndex = insertStrategy.insert(childCategory, items);


        assertEquals(2, items.size());
        assertEquals(insertIndex, items.indexOf(childCategory));
        assertEquals(childCategory, items.get(1));
    }

    @Test
    public void addChildToExistingAndNotExpandedParent() {
        List<IRecyclerItem> items = new ArrayList<>();

        ParentCategoryItem parentCategory = new ParentCategoryItem(getDummyCategory());
        parentCategory.setExpanded(false);
        items.add(parentCategory);


        ChildCategoryItem childCategory = new ChildCategoryItem(getDummyCategory(), parentCategory);
        int insertIndex = insertStrategy.insert(childCategory, items);


        assertEquals(1, items.size());
        assertEquals(insertIndex, InsertStrategy.INVALID_INDEX);
        assertTrue(((ParentCategoryItem) items.get(0)).getChildren().contains(childCategory));
    }

    @Test
    public void cannotAddChildToNotExistingParentAndInvalidIndexShouldBeReturned() {
        ChildCategoryItem childItem = new ChildCategoryItem(getDummyCategory(), new ParentCategoryItem(getDummyCategory()));

        List<IRecyclerItem> items = new ArrayList<>();
        int insertIndex = insertStrategy.insert(childItem, items);

        assertEquals(0, items.size());
        assertEquals(InsertStrategy.INVALID_INDEX, insertIndex);
    }

    @Test
    public void cannotAddChildToNotExistingParentAndInvalidIndexShouldBeReturned2() {
        ParentCategoryItem parent = new ParentCategoryItem(getDummyCategory());
        parent.setExpanded(true);

        ChildCategoryItem childItem = new ChildCategoryItem(getDummyCategory(), parent);

        List<IRecyclerItem> items = new ArrayList<>();
        int insertIndex = insertStrategy.insert(childItem, items);

        assertEquals(0, items.size());
        assertEquals(InsertStrategy.INVALID_INDEX, insertIndex);
    }

    private List<IRecyclerItem> createListWithItems(int itemCount) {
        List<IRecyclerItem> items = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            items.add(new ParentCategoryItem(getDummyCategory()));
        }

        return items;
    }

    private Category getDummyCategory() {
        return new Category(
                "Kategorie",
                new Color(Color.WHITE),
                ExpenseType.expense(),
                new ArrayList<Category>()
        );
    }
}
