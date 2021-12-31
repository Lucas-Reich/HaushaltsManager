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

public class BooleanPriceTypeParserTest {
    private BooleanPriceTypeParser parser;

    @Before
    public void setUp() {
        parser = new BooleanPriceTypeParser();
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
            add(buildLine("false"));
            add(buildLine("FALSE"));
            add(buildLine("0"));
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
            add(buildLine("true"));
            add(buildLine("TRUE"));
            add(buildLine("1"));
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
    public void parserThrowsExceptionIfGivenValueIsNotABoolean() {
        for (Line line : linesWithInvalidValueDataProvider()) {
            try {
                parser.parse(line, createMapping());
            } catch (InvalidInputException e) {
                String expectedExceptionMessage = String.format(
                        "Could not create 'Boolean' from '%s', invalid input.",
                        line.getAsString(0)
                );
                assertEquals(expectedExceptionMessage, e.getMessage());
            }
        }
    }

    private List<Line> linesWithInvalidValueDataProvider() {
        return new ArrayList<Line>() {{
            add(buildLine(""));
            add(buildLine("0.0"));
            add(buildLine("invalid"));
            add(buildLine("wahr"));
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
