package com.example.lucas.haushaltsmanager.Utils.ExpenseUtils;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.IBooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ExpenseGrouper {
    /**
     * Kindbuchungen werden von der Funktion nicht beachtet.
     */
    // TODO: Kann ich die Funktion durch byYears() ersetzen?
    public List<IBooking> byYearNew(List<IBooking> expenses, int year) {
        List<IBooking> groupedExpenses = new ArrayList<>();

        for (IBooking expense : expenses) {
            if (isInYear(expense, year)) {
                groupedExpenses.add(expense);
            }
        }

        return groupedExpenses;
    }

    public HashMap<Integer, List<Booking>> byYears(List<Booking> expenses) {
        HashMap<Integer, List<Booking>> groupedExpenses = new HashMap<>();

        for (Booking expense : expenses) {
            int expenseYear = expense.getDate().get(Calendar.YEAR);

            if (!groupedExpenses.containsKey(expenseYear))
                groupedExpenses.put(expenseYear, new ArrayList<>());

            groupedExpenses.get(expenseYear).add(expense);
        }

        return groupedExpenses;
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

    public List<IBooking> byMonth(List<IBooking> bookings, int month, int year) {
        return byMonths(bookings, year).get(month);
    }

    public List<List<IBooking>> byMonths(List<IBooking> bookings, int year) {
        List<List<IBooking>> groupedExpenses = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            groupedExpenses.add(new ArrayList<>());
        }

        for (IBooking booking : bookings) {
            if (!isInYear(booking, year)) {
                continue;
            }

            groupedExpenses.get(extractMonth(booking)).add(booking);
        }

        return groupedExpenses;
    }

    private int extractMonth(IBooking expense) {
        return expense.getDate().get(Calendar.MONTH);
    }

    private boolean isInYear(IBooking expense, int year) {
        return expense.getDate().get(Calendar.YEAR) == year;
    }
}
