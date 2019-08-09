package com.example.lucas.haushaltsmanager.ExpenseImporter.Transformer;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidLineException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;

public interface ITransformer {
    /**
     * Transformiert die Zeile einer Datei in die interne Datenstruktur "Line".
     *
     * @param string Zeile einer Datei
     * @return Line
     */
    Line transform(String string) throws InvalidLineException;
}
