package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.BookingTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.IBooking;
import com.example.lucas.haushaltsmanager.entities.booking.ParentBooking;

public class ChildExpenseRepository implements ChildExpenseRepositoryInterface {
    private static final String TABLE = "BOOKINGS";

    private final SQLiteDatabase mDatabase;
    private final ExpenseRepository mBookingRepo;
    private final BookingDAO bookingRepository;
    private final TransformerInterface<IBooking> bookingTransformer;

    public ChildExpenseRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        mBookingRepo = new ExpenseRepository(context);
        bookingRepository = AppDatabase.getDatabase(context).bookingDAO();
        bookingTransformer = new BookingTransformer();
    }

    public Booking extractChildFromBooking(Booking childExpense) throws ChildExpenseNotFoundException {
        if (!exists((IBooking) childExpense)) {
            throw new ChildExpenseNotFoundException(childExpense.getId());
        }

        try {
            if (isLastChildOfParent(childExpense)) {
                ParentBooking parentExpense = getParent(childExpense);

                delete(childExpense);
                mBookingRepo.delete(parentExpense);
            } else {

                delete(childExpense);
            }

            bookingRepository.insert(childExpense);
            return childExpense;
        } catch (Exception e) {

            // TODO: Was soll passieren, wenn das Kind nicht gelöscht werden kann?
            return null;
        }
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
            bookingRepository.delete((Booking) parentBooking);

            ParentBooking parent = new ParentBooking("");
            parent.addChild(childExpense);
            mBookingRepo.insert(parent);
        }

        return childExpense;
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
                mBookingRepo.delete(parentExpense);
            } catch (Exception e) {

                throw CannotDeleteChildExpenseException.relatedExpenseNotFound(childExpense);
            }

            return;
        }

        mDatabase.delete(TABLE,
                "id = ?",
                new String[]{childExpense.getId().toString()}
        );
    }

    private void insert(ParentBooking parent, Booking child) {
        ContentValues values = new ContentValues();
        values.put("parent_id", parent.getId().toString());

        values.put("id", child.getId().toString());
        values.put("price", child.getUnsignedPrice());
        values.put("expenditure", child.isExpenditure() ? 1 : 0);
        values.put("title", child.getTitle());
        values.put("date", child.getDate().getTimeInMillis());
        values.put("category_id", child.getCategoryId().toString());
        values.put("account_id", child.getAccountId().toString());
        values.put("hidden", 0);

        mDatabase.insertOrThrow(
                TABLE,
                null,
                values
        );
    }

    private boolean exists(IBooking booking) {
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

    private ParentBooking getParent(Booking childExpense) throws ChildExpenseNotFoundException, ExpenseNotFoundException {
        if (!exists((IBooking) childExpense)) {
            throw new ChildExpenseNotFoundException(childExpense.getId());
        }

        Cursor c = executeRaw(new GetParentBookingQuery(childExpense));

        if (!c.moveToFirst()) {
            throw ExpenseNotFoundException.parentExpenseNotFoundException(childExpense);
        }

        return (ParentBooking) bookingTransformer.transform(c);
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
}
