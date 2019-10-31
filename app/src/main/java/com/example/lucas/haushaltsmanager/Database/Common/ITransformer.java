package com.example.lucas.haushaltsmanager.Database.Common;

public interface ITransformer<T> {
    /**
     * Diese Methode sollte genutzt werden, wenn mehr als ein Eintrag aus der Datenbank erwartet wird.
     *
     * @param queryResult Ergebnis der Query
     * @return Transformiertes Objekt
     */
    T transform(IQueryResult queryResult);

    /**
     * Diese Methode sollte genutzt werden, wenn nur ein Eintrag aus der Datenbank erwartet wird.
     * Die Methode schließt den Cursor nach den benutzung.
     *
     * @param queryResult Ergebnis der Query
     * @return Transformiertes Objekt
     */
    T transformAndClose(IQueryResult queryResult);
}