package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;

import java.util.List;

public interface IParser<T> {
    @NonNull
    List<IRequiredField> getRequiredFields();

    @NonNull
    T parse(@NonNull Line line, @NonNull MappingList mappings) throws NoMappingFoundException, InvalidInputException;
}
