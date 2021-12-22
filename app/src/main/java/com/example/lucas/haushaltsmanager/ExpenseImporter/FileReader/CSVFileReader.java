package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader;

import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.CSVFile;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.Utils.LineCounter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class CSVFileReader implements IFileReader {
    private final int lineCount;

    private final Scanner scanner;
    private final CSVFile file;
    private String currentLine;

    public CSVFileReader(CSVFile file) throws FileNotFoundException {
        scanner = new Scanner(new FileReader(file));

        this.file = file;
        this.lineCount = new LineCounter(file.getPath()).getLineCount();
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
    public void close() {
        scanner.close();
    }

    @Override
    public String getHeaderLine() {
        return file.getHeader();
    }

    @Override
    public Line getCurrentLine() {
        if (null == currentLine) {
            return null;
        }

        return new Line(
                currentLine,
                file.getDelimiter()
        );
    }

    private boolean isClosed(Scanner scanner) {
        try {
            scanner.hasNextLine();

            return false;
        } catch (IllegalStateException e) {
            return true;
        }
    }
}