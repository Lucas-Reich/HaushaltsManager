package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.support.annotation.*;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapterCreator {
    private static final String TAG = ExpandableListAdapterCreator.class.getSimpleName();

    private ArrayList<ExpenseObject> mExpenses;
    private List<Long> mActiveAccounts;
    private ArrayList<ExpenseObject> mListDataHeader;
    private HashMap<ExpenseObject, List<ExpenseObject>> mListDataChild;
    private Context mContext;

    public ExpandableListAdapterCreator(ArrayList<ExpenseObject> expenses, List<Long> activeAccounts, Context context) {

        //die liste der aktiven konten soll hier erstellt werden
        mExpenses = expenses;
        mContext = context;
        mListDataHeader = new ArrayList<>();
        mListDataChild = new HashMap<>();
        mActiveAccounts = activeAccounts;
    }

    /**
     * Methode um einen neuen ExpandableListAdapter zu erstellen.
     *
     * @return ExpandableListAdapter
     */
    public ExpandableListAdapter getExpandableListAdapter() {

        prepareAdapter();
        return new ExpandableListAdapter(mContext, mListDataHeader, mListDataChild);
    }

    /**
     * Methode welche die Liste der Buchungen für den Adapter vorbereitet.
     * Dabei werden Buchunge, die nicht angezeigt werden soll ausgenommen und
     * Datumstrenner zwischen zwei Buchungen mit unterschiedlichem Datum eingefügt.
     */
    private void prepareAdapter() {

        String separatorDate = "";
        for (ExpenseObject expense : mExpenses) {

            if (isExpenseVisible(expense)) {

                if (expense.getDate().equals(separatorDate)) {

                    addExpenseToListViewDataSource(expense, getChildrenToDisplay(expense));
                } else {

                    createAndInsertDateSeparator(expense.getDateTime());
                    addExpenseToListViewDataSource(expense, getChildrenToDisplay(expense));
                    separatorDate = expense.getDate();
                }
            }
        }
    }

    private void createAndInsertDateSeparator(Calendar previousDate) {

        ExpenseObject dateSeparator = createDateSeparator(previousDate);
        addExpenseToListViewDataSource(dateSeparator, null);
    }

    /**
     * Funktion um ein ExpenseObject zu erstellen, dass einem Datumstrenner entspricht.
     *
     * @param date Datum des Datumstrenners
     * @return Datumstrenner
     */
    private ExpenseObject createDateSeparator(Calendar date) {

        ExpenseObject dateSeparator = ExpenseObject.createDummyExpense(mContext);
        dateSeparator.setExpenseType(ExpenseObject.EXPENSE_TYPES.DATE_PLACEHOLDER);
        dateSeparator.setDateTime(date);

        return dateSeparator;
    }

    /**
     * Methode um die Groupdaten und die zugehörigen Childdaten in die Datengrundlage der ExpandableListView einfügt
     *
     * @param groupData GroupExpense
     * @param childData Zugehörigen Childdaten, bei null wird eine leere liste eingefügt
     */
    private void addExpenseToListViewDataSource(@NonNull ExpenseObject groupData, @Nullable ArrayList<ExpenseObject> childData) {

        mListDataHeader.add(groupData);
        mListDataChild.put(groupData, childData != null ? childData : new ArrayList<ExpenseObject>());
    }

    /**
     * Methode um die Kinder einer Buchung zu bekommen, die in der ExpandableListView angezeigt werden sollen.
     * Ein Kind wird angezeigt, wenn das Konto des Kindes in der mActiveAccounts liste gespeichert ist.
     *
     * @param parentExpense ParentExpense
     * @return Liste der anzuzeigenden Kinder
     */
    private ArrayList<ExpenseObject> getChildrenToDisplay(ExpenseObject parentExpense) {

        ArrayList<ExpenseObject> childrenToDisplay = new ArrayList<>();
        for (ExpenseObject childExpense : parentExpense.getChildren()) {

            if (isExpenseVisible(childExpense))
                childrenToDisplay.add(childExpense);
        }

        return childrenToDisplay;
    }

    /**
     * Methode um zu Überprüfen ob eine Buchung sichtbar sein soll oder nicht.
     * Buchungen sind nicht sichtbar, wenn keines der Kinder angezeigt werden soll,
     * oder wenn die Buchung selber nicht angezeigt werden soll
     *
     * @param expense Die zu überprüfende Ausgabe
     * @return Boolean
     */
    private Boolean isExpenseVisible(ExpenseObject expense) {

        if (expense.isParent()) {

            //hat eine Buchung kinder die angezeigt werden sollen, dann ist die Buchung sichtbar
            return getChildrenToDisplay(expense).size() > 0;
        } else
            return mActiveAccounts.contains(expense.getAccount().getIndex());
    }
}
