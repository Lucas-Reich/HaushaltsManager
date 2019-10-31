package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LineCounter {
    private int lineCount = 0;

    public LineCounter(String file) {
        countLines(file);
    }

    public int getLineCount() {
        return lineCount;
    }

    private void countLines(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            while (reader.readLine() != null) {
                lineCount++;
            }

            reader.close();
        } catch (IOException e) {
            lineCount = -1;
        }
    }
}
