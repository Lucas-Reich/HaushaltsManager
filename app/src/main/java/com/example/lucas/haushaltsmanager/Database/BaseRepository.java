package com.example.lucas.haushaltsmanager.Database;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;

import java.util.ArrayList;
import java.util.Calendar;

public interface BaseRepository {
    void open();

    void close();

    boolean isOpen();

    boolean has();

    Object getEntityById(long entityId) throws EntityNotExistingException;

    ArrayList<Object> getAll();

    ArrayList<Object> getAll(Calendar from, Calendar till);

    Object insert(Object entity);//sollte eine exception auslösen, wenn die entity bereits vorhanden ist

    void delete(long entityId);//sollte eine exception auslösen, wenn nicht gelöscht werden kann

    Object cursorToEntity(Cursor c);
}
