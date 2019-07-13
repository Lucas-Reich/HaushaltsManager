package com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Queries;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Common.IQuery;
import com.example.lucas.haushaltsmanager.Entities.Tag;

public class InsertQuery implements IQuery {
    private final Tag mTag;

    public InsertQuery(Tag tag) {
        mTag = tag;
    }

    @Override
    public String getQuery() {
        return "INSERT INTO"
                + ExpensesDbHelper.TABLE_TAGS + "("
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID + ","
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_NAME + ")"
                + " VALUES (?, ?);";
    }

    @Override
    public String[] getDefinition() {
        return new String[]{
                Long.toString(mTag.getIndex()),
                mTag.getName()
        };
    }
}
