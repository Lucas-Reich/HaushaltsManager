package com.example.lucas.haushaltsmanager.Database.Common;

public interface IQuery {
    /**
     * Diese Methode definiert die auszuführende Query.
     * Sie enthält das SQL statement, welches später ausgeführt werden soll.
     *
     * @return SQL Statement
     */
    String getQuery();

    /**
     * Diese Methode enthält Argumente, welche in die Query eingefügt werden sollen.
     *
     * @return Query Argumente
     */
    String[] getDefinition();
}