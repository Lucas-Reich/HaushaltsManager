package com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Queries;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Common.IQuery;
import com.example.lucas.haushaltsmanager.Entities.Tag;

public class UpdateQuery implements IQuery {
    private final Tag mTag;

    public UpdateQuery(Tag tag) {
        mTag = tag;
    }

    @Override
    public String getQuery() {
        return "UPDATE"
                + ExpensesDbHelper.TABLE_TAGS
                + " SET "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_NAME + " = ?"
                + " WHERE "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID + " = ?";
    }

    @Override
    public String[] getDefinition() {
        return new String[]{
                mTag.getName(),
                Long.toString(mTag.getIndex())
        };
    }
}
