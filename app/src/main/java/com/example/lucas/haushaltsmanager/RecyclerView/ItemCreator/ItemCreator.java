package com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.TemplateBooking;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateBookingItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateCategoryItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateFileItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateReportItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateTemplateItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

import java.io.File;
import java.util.List;

public class ItemCreator {
    public static List<IRecyclerItem> createExpenseItems(List<ExpenseObject> bookings) {
        return new CreateBookingItemsStrategy().create(bookings);
    }

    public static List<IRecyclerItem> createCategoryItems(List<Category> categories) {
        return new CreateCategoryItemsStrategy().create(categories);
    }

    public static List<IRecyclerItem> createTemplateItems(List<TemplateBooking> templateBookings) {
        return new CreateTemplateItemsStrategy().create(templateBookings);
    }

    public static List<IRecyclerItem> createFileItems(List<File> files) {
        return new CreateFileItemsStrategy().create(files);
    }

    public static List<IRecyclerItem> createReportItems(List<ExpenseObject> flatBookingList) {
        return new CreateReportItemsStrategy().create(flatBookingList);
    }
}
