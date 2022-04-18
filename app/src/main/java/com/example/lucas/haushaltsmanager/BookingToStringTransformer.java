package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.category.Category;
import com.example.lucas.haushaltsmanager.entities.Currency;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

class BookingToStringTransformer {
    public String transform(Booking booking, Category category, Account account) {
        StringBuilder bookingString = new StringBuilder();

        bookingString.append(booking.getUnsignedPrice()).append(",");
        bookingString.append(booking.isExpenditure()).append(",");
        bookingString.append(booking.getTitle()).append(",");
        bookingString.append(booking.getDateString()).append(",");
        bookingString.append(getDefaultCurrency()).append(",");

        bookingString.append(category != null ? category.getName() : "").append(",");

        bookingString.append(account != null ? account.getName() : "").append("\r\n");

        return bookingString.toString();
    }

    private String getDefaultCurrency() {
        return new Currency().getName();
    }
}
