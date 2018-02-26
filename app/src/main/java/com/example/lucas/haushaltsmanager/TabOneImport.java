package com.example.lucas.haushaltsmanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class TabOneImport extends Fragment {

    ExpensesDataSource mDatabase;
    String mSeparator;
    ArrayList<ExpenseObject> mExpenses;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSeparator = ",";
        mExpenses = new ArrayList<>();

        mDatabase = new ExpensesDataSource(getContext());
        mDatabase.open();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDatabase.close();
    }

    @Nullable
    @Override
    //Anleitung um einen FileParser zu schreiben: https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_one_import, container, false);


        //daten müssen eingelesen werden

        //gucke in die erste zeile --> sind alle daten dort, um ein ExpenseObject zu erstellen?
        // --> JA: fahre normal fort
        // --> NEIN: ist das minimum an Daten vorhanden um ein ExpenseObject zu erstellen?
        //  --> JA: fahre normal fort
        //  --> NEIN: gebe einen Fehler aus und breche ab

        //daten müsen zeile für zeile durchgegangen werden, in ein ExpenseObject umgewandelt werden und
        //  -> sind alle Kategorien, Konten, Währungen, Tags bereits in der Datenbank oder müssen neue erstellt werden

        //
        return rootView;
    }

    private ExpenseObject stringToExpenseObject(String[] expenseString) {

        //es muss gecheckt werden ob ein wert eine zahl ist oder ein string und je nachdem der datentyp ausgewählt werden
        //eigene Exception (InvalidExpenseData) erstellen und auslösen, wenn ein String nicht in eine Buchung umgewandelt werden kann
        throw new UnsupportedOperationException("Das umwandeln von Strings in ExpenseObjects wird nicht unterstützt!");
    }
}
