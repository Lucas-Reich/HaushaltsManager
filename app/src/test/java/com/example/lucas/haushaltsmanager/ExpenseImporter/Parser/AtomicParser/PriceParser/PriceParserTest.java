package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.PriceParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields.Type;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields.Value;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PriceParserTest {
    private PriceParser parser;
    private final String INCOME = "0";
    private final String EXPENSE = "1";

    @Before
    public void setUp() {
        this.parser = new PriceParser(mock(Currency.class));
    }

    @Test
    public void getRequiredFieldReturnsExpectedFields() {
        List<IRequiredField> requiredFields = parser.getRequiredFields();

        TestCase.assertEquals(2, requiredFields.size());
        TestCase.assertTrue((requiredFields.get(0) instanceof Value));
        TestCase.assertTrue((requiredFields.get(1) instanceof Type));
    }

    @Test
    public void parserCreatesExpense() {
        Line line = buildLine("100", EXPENSE);

        Price price = parser.parse(line, createPriceMappingList());

        assertTrue(price.isNegative());
        assertEquals(-100D, price.getSignedValue(), 0);
    }

    @Test
    public void parserCreatesIncome() {
        Line line = buildLine("100", INCOME);

        Price price = parser.parse(line, createPriceMappingList());

        assertFalse(price.isNegative());
        assertEquals(100D, price.getSignedValue(), 0);
    }

    @Test
    public void parserIgnoresAlgebraicSign() {
        Line line = buildLine("-100", INCOME);

        Price price = parser.parse(line, createPriceMappingList());

        assertFalse(price.isNegative());
        assertEquals(100D, price.getSignedValue(), 0);
    }

    @Test
    public void parserThrowsExceptionIfMappingNotFound() {
        Line line = buildLine("any string", EXPENSE);

        try {
            parser.parse(line, new MappingList());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'Value'.", e.getMessage());
        }
    }

    @Test
    public void parserThrowsExceptionForEmptyPriceValue() {
        Line line = buildLine("", EXPENSE);

        try {
            parser.parse(line, createPriceMappingList());
        } catch (InvalidInputException e) {
            assertEquals("Could not create Price from 'empty string', invalid input.", e.getMessage());
        }
    }

    @Test
    public void parserThrowsExceptionForEmptyPriceType() {
        Line line = buildLine("100", "");

        try {
            parser.parse(line, createPriceMappingList());
        } catch (InvalidInputException e) {
            assertEquals("Could not create Price from 'empty string', invalid input.", e.getMessage());
        }
    }

    @Test
    public void parserThrowsExceptionForInvalidValue() {
        Line line = buildLine("invalid_input", INCOME);

        try {
            parser.parse(line, createPriceMappingList());
        } catch (InvalidInputException e) {
            assertEquals("Could not create Price from 'invalid_input', invalid value.", e.getMessage());
        }
    }

    @Test
    public void parserThrowsExceptionForInvalidType() {
        Line line = buildLine("313", "invalid_input");

        try {
            parser.parse(line, createPriceMappingList());
        } catch (InvalidInputException e) {
            assertEquals("Could not create Price from 'invalid_input', invalid type.", e.getMessage());
        }
    }

    private MappingList createPriceMappingList() {
        MappingList mappingList = new MappingList();
        mappingList.addMapping(PriceParser.PRICE_VALUE_KEY, 0);
        mappingList.addMapping(PriceParser.PRICE_TYPE_KEY, 1);

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
