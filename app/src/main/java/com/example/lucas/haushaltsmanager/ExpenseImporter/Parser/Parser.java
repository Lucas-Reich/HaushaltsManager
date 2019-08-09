package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.AccountParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.BookingParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;

public class Parser implements IParser {
    // TODO: Kann ich diesen Parser durch die einzelnen ObjectParser ersetzen?
    private final Currency mainCurrency;
    private final MappingList mappingList;

    public Parser(Currency mainCurrency, MappingList mappingList) {
        this.mainCurrency = mainCurrency;
        this.mappingList = mappingList;
    }

    @Override
    public Account parseAccount(Line line) throws NoMappingFoundException, InvalidInputException {
        AccountParser parser = new AccountParser(mainCurrency);

        return parser.parse(line, mappingList);
    }

    @Override
    public ExpenseObject parseBooking(Line line) throws NoMappingFoundException, InvalidInputException {
        BookingParser parser = new BookingParser(mainCurrency);

        return parser.parse(line, mappingList);
    }

}
