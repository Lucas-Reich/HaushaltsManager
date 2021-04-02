package com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking;

import com.example.lucas.haushaltsmanager.Entities.Expense.Booking;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;

public interface IBookingItem extends IRecyclerItem {
    Booking getContent();
}
