package com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImportStrategies.IImportStrategy;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.IFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.ISub;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.util.ArrayList;
import java.util.List;

public class Importer implements IImporter {
    private List<ISub> subs = new ArrayList<>();

    private IFileReader fileReader;
    private MappingList mappingList;

    private IImportStrategy importStrategy;

    public Importer(IFileReader fileReader, IImportStrategy strategy) {
        this.fileReader = fileReader;
        this.importStrategy = strategy;
    }

    @Override
    public void run() {
        while (fileReader.moveToNext()) {
            try {
                Line line = fileReader.getCurrentLine();

                importStrategy.handle(line, mappingList);

                notifySubs(true);
            } catch (NoMappingFoundException | InvalidInputException e) {
                notifySubs(false);
            }
        }

        releaseResources();
    }

    @Override
    public void abort() {
        fileReader.close();
        importStrategy.abort();

        releaseResources();
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

    public List<IRequiredField> getRequiredFields() {
        return importStrategy.getRequiredFields();
    }

    public void setMapping(MappingList mapping) {
        this.mappingList = mapping;
    }

    private void releaseResources() {
        fileReader.close();

        importStrategy.finish();
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
