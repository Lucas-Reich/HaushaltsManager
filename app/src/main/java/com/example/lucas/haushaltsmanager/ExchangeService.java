package com.example.lucas.haushaltsmanager;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExchangeService extends IntentService {

    ArrayList<ExpenseObject> expenses;
    ExpensesDataSource database;
    SharedPreferences preferences;



    public ExchangeService() {

        super("ExchangeService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        database = new ExpensesDataSource(ExchangeService.this);
        preferences = getSharedPreferences("UserSettings", 0);


        long baseCurIndex = preferences.getLong("BaseCurrency", 0);

        database.open();
        expenses = database.convertExpenses();

        for (ExpenseObject expense : expenses) {

            long expenseCurIndex = expense.getExpenseCurrency().getIndex();

            HashMap<Double, String> exchangeRate = database.getExtendedExchangeRate(expenseCurIndex, baseCurIndex, expense.getDBDateTime());
            Map.Entry<Double, String> entry = exchangeRate.entrySet().iterator().next();

            //if the exchange rate fetch date matches the expense date then calc the price
            if (entry.getValue().equals(expense.getDate())) {

                database.updateExpenseExchangeRate(expense.getIndex(), entry.getKey());
                database.deleteConvertExpense(expense.getIndex());
            }
        }
    }
}
