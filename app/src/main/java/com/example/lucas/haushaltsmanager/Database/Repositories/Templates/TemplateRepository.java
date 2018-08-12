package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions.CannotDeleteTemplateException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions.TemplateNotExistingException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.List;

public class TemplateRepository {

    public static boolean exists(ExpenseObject template) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + "." + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID + " = " + template.getIndex()
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

    public static ExpenseObject get(long templateId) throws TemplateNotExistingException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID + ","
                + " FROM " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID + " = " + templateId;

        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();

        ExpenseObject template;
        try {
            template = ChildExpenseRepository.get(c.getLong(c.getColumnIndex(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID)));
        } catch (ChildExpenseNotFoundException e) {
            throw new TemplateNotExistingException(templateId);
        } finally {
            c.close();
        }

        DatabaseManager.getInstance().closeDatabase();
        return template;
    }

    public static List<ExpenseObject> getAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID
                + " FROM " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + ";";

        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();
        ArrayList<ExpenseObject> templateBookings = new ArrayList<>();
        while (!c.isAfterLast()) {

            long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID));

            try {
                templateBookings.add(ExpenseRepository.get(index));
            } catch (ExpenseNotFoundException e) {
                //Kann die Buchung zu einem Template nicht mehr gefunden werden wird das Template aus der Datenbank gelöscht.
                //Es gibt nämlich keine weg mehr eine glöschte Buchung wiederherzustellen.
                db.delete(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, ExpensesDbHelper.TEMPLATE_COL_ID + " = ?", new String[]{"" + index});
            }

            c.moveToNext();
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return templateBookings;
    }

    public static ExpenseObject insert(ExpenseObject template) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ExpenseRepository.assertSavableExpense(template);

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID, template.getIndex());

        long insertedTemplateId = db.insert(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return new ExpenseObject(
                insertedTemplateId,
                template.getTitle(),
                template.getUnsignedPrice(),
                template.getDateTime(),
                template.isExpenditure(),
                template.getCategory(),
                template.getNotice(),
                template.getAccountId(),
                template.getExpenseType(),
                template.getTags(),
                template.getChildren(),
                template.getCurrency()
        );
    }

    public static void delete(ExpenseObject template) throws CannotDeleteTemplateException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        if (!exists(template))
            throw new CannotDeleteTemplateException(template);

        db.delete(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID + " = ?", new String[]{"" + template.getIndex()});
        DatabaseManager.getInstance().closeDatabase();
    }

    public static boolean update(long templateId, ExpenseObject newTemplate) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID, newTemplate.getIndex());

        int affectedRows = db.update(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, values, ExpensesDbHelper.TEMPLATE_COL_ID + " = ?", new String[]{"" + templateId});
        DatabaseManager.getInstance().closeDatabase();

        return affectedRows == 1;
    }
}
