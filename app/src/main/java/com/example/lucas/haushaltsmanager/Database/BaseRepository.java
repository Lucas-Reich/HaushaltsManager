package com.example.lucas.haushaltsmanager.Database;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

import java.util.List;

public interface BaseRepository<T> {

    T create(T entity);

    T get(long entityId) throws EntityNotExistingException;

    List<T> getAll();

    void update(T entity) throws EntityNotExistingException;// TODO: Sollte ich eine CouldNotUpdateEntityException auslösen?

    void delete(T entity) throws CouldNotDeleteEntityException;

    boolean exists(T object);

    void closeDatabase();

//    T static fromCursor(Cursor c);
}
