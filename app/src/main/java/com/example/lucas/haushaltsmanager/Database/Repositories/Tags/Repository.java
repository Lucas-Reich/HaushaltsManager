package com.example.lucas.haushaltsmanager.Database.Repositories.Tags;

import com.example.lucas.haushaltsmanager.Database.DefaultDatabase;
import com.example.lucas.haushaltsmanager.Database.QueryResultInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Queries.*;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.List;

public class Repository implements TagRepositoryInterface {
    private DefaultDatabase database;

    Repository(DefaultDatabase database) {
        this.database = database;
    }

    @Override
    public Tag save(Tag tag) {
        QueryResultInterface<Tag> result = database.query(new InsertQuery(tag));

        return result.getSingleResult();
    }

    public Tag find(long id) {
        QueryResultInterface<Tag> result = database.query(new GetQuery(id));

        return result.getSingleResult();
    }

    @Override
    public List<Tag> getAll() {
        QueryResultInterface<Tag> result = database.query(new GetAllQuery());

        return result.getAll();
    }

    @Override
    public void update(Tag tag) {
        QueryResultInterface<Tag> result = database.query(new UpdateQuery(tag));

        result.close();
    }

    @Override
    public void delete(Tag tag) {
        QueryResultInterface<Tag> result = database.query(new DeleteQuery(tag));

        result.close();
    }

    @Override
    public boolean exists(Tag tag) {
        return find(tag.getIndex()) == null;

    }
}
