package com.example.lucas.haushaltsmanager.Database.Repositories.Tags;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.Common.IQueryResult;
import com.example.lucas.haushaltsmanager.Database.Common.ITransformer;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Tag;

public class TagTransformer implements ITransformer<Tag> {

    @Override
    public Tag transform(IQueryResult queryResult) {
        if (!queryResult.moveToNext()) {
            return null;
        }

        return fromCursor(queryResult.getCurrent());
    }

    @Override
    public Tag transformAndClose(IQueryResult queryResult) {
        Tag tag = transform(queryResult);

        queryResult.close();

        return tag;
    }

    private Tag fromCursor(Cursor c) {
        return new Tag(
                c.getLong(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_ID)),
                c.getString(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_NAME))
        );
    }
}
