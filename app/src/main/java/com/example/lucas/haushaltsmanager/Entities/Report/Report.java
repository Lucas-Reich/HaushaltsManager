package com.example.lucas.haushaltsmanager.Entities.Report;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Booking.ExpenseType;
import com.example.lucas.haushaltsmanager.Entities.Booking.IBooking;
import com.example.lucas.haushaltsmanager.Entities.Booking.ParentBooking;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report implements ReportInterface {
    private String mCardTitle;
    private final List<IBooking> bookings;

    public Report(
            @NonNull String cardTitle,
            @NonNull List<IBooking> bookings
    ) {
        setCardTitle(cardTitle);
        this.bookings = bookings;
    }

    @NonNull
    public List<IBooking> getExpenses() {
        return bookings;
    }

    @Override
    public double getTotal() {

        return getIncoming() + getOutgoing();
    }

    @Override
    public double getIncoming() {
        ExpenseSum expenseSum = new ExpenseSum();

        return expenseSum.byExpenditureTypeNew(false, bookings);
    }

    @Override
    public double getOutgoing() {
        ExpenseSum expenseSum = new ExpenseSum();

        return expenseSum.byExpenditureTypeNew(true, bookings);
    }

    @Override
    public int getBookingCount() {
        return getExpensesCount(bookings);
    }

    @Override
    public Category getMostStressedCategory() {
        HashMap<Category, Double> categories = sumExpensesByCategory();

        if (categories.isEmpty()) {
            return getPlaceholderCategory(R.string.no_expenses);
        }

        return getMaxEntry(categories).getKey();
    }

    @Override
    public String getCardTitle() {
        return mCardTitle;
    }

    @Override
    public void setCardTitle(String title) {
        mCardTitle = title;
    }

    public List<IBooking> getBookings() {
        return bookings;
    }

    private String getResourceString(@StringRes int stringRes) {
        return app.getContext().getString(stringRes);
    }

    private Category getPlaceholderCategory(@StringRes int titleRes) {
        return new Category(
                getResourceString(titleRes),
                Color.white(),
                ExpenseType.deposit()
        );
    }

    private HashMap<Category, Double> sumExpensesByCategory() {
        ExpenseSum expenseSum = new ExpenseSum();

        return expenseSum.byCategoryNew(bookings);
    }

    private Map.Entry<Category, Double> getMaxEntry(HashMap<Category, Double> categoryDoubleHashMap) {
        Map.Entry<Category, Double> minCategory = null;

        for (Map.Entry<Category, Double> entry : categoryDoubleHashMap.entrySet()) {
            if (null == minCategory || entry.getValue() < minCategory.getValue()) {
                minCategory = entry;
            }
        }

        return minCategory;
    }

    private int getExpensesCount(List<IBooking> bookings) {
        int count = 0;

        for (IBooking booking : bookings) {
            if (booking instanceof ParentBooking) {
                count += ((ParentBooking) booking).getChildren().size();
            } else {
                count += 1;
            }
        }

        return count;
    }
}
