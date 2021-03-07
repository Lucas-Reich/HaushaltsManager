package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Template;

import java.util.ArrayList;
import java.util.List;

public class TemplateRepository {
    private SQLiteDatabase mDatabase;
    private ExpenseRepository mBookingRepo;

    public TemplateRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        mBookingRepo = new ExpenseRepository(context);
    }

    public boolean existsWithoutIndex(ExpenseObject expense) {
        Cursor c = executeRaw(new TemplateBookingExistsQuery(expense));

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public List<Template> getAll() {
        Cursor c = executeRaw(new GetAllTemplateBookingsQuery());

        c.moveToFirst();
        ArrayList<Template> templateBookings = new ArrayList<>();
        while (!c.isAfterLast()) {
            long templateId = c.getLong(c.getColumnIndex(ExpensesDbHelper.TEMPLATE_COL_ID));
            long expenseId = c.getLong(c.getColumnIndex(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID));

            try {

                templateBookings.add(new Template(expenseId, mBookingRepo.get(expenseId)));
            } catch (ExpenseNotFoundException e) {

                //Kann die Buchung zu einem Template nicht mehr gefunden werden wird das Template aus der Datenbank gelöscht.
                mDatabase.delete(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, ExpensesDbHelper.TEMPLATE_COL_ID + " = ?", new String[]{"" + templateId});
            }

            c.moveToNext();
        }

        c.close();
        return templateBookings;
    }

    public Template insert(Template template) {

        // TODO: Ich sollte überprüfen ob es die Buchung template.getTemplate() auch wirklich in der Buchungs Tabelle gibt

        mBookingRepo.assertSavableExpense(template.getTemplate());

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID, template.getTemplate().getIndex());

        long insertedTemplateId = mDatabase.insert(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, null, values);

        return new Template(
                insertedTemplateId,
                template.getTemplate()
        );
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }
}
