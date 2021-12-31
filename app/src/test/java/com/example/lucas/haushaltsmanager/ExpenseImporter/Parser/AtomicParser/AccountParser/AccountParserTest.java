package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.RequiredFields.AccountTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.entities.Account;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class AccountParserTest {
    private AccountParser parser;

    @Before
    public void setUp() {
        this.parser = new AccountParser();
    }

    @Test
    public void getRequiredFieldReturnsExpectedFields() {
        // Act
        List<IRequiredField> requiredFields = parser.getRequiredFields();

        // Assert
        assertEquals(1, requiredFields.size());
        assertTrue(requiredFields.get(0) instanceof AccountTitle);
    }

    @Test
    public void parserCreatesAccount() {
        // Arrange
        String expectedAccountTitle = "any string";
        Line line = buildCommaDelimitedLine(expectedAccountTitle);

        // Act
        Account account = parser.parse(line, createMappingForAccountTitle());

        // Assert
        assertEquals(expectedAccountTitle, account.getName());
        assertEquals(0D, account.getBalance().getAbsoluteValue());
    }

    @Test
    public void parserThrowsExceptionIfMappingNotFound() {
        Line line = buildCommaDelimitedLine("any string");

        try {
            parser.parse(line, new MappingList());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'AccountTitle'.", e.getMessage());
        }
    }

    @Test
    public void parserThrowsExceptionForEmptyAccountTitle() {
        Line line = buildCommaDelimitedLine("");

        try {
            parser.parse(line, createMappingForAccountTitle());
        } catch (InvalidInputException e) {
            assertEquals("Could not create 'Account' from 'empty string', invalid input.", e.getMessage());
        }
    }

    private MappingList createMappingForAccountTitle() {
        MappingList mappingList = new MappingList();
        mappingList.addMapping(AccountParser.ACCOUNT_TITLE_KEY, 0);

        return mappingList;
    }

    private Line buildCommaDelimitedLine(String... input) {
        IDelimiter delimiter = new Comma();

        return new Line(
                String.join(delimiter.getDelimiter(), input),
                delimiter
        );
    }
}