package com.example.lucas.haushaltsmanager.Database.Repositories.Tags;


import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.QueryResultInterface;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.ArrayList;
import java.util.List;

public class Result implements QueryResultInterface<Tag> {
    private Cursor c;
    private TransformerInterface<Tag> transformer;

    public Result(Cursor c, TransformerInterface<Tag> transformer) {
        this.c = c;

        this.transformer = transformer;
    }

    public Tag getNextRow() {
        c.moveToNext();

        return transformer.transform(c);
    }

    public Tag getSingleResult() {
        Tag tag = getNextRow();
        close();

        return tag;
    }

    public List<Tag> getAll() {
        List<Tag> tags = new ArrayList<>();

        while (!c.isAfterLast()) {
            tags.add(getNextRow());
        }

        close();

        return tags;
    }

    public void close() {
        c.close();
    }
}
