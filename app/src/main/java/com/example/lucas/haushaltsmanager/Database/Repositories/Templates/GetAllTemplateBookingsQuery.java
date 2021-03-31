package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;

class GetAllTemplateBookingsQuery implements QueryInterface {
    @Override
    public String sql() {
        return "SELECT "
                + "TEMPLATE_BOOKINGS.id, "
                + "TEMPLATE_BOOKINGS.expense_type, "
                + "TEMPLATE_BOOKINGS.price, "
                + "TEMPLATE_BOOKINGS.expenditure, "
                + "TEMPLATE_BOOKINGS.title, "
                + "TEMPLATE_BOOKINGS.date, "
                + "TEMPLATE_BOOKINGS.account_id, "
                + "TEMPLATE_BOOKINGS.category_id, "
                + "CATEGORIES.name, "
                + "CATEGORIES.color, "
                + "CATEGORIES.default_expense_type "
                + "FROM TEMPLATE_BOOKINGS "
                + "LEFT JOIN CATEGORIES ON TEMPLATE_BOOKINGS.category_id = CATEGORIES.id "
                + "ORDER BY TEMPLATE_BOOKINGS.date DESC;";
    }

    @Override
    public Object[] values() {
        return new Object[]{};
    }
}
