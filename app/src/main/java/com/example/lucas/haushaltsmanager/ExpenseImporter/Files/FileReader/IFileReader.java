package com.example.lucas.haushaltsmanager.ExpenseImporter.Files.FileReader;

public interface IFileReader {
    int getLineCount();

    /**
     * Diese Methode gibt die erste Zeile der Datei zurück.
     * Dabei wird der interne Zeiger nicht bewegt.
     *
     * @return String
     */
    String getHeaderLine();

    /**
     * Diese Methode gibt die Zeile zurück, auf die der Zeiger aktuelle liegt.
     * Der Aufruf dieser Methode bewegt den Zeiger nicht.
     *
     * @return String
     */
    String getCurrentLine();

    /**
     * Methode bewegt den internen zeiger auf die nächste Zeile des files und gibt, je nachdem ob es möglich war, true oder false zurück.
     *
     * @return Boolean
     */
    boolean moveToNext() throws IllegalStateException;

    /**
     * Methode um den, vom FileReader belegten Speicher, freizugeben.
     */
    void close();
}
