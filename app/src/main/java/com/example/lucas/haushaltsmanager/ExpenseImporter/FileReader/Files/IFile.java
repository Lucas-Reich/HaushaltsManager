package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files;

public interface IFile {
    /**
     * Gibt den Typ der Datei zurück.
     * z.B.: csv, pdf, ...
     *
     * @return Dateityp
     */
    String getType();

    /**
     * Gibt den Pfad der Datei zurück
     *
     * @return Dateipfad
     */
    String getPath();
}
