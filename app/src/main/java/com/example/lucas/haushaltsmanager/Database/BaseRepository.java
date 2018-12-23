package com.example.lucas.haushaltsmanager.Database;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

import java.util.List;

public interface BaseRepository<T> {

    T create(T entity);

    T get(long entityId) throws EntityNotExistingException;

    List<T> getAll();

    void update(T entity) throws EntityNotExistingException;

    void delete(T entity) throws CouldNotDeleteEntityException;

    boolean exists(T entity);

    void closeDatabase();
}
