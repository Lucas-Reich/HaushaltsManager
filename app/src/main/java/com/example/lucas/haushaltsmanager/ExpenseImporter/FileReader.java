package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.Entities.File;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.IFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ILine;

public class FileReader implements IFileReader {
    private File file;

    public FileReader(File file) {
        this.file = file;
    }

    @Override
    public ILine get(int lineNumber) {
        return null;
    }

    @Override
    public ILine getCurrent() {
        return null;
    }

    @Override
    public boolean moveToNext() {
        return false;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
