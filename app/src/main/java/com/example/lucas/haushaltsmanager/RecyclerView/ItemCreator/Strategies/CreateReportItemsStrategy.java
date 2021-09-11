package com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.Strategies;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Booking.IBooking;
import com.example.lucas.haushaltsmanager.Entities.Report.Report;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.ReportItem.ReportItem;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseGrouper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateReportItemsStrategy implements RecyclerItemCreatorStrategyInterface<IBooking> {
    private final ExpenseGrouper expenseGrouper;

    public CreateReportItemsStrategy() {
        expenseGrouper = new ExpenseGrouper();
    }

    public List<IRecyclerItem> create(List<IBooking> bookings) {
        if (bookings.isEmpty()) {
            return new ArrayList<>();
        }

        List<IRecyclerItem> reportItems = new ArrayList<>();

        for (int i = getCurrentMonth(); i >= 1; i--) {
            reportItems.add(new ReportItem(new Report(
                    getStringifiedMonth(i - 1),
                    groupExpensesByMonth(i - 1, bookings)
            )));
        }

        return reportItems;
    }

    private List<IBooking> groupExpensesByMonth(int month, List<IBooking> bookings) {
        return expenseGrouper.byMonth(
                bookings,
                month,
                getCurrentYear()
        );
    }

    private int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private String getStringifiedMonth(int month) {
        String[] months = app.getContext().getResources().getStringArray(R.array.months);

        return months[month];
    }

    private int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }
}

