package com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidLineException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.FileReader.IFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.ISub;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.ISaver;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Transformer.CSVTransformer;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Transformer.ITransformer;

import java.util.ArrayList;
import java.util.List;

public class Importer implements IImporter {
    private IFileReader fileReader;
    private ISaver saver;
    private List<ISub> subs;
    private ITransformer transformer;

    private boolean abortExecution = false;

    Importer(IFileReader fileReader, ISaver saver, ITransformer transformer) {
        this.fileReader = fileReader;
        this.saver = saver;
        this.transformer = transformer;

        subs = new ArrayList<>();
    }

    public static Importer createCSVImporter(IFileReader fileReader, ISaver saver) {
        String headerLine = fileReader.getHeaderLine();

        return new Importer(
                fileReader,
                saver,
                new CSVTransformer(headerLine)
        );
    }

    @Override
    public void run() {
        while (fileReader.moveToNext() && !abortExecution) {
            try {
                Line line = getCurrentLine(fileReader);

                boolean lineImportResult = saver.save(line); // TODO: Sollte ich die Exceptions dieser Methode auch hier abfangen?

                notifySubs(lineImportResult);
            } catch (InvalidLineException e) {
                notifySubs(false);
            }
        }

        saver.finish();
        fileReader.close();
    }

    @Override
    public void abort() {
        abortExecution = true;

        saver.revert();

        saver.finish();
        fileReader.close();
    }

    @Override
    public void addSub(ISub sub) {
        subs.add(sub);
    }

    @Override
    public void removeSub(ISub sub) {
        subs.remove(sub);
    }

    @Override
    public int totalSteps() {
        return fileReader.getLineCount();
    }

    private Line getCurrentLine(IFileReader fileReader) throws InvalidLineException {
        String currentLine = fileReader.getCurrentLine();

        return transformer.transform(currentLine);
    }

    private void notifySubs(boolean successful) {
        for (ISub sub : subs) {
            if (successful) {
                sub.notifySuccess();
                break;
            }

            sub.notifyFailure();
        }
    }
}
