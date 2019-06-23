package com.example.lucas.haushaltsmanager.Database.Repositories.Tags;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.QueryResultInterface;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.ArrayList;
import java.util.List;

public class QueryResult implements QueryResultInterface<Tag> {
    private Cursor mCursor;
    private TransformerInterface<Tag> mTransformer;

    public QueryResult(Cursor c, TransformerInterface<Tag> transformer) {
        mCursor = c;
        mTransformer = transformer;
    }

    public Tag getNextRow() {
        mCursor.moveToNext();

        return mTransformer.transform(mCursor);
    }

    public Tag getSingleResult() {
        Tag tag = getNextRow();
        close();

        return tag;
    }

    public List<Tag> getAll() {
        List<Tag> tags = new ArrayList<>();

        while (!mCursor.isAfterLast()) {
            tags.add(getNextRow());
        }

        close();

        return tags;
    }

    public void close() {
        mCursor.close();
    }
}
