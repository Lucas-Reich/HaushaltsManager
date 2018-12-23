package com.example.lucas.haushaltsmanager.Database.Repositories.Tags;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Tag;

public class TagTransformer implements TransformerInterface<Tag> {

    @Override
    public Tag transform(Cursor c) {
        if (c.isAfterLast()) {
            return null;
        }

        return fromCursor(c);
    }

    private Tag fromCursor(Cursor c) {
        return new Tag(
                c.getLong(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_ID)),
                c.getString(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_NAME))
        );
    }
}
