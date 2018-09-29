package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags.BookingTagRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChildExpenseRepository {
    private SQLiteDatabase mDatabase;
    private BookingTagRepository mBookingTagRepo;
    private ExpenseRepository mBookingRepo;
    private AccountRepository mAccountRepo;

    public ChildExpenseRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        mBookingTagRepo = new BookingTagRepository(context);
        mBookingRepo = new ExpenseRepository(context);
        mAccountRepo = new AccountRepository(context);
    }

    public boolean exists(ExpenseObject expense) {
        String selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + " = " + expense.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_TITLE + " = '" + expense.getTitle() + "'"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PRICE + " = " + expense.getUnsignedPrice()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE + " = '" + expense.getExpenseType().name() + "'"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + expense.getCategory().getIndex()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + " = " + expense.getAccountId()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE + " = " + (expense.isExpenditure() ? 1 : 0)
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " = '" + expense.getDateTime().getTimeInMillis() + "'"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_NOTICE + " = '" + expense.getNotice() + "'"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID + " = " + expense.getCurrency().getIndex()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PARENT_ID + " IS NOT NULL"
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    /**
     * Diese Funktion stellt sicher dass keine Kind zu einer ChildExpense hinzugefügt werden kann.
     * Sie überprüft ebenfalls ob die Parentbuchung bereits ChildExpenses hat oder nicht.
     * Hat die Parentbuchung keine Kinder wird eine Dummy Ausgabe erstellt, zu der die Kinder hinzugefügt werden.
     *
     * @param childExpense  Buchung welche dem Parent als Kind hinzugefügt werden soll
     * @param parentBooking Buchung der ein neues Kind hinzugefügt werden soll
     * @return ChildExpense, mit dem korrekten Index
     */
    public ExpenseObject addChildToBooking(ExpenseObject childExpense, ExpenseObject parentBooking) throws AddChildToChildException {
        if (exists(parentBooking))
            throw new AddChildToChildException();

        if (parentBooking.isParent()) {
            parentBooking.addChild(insert(parentBooking, childExpense));

            return parentBooking;
        } else {

            try {
                mBookingRepo.delete(parentBooking);

                ExpenseObject dummyParentExpense = ExpenseObject.createDummyExpense();
                dummyParentExpense.addChild(parentBooking);
                dummyParentExpense.addChild(childExpense);

                return mBookingRepo.insert(dummyParentExpense);
            } catch (CannotDeleteExpenseException e) {
                //Kann nicht passieren, da nur Buchung mit Kindern nicht gelöscht werden können und ich hier vorher übeprüft habe ob die Buchung Kinder hat oder nicht
                return null;//todo kann ich die isChild funktionalität so implementieren dass ich nicht null returnen muss?
            }
        }
    }

    /**
     * Methode um mehrere Buchungen zusammenzufügen
     *
     * @param expenses Liste der Buchungen die zusammengefügt werden sollen
     * @return Parent der zusammengefügten Buchungen, mit den hinzugefügten KinmDatabaseuchungen
     */
    public ExpenseObject combineExpenses(List<ExpenseObject> expenses) {
        ExpenseObject dummyParentExpense = ExpenseObject.createDummyExpense();

        for (ExpenseObject expense : expenses) {
            if (expense.isParent()) {

                dummyParentExpense.addChildren(expense.getChildren());
                try {
                    for (ExpenseObject child : expense.getChildren())
                        delete(child);
                } catch (CannotDeleteChildExpenseException e) {

                    //todo was soll passieren wenn ein Kind nicht gelöscht werden kann
                }
            } else {

                try {
                    dummyParentExpense.addChild(expense);
                    mBookingRepo.delete(expense);
                } catch (CannotDeleteExpenseException e) {

                    //todo kann eine ParentExpense nicht gefunden werden muss der gesamte vorgang abgebrochen werden
                    //Beispiel: https://stackoverflow.com/questions/6909221/android-sqlite-rollback
                }
            }
        }

        return mBookingRepo.insert(dummyParentExpense);
    }

    public ExpenseObject extractChildFromBooking(ExpenseObject childExpense) throws ChildExpenseNotFoundException {
        if (!exists(childExpense))
            throw new ChildExpenseNotFoundException(childExpense.getIndex());

        try {
            if (isLastChildOfParent(childExpense)) {
                ExpenseObject parentExpense = getParent(childExpense);

                delete(childExpense);
                parentExpense.removeChild(childExpense);
                childExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE);
                mBookingRepo.delete(parentExpense);
            } else {

                delete(childExpense);
                childExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE);
            }

            return mBookingRepo.insert(childExpense);
        } catch (Exception e) {

            //todo was soll passieren wenn das Kind nicht gelöscht werden kann
            return null;
        }
    }

    public ExpenseObject get(long expenseId) throws ChildExpenseNotFoundException {
        String selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PRICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_TITLE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_NOTICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CURRENCIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID + " = " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + " = " + expenseId
                + " ORDER BY " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " DESC;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new ChildExpenseNotFoundException(expenseId);
        }

        return fromCursor(c);
    }

    public List<ExpenseObject> getAll(long parentId) {
        String selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PRICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_TITLE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_NOTICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CURRENCIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID + " = " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PARENT_ID + " = " + parentId
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_HIDDEN + " != 1"
                + " ORDER BY " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " DESC;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        ArrayList<ExpenseObject> childBookings = new ArrayList<>();
        while (c.moveToNext())
            childBookings.add(fromCursor(c));

        return childBookings;
    }

    public ExpenseObject insert(ExpenseObject parentExpense, ExpenseObject childExpense) {
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE, childExpense.getExpenseType().name());
        values.put(ExpensesDbHelper.BOOKINGS_COL_PRICE, childExpense.getUnsignedPrice());
        values.put(ExpensesDbHelper.BOOKINGS_COL_PARENT_ID, parentExpense.getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID, childExpense.getCategory().getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, childExpense.isExpenditure());
        values.put(ExpensesDbHelper.BOOKINGS_COL_TITLE, childExpense.getTitle());
        values.put(ExpensesDbHelper.BOOKINGS_COL_DATE, childExpense.getDateTime().getTimeInMillis());
        values.put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, childExpense.getNotice());
        values.put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, childExpense.getAccountId());
        values.put(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID, childExpense.getCurrency().getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_COL_HIDDEN, 0);

        long insertedChildId = mDatabase.insert(ExpensesDbHelper.TABLE_BOOKINGS, null, values);

        try {
            updateAccountBalance(
                    childExpense.getAccountId(),
                    childExpense.getSignedPrice()
            );
        } catch (AccountNotFoundException e) {
            //Kann nicht passieren, da der User bei der Buchungserstellung nur aus Konten auswählen kann die bereits existieren
        }

        for (Tag tag : childExpense.getTags())
            mBookingTagRepo.insert(insertedChildId, tag);

        return new ExpenseObject(
                insertedChildId,
                childExpense.getTitle(),
                childExpense.getUnsignedPrice(),
                childExpense.getDateTime(),
                childExpense.isExpenditure(),
                childExpense.getCategory(),
                childExpense.getNotice(),
                childExpense.getAccountId(),
                childExpense.getExpenseType(),
                childExpense.getTags(),
                childExpense.getChildren(),
                childExpense.getCurrency()
        );
    }

    public void update(ExpenseObject childExpense) throws ChildExpenseNotFoundException {
        ContentValues updatedChild = new ContentValues();
        updatedChild.put(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE, childExpense.getExpenseType().name());
        updatedChild.put(ExpensesDbHelper.BOOKINGS_COL_PRICE, childExpense.getUnsignedPrice());
        updatedChild.put(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID, childExpense.getCategory().getIndex());
        updatedChild.put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, childExpense.isExpenditure());
        updatedChild.put(ExpensesDbHelper.BOOKINGS_COL_TITLE, childExpense.getTitle());
        updatedChild.put(ExpensesDbHelper.BOOKINGS_COL_DATE, childExpense.getDateTime().getTimeInMillis());
        updatedChild.put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, childExpense.getNotice());
        updatedChild.put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, childExpense.getAccountId());
        updatedChild.put(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID, childExpense.getCurrency().getIndex());

        mBookingTagRepo.deleteAll(childExpense);
        for (Tag tag : childExpense.getTags()) {
            mBookingTagRepo.insert(childExpense.getIndex(), tag);
        }

        try {
            ExpenseObject oldExpense = get(childExpense.getIndex());

            updateAccountBalance(
                    childExpense.getAccountId(),
                    childExpense.getSignedPrice() - oldExpense.getSignedPrice()
            );

            int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_BOOKINGS, updatedChild, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{childExpense.getIndex() + ""});

            if (affectedRows == 0)
                throw new ChildExpenseNotFoundException(childExpense.getIndex());
        } catch (ChildExpenseNotFoundException e) {

            throw new ChildExpenseNotFoundException(childExpense.getIndex());
        } catch (AccountNotFoundException e) {

            //todo do nothing?
        }
    }

    public void delete(ExpenseObject childExpense) throws CannotDeleteChildExpenseException {

        if (isLastChildOfParent(childExpense)) {

            try {
                ExpenseObject parentExpense = getParent(childExpense);

                mDatabase.delete(ExpensesDbHelper.TABLE_BOOKINGS, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{"" + childExpense.getIndex()});
                parentExpense.removeChild(childExpense);
                updateAccountBalance(
                        childExpense.getAccountId(),
                        -childExpense.getSignedPrice()
                );
                mBookingRepo.delete(parentExpense);
            } catch (Exception e) {

                throw CannotDeleteChildExpenseException.RelatedExpenseNotFound(childExpense);
            }
        } else {

            try {
                mDatabase.delete(ExpensesDbHelper.TABLE_BOOKINGS, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{"" + childExpense.getIndex()});
                updateAccountBalance(
                        childExpense.getAccountId(),
                        -childExpense.getSignedPrice()
                );

            } catch (AccountNotFoundException e) {

                //sollte nicht passieren können, da Konten erst gelöscht werden können wenn es keine Buchungen mehr mit diesem Konto gibt
            }
        }
    }

    public void hide(ExpenseObject childExpense) throws ChildExpenseNotFoundException {
        //todo kann auch durch die Methode des parents ersetzt werden

        try {
            if (isLastVisibleChildOfParent(childExpense)) {

                ExpenseObject parentExpense = getParent(childExpense);
                mBookingRepo.hide(parentExpense);
            }

            ContentValues values = new ContentValues();
            values.put(ExpensesDbHelper.BOOKINGS_COL_HIDDEN, 1);

            int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_BOOKINGS, values, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{"" + childExpense.getIndex()});

            if (affectedRows == 0)
                throw new ChildExpenseNotFoundException(childExpense.getIndex());

            try {
                updateAccountBalance(
                        childExpense.getAccountId(),
                        -childExpense.getSignedPrice()
                );
            } catch (AccountNotFoundException e) {

                //todo wenn der Kontostand nicht geupdated werden kann muss die gesamte transaktion zurückgenommen werden
            }
        } catch (ExpenseNotFoundException e) {

            //todo dem aufrufenden code mitteilen dass die buchung nicht versteckt werden konnte
        }
    }

    public boolean isHidden(ExpenseObject childExpense) throws ChildExpenseNotFoundException {
        String selectQuery = "SELECT"
                + " " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_HIDDEN
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + " = " + childExpense.getIndex()
                + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst())
            throw new ChildExpenseNotFoundException(childExpense.getIndex());


        boolean isHidden = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_HIDDEN)) == 1;
        c.close();

        return isHidden;
    }

    private boolean isLastChildOfParent(ExpenseObject childExpense) {
        String subSelect = "(SELECT "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PARENT_ID
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + " = " + childExpense.getIndex()
                + ")";

        String selectQuery = "SELECT "
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PARENT_ID + " = " + subSelect
                + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.getCount() == 1) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    private boolean isLastVisibleChildOfParent(ExpenseObject childExpense) throws ChildExpenseNotFoundException, ExpenseNotFoundException {
        ExpenseObject parentExpense = getParent(childExpense);

        String selectQuery = "SELECT "
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PARENT_ID + " = " + parentExpense.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_HIDDEN + " != 1"
                + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.getCount() == 1) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public ExpenseObject getParent(ExpenseObject childExpense) throws ChildExpenseNotFoundException, ExpenseNotFoundException {

        if (!exists(childExpense))
            throw new ChildExpenseNotFoundException(childExpense.getIndex());

        String subQuery = "(SELECT "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PARENT_ID
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + " = '" + childExpense.getIndex() + "'"
                + ")";

        String selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PRICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_TITLE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_NOTICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CURRENCIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID + " = " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + " = " + subQuery
                + " ORDER BY " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " DESC;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst())
            throw ExpenseNotFoundException.parentExpenseNotFoundException(childExpense);

        return ExpenseRepository.cursorToExpense(c);
    }

    public ExpenseObject fromCursor(Cursor c) {
        long expenseId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ID));
        Calendar date = Calendar.getInstance();
        String dateString = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_DATE));
        date.setTimeInMillis(Long.parseLong(dateString));
        String title = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_TITLE));
        double price = c.getDouble(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_PRICE));
        boolean expenditure = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE)) == 1;
        String notice = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_NOTICE));
        long accountId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID));
        Category expenseCategory = CategoryRepository.cursorToCategory(c);
        Currency expenseCurrency = CurrencyRepository.fromCursor(c);

        if (c.isLast())
            c.close();

        return new ExpenseObject(
                expenseId,
                title,
                price,
                date,
                expenditure,
                expenseCategory,
                notice,
                accountId,
                ExpenseObject.EXPENSE_TYPES.CHILD_EXPENSE,
                mBookingTagRepo.get(expenseId),
                new ArrayList<ExpenseObject>(),
                expenseCurrency
        );
    }

    public void closeDatabase() {
        //3 Mal weil 3 Datenbankverbindungen (ChildExpenseRepo, AccountRepo, BookingTagRepo, BookingRepo) geöffnet werden

        DatabaseManager.getInstance().closeDatabase();
        DatabaseManager.getInstance().closeDatabase();
        DatabaseManager.getInstance().closeDatabase();
        DatabaseManager.getInstance().closeDatabase();
    }

    /**
     * Methode um den Kontostand anzupassen.
     *
     * @param accountId Konto welches angepasst werden soll
     * @param amount    Betrag der angezogen oder hinzugefügt werden soll
     */
    private void updateAccountBalance(long accountId, double amount) throws AccountNotFoundException {

        Account account = mAccountRepo.get(accountId);
        account.setBalance(account.getBalance() + amount);
        mAccountRepo.update(account);
    }
}
