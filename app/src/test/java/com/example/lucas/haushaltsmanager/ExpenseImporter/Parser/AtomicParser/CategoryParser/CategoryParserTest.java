package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser;

import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.RequiredFields.Title;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class CategoryParserTest {
    private CategoryParser parser;

    @Before
    public void setUp() {
        parser = new CategoryParser();
    }

    @Test
    public void getRequiredFieldReturnsExpectedFields() {
        List<IRequiredField> requiredFields = parser.getRequiredFields();

        assertEquals(1, requiredFields.size());
        assertTrue((requiredFields.get(0) instanceof Title));
    }

    @Test
    public void parserCreatesCategory() {
        // SetUp
        String expectedCategoryTitle = "any string";
        Line line = buildLine(expectedCategoryTitle);

        // Act
        Category category = parser.parse(line, createCategoryMappingList());

        // Assert
        assertEquals(expectedCategoryTitle, category.getName());
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
    public void parserThrowsExceptionForEmptyCategoryTitle() {
        Line line = buildLine("");

        try {
            parser.parse(line, createCategoryMappingList());
        } catch (InvalidInputException e) {
            assertEquals("Could not create Category from 'empty string', invalid input.", e.getMessage());
        }
    }

    private MappingList createCategoryMappingList() {
        MappingList mappingList = new MappingList();
        mappingList.addMapping(CategoryParser.CATEGORY_TITLE_KEY, 0);

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