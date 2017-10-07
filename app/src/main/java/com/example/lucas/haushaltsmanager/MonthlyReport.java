package com.example.lucas.haushaltsmanager;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class MonthlyReport {

    private String month;
    private List<ExpenseObject> expenses;
    private String currency;
    private String TAG = "MonthlyReport";

    MonthlyReport(String month, ArrayList<ExpenseObject> expenses, String currency) {

        this.month = month;
        this.expenses = expenses;
        this.currency = currency;
    }

    String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<ExpenseObject> getExpenses() {
        return expenses;
    }

    String getCurrency() {

        return currency;
    }

    int countBookings() {

        return expenses.size();
    }

    double countIncomingMoney() {

        double incomingMoney = 0;

        for (ExpenseObject expense : expenses) {

            if (!expense.getExpenditure()) {

                incomingMoney += expense.getPrice();
            }
        }

        return incomingMoney;
    }

    double countOutgoingMoney() {

        double outgoingMoney = 0;

        for (ExpenseObject expense : expenses) {

            if (expense.getExpenditure()) {

                outgoingMoney += expense.getPrice();
            }
        }

        return outgoingMoney;
    }

    double calcMonthlyTotal() {

        return (countIncomingMoney() - countOutgoingMoney());
    }

    String getMostStressedCategory() {

        MonthlyExpenses mostStressedCategory = new MonthlyExpenses("", 0);

        ArrayList<MonthlyExpenses> monthlyExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {

            String categoryName = expense.getCategory().getCategoryName();

            for (int i = 0; i < monthlyExpenses.size(); i++) {

                if (monthlyExpenses.get(i).getCategory().equals(categoryName)) {

                    double money = monthlyExpenses.get(i).getSpendMoney();
                    monthlyExpenses.get(i).setSpendMoney(money + expense.getPrice());
                } else {

                    monthlyExpenses.add(new MonthlyExpenses(categoryName, expense.getPrice()));
                }
            }
        }

        for (MonthlyExpenses monthlyExpense : monthlyExpenses) {

            if (monthlyExpense.getSpendMoney() > mostStressedCategory.getSpendMoney()) {

                mostStressedCategory = monthlyExpense;
            }
        }

        Log.d(TAG, "getMostStressedCategory: " + mostStressedCategory.getCategory());

        return mostStressedCategory.getCategory();
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
