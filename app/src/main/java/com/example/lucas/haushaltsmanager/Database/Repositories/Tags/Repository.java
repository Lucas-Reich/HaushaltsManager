package com.example.lucas.haushaltsmanager.Database.Repositories.Tags;

import com.example.lucas.haushaltsmanager.Database.Common.DefaultDatabase;
import com.example.lucas.haushaltsmanager.Database.Common.IQueryResult;
import com.example.lucas.haushaltsmanager.Database.Common.ITransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Queries.DeleteQuery;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Queries.GetQuery;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Queries.InsertQuery;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Queries.UpdateQuery;
import com.example.lucas.haushaltsmanager.Entities.Tag;

public class Repository implements TagRepositoryInterface {
    private DefaultDatabase database;
    private ITransformer<Tag> transformer;

    Repository(DefaultDatabase database) {
        this.database = database;
        transformer = new TagTransformer();
    }

    @Override
    public Tag save(Tag tag) {
        IQueryResult result = database.query(null);

        return transformer.transform(result);
    }

    @Override
    public Tag find(long id) {
        IQueryResult result = database.query(null);

        return transformer.transform(result);
    }

    @Override
    public void update(Tag tag) {
        IQueryResult result = database.query(null);

        result.close();
    }

    @Override
    public void delete(Tag tag) {
        IQueryResult result = database.query(null);

        result.close();
    }

    @Override
    public boolean exists(Tag tag) {
        return find(tag.getIndex()) == null;
    }
}
