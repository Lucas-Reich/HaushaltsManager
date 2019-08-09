package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.DelimiterInterface;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.AccountParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.AccountMappings.ImportableAccountTitle;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;

public class AccountParserTest {
    private AccountParser parser;

    @Before
    public void setUp() {
        this.parser = new AccountParser(mock(Currency.class));
    }

    @Test
    public void parserCreatesAccount() {
        // SetUp
        String expectedAccountTitel = "any string";
        Line line = buildLine(expectedAccountTitel);

        // Act
        Account account = parser.parse(line, createAccountMappingList());

        // Assert
        assertEquals(expectedAccountTitel, account.getTitle());
    }

    @Test
    public void parserThrowsExceptionIfMappingNotFound() {
        Line line = buildLine("any string");

        try {
            parser.parse(line, new MappingList());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'account_title'.", e.getMessage());
        }
    }

    @Test
    public void parserThrowsExceptionForEmptyAccountTitle() {
        Line line = buildLine("");

        try {
            parser.parse(line, createAccountMappingList());
        } catch (InvalidInputException e) {
            assertEquals("Could not create Account from 'empty string', invalid input.", e.getMessage());
        }
    }

    private MappingList createAccountMappingList() {
        MappingList mappingList = new MappingList();
        mappingList.addMapping(new ImportableAccountTitle(AccountParser.ACCOUNT_TITLE_KEY));

        return mappingList;
    }

    private Line buildLine(String... input) {
        DelimiterInterface delimiter = new Comma();

        return new Line(
                String.join(delimiter.getDelimiter(), AccountParser.ACCOUNT_TITLE_KEY),
                String.join(delimiter.getDelimiter(), input),
                delimiter
        );
    }
}