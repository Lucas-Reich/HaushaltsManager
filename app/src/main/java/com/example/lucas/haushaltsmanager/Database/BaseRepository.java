package com.example.lucas.haushaltsmanager.Database;

public interface BaseRepository {
    void open();
    void close();
    boolean isOpen();

}
