package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

class ChildBookingExistsQuery implements QueryInterface {
    private final ExpenseObject childBooking;

    public ChildBookingExistsQuery(ExpenseObject childBooking) {
        this.childBooking = childBooking;
    }

    @Override
    public String sql() {
        return "SELECT *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_TITLE + " = '%s'"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PRICE + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE + " = '%s'"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " = '%s'"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_NOTICE + " = '%s'"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PARENT_ID + " IS NOT NULL"
                + " LIMIT 1;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                childBooking.getIndex(),
                childBooking.getTitle(),
                childBooking.getUnsignedPrice(),
                childBooking.getExpenseType().name(),
                childBooking.getCategory().getIndex(),
                childBooking.getAccountId(),
                (childBooking.isExpenditure() ? 1 : 0),
                childBooking.getDate().getTimeInMillis(),
                childBooking.getNotice(),
                childBooking.getCurrency().getIndex()
        };
    }
}
