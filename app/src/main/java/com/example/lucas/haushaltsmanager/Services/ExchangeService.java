package com.example.lucas.haushaltsmanager.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExchangeService extends IntentService {

    ArrayList<ExpenseObject> expenses;
    ExpensesDataSource database;
    SharedPreferences preferences;


    /**
     * Service um Buchungen, die noch nicht von einer Fremdwährung in die eigene umgerechnet wurden umzurechnen.
     * todo was passiert wenn eine user nie seine internetverbindung anschaltet (bzw ich kein recht darauf habe) aber trotzdem buchungen in eine anderen währung macht
     *  -> dann wird der store immer voller kann aber nie abgearbeitet werden
     */
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

            HashMap<Double, Long> exchangeRate = database.getExtendedExchangeRate(expenseCurIndex, baseCurIndex, expense.getDateTime().getTimeInMillis());
            Map.Entry<Double, Long> entry = exchangeRate.entrySet().iterator().next();

            //todo aus der Datenbank kommen nun long werte anstatt von String datumsangaben
            if (entry.getValue().equals(expense.getDate())) {

                database.updateExpenseExchangeRate(expense.getIndex(), entry.getKey());
                database.deleteConvertExpense(expense.getIndex());
            }
        }
    }
}
