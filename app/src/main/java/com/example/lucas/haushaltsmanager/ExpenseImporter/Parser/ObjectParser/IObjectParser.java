package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;

public interface IObjectParser<T> {
    T parse(Line line, MappingList mapping);
}
