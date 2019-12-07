package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.Template;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ExpenseItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem.TemplateItem;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TemplateListInsertStrategyTest {
    private TemplateListInsertStrategy insertStrategy = new TemplateListInsertStrategy();


    @Test
    public void cannotAddWrongClass() {
        try {
            insertStrategy.insert(new ExpenseItem(null, null), new ArrayList<IRecyclerItem>());

            Assert.fail("ExpenseItem could be registered in TemplateList");
        } catch (IllegalArgumentException e) {
            assertEquals("TemplateListInsertStrategy requires TemplateItems. Class given: ExpenseItem", e.getMessage());
        }
    }

    @Test
    public void newTemplateIsInsertedAtCorrectPosition() {
        List<IRecyclerItem> templateItems = createListWithItems(5);

        TemplateItem templateItem = new TemplateItem(getDummyTemplate());

        int insertIndex = insertStrategy.insert(templateItem, templateItems);

        assertEquals(5, insertIndex);
        assertEquals(templateItem, templateItems.get(insertIndex));
    }

    private List<IRecyclerItem> createListWithItems(int itemCount) {
        List<IRecyclerItem> items = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            items.add(new TemplateItem(getDummyTemplate()));
        }

        return items;
    }

    private Template getDummyTemplate() {
        return new Template(getDummyExpense());
    }

    private ExpenseObject getDummyExpense() {
        Currency currency = new Currency("Euro", "EUR", "€");

        return new ExpenseObject(
                "Ausgabe",
                new Price(100, false, currency),
                new Category("Kategorie", new Color(Color.WHITE), true, new ArrayList<Category>()),
                ExpensesDbHelper.INVALID_INDEX,
                currency
        );
    }
}
