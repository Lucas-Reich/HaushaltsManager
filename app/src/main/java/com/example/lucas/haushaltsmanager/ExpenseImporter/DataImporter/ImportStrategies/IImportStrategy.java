package com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImportStrategies;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.util.List;

public interface IImportStrategy {
    List<IRequiredField> getRequiredFields();

    void handle(Line line, MappingList mapping) throws NoMappingFoundException, InvalidInputException;

    void abort();

    void finish();
}
