package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;

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
     * @return Line
     */
    Line getCurrentLine();

    /**
     * Methode bewegt den internen zeiger auf die nächste Zeile des files und gibt, je nachdem ob es möglich war, true oder false zurück.
     *
     * @return Boolean
     */
    boolean moveToNext();

    /**
     * Methode um den, vom CSVFileReader belegten Speicher, freizugeben.
     */
    void close();
}
