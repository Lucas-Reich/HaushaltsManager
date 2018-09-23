package com.example.lucas.haushaltsmanager.Database;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

import java.util.List;

public interface BaseRepository<T> {

    T create(T entity);

    T get(long entityId) throws EntityNotExistingException;

    List<T> getAll();

    void update(T entity) throws EntityNotExistingException;//todo oder sollte ich eine CouldNotUpdateEntityException zurückgeben?

    void delete(T entity) throws CouldNotDeleteEntityException;

    boolean exists(T object);

//    T static fromCursor(Cursor c);
}
