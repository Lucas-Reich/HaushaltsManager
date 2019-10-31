package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;

import java.util.List;

public interface IParser<T> {
    List<IRequiredField> getRequiredFields();

    T parse(Line line, MappingList mappings) throws NoMappingFoundException, InvalidInputException;
}
