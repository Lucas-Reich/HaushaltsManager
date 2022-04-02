package com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator;

import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateBookingItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateCategoryItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateFileItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateReportItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies.CreateTemplateItemsStrategy;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.IBooking;
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBooking;

import java.io.File;
import java.util.List;

public class RecyclerItemFactory {
    public static List<IRecyclerItem> createBookingsItems(List<IBooking> bookings) {
        return new CreateBookingItemsStrategy().create(bookings);
    }

    public static List<IRecyclerItem> createReportItems(List<Booking> bookings) {
        return new CreateReportItemsStrategy().create(bookings);
    }

    public static List<IRecyclerItem> createCategoryItems(List<Category> categories) {
        return new CreateCategoryItemsStrategy().create(categories);
    }

    public static List<IRecyclerItem> createTemplateBookingItems(List<TemplateBooking> templateBookings) {
        return new CreateTemplateItemsStrategy().create(templateBookings);
    }

    public static List<IRecyclerItem> createFileItems(List<File> files) {
        return new CreateFileItemsStrategy().create(files);
    }
}
