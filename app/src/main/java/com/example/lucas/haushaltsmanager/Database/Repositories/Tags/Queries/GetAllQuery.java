package com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Queries;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Common.IQuery;

public class GetAllQuery implements IQuery {

    @Override
    public String getQuery() {
        return "SELECT "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_NAME
                + " FROM " + ExpensesDbHelper.TABLE_TAGS + ";";
    }

    @Override
    public String[] getDefinition() {
        return new String[0];
    }
}
