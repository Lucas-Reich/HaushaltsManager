package com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.Booking;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Template;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ExpenseItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.FileItem.FileItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ParentCategoryItem.ParentCategoryItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ParentExpenseItem.ParentExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.TemplateItem.TemplateItem;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Diese Klasse soll aus einer Liste von Objekten, eine Liste von RecyclerItems erstellen.
 */
public class ItemCreator {
    private static final String SORT_DESC = "DESC";
    private static final String SORT_ASC = "ASC";

    public static List<IRecyclerItem> createExpenseItems(List<ExpenseObject> bookings) {
        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }

        String order = SORT_DESC;

        ItemCreator itemCreator = new ItemCreator();

        itemCreator.sortBookingsByDate(bookings, order);

        DateItem currentDate = new DateItem(bookings.get(0).getDate());

        List<IRecyclerItem> recyclerItems = new ArrayList<>();
        for (Booking booking : bookings) {
            if (itemCreator.changeDate(booking, currentDate, order)) {
                currentDate = new DateItem(booking.getDate());

                recyclerItems.add(currentDate);
            }

            recyclerItems.add(itemCreator.createExpenseItem(booking, currentDate));
        }

        return recyclerItems;
    }

    public static List<IRecyclerItem> createCategoryItems(List<Category> categories) {
        if (categories.isEmpty()) {
            return new ArrayList<>();
        }

        List<IRecyclerItem> categoryItems = new ArrayList<>();
        for (Category category : categories) {
            categoryItems.add(new ParentCategoryItem(category));
        }

        return categoryItems;
    }

    public static List<IRecyclerItem> createTemplateItems(List<Template> templates) {
        if (templates.isEmpty()) {
            return new ArrayList<>();
        }

        List<IRecyclerItem> templateItems = new ArrayList<>();
        for (Template template : templates) {
            templateItems.add(new TemplateItem(template));
        }

        return templateItems;
    }

    public static List<IRecyclerItem> createFileItems(List<File> files) {
        if (files.isEmpty()) {
            return new ArrayList<>();
        }

        List<IRecyclerItem> fileItems = new ArrayList<>();
        for (File file : files) {
            fileItems.add(new FileItem(file));
        }

        return fileItems;
    }

    private boolean changeDate(Booking booking, DateItem currentDate, String order) {
        if (order.equals(SORT_ASC)) {
            return CalendarUtils.beforeByDate(booking.getDate(), currentDate.getContent());
        }

        return CalendarUtils.afterByDate(booking.getDate(), currentDate.getContent());
    }

    private IRecyclerItem createExpenseItem(Booking booking, DateItem currentDate) {
        if (booking instanceof ParentExpenseObject) {
            return new ParentExpenseItem((ParentExpenseObject) booking, currentDate);
        }

        if (booking instanceof ExpenseObject && ((ExpenseObject) booking).isParent()) {
            return new ParentExpenseItem(ParentExpenseObject.fromParentExpense((ExpenseObject) booking), currentDate);
        }

        return new ExpenseItem((ExpenseObject) booking, currentDate);
    }

    private void sortBookingsByDate(List<ExpenseObject> bookings, final String order) {
        Collections.sort(bookings, new Comparator<Booking>() {
            @Override
            public int compare(Booking booking1, Booking booking2) {
                if (order.equals("DESC")) {
                    return booking1.getDate().compareTo(booking2.getDate());
                }

                return booking2.getDate().compareTo(booking1.getDate());
            }
        });
    }
}
