package com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.Utils.DelimiterIdentifier;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;

public interface IDelimiterIdentifier {
    IDelimiter identifyDelimiter(String input);
}
