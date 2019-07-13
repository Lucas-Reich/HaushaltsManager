package com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Queries;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Common.IQuery;

public class GetQuery implements IQuery {
    private final long mId;

    public GetQuery(long id) {
        mId = id;
    }

    public String getQuery() {
        return "SELECT "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_NAME
                + " FROM " + ExpensesDbHelper.TABLE_TAGS
                + " WHERE " + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID + " = ?"
                + ";";
    }

    public String[] getDefinition() {
        return new String[]{
                Long.toString(mId)
        };
    }
}
