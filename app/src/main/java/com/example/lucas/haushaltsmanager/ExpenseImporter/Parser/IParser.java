package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;

public interface IParser {
    Account parseAccount(Line line) throws NoMappingFoundException, InvalidInputException;

    ExpenseObject parseBooking(Line line) throws NoMappingFoundException, InvalidInputException;
}
