package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import com.example.lucas.haushaltsmanager.entities.Booking.ExpenseType;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Color;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.TemplateBooking;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem.TemplateItem;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TemplateBookingListInsertStrategyTest {
    private final TemplateListInsertStrategy insertStrategy = new TemplateListInsertStrategy();


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

    private TemplateBooking getDummyTemplate() {
        return new TemplateBooking(getDummyExpense());
    }

    private Booking getDummyExpense() {
        return new Booking(
                "Ausgabe",
                new Price(100, false),
                new Category("Kategorie", new Color(Color.WHITE), ExpenseType.Companion.expense()),
                UUID.randomUUID()
        );
    }
}
