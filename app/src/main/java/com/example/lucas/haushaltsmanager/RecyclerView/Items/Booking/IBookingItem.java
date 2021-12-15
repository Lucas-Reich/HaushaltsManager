package com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.ISelectableRecyclerItem;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

public interface IBookingItem extends ISelectableRecyclerItem {
    Booking getContent();
}
