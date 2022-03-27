package com.example.lucas.haushaltsmanager.Utils.ExpenseUtils;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ExpenseGrouper {
    public List<Booking> byYear(List<Booking> bookings, int year) {
        List<Booking> bookingForYear = byYears(bookings).get(year);

        if (null != bookingForYear) {
            return bookingForYear;
        }

        return new ArrayList<>();
    }

    public HashMap<Integer, List<Booking>> byYears(List<Booking> bookings) {
        HashMap<Integer, List<Booking>> groupedBookings = new HashMap<>();

        for (Booking booking : bookings) {
            int bookingYear = booking.getDate().get(Calendar.YEAR);

            if (!groupedBookings.containsKey(bookingYear))
                groupedBookings.put(bookingYear, new ArrayList<>());

            groupedBookings.get(bookingYear).add(booking);
        }

        return groupedBookings;
    } // IMPROVEMENT: Tests für Methode byYears() schreiben

    /**
     * Kindbuchungen werden von der Funktion nicht beachtet.
     */
    public HashMap<Category, List<Booking>> byCategory(List<Booking> expenses) {
        HashMap<Category, List<Booking>> groupedExpenses = new HashMap<>();

        CategoryDAO categoryRepository = AppDatabase.getDatabase(app.getContext()).categoryDAO();

        for (Booking expense : expenses) {
            Category expenseCategory = categoryRepository.get(expense.getCategoryId()); // TODO: Do differently

            if (!groupedExpenses.containsKey(expenseCategory))
                groupedExpenses.put(expenseCategory, new ArrayList<>());

            groupedExpenses.get(expenseCategory).add(expense);
        }

        return groupedExpenses;
    }

    public List<Booking> byMonth(List<Booking> bookings, int month, int year) {
        return byMonths(bookings, year).get(month);
    }

    public List<List<Booking>> byMonths(List<Booking> bookings, int year) {
        List<List<Booking>> groupedExpenses = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            groupedExpenses.add(new ArrayList<>());
        }

        for (Booking booking : bookings) {
            if (!isInYear(booking, year)) {
                continue;
            }

            groupedExpenses.get(extractMonth(booking)).add(booking);
        }

        return groupedExpenses;
    }

    private int extractMonth(Booking expense) {
        return expense.getDate().get(Calendar.MONTH);
    }

    private boolean isInYear(Booking expense, int year) {
        return expense.getDate().get(Calendar.YEAR) == year;
    }
}
