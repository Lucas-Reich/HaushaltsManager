package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthlyReport {

    private String mMonth;
    private List<ExpenseObject> mExpenses;
    private String mCurrency;
    private Context mContext;

    public MonthlyReport(@NonNull String month, @NonNull ArrayList<ExpenseObject> expense, @NonNull String currency, Context context) {

        mMonth = month;
        mExpenses = expense;
        mCurrency = currency;
        mContext = context;
    }

    /**
     * Methode um Buchungen hinzuzufügen
     *
     * @param expense Neue Buchungen
     */
    public void addExpense(ExpenseObject expense) {
        mExpenses.add(expense);
    }

    /**
     * Methode um den Monat zu erhalten
     *
     * @return Monat
     */
    @NonNull
    public String getMonth() {
        return mMonth;
    }

    /**
     * Methode um den Monat zu setzen
     *
     * @param month Monat
     */
    public void setMonth(String month) {
        mMonth = month;
    }

    /**
     * Methode um alle Buchungen des Monats zu erhalten
     *
     * @return Liste mit allen Buchungen
     */
    @NonNull
    public List<ExpenseObject> getExpenses() {
        return mExpenses;
    }

    /**
     * Methode um die Hauptwährung zu erhalten
     *
     * @return Währung als String
     */
    @NonNull
    String getCurrency() {
        return mCurrency;
    }

    /**
     * Methode um die Anzahl der Buchungen in diesem Monat zu bekommen.
     *
     * @return Anzahl an Buchungen
     */
    public int countBookings() {
        return mExpenses.size();
    }

    /**
     * Methode um die gesamten Einnahmen des Monats zu errechnen.
     *
     * @return Einnahmen des Monats
     */
    public double countIncomingMoney() {

        double incomingMoney = 0;
        for (ExpenseObject expense : mExpenses) {

            if (!expense.isExpenditure())
                incomingMoney += expense.getUnsignedPrice();
        }

        return incomingMoney;
    }

    /**
     * Methode um die gesamten Ausgaben des Monats zu errechnen.
     *
     * @return Monatliche Ausgaben
     */
    public double countOutgoingMoney() {

        double outgoingMoney = 0;
        for (ExpenseObject expense : mExpenses) {

            if (expense.isExpenditure())
                outgoingMoney += expense.getUnsignedPrice();
        }

        return outgoingMoney;
    }

    /**
     * Methode die die Ausgaben und Einnahmen verrechnet und das Total zurückgibt
     *
     * @return Ausgaben total
     */
    public double calcMonthlyTotal() {
        return (countIncomingMoney() - countOutgoingMoney());
    }

    /**
     * Methode, welche die Kategorie mit den meisten Ausgaben des Monats zurückgibt.
     *
     * @return Category
     */
    public Category getMostStressedCategory() {
        HashMap<Category, Double> categories = sumExpensesByCategory();

        if (categories.isEmpty())
            return new Category(mContext.getResources().getString(R.string.no_expenses), "#FFFFFF", false, new ArrayList<Category>());

        Map.Entry<Category, Double> minCategory = null;
        for (Map.Entry<Category, Double> entry : categories.entrySet()) {
            if (null == minCategory || entry.getValue() < minCategory.getValue()) {
                minCategory = entry;
            }
        }

        return minCategory.getKey();
    }

    /**
     * Methode um die Ausgaben eines Monats nach der Kategorie aufzusummieren.
     *
     * @return HashMap
     */
    private HashMap<Category, Double> sumExpensesByCategory() {

        HashMap<Category, Double> categories = new HashMap<>();
        for (ExpenseObject expense : mExpenses) {
            Category expenseCategory = expense.getCategory();

            if (!categories.containsKey(expenseCategory))
                categories.put(expenseCategory, 0d);

            categories.put(expenseCategory, categories.get(expenseCategory) + expense.getSignedPrice());
        }

        return categories;
    }
}
