package com.example.lucas.haushaltsmanager;

import java.util.ArrayList;
import java.util.List;

class MonthlyReport {

    private String month;
    private List<ExpenseObject> expenses;
    private String account;
    private String currency;

    public MonthlyReport(String month, ArrayList<ExpenseObject> expenses, String account, String currency) {

        this.month = month;
        this.expenses = expenses;
        this.account = account;
        this.currency = currency;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<ExpenseObject> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseObject> expenses) {
        this.expenses = expenses;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setCurrency(String currency) {

        this.currency = currency;
    }

    public String getCurrency() {

        return currency;
    }

    public int countBookings() {

        return expenses.size();
    }

    public  double countIncomingMoney() {

        double incomingMoney = 0;

        for (ExpenseObject expense : expenses) {

            double temp = expense.getPrice();

            if (temp >= 0) {

                incomingMoney += temp;
            }
        }

        return incomingMoney;
    }

    public double countOutgoingMoney() {

        double outgoingMoney = 0;

        for (ExpenseObject expense : expenses) {

            double temp = expense.getPrice();

            if (temp <= 0) {

                outgoingMoney += temp;
            }
        }

        return outgoingMoney;
    }

    public double calcMonthlyTotal() {

        return (countIncomingMoney() + countOutgoingMoney());
    }

    public String getMostStressedCategory() {

        MonthlyExpenses mostStressedCategory = new MonthlyExpenses("", 0);

        ArrayList<MonthlyExpenses> monthlyExpenses = new ArrayList<>();

        for(ExpenseObject expense : expenses) {

            String categoryName = expense.getCategory().getCategoryName();

            for (int i = 0; i < monthlyExpenses.size(); i++) {

                if (monthlyExpenses.get(i).getCategory() == categoryName) {

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
