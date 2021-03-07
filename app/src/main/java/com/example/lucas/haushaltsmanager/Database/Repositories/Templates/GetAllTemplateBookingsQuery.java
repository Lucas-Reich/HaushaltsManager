package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;

class GetAllTemplateBookingsQuery implements QueryInterface {
    @Override
    public String sql() {
        return "SELECT "
                + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + "." + ExpensesDbHelper.TEMPLATE_COL_ID + ","
                + " " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + "." + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID
                + " FROM " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + ";";
    }

    @Override
    public Object[] values() {
        return new Object[]{};
    }
}
