package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader;

import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.Utils.LineCounter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LineCounterTest {
    // TODO: Kann ich irgendwie den BufferedReader in dem LineCounter mocken, sodass ich die Klasse geiler testen kann?
    //  somit brauch ich auch keine richtigen Dateien mehr

    @Test
    public void canHandleInvalidFilePath() {
        LineCounter counter = new LineCounter("invalid/file/path");

        assertEquals(-1, counter.getLineCount());
    }

    @Test
    public void correctlyCountsLinesOfFile() {
        String emptyFilePath = "/Users/lucas/StudioProjects/HaushaltsManager/app/src/test/java/com/example/lucas/haushaltsmanager/ExpenseImporter/FileReader/TextFiles/emptyFile.txt";
        LineCounter counter = new LineCounter(emptyFilePath);

        assertEquals(0, counter.getLineCount());
    }

    @Test
    public void correctlyCountsLinesOfFile2() {
        String multilineFilePath = "/Users/lucas/StudioProjects/HaushaltsManager/app/src/test/java/com/example/lucas/haushaltsmanager/ExpenseImporter/FileReader/TextFiles/multiLineFile.txt";
        LineCounter counter = new LineCounter(multilineFilePath);

        assertEquals(11, counter.getLineCount());
    }
}
