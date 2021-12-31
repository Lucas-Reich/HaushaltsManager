package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.RequiredFields.PriceType;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class NumericPriceTypeParserTest {
    private NumericPriceTypeParser parser;

    @Before
    public void setUp() {
        parser = new NumericPriceTypeParser();
    }

    @Test
    public void getRequiredFieldReturnsExpectedFields() {
        // Act
        List<IRequiredField> actualRequiredFields = parser.getRequiredFields();

        // Assert
        assertEquals(1, actualRequiredFields.size());
        assertTrue(actualRequiredFields.get(0) instanceof PriceType);
    }

    @Test
    public void parserCanParseValidPositiveValue() {
        for (Line line : validLinesWithPositiveValueDataProvider()) {
            // Act
            boolean actualPriceType = parser.parse(line, createMapping());

            // Assert
            assertEquals(IPriceTypeParser.VALUE_POSITIVE, actualPriceType);
        }
    }

    private List<Line> validLinesWithPositiveValueDataProvider() {
        return new ArrayList<Line>() {{
            add(buildLine("0"));
            add(buildLine("1"));
            add(buildLine("10.5"));
            add(buildLine("100"));
            add(buildLine("1000000"));
        }};
    }

    @Test
    public void parserCanParseValidNegativeValue() {
        for (Line line : validLinesWithNegativeValueDataProvider()) {
            // Act
            boolean actualPriceType = parser.parse(line, createMapping());

            // Assert
            assertEquals(IPriceTypeParser.VALUE_NEGATIVE, actualPriceType);
        }
    }

    private List<Line> validLinesWithNegativeValueDataProvider() {
        return new ArrayList<Line>() {{
            add(buildLine("-1"));
            add(buildLine("-10.5"));
            add(buildLine("-100"));
            add(buildLine("-1000000"));
        }};
    }

    @Test
    public void parserThrowsExceptionIfMappingIsNotProvided() {
        try {
            parser.parse(buildLine(""), new MappingList());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'PriceType'.", e.getMessage());
        }
    }

    @Test
    public void parserThrowsExceptionIfGivenValueIsNotNumeric() {
        for (Line line : invalidLinesDataProvider()) {
            try {
                parser.parse(line, createMapping());
            } catch (InvalidInputException e) {
                String expectedExceptionMessage = String.format(
                        "Could not create 'Boolean' from '%s', invalid numeric value given.",
                        line.getAsString(0)
                );
                assertEquals(expectedExceptionMessage, e.getMessage());
            }
        }
    }

    private List<Line> invalidLinesDataProvider() {
        return new ArrayList<Line>() {{
            add(buildLine(""));
            add(buildLine("invalid"));
            add(buildLine("invalid100"));
            add(buildLine("100invalid"));
            add(buildLine("--100"));
        }};
    }

    private MappingList createMapping() {
        MappingList mappingList = new MappingList();
        mappingList.addMapping(IPriceTypeParser.Companion.getPRICE_TYPE_KEY(), 0);

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
