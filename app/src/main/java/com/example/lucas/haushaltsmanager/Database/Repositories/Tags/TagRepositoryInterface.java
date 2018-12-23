package com.example.lucas.haushaltsmanager.Database.Repositories.Tags;

import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.List;

public interface TagRepositoryInterface {

    Tag save(Tag tag);

    Tag find(long id);

    List<Tag> getAll();

    void update(Tag tag);

    void delete(Tag tag);

    boolean exists(Tag tag);
}
