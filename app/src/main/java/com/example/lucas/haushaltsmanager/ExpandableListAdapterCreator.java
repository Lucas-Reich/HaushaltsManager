package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapterCreator {

    private String TAG = ExpandableListAdapterCreator.class.getSimpleName();

    private ArrayList<ExpenseObject> mExpenses;
    private ArrayList<ExpenseObject> mListDataHeader;
    private HashMap<ExpenseObject, List<ExpenseObject>> mListDataChild;
    private ExpensesDataSource mDatabase;
    private Context mContext;
    private List<Long> mActiveAccounts;

    public ExpandableListAdapterCreator(ArrayList<ExpenseObject> expenses, List<Long> activeAccounts, Context context) {

        //die liste der aktiven konten soll hier erstellt werden
        mExpenses = expenses;
        mContext = context;
        mDatabase = new ExpensesDataSource(mContext);
        mDatabase.open();
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
        mDatabase.close();
        return new ExpandableListAdapter(mContext, mListDataHeader, mListDataChild);
    }

    /**
     * Ersatz für die prepareAdapter methode, da diese nicht in der lage ist die Datumstrenner aus der liste zu nehmen.
     * Da diese Funktion aber noch Probleme mit der HasMap klasse hat wird sie noch nicht eingesetzt
     */
    private void prepareAdapter() {
        Log.d(TAG, "prepareAdapter: Preparing new Adapter data!");
        clearExistingViewDataSource();

        int counter = 0;
        String separatorDate = "";
        for (int i = 0; i < mExpenses.size() - 1; ) {

            ExpenseObject expense = mExpenses.get(i);
            if (mExpenses.get(i).getDate().equals(separatorDate) && i + 1 != mExpenses.size()) {

                if (isExpenseVisible(expense)) {

                    addDataToListViewDataSource(expense, getChildrenToDisplay(expense));
                    counter++;
                }
                i++;
            } else {

                if (counter == 0 && mListDataHeader.size() != 0)
                    removeItemAtIndex(i - 1);

                ExpenseObject dateSeparator = createDateSeparator(mExpenses.get(i).getDateTime());
                addDataToListViewDataSource(dateSeparator, null);
                separatorDate = expense.getDate();

                counter = 0;
            }
        }

        if (counter == 0 && mListDataHeader.size() != 0)
            removeItemAtIndex(mListDataHeader.size() - 1);
    }

    /**
     * Methode um eine Buchung aus dem Datensatz des ExpandableListAdapters basierend auf dem Index zu löschen
     *
     * @param index 0 basierter Index der zu löschenden Buchung
     */
    private void removeItemAtIndex(int index) {

        ExpenseObject itemToRemove = mListDataHeader.get(index);
        removeItem(itemToRemove);
    }

    /**
     * Methode um eine Buchung aus dem Datensatz des ExpandableListAdapters zu löschen
     *
     * @param expense Zu löschende Buchung
     */
    private void removeItem(ExpenseObject expense) {

        mListDataChild.remove(expense);
        mListDataHeader.remove(expense);
    }

    /**
     * Methode um die Groupdaten und die zugehörigen Childdaten in die Datengrundlage der ExpandableListView einfügt
     *
     * @param groupData GroupExpense
     * @param childData Zugehörigen Childdaten, bei null wird eine leere liste eingefügt
     */
    private void addDataToListViewDataSource(@NonNull ExpenseObject groupData, @Nullable ArrayList<ExpenseObject> childData) {

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
        for (ExpenseObject child : parentExpense.getChildren()) {

            if (isExpenseVisible(child))
                childrenToDisplay.add(child);
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

    /**
     * Methode um die bereits bestehenden Datengrundlage der ExpandabeListView zu löschen
     */
    private void clearExistingViewDataSource() {
        mListDataHeader.clear();
        mListDataChild.clear();
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
}
