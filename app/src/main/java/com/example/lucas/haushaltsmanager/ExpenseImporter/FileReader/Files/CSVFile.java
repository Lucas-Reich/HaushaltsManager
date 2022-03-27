package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files;

import android.util.Log;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.Utils.DelimiterIdentifier.CSVDelimiterIdentifier;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.Utils.FileTypeVerifier;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

// TODO: Gibt es ein Design Pattern mit dem ich das erweitern vermeiden kann?
public class CSVFile extends File {
    private static final String REQUIRED_FILE_EXTENSION = "csv";

    private final IDelimiter delimiter;
    private final String header;

    private CSVFile(String filePath) throws FileNotFoundException, InvalidFileException {
        super(filePath);
        assertIsFile();

        header = readFirstLine(filePath);

        delimiter = new CSVDelimiterIdentifier().identifyDelimiter(header);
    }

    public static CSVFile open(String path) throws FileNotFoundException, InvalidFileException {
        assertCorrectType(path);

        return new CSVFile(path);
    }

    public String getHeader() {
        return header;
    }

    public String[] getHeaders() {
        return header.split(delimiter.getDelimiter());
    }

    public IDelimiter getDelimiter() {
        return delimiter;
    }

    private static void assertCorrectType(String file) throws InvalidFileException {
        FileTypeVerifier verifier = new FileTypeVerifier(REQUIRED_FILE_EXTENSION);

        if (verifier.verifyType(file)) {
            return;
        }

        throw InvalidFileException.invalidType(REQUIRED_FILE_EXTENSION, file, FileUtils.getType(file));
    }

    private String readFirstLine(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileReader(filePath));

        String firstLine = "";

        try {
            firstLine = scanner.nextLine();

            scanner.close();
        } catch (NoSuchElementException e) {
            scanner.close();
        }

        return firstLine;
    }

}
