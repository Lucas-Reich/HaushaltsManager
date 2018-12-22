package com.example.lucas.haushaltsmanager.Database;

import java.util.List;

public interface QueryResultInterface<T> {
    T getNextRow();

    T getSingleResult();

    List<T> getAll();

    void close();
}