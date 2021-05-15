package com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking;

import com.example.lucas.haushaltsmanager.Entities.Expense.IBooking;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

public interface IBookingItem extends IRecyclerItem {
    IBooking getContent();
}
