package com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies;

import com.example.lucas.haushaltsmanager.Entities.Expense.IBooking;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentBooking;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.ParentBookingItem.ParentBookingItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.DateItem.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSorter;

import java.util.ArrayList;
import java.util.List;

public class CreateBookingItemsStrategy implements RecyclerItemCreatorStrategyInterface<ExpenseObject> {
    private static final String SORT_DESC = "DESC";
    private static final String SORT_ASC = "ASC";

    @Override
    public List<IRecyclerItem> create(List<ExpenseObject> bookings) {
        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }

        new ExpenseSorter().byDate(
                bookings,
                ExpenseSorter.SORT_DESC
        );

        DateItem currentDate = new DateItem(bookings.get(0).getDate());

        List<IRecyclerItem> recyclerItems = new ArrayList<>();
        for (IBooking booking : bookings) {
            if (changeDate(booking, currentDate, SORT_DESC)) {
                currentDate = new DateItem(booking.getDate());

                recyclerItems.add(currentDate);
            }

            recyclerItems.add(createExpenseItem(booking, currentDate));
        }

        return recyclerItems;
    }

    private boolean changeDate(IBooking booking, DateItem currentDate, String order) {
        if (order.equals(SORT_ASC)) {
            return CalendarUtils.beforeByDate(booking.getDate(), currentDate.getContent());
        }

        return CalendarUtils.afterByDate(booking.getDate(), currentDate.getContent());
    }

    private IRecyclerItem createExpenseItem(IBooking booking, DateItem currentDate) {
        if (booking instanceof ParentBooking) {
            return new ParentBookingItem((ParentBooking) booking, currentDate);
        }

        if (booking instanceof ExpenseObject && ((ExpenseObject) booking).isParent()) {
            return new ParentBookingItem(new ParentBooking(
                    booking.getId(),
                    booking.getTitle(),
                    booking.getDate(),
                    ((ExpenseObject) booking).getChildren()
            ), currentDate);
        }

        return new ExpenseItem((ExpenseObject) booking, currentDate);
    }
}
