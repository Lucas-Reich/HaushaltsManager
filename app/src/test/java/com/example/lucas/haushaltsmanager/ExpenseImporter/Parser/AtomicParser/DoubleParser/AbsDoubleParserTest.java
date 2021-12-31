package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser;

import static org.junit.Assert.assertEquals;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.AbsDoubleParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.RequiredFields.PriceValue;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AbsDoubleParserTest {
    private AbsDoubleParser parser;

    @Before
    public void setUp() {
        parser = new AbsDoubleParser();
    }

    @Test
    public void getRequiredFieldReturnsExpectedFields() {
        // Act
        List<IRequiredField> requiredFields = parser.getRequiredFields();

        // Assert
        TestCase.assertEquals(1, requiredFields.size());
        TestCase.assertTrue(requiredFields.get(0) instanceof PriceValue);
    }

    @Test
    public void parserCanParseValidInputAndReturnAbsValueOfNumber() {
        for (Line line : lineWithValidInputDataProvider()) {
            // Act
            Double actualResult = parser.parse(line, createMapping());

            // Assert
            assertEquals(100D, actualResult, 0);
        }
    }

    private List<Line> lineWithValidInputDataProvider() {
        return new ArrayList<Line>() {{
            add(buildLine("100"));
            add(buildLine("-100"));
            add(buildLine("100.00"));
            add(buildLine("-100.00"));
        }};
    }

    @Test
    public void parserThrowsExceptionIfNoMappingIsProvided() {
        try {
            parser.parse(buildLine(""), new MappingList());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'PriceValue'.", e.getMessage());
        }
    }

    @Test
    public void throwsExceptionForInvalidInput() {
        for (Line line : linesWithInvalidValuesDataProvider()) {
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

    private List<Line> linesWithInvalidValuesDataProvider() {
        return new ArrayList<Line>() {{
            add(buildLine(""));
            add(buildLine("invalid"));
            add(buildLine("--100"));
            add(buildLine("invalid_100"));
            add(buildLine("100_invalid"));
        }};
    }

    private MappingList createMapping() {
        MappingList mapping = new MappingList();
        mapping.addMapping(AbsDoubleParser.PRICE_VALUE_KEY, 0);

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
