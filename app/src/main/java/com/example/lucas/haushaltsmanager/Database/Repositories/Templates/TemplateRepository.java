package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions.CannotDeleteTemplateException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions.TemplateNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Template;

import java.util.ArrayList;
import java.util.List;

public class TemplateRepository {

    public static boolean exists(Template template) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + "." + ExpensesDbHelper.TEMPLATE_COL_ID + " = " + template.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + "." + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID + " = " + template.getTemplate().getIndex()
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

    public static boolean existsWithoutIndex(ExpenseObject expense) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        //todo kann man irgendwie anders überprüfen ob eine ausgabe ein Template ist?

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + "." + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID + " = " + expense.getIndex()
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

    public static Template get(long templateId) throws TemplateNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + "." + ExpensesDbHelper.TEMPLATE_COL_ID + ","
                + " " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + "." + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID
                + " FROM " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + "." + ExpensesDbHelper.TEMPLATE_COL_ID + " = " + templateId;

        Cursor c = db.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new TemplateNotFoundException(templateId);
        }

        try {
            Template template = cursorToTemplate(c);

            c.close();
            DatabaseManager.getInstance().closeDatabase();
            return template;
        } catch (ExpenseNotFoundException e) {

            c.close();
            DatabaseManager.getInstance().closeDatabase();
            throw new TemplateNotFoundException(templateId);
        }
    }

    public static List<Template> getAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + "." + ExpensesDbHelper.TEMPLATE_COL_ID + ","
                + " " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + "." + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID
                + " FROM " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + ";";

        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();
        ArrayList<Template> templateBookings = new ArrayList<>();
        while (!c.isAfterLast()) {
            long templateId = c.getLong(c.getColumnIndex(ExpensesDbHelper.TEMPLATE_COL_ID));
            long expenseId = c.getLong(c.getColumnIndex(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID));

            try {

                templateBookings.add(new Template(expenseId, ExpenseRepository.get(expenseId)));
            } catch (ExpenseNotFoundException e) {

                //Kann die Buchung zu einem Template nicht mehr gefunden werden wird das Template aus der Datenbank gelöscht.
                db.delete(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, ExpensesDbHelper.TEMPLATE_COL_ID + " = ?", new String[]{"" + templateId});
            }

            c.moveToNext();
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return templateBookings;
    }

    public static Template insert(Template template) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        //todo ich sollte überprüfen ob es die Buchung template.getTemplate() auch wirklich in der Buchungs Tabelle gibt

        ExpenseRepository.assertSavableExpense(template.getTemplate());

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID, template.getTemplate().getIndex());

        long insertedTemplateId = db.insert(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return new Template(
                insertedTemplateId,
                template.getTemplate()
        );
    }

    public static void delete(Template template) throws CannotDeleteTemplateException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        try {
            db.delete(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID + " = ?", new String[]{"" + template.getIndex()});
            DatabaseManager.getInstance().closeDatabase();

            if (ExpenseRepository.isHidden(template.getTemplate())) {
                ExpenseRepository.delete(template.getTemplate());
            }
        } catch (ExpenseNotFoundException e) {

            throw new CannotDeleteTemplateException(template);
        } catch (CannotDeleteExpenseException e) {

            throw new CannotDeleteTemplateException(template);
        }
    }

    public static void update(Template template) throws TemplateNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID, template.getTemplate().getIndex());

        int affectedRows = db.update(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, values, ExpensesDbHelper.TEMPLATE_COL_ID + " = ?", new String[]{"" + template.getIndex()});
        DatabaseManager.getInstance().closeDatabase();

        if (affectedRows == 0) {
            throw new TemplateNotFoundException(template.getIndex());
        }
    }

    public static Template cursorToTemplate(Cursor c) throws ExpenseNotFoundException {
        long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.TEMPLATE_COL_ID));
        long expenseIndex = c.getLong(c.getColumnIndex(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID));

        return new Template(
                index,
                ExpenseRepository.get(expenseIndex)
        );
    }
}
