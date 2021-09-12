package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.Room;

import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountDAO;
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
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.entities.Booking.IBooking;
import com.example.lucas.haushaltsmanager.entities.Booking.ParentBooking;
import com.example.lucas.haushaltsmanager.entities.Price;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChildExpenseRepository implements ChildExpenseRepositoryInterface {
    private static final String TABLE = "BOOKINGS";

    private final SQLiteDatabase mDatabase;
    private final ExpenseRepository mBookingRepo;
    private final AccountDAO accountRepo;
    private final ChildExpenseTransformer transformer;
    private final TransformerInterface<IBooking> bookingTransformer;

    public ChildExpenseRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        mBookingRepo = new ExpenseRepository(context);
        accountRepo = Room.databaseBuilder(context, AppDatabase.class, "expenses")
                .build().accountDAO();
        transformer = new ChildExpenseTransformer(
                new CategoryTransformer()
        );
        bookingTransformer = new BookingTransformer(
                new CategoryTransformer()
        );
    }

    public boolean exists(Booking expense) {
        Cursor c = executeRaw(new ChildBookingExistsQuery(expense));

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public Booking extractChildFromBooking(Booking childExpense) throws ChildExpenseNotFoundException {
        if (!exists(childExpense)) {
            throw new ChildExpenseNotFoundException(childExpense.getId());
        }

        try {
            if (isLastChildOfParent(childExpense)) {
                ParentBooking parentExpense = getParent(childExpense);

                delete(childExpense);
                childExpense.setExpenseType(Booking.EXPENSE_TYPES.NORMAL_EXPENSE);
                mBookingRepo.delete(parentExpense);
            } else {

                delete(childExpense);
                childExpense.setExpenseType(Booking.EXPENSE_TYPES.NORMAL_EXPENSE);
            }

            mBookingRepo.insert(childExpense);
            return childExpense;
        } catch (Exception e) {

            // TODO: Was soll passieren, wenn das Kind nicht gelöscht werden kann?
            return null;
        }
    }

    public void insert(Booking parentExpense, Booking childExpense) {
        insert(parentExpense.getId(), childExpense);
    }

    public void insert(ParentBooking parent, Booking child) {
        insert(parent.getId(), child);
    }

    public void update(Booking childExpense) throws ChildExpenseNotFoundException {
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
            Booking oldExpense = get(childExpense.getId());

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

    public void delete(Booking childExpense) throws CannotDeleteChildExpenseException {
        if (isLastChildOfParent(childExpense)) {
            try {
                ParentBooking parentExpense = getParent(childExpense);

                mDatabase.delete(
                        TABLE,
                        "id = ?",
                        new String[]{childExpense.getId().toString()}
                );
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

    public void hide(Booking childExpense) throws ChildExpenseNotFoundException {
        // REFACTOR: Kann durch die Methode des parents ersetzt werden.

        try {
            if (isLastVisibleChildOfParent(childExpense)) {

                ParentBooking parentExpense = getParent(childExpense);
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

    public Booking get(UUID id) throws ChildExpenseNotFoundException {
        Cursor c = executeRaw(new GetChildBookingQuery(id));

        if (!c.moveToFirst()) {
            throw new ChildExpenseNotFoundException(id);
        }

        return transformer.transform(c);
    }

    public List<Booking> getAll(UUID parentId) {
        Cursor c = executeRaw(new GetAllChildBookingsQuery(parentId));

        ArrayList<Booking> childBookings = new ArrayList<>();
        while (c.moveToNext()) {
            childBookings.add(transformer.transform(c));
        }

        return childBookings;
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
    public Booking addChildToBooking(Booking childExpense, IBooking parentBooking) throws AddChildToChildException {
        if (exists(parentBooking)) {
            throw new AddChildToChildException(childExpense, parentBooking);
        }

        if (parentBooking instanceof ParentBooking) {

            insert((ParentBooking) parentBooking, childExpense);
        } else {

            try {
                mBookingRepo.delete(parentBooking);

                ParentBooking parent = new ParentBooking("");
                parent.addChild(childExpense);
                mBookingRepo.insert(parent);
            } catch (CannotDeleteExpenseException e) {
                //Kann nicht passieren, da nur Buchung mit Kindern nicht gelöscht werden können und ich hier vorher übeprüft habe ob die Buchung Kinder hat oder nicht
                // TODO: Die isChild funktionalität so implementieren, dass nich NULL zurückgegeben werden muss.
                return null;
            }
        }

        return childExpense;
    }

    public boolean exists(IBooking booking) {
        Cursor c = executeRaw(new QueryInterface() {
            @Override
            public String sql() {
                return "SELECT count(*) FROM bookings WHERE id = %s";
            }

            @Override
            public Object[] values() {
                return new Object[]{
                        booking.getId().toString()
                };
            }
        });

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public ParentBooking getParent(Booking childExpense) throws ChildExpenseNotFoundException, ExpenseNotFoundException {
        if (!exists(childExpense)) {
            throw new ChildExpenseNotFoundException(childExpense.getId());
        }

        Cursor c = executeRaw(new GetParentBookingQuery(childExpense));

        if (!c.moveToFirst()) {
            throw ExpenseNotFoundException.parentExpenseNotFoundException(childExpense);
        }

        return (ParentBooking) bookingTransformer.transform(c);
    }

    private void insert(UUID parentId, Booking child) {
        ContentValues values = new ContentValues();
        values.put("parent_id", parentId.toString());

        values.put("id", child.getId().toString());
        values.put("expense_type", Booking.EXPENSE_TYPES.CHILD_EXPENSE.name());
        values.put("price", child.getUnsignedPrice());
        values.put("expenditure", child.isExpenditure() ? 1 : 0);
        values.put("title", child.getTitle());
        values.put("date", child.getDate().getTimeInMillis());
        values.put("notice", child.getNotice());
        values.put("category_id", child.getCategory().getId().toString());
        values.put("account_id", child.getAccountId().toString());
        values.put("hidden", 0);

        mDatabase.insertOrThrow(
                TABLE,
                null,
                values
        );

        try {
            updateAccountBalance(
                    child.getAccountId(),
                    child.getSignedPrice()
            );
        } catch (AccountNotFoundException e) {
            //Kann nicht passieren, da der User bei der Buchungserstellung nur aus Konten auswählen kann die bereits existieren
        }
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }

    private boolean isLastChildOfParent(Booking childExpense) {
        Cursor c = executeRaw(new IsChildBookingLastOfParentQuery(childExpense));

        if (c.getCount() == 1) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    private boolean isLastVisibleChildOfParent(Booking childExpense) throws ChildExpenseNotFoundException, ExpenseNotFoundException {
        ParentBooking parentExpense = getParent(childExpense);

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
        Account account = accountRepo.get(accountId);
        double newBalance = account.getPrice().getSignedValue() + amount;
        account.setPrice(new Price(newBalance));
        accountRepo.update(account);
    }
}
