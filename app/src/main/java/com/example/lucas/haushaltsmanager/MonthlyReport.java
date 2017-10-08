package com.example.lucas.haushaltsmanager;

import java.util.ArrayList;
import java.util.List;

class MonthlyReport {

    private String month;
    private List<ExpenseObject> expenses;
    private String currency;

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

        MonthlyExpenses mostStressedCategory = new MonthlyExpenses("Keine Ausgaben", 0);

        ArrayList<MonthlyExpenses> monthlyExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {

            int index = containsHelper(monthlyExpenses, expense.getCategory().getCategoryName());

            if (index == -1) {

                monthlyExpenses.add(new MonthlyExpenses(expense.getCategory().getCategoryName(), expense.getPrice()));
            } else {

                MonthlyExpenses monthly = monthlyExpenses.get(index);

                if (expense.getExpenditure()){

                    monthly.spendMoney += expense.getPrice();
                } else {

                    monthly.spendMoney -= expense.getPrice();
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
     *
     * @param monthlyExpenses ArrayList of monthly expenses
     * @param category Category name which has to be found
     *
     * @return index of the category name if found or -1 if not in List
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
