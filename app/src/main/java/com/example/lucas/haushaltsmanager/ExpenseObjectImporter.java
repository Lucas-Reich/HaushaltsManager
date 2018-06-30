package com.example.lucas.haushaltsmanager;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExpenseObjectImporter {

    private ExpensesDataSource mDatabase;
    private File mFile;

    public ExpenseObjectImporter(File file, Context context) {

        assertFile(file);
        mFile = file;

        mDatabase = new ExpensesDataSource(context);
    }

    /**
     * Methode um sicherzustellen, dass die angegebene Datei auch wirklich eine Datei ist.
     *
     * @param file Zu überorüfenden Datei
     * @throws IllegalArgumentException Wenn die angegebene Datei kein Datei ist wird eine exception ausgelöst
     */
    private void assertFile(File file) throws IllegalArgumentException {
        if (!file.isFile())
            throw new IllegalArgumentException(String.format("%s is not a file", file.getName()));
    }

    /**
     * Methode um den Inhalt der angegebenen Datei zu importieren.
     */
    public void readAndSaveExpenseObjects() {

        List<ExpenseObject> expenses = new ArrayList<>();
        BufferedReader br = null;
        String line;

        try {

            br = new BufferedReader(new FileReader(mFile));
            while ((line = br.readLine()) != null) {

                expenses.add(stringToExpenseObject(line));
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

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

    /**
     * Methode um aus einem string ExpenseObject ein richtiges Objekt zu machen.
     *
     * @param expenseString Stringyfiziertes ExpenseObject
     * @return ExpenseObject
     */
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

    /**
     * Methode um die Buchugen in der Datenbank zu speichern.
     *
     * @param expenses Buchungen die in der Datenbank gespeichert werden sollen.
     */
    private void saveExpenseObjects(List<ExpenseObject> expenses) {

        mDatabase.open();
        mDatabase.createBookings(expenses);
        mDatabase.close();
    }
}
