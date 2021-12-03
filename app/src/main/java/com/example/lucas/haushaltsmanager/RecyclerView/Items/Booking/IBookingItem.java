package com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

public interface IBookingItem extends IRecyclerItem {
    Booking getContent();
}
