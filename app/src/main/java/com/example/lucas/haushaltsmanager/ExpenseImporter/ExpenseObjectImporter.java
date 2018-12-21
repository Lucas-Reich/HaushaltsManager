package com.example.lucas.haushaltsmanager.ExpenseImporter;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpenseObjectImporter {
    private static final String TAG = ExpenseObjectImporter.class.getSimpleName();

    private File mFile;
    private Context mContext;
    private ExpenseMap mExpenseMap;

    //todo den User die Informationen in der CSV Datei bestimmen lassen (siehe https://trello.com/c/fYk2L9vt/56-ausgaben-importer)
    public ExpenseObjectImporter(File file, Context context) {
        mContext = context;

        assertFile(file);
        mFile = file;

        mExpenseMap = new ExpenseMap();
    }

    private HashMap<String, String> mapTableHeaders() {
        HashMap<String, String> mappedHeaders = new HashMap<>();
        String[] givenHeaders = getTableHeaders(mFile);

        for (String header : givenHeaders) {

            // TODO zeige den momentanen header an und frage den user zu welchem datenbank feld dieses gehört
        }

        return mappedHeaders;
    }

    private String[] getTableHeaders(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            return br.readLine().split(",");
        } catch (IOException e) {
            return new String[]{};
        }
    }

    private void assertFile(File file) throws IllegalArgumentException {
        if (!file.isFile())
            throw new IllegalArgumentException(String.format("%s is not a file", file.getName()));
    }

    public void readAndSaveExpenseObjects() {

        List<ExpenseObject> expenses = new ArrayList<>();
        BufferedReader br = null;
        String line;

        try {

            br = new BufferedReader(new FileReader(mFile));
            while ((line = br.readLine()) != null) {

                expenses.add(stringToExpenseObject(line));
            }
        } catch (IOException e) {

            // TODO do smth
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        saveExpenseObjects(expenses);
    }

    private ExpenseObject stringToExpenseObject(String expenseString) {

        //prüfe ob alle notwendigen attribute gesetzt sind
        //mappe die angegebenen attribute zu meinen attributen
        //erstelle das ExpenseObject
        return null;
    }

    private boolean hasNecessaryAttributes(String expenseString) {

        //todo check implementieren
        return false;
    }

    private void saveExpenseObjects(List<ExpenseObject> expenses) {
        ExpenseRepository expenseRepo = new ExpenseRepository(mContext);

        for (ExpenseObject expense : expenses)
            expenseRepo.insert(expense);
    }
}
