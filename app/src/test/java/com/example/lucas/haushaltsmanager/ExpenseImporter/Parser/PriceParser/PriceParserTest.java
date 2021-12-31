package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.PriceParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.AbsDoubleParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.IPriceTypeParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.RequiredFields.PriceType;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.RequiredFields.PriceValue;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.entities.Price;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class PriceParserTest {
    private PriceParser parser;

    @Before
    public void setUp() {
        this.parser = new PriceParser(new AbsDoubleParser());
    }

    @Test
    public void getRequiredFieldReturnsExpectedFields() {
        // Act
        List<IRequiredField> requiredFields = parser.getRequiredFields();

        // Assert
        assertEquals(2, requiredFields.size());
        assertTrue(requiredFields.get(0) instanceof PriceValue);
        assertTrue(requiredFields.get(1) instanceof PriceType);
    }

    @Test
    public void parserCanCreatePriceFromSingleColumn() {
        // Arrange
        Line line = buildLine("100");

        // Act
        Price actualPrice = parser.parse(line, createMappingForValueAndTypeInSameValue());

        // Assert
        assertEquals(100D, actualPrice.getPrice(), 0);
    }

    @Test
    public void parserCanCreatePriceFromTwoColumns() {
        // Arrange
        Line line = buildLine("100", "1");

        // Act
        Price actualPrice = parser.parse(line, createMappingForValueAndInSeparateValues());

        // Assert
        assertEquals(-100D, actualPrice.getPrice(), 0);
    }

    @Test
    public void parserThrowsExceptionIfNoMappingIsProvided() {
        try {
            parser.parse(buildLine(), new MappingList());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'PriceType'.", e.getMessage());
        }
    }

    @Test
    public void parserThrowsExceptionForInvalidValue() {
        Line line = buildLine("invalid_input");

        try {
            parser.parse(line, createMappingForValueAndTypeInSameValue());
        } catch (InvalidInputException e) {
            assertEquals("Could not create 'Boolean' from 'invalid_input', invalid numeric value given.", e.getMessage());
        }
    }

    private MappingList createMappingForValueAndTypeInSameValue() {
        MappingList mapping = new MappingList();
        mapping.addMapping(AbsDoubleParser.PRICE_VALUE_KEY, 0);
        mapping.addMapping(IPriceTypeParser.Companion.getPRICE_TYPE_KEY(), 0);

        return mapping;
    }

    private MappingList createMappingForValueAndInSeparateValues() {
        MappingList mapping = new MappingList();
        mapping.addMapping(AbsDoubleParser.PRICE_VALUE_KEY, 0);
        mapping.addMapping(IPriceTypeParser.Companion.getPRICE_TYPE_KEY(), 1);

        return mapping;
    }

    private Line buildLine(String... input) {
        IDelimiter delimiter = new Comma();

        return new Line(
                String.join(delimiter.getDelimiter(), input),
                delimiter
        );
    }
}
