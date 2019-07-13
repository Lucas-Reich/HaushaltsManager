package com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Queries;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Common.IQuery;
import com.example.lucas.haushaltsmanager.Entities.Tag;

public class DeleteQuery implements IQuery {
    private final Tag mTag;

    public DeleteQuery(Tag tag) {
        mTag = tag;
    }

    @Override
    public String getQuery() {
        return "DELETE FROM "
                + ExpensesDbHelper.TABLE_TAGS
                + " WHERE "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID + " = ?"
                + ";";
    }

    @Override
    public String[] getDefinition() {
        return new String[]{
                Long.toString(mTag.getIndex())
        };
    }
}
