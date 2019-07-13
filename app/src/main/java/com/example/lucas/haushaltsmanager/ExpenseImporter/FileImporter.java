package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.File;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.IDatabaseInserter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.IFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ILine;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ITransformer;

public class FileImporter {
    private IFileReader fileReader;
    private ITransformer transformer;
    private IDatabaseInserter inserter;

    public FileImporter(File file) {
        fileReader = new FileReader(file);
    }

    public void importFile() {

        while (fileReader.moveToNext()) {
            ILine line = fileReader.getCurrent();

            ExpenseObject expense = transformer.transform(line); // TODO: Was passiert, wenn die Linie nicht transformiert werden konnte?

            boolean wasSuccessful = inserter.insert(expense);

            if (wasSuccessful) {
                updateProgressBar();
            } else {
                saveToConflict(expense, line);
            }
        }
    }

    private void saveToConflict(ExpenseObject expense, ILine line) {
        // TODO: Konnte eine Zeile des Files nicht gespeichert werden sollte diese dem User am Ende präsentiert werden.
        //  Der User soll dann entscheiden, was mit ihnen passieren soll.
    }

    private void updateProgressBar() {
        // TODO: Dem User soll eine fortschritts Anzeige angezeigt werden, sodass er weiß, wie der Stand des Imports ist.

        // TODO: Sollte ich dem User während des Imports anzeigen, wenn eine Linie nicht importiert werden konnte?
    }
}
