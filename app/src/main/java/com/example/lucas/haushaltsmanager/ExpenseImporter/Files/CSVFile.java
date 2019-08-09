package com.example.lucas.haushaltsmanager.ExpenseImporter.Files;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.DelimiterInterface;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class CSVFile extends File {
    private static final String REQUIRED_FILE_EXTENSION = ".csv";

    private DelimiterInterface delimiter;
    private String header;

    private CSVFile(String filePath) throws FileNotFoundException {
        super(filePath);

        header = readFirstLine(filePath);

        delimiter = new DelimiterIdentifier().identifyDelimiter(header);
    }

    public static CSVFile open(String path) throws FileNotFoundException {
        assertCorrectFileExtension(path);

        return new CSVFile(path);
    }

    public String getHeader() {
        return header;
    }

    public String[] getHeaders() {
        return header.split(delimiter.getDelimiter());
    }

    public DelimiterInterface getDelimiter() {
        return delimiter;
    }

    private static void assertCorrectFileExtension(String file) {
        ExtensionVerifier verifier = new ExtensionVerifier(REQUIRED_FILE_EXTENSION);

        if (verifier.verifyExtension(file)) {
            return;
        }

        throw new InvalidFileException(REQUIRED_FILE_EXTENSION, file);
    }

    private String readFirstLine(String filePath) throws FileNotFoundException {
        // TODO: Was mache ich, wenn es keine Zeilen in der Datei gibt?
        Scanner scanner = new Scanner(new FileReader(filePath));
        String firstLine = scanner.nextLine();
        scanner.close();

        return firstLine;
    }

}
