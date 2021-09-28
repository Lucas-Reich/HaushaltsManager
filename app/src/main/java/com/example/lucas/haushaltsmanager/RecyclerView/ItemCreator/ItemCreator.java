package com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator;

import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateBookingItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateCategoryItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateFileItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateReportItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateTemplateItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.booking.IBooking;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBooking;

import java.io.File;
import java.util.List;

public class ItemCreator {
    public static List<IRecyclerItem> createBookingItems(List<IBooking> bookings) {
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

    public static List<IRecyclerItem> createReportItems(List<IBooking> bookings) {
        return new CreateReportItemsStrategy().create(bookings);
    }
}
