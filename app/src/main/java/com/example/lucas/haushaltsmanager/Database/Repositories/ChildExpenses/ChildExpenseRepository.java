package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.BookingTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChildExpenseRepository implements ChildExpenseRepositoryInterface {
    private static final String TABLE = "BOOKINGS";

    private SQLiteDatabase mDatabase;
    private ExpenseRepository mBookingRepo;
    private AccountRepository mAccountRepo;
    private final ChildExpenseTransformer transformer;
    private final TransformerInterface<ExpenseObject> bookingTransformer;

    public ChildExpenseRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        mBookingRepo = new ExpenseRepository(context);
        mAccountRepo = new AccountRepository(context);
        transformer = new ChildExpenseTransformer(
                new CategoryTransformer()
        );
        bookingTransformer = new BookingTransformer(
                new CategoryTransformer()
        );
    }

    public boolean exists(ExpenseObject expense) {
        Cursor c = executeRaw(new ChildBookingExistsQuery(expense));

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
        if (exists(parentBooking)) {
            throw new AddChildToChildException(childExpense, parentBooking);
        }

        if (parentBooking.isParent()) {

            insert(parentBooking, childExpense);
            return parentBooking;
        } else {

            try {
                mBookingRepo.delete(parentBooking);

                ExpenseObject dummyParentExpense = ExpenseObject.createDummyExpense();
                dummyParentExpense.setCategory(parentBooking.getCategory());

                dummyParentExpense.addChild(parentBooking);

                mBookingRepo.insert(dummyParentExpense);

                insert(dummyParentExpense, childExpense);
                return dummyParentExpense;
            } catch (CannotDeleteExpenseException e) {
                //Kann nicht passieren, da nur Buchung mit Kindern nicht gelöscht werden können und ich hier vorher übeprüft habe ob die Buchung Kinder hat oder nicht
                // TODO: Die isChild funktionalität so implementieren, dass nich NULL zurückgegeben werden muss.
                return null;
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

                    // TODO: Was soll passieren wenn ein Kind nicht gelöscht werden kann?
                }
            } else {

                try {
                    dummyParentExpense.addChild(expense);
                    mBookingRepo.delete(expense);
                } catch (CannotDeleteExpenseException e) {

                    // TODO: Kann eine ParentExpense nicht gefunden werden muss der gesamte vorgang abgebrochen werden
                    //Beispiel: https://stackoverflow.com/questions/6909221/android-sqlite-rollback
                }
            }
        }

        mBookingRepo.insert(dummyParentExpense);
        return dummyParentExpense;
    }

    public ExpenseObject extractChildFromBooking(ExpenseObject childExpense) throws ChildExpenseNotFoundException {
        if (!exists(childExpense)) {
            throw new ChildExpenseNotFoundException(childExpense.getId());
        }

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

            mBookingRepo.insert(childExpense);
            return childExpense;
        } catch (Exception e) {

            // TODO: Was soll passieren, wenn das Kind nicht gelöscht werden kann?
            return null;
        }
    }

    public void insert(ExpenseObject parentExpense, ExpenseObject childExpense) {
        ContentValues values = new ContentValues();
        values.put("id", childExpense.getId().toString());
        values.put("expense_type", childExpense.getExpenseType().name());
        values.put("price", childExpense.getUnsignedPrice());
        values.put("parent_id", parentExpense.getId().toString());
        values.put("category_id", childExpense.getCategory().getId().toString());
        values.put("expenditure", childExpense.isExpenditure());
        values.put("title", childExpense.getTitle());
        values.put("date", childExpense.getDate().getTimeInMillis());
        values.put("notice", childExpense.getNotice());
        values.put("account_id", childExpense.getAccountId().toString());
        values.put("hidden", 0);

        mDatabase.insertOrThrow(
                TABLE,
                null,
                values
        );

        try {
            updateAccountBalance(
                    childExpense.getAccountId(),
                    childExpense.getSignedPrice()
            );
        } catch (AccountNotFoundException e) {
            //Kann nicht passieren, da der User bei der Buchungserstellung nur aus Konten auswählen kann die bereits existieren
        }
    }

    public void update(ExpenseObject childExpense) throws ChildExpenseNotFoundException {
        ContentValues updatedChild = new ContentValues();
        updatedChild.put("expense_type", childExpense.getExpenseType().name());
        updatedChild.put("price", childExpense.getUnsignedPrice());
        updatedChild.put("category_id", childExpense.getCategory().getId().toString());
        updatedChild.put("expenditure", childExpense.isExpenditure());
        updatedChild.put("title", childExpense.getTitle());
        updatedChild.put("date", childExpense.getDate().getTimeInMillis());
        updatedChild.put("notice", childExpense.getNotice());
        updatedChild.put("account_id", childExpense.getAccountId().toString());

        try {
            ExpenseObject oldExpense = get(childExpense.getId());

            updateAccountBalance(
                    childExpense.getAccountId(),
                    childExpense.getSignedPrice() - oldExpense.getSignedPrice()
            );

            int affectedRows = mDatabase.update(
                    TABLE,
                    updatedChild,
                    "id = ?",
                    new String[]{childExpense.getId().toString()}
            );

            if (affectedRows == 0) {
                throw new ChildExpenseNotFoundException(childExpense.getId());
            }
        } catch (ChildExpenseNotFoundException e) {

            throw new ChildExpenseNotFoundException(childExpense.getId());
        } catch (AccountNotFoundException e) {

            // TODO: Was sollte passieren?
        }
    }

    public void delete(ExpenseObject childExpense) throws CannotDeleteChildExpenseException {
        if (isLastChildOfParent(childExpense)) {
            try {
                ExpenseObject parentExpense = getParent(childExpense);

                mDatabase.delete(
                        TABLE,
                        "id = ?",
                        new String[]{childExpense.getId().toString()}
                );
                parentExpense.removeChild(childExpense);
                updateAccountBalance(
                        childExpense.getAccountId(),
                        -childExpense.getSignedPrice()
                );
                mBookingRepo.delete(parentExpense);
            } catch (Exception e) {

                throw CannotDeleteChildExpenseException.RelatedExpenseNotFound(childExpense);
            }

            return;
        }


        try {
            mDatabase.delete(TABLE,
                    "id = ?",
                    new String[]{childExpense.getId().toString()}
            );
            updateAccountBalance(
                    childExpense.getAccountId(),
                    -childExpense.getSignedPrice()
            );

        } catch (AccountNotFoundException e) {

            //sollte nicht passieren können, da Konten erst gelöscht werden können wenn es keine Buchungen mehr mit diesem Konto gibt
        }
    }

    public void hide(ExpenseObject childExpense) throws ChildExpenseNotFoundException {
        // REFACTOR: Kann durch die Methode des parents ersetzt werden.

        try {
            if (isLastVisibleChildOfParent(childExpense)) {

                ExpenseObject parentExpense = getParent(childExpense);
                mBookingRepo.hide(parentExpense);
            }

            ContentValues values = new ContentValues();
            values.put("hidden", 1);

            int affectedRows = mDatabase.update(
                    TABLE,
                    values,
                    "id = ?",
                    new String[]{childExpense.getId().toString()}
            );

            if (affectedRows == 0) {
                throw new ChildExpenseNotFoundException(childExpense.getId());
            }

            try {
                updateAccountBalance(
                        childExpense.getAccountId(),
                        -childExpense.getSignedPrice()
                );
            } catch (AccountNotFoundException e) {

                // TODO: Wenn der Kontostand nicht geupdated werden kann muss die gesamte Transaktion zurückgenommen werden
            }
        } catch (ExpenseNotFoundException e) {

            // TODO: Dem aufrufenden Code mitteilen dass die Buchung nicht versteckt werden konnte
        }
    }

    public void closeDatabase() {
        //3 Mal weil 3 Datenbankverbindungen (ChildExpenseRepo, AccountRepo, BookingTagRepo, BookingRepo) geöffnet werden

        DatabaseManager.getInstance().closeDatabase();
        DatabaseManager.getInstance().closeDatabase();
        DatabaseManager.getInstance().closeDatabase();
        DatabaseManager.getInstance().closeDatabase();
    }

    public ExpenseObject get(UUID id) throws ChildExpenseNotFoundException {
        Cursor c = executeRaw(new GetChildBookingQuery(id));

        if (!c.moveToFirst()) {
            throw new ChildExpenseNotFoundException(id);
        }

        return transformer.transform(c);
    }

    public List<ExpenseObject> getAll(UUID parentId) {
        Cursor c = executeRaw(new GetAllChildBookingsQuery(parentId));

        ArrayList<ExpenseObject> childBookings = new ArrayList<>();
        while (c.moveToNext()) {
            childBookings.add(transformer.transform(c));
        }

        return childBookings;
    }

    public ExpenseObject getParent(ExpenseObject childExpense) throws ChildExpenseNotFoundException, ExpenseNotFoundException {
        if (!exists(childExpense)) {
            throw new ChildExpenseNotFoundException(childExpense.getId());
        }

        Cursor c = executeRaw(new GetParentBookingQuery(childExpense));

        if (!c.moveToFirst()) {
            throw ExpenseNotFoundException.parentExpenseNotFoundException(childExpense);
        }

        return bookingTransformer.transform(c);
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }

    private boolean isLastChildOfParent(ExpenseObject childExpense) {
        Cursor c = executeRaw(new IsChildBookingLastOfParentQuery(childExpense));

        if (c.getCount() == 1) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    private boolean isLastVisibleChildOfParent(ExpenseObject childExpense) throws ChildExpenseNotFoundException, ExpenseNotFoundException {
        ExpenseObject parentExpense = getParent(childExpense);

        Cursor c = executeRaw(new IsChildBookingLastVisibleOfParentQuery(parentExpense));

        if (c.getCount() == 1) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    /**
     * Methode um den Kontostand anzupassen.
     *
     * @param accountId Konto welches angepasst werden soll
     * @param amount    Betrag der angezogen oder hinzugefügt werden soll
     */
    private void updateAccountBalance(UUID accountId, double amount) throws AccountNotFoundException {
        Account account = mAccountRepo.get(accountId);
        double newBalance = account.getBalance().getSignedValue() + amount;
        account.setBalance(new Price(newBalance));
        mAccountRepo.update(account);
    }
}
