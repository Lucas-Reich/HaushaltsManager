package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions.RecurringBookingNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecurringBookingRepository {
    private static final String TAG = RecurringBookingRepository.class.getSimpleName();
    //todo ein neues Objekt erstellen RecurringBooking
    //dieses Objekt hat index, start, end, häufigkeit und ExpenseObject als felder
    private SQLiteDatabase mDatabase;
    private ExpenseRepository mBookingRepo;

    public RecurringBookingRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        mBookingRepo = new ExpenseRepository(context);
    }

    public boolean exists(ExpenseObject recurringBooking) {
        String selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + " = " + recurringBooking.getIndex()
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return false;
    }

    public ExpenseObject create(ExpenseObject expense, long startTimeInMillis, int frequency, long endTimeInMillis) {
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, expense.getIndex());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START, startTimeInMillis);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY, frequency);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, endTimeInMillis);

        long insertedRecurringBookingId = mDatabase.insert(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, null, values);

        return new ExpenseObject(
                insertedRecurringBookingId,
                expense.getTitle(),
                expense.getUnsignedPrice(),
                expense.getDateTime(),
                expense.isExpenditure(),
                expense.getCategory(),
                expense.getNotice(),
                expense.getAccountId(),
                expense.getExpenseType(),
                expense.getTags(),
                expense.getChildren(),
                expense.getCurrency()
        );
    }

    public ExpenseObject get(long index) throws RecurringBookingNotFoundException {
        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = " + index;

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst())
            throw new RecurringBookingNotFoundException(index);

        try {

            return getExpense(c);
        } catch (EntityNotExistingException e) {

            //Kann keine Buchung zu einer Wiederkehrenden Buchung gefunden werden so wird diese aus der Datenbank gelöscht.
            //Buchungen die einmal gelöscht wurden können nachträglicg nicht mehr wiederhergestellt werden.
            mDatabase.delete(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = ?", new String[]{"" + index});
            throw new RecurringBookingNotFoundException(index);
        }
    }

    public List<RecurringBooking> getAll2(Calendar start, Calendar end) {
        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_START + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END + " > " + start.getTimeInMillis() + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        List<RecurringBooking> recurringBookings = new ArrayList<>();
        while (c.moveToNext()) {
            RecurringBooking expense = fromCursor(c);

            if (expense.occursInRange(start, end))
                recurringBookings.add(expense);
        }

        return recurringBookings;
    }

    private RecurringBooking fromCursor(Cursor c) {
        long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID));

        long startInMillis = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START));
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startInMillis);

        long endInMillis = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END));
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(endInMillis);

        int frequencyInHours = c.getInt(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY));

        //TODO kann man das auch irgendwie anders handeln?
        ExpenseObject expense = null;
        long expenseId = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID));
        try {

            expense = mBookingRepo.get(expenseId);
        } catch (ExpenseNotFoundException e) {

            Log.e(TAG, "Failed to fetch Expense " + expenseId, e);
        }

        if (c.isLast())
            c.close();

        return new RecurringBooking(
                index,
                start,
                end,
                frequencyInHours,
                expense
        );
    }

    public List<ExpenseObject> getAll(Calendar dateRangeStart, Calendar dateRangeEnd) {//TODO nicht ganz zufrieden mit der funktion, bitte überdenken

        //exclude all events which end before the given date range
        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END + " > " + dateRangeStart.getTimeInMillis() + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        ExpenseObject expense;
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        ArrayList<ExpenseObject> allRecurringBookings = new ArrayList<>();
        while (c.moveToNext()) {

            //getAll start date of recurring booking
            String startDateString = c.getString(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START));
            start.setTimeInMillis(Long.parseLong(startDateString));

            //getAll end date of recurring booking
            String endDateString = c.getString(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END));
            end.setTimeInMillis(Long.parseLong(endDateString));

            //getAll frequency
            int frequency = c.getInt(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY));

            //getAll the time difference between the last occurrence and the start of the date range in hours
            int temp = 0 - ((int) ((start.getTimeInMillis() - dateRangeStart.getTimeInMillis()) / 3600000) % frequency);

            //set start date of recurring event to the first date of the date range
            start.setTimeInMillis(dateRangeStart.getTimeInMillis());

            //set start date of recurring event to the last occurrence of the event
            start.add(Calendar.HOUR, temp);

            //as long as the start date is before the end of its cycle and also before the end of the given date range
            while (start.before(end) && start.before(dateRangeEnd)) {

                if (start.after(dateRangeStart)) {
                    try {
                        expense = getExpense(c);
                        expense.setDateTime(start);

                        allRecurringBookings.add(expense);
                    } catch (EntityNotExistingException e) {
                        //todo remove
                    }
                }

                start.add(Calendar.HOUR, frequency);
            }
            c.moveToNext();
        }

        c.close();
        return allRecurringBookings;
    }

    public boolean update(ExpenseObject newRecurringBooking, long startDateInMills, int frequency, long endDateInMills, long recurringId) {
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, newRecurringBooking.getIndex());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START, startDateInMills);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY, frequency);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, endDateInMills);

        return 1 == mDatabase.update(
                ExpensesDbHelper.TABLE_RECURRING_BOOKINGS,
                values,
                ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = ?",
                new String[]{"" + recurringId}
        );
    }

    public void delete(long recurringBookingId) {

        mDatabase.delete(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + "= ?", new String[]{"" + recurringBookingId});
    }

    private ExpenseObject getExpense(Cursor c) throws EntityNotExistingException {
        //ich kann nun nicht mehr unterscheiden welchen typ von ausgabe ich genau habe
        //an sich nicht schlimm da ich auch Kinder aus dem ExpensesRepo bekomme, aber nicht der schönste weg
        //todo gibt es einen anderen Weg die Expense zu bekommen
        ExpenseObject expense = mBookingRepo.get(
                c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID))
        );

        if (c.isLast())
            c.close();

        return expense;
    }
}
