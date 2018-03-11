package com.example.lucas.haushaltsmanager;

import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.List;

public class MonthlyReport {

    private String month;
    private List<ExpenseObject> expenses;
    private String currency;

    public MonthlyReport(@NonNull String month,@NonNull ArrayList<ExpenseObject> expenses,@NonNull String currency) {

        this.month = month;
        this.expenses = expenses;
        this.currency = currency;
    }

    /**
     * Methode um Buchungen hinzuzufügen
     *
     * @param expense Neue Buchungen
     */
    public void addExpense(ExpenseObject expense) {

        this.expenses.add(expense);
    }

    /**
     * Methode um den Monat zu erhalten
     *
     * @return Monat
     */
    @NonNull
    public String getMonth() {

        return month;
    }

    /**
     * Methode um den Monat zu setzen
     *
     * @param month Monat
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * Methode um alle Buchungen des Monats zu erhalten
     *
     * @return Liste mit allen Buchungen
     */
    @NonNull
    public List<ExpenseObject> getExpenses() {
        return expenses;
    }

    /**
     * Methode um die Hauptwährung zu erhalten
     *
     * @return Währung als String
     */
    @NonNull
    String getCurrency() {

        return currency;
    }

    /**
     * Methode um die Anzahl der Buchungen in diesem Monat zu bekommen.
     *
     * @return Anzahl an Buchungen
     */
    public int countBookings() {

        return expenses.size();
    }

    /**
     * Methode um die gesamten Einnahmen des Monats zu errechnen.
     *
     * @return Einnahmen des Monats
     */
    public double countIncomingMoney() {

        double incomingMoney = 0;

        for (ExpenseObject expense : expenses) {

            if (!expense.getExpenditure()) {

                incomingMoney += expense.getUnsignedPrice();
            }
        }

        return incomingMoney;
    }

    /**
     * Methode um die Gesamten Ausgaben des Monats zu errechnen.
     *
     * @return Ausgaben des Monats
     */
    public double countOutgoingMoney() {

        double outgoingMoney = 0;

        for (ExpenseObject expense : expenses) {

            if (expense.getExpenditure()) {

                outgoingMoney += expense.getUnsignedPrice();
            }
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
     * Methode um herauszufinden, in welches Kategorie die meisten Ausgaben gemacht wurden.
     *
     * @return Kategorie mit den meisten Ausgaben
     */
    public String getMostStressedCategory() {

        MonthlyExpenses mostStressedCategory = new MonthlyExpenses("Keine Ausgaben", 0);

        ArrayList<MonthlyExpenses> monthlyExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {

            int index = containsHelper(monthlyExpenses, expense.getCategory().getCategoryName());

            if (index == -1) {

                monthlyExpenses.add(new MonthlyExpenses(expense.getCategory().getCategoryName(), expense.getUnsignedPrice()));
            } else {

                MonthlyExpenses monthly = monthlyExpenses.get(index);

                if (expense.getExpenditure()) {

                    monthly.spendMoney += expense.getUnsignedPrice();
                } else {

                    monthly.spendMoney -= expense.getUnsignedPrice();
                }
                monthlyExpenses.set(index, monthly);
            }
        }

        for (MonthlyExpenses monthlyExpense : monthlyExpenses) {

            if (monthlyExpense.getSpendMoney() >= mostStressedCategory.getSpendMoney()) {

                mostStressedCategory = monthlyExpense;
            }
        }

        return mostStressedCategory.getCategory();
    }

    /**
     * @param monthlyExpenses ArrayList of monthly expenses
     * @param category        Category getName which has to be found
     * @return index of the CATEGORY getName if found or -1 if not in List
     */
    private int containsHelper(ArrayList<MonthlyExpenses> monthlyExpenses, String category) {

        for (MonthlyExpenses monthlyExpense : monthlyExpenses) {

            if (monthlyExpense.getCategory().equals(category)) {

                return monthlyExpenses.indexOf(monthlyExpense);
            }
        }
        return -1;
    }

    private class MonthlyExpenses {

        private String category;
        private double spendMoney;

        MonthlyExpenses(String month, double spendMoney) {

            this.category = month;
            this.spendMoney = spendMoney;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getSpendMoney() {
            return spendMoney;
        }

        public void setSpendMoney(double spendMoney) {
            this.spendMoney = spendMoney;
        }
    }
}
