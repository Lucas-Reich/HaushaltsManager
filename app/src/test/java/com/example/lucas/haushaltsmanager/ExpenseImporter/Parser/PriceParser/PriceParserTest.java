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
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.RequiredFields.PriceValue;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.NumericPriceTypeParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.RequiredFields.PriceType;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.entities.Price;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PriceParserTest {
    private PriceParser parser;

    @Before
    public void setUp() {
        this.parser = new PriceParser(new AbsDoubleParser(), new NumericPriceTypeParser());
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
        for (Line line : validPositiveSingleColumnValues()) {
            // Act
            Price actualPrice = parser.parse(line, createMappingForValueAndTypeInSameValue());

            // Assert
            assertEquals(100D, actualPrice.getPrice(), 0);
        }
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

    private List<Line> validPositiveSingleColumnValues() {
        return new ArrayList<Line>() {{
            add(buildLine("100"));
            add(buildLine("100.0"));
            add(buildLine("100,0"));
            add(buildLine("100.00"));
            add(buildLine("100,00"));
        }};
    }

    private MappingList createMappingForValueAndTypeInSameValue() {
        MappingList mapping = new MappingList();
        mapping.addMapping(AbsDoubleParser.PRICE_VALUE_KEY, 0);
        mapping.addMapping(NumericPriceTypeParser.PRICE_TYPE_KEY, 0);

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
