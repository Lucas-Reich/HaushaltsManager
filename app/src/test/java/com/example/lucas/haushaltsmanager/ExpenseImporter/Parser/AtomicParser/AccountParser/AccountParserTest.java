package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.RequiredFields.Title;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class AccountParserTest {
    private AccountParser parser;

    @Before
    public void setUp() {
        this.parser = new AccountParser();
    }

    @Test
    public void getRequiredFieldReturnsExpectedFields() {
        List<IRequiredField> requiredFields = parser.getRequiredFields();

        assertEquals(1, requiredFields.size());
        assertTrue((requiredFields.get(0) instanceof Title));
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
            assertEquals("No mapping defined for key 'Title'.", e.getMessage());
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
        mappingList.addMapping(AccountParser.ACCOUNT_TITLE_KEY, 0);

        return mappingList;
    }

    private Line buildLine(String... input) {
        IDelimiter delimiter = new Comma();

        return new Line(
                String.join(delimiter.getDelimiter(), input),
                delimiter
        );
    }
}