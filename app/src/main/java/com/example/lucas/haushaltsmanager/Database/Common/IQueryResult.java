package com.example.lucas.haushaltsmanager.Database.Common;

import android.database.Cursor;

public interface IQueryResult {
    /**
     * Diese Methode bewegt den Zeiger auf das nächste Element.
     * Gibt es kein weiteres Element gibt diese Methode FALSE zurück, andernfalls TRUE.
     *
     * @return TRUE, wenn der Zeiger erfolgreich weiterbewegt wurde.
     */
    boolean moveToNext();

    /**
     * Methode, welche das aktuell ausgewählte Element zurückgibt.
     *
     * @return Cursor
     */
    Cursor getCurrent();

    /**
     * Methode welche die Verbindung zur Datenbank schließt und Ressourcen wieder freigibt.
     */
    void close();
}