package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions.RecurringBookingNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecurringBookingRepository {
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

        String selectQuery;
        selectQuery = "SELECT"
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

    public ExpenseObject insert(ExpenseObject expense, long startTimeInMillis, int frequency, long endTimeInMillis) {

        mBookingRepo.assertSavableExpense(expense);

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

    public ExpenseObject get(long recurringBookingId) throws RecurringBookingNotFoundException {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = " + recurringBookingId;

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new RecurringBookingNotFoundException(recurringBookingId);
        }

        ExpenseObject expense;
        try {
            expense = getExpense(c);
        } catch (EntityNotExistingException e) {

            //Kann keine Buchung zu einer Wiederkehrenden Buchung gefunden werden so wird diese aus der Datenbank gelöcht.
            //Buchungen die einmal gelöscht wurden können nachträglicg nicht mehr wiederhergestellt werden.
            mDatabase.delete(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = ?", new String[]{"" + recurringBookingId});
            throw new RecurringBookingNotFoundException(recurringBookingId);
        }

        c.close();
        return expense;
    }

    public List<ExpenseObject> getAll(Calendar dateRangeStart, Calendar dateRangeEnd) {//TODO nicht ganz zufrieden mit der funktion, bitte überdenken

        //exclude all events which end before the given date range
        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END + " > " + dateRangeEnd.getTimeInMillis() + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        c.moveToFirst();

        ExpenseObject expense;
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        ArrayList<ExpenseObject> allRecurringBookings = new ArrayList<>();
        while (!c.isAfterLast()) {

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

        int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, values, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = ?", new String[]{"" + recurringId});

        return affectedRows == 1;
    }

    public void delete(long recurringBookingId) {

        mDatabase.delete(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + "= ?", new String[]{"" + recurringBookingId});
    }

    private static ExpenseObject getExpense(Cursor c) throws EntityNotExistingException {
        //ich kann nun nicht mehr unterscheiden welchen typ von ausgabe ich genau habe
        //an sich nicht schlimm da ich auch Kinder aus dem ExpensesRepo bekomme, aber nicht der schönste weg
        //todo gibt es einen anderen Weg die Expense zu bekommen
        return new ExpenseRepository(app.getContext()).get(c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID)));//todo
    }
}
