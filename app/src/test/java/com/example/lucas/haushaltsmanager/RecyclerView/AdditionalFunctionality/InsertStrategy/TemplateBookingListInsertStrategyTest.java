package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import static org.junit.Assert.assertEquals;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem.TemplateItem;
import com.example.lucas.haushaltsmanager.entities.category.Category;
import com.example.lucas.haushaltsmanager.entities.Color;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.booking.ExpenseType;
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBookingWithoutCategory;
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBooking;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class TemplateBookingListInsertStrategyTest {
    private final TemplateListInsertStrategy insertStrategy = new TemplateListInsertStrategy();


    @Test
    public void cannotAddWrongClass() {
        try {
            insertStrategy.insert(new ExpenseItem(null, null), new ArrayList<>());

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
        return new TemplateBooking(getDummyExpense(), new Category("Category", Color.Companion.white(), ExpenseType.Companion.expense()));
    }

    private TemplateBookingWithoutCategory getDummyExpense() {
        return new TemplateBookingWithoutCategory(
                UUID.randomUUID(),
                "title",
                new Price(-105),
                Calendar.getInstance(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }
}
