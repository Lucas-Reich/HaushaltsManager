package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;

public class File extends java.io.File implements IFile {
    private final String path;

    public File(@NonNull String path) throws InvalidFileException {
        super(path);

        this.path = path;
    }

    @Override
    public String getType() {
        int indexOfDot = path.indexOf(".");

        return path.substring(indexOfDot + 1);

    }

    @Override
    @NonNull
    public String getPath() {
        return path;
    }

    protected void assertIsFile() throws InvalidFileException {
        if (isFile()) {
            return;
        }

        throw InvalidFileException.notAFile(path);
    }
}
