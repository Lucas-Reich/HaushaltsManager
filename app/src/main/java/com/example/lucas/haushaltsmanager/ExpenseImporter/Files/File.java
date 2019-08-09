package com.example.lucas.haushaltsmanager.ExpenseImporter.Files;

public class File implements IFile {
    private final String path;

    public File(String path) {
        // TODO: Hier müsste ich sicherstellen, dass der agegebene Pfad auch eine Datei ist
        this.path = path;
    }

    @Override
    public String getExtension() {
        int indexOfDot = path.indexOf(".");

        return path.substring(indexOfDot + 1);

    }

    @Override
    public String getPath() {
        return path;
    }
}
