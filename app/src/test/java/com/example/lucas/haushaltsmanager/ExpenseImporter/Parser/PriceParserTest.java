package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.DelimiterInterface;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.PriceParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.PriceMappings.ImportablePriceType;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.PriceMappings.ImportablePriceValue;

import org.junit.Before;
import org.junit.Test;

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
            assertEquals("No mapping defined for key 'price_value'.", e.getMessage());
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
        mappingList.addMapping(new ImportablePriceValue(PriceParser.PRICE_VALUE_KEY));
        mappingList.addMapping(new ImportablePriceType(PriceParser.PRICE_TYPE_KEY));

        return mappingList;
    }

    private Line buildLine(String... input) {
        DelimiterInterface delimiter = new Comma();

        return new Line(
                String.join(delimiter.getDelimiter(), PriceParser.PRICE_VALUE_KEY, PriceParser.PRICE_TYPE_KEY),
                String.join(delimiter.getDelimiter(), input),
                delimiter
        );
    }
}
