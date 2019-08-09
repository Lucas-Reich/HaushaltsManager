package com.example.lucas.haushaltsmanager.ExpenseImporter.Files.FileReader;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.IFile;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.LineCounter;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReader implements IFileReader {
    private static final String REQUIRED_FILE_EXTENSION = "csv";

    private Scanner scanner;
    private int lineCount;
    private String currentLine;
    private final String headerLine;

    public FileReader(IFile file) throws FileNotFoundException {
        scanner = new Scanner(new java.io.FileReader(file.getPath()));

        headerLine = scanner.nextLine();

        scanner = new Scanner(new java.io.FileReader(file.getPath())); // Hack, um den internen Scanner Zeiger zu resetten.

        lineCount = countLines(file.getPath());
    }

    public static IFileReader read(IFile file) throws InvalidFileException, FileNotFoundException {
        assertCorrectFileExtension(file);

        return new FileReader(file);
    }

    @Override
    public int getLineCount() {
        return lineCount;
    }

    @Override
    public boolean moveToNext() {
        if (isClosed(scanner) || !scanner.hasNextLine()) {
            return false;
        }

        currentLine = scanner.nextLine();

        return true;
    }

    @Override
    public String getCurrentLine() {
        return currentLine;
    }

    @Override
    public void close() {
        scanner.close();
    }

    @Override
    public String getHeaderLine() {
        return headerLine;
    }

    private boolean isClosed(Scanner scanner) {
        try {
            scanner.hasNextLine();

            return false;
        } catch (IllegalStateException e) {
            return true;
        }
    }

    private int countLines(String path) {
        LineCounter counter = new LineCounter(path);

        return counter.getLineCount();
    }

    private static void assertCorrectFileExtension(IFile path) throws InvalidFileException {
        String fileExtension = path.getExtension();

        if (!fileExtension.equals(REQUIRED_FILE_EXTENSION)) {
            throw new InvalidFileException(REQUIRED_FILE_EXTENSION, path.getPath());
        }
    }
}