package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public static boolean exists(ExpenseObject recurringBooking) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + " = " + recurringBooking.getIndex()
                + " LIMIT 1;";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            DatabaseManager.getInstance().closeDatabase();
            return true;
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return false;
    }

    public static ExpenseObject insert(ExpenseObject expense, long startTimeInMillis, int frequency, long endTimeInMillis) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ExpenseRepository.assertSavableExpense(expense);

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, expense.getIndex());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START, startTimeInMillis);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY, frequency);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, endTimeInMillis);

        long insertedRecurringBookingId = db.insert(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, null, values);
        DatabaseManager.getInstance().closeDatabase();

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

    public static ExpenseObject get(long recurringBookingId) throws RecurringBookingNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = " + recurringBookingId;

        Cursor c = db.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new RecurringBookingNotFoundException(recurringBookingId);
        }

        ExpenseObject expense;
        try {
            expense = getExpense(c);
        } catch (EntityNotExistingException e) {

            //Kann keine Buchung zu einer Wiederkehrenden Buchung gefunden werden so wird diese aus der Datenbank gelöcht.
            //Buchungen die einmal gelöscht wurden können nachträglicg nicht mehr wiederhergestellt werden.
            db.delete(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = ?", new String[]{"" + recurringBookingId});
            throw new RecurringBookingNotFoundException(recurringBookingId);
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return expense;
    }

    public static List<ExpenseObject> getAll(Calendar dateRangeStart, Calendar dateRangeEnd) {//TODO nicht ganz zufrieden mit der funktion, bitte überdenken
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        //exclude all events which end before the given date range
        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END + " > " + dateRangeEnd.getTimeInMillis() + ";";

        Cursor c = db.rawQuery(selectQuery, null);

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

        DatabaseManager.getInstance().closeDatabase();
        c.close();
        return allRecurringBookings;
    }

    public static boolean update(ExpenseObject newRecurringBooking, long startDateInMills, int frequency, long endDateInMills, long recurringId) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, newRecurringBooking.getIndex());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START, startDateInMills);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY, frequency);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, endDateInMills);

        int affectedRows = db.update(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, values, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = ?", new String[]{"" + recurringId});
        DatabaseManager.getInstance().closeDatabase();

        return affectedRows == 1;
    }

    public static void delete(long recurringBookingId) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        db.delete(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + "= ?", new String[]{"" + recurringBookingId});
        DatabaseManager.getInstance().closeDatabase();
    }

    private static ExpenseObject getExpense(Cursor c) throws EntityNotExistingException {
        //ich kann nun nicht mehr unterscheiden welchen typ von ausgabe ich genau habe
        //an sich nicht schlimm da ich auch Kinder aus dem ExpensesRepo bekomme, aber nicht der schönste weg
        //todo gibt es einen anderen Weg die Expense zu bekommen
        return ExpenseRepository.get(c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID)));
    }
}
