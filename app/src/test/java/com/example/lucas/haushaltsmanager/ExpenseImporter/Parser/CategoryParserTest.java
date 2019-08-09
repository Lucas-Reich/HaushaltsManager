package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.DelimiterInterface;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.CategoryParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.ImportableCategoryTitle;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class CategoryParserTest {
    private CategoryParser parser;

    @Before
    public void setUp() {
        parser = new CategoryParser();
    }

    @Test
    public void parserCreatesCategory() {
        // SetUp
        String expectedCategoryTitle = "any string";
        Line line = buildLine(expectedCategoryTitle);

        // Act
        Category category = parser.parse(line, createCategoryMappingList());

        // Assert
        assertEquals(expectedCategoryTitle, category.getTitle());
    }

    @Test
    public void parserThrowsExceptionIfMappingNotFound() {
        Line line = buildLine("any string");

        try {
            parser.parse(line, new MappingList());
        } catch (NoMappingFoundException e) {
            assertEquals("No mapping defined for key 'category_title'.", e.getMessage());
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
        mappingList.addMapping(new ImportableCategoryTitle(CategoryParser.CATEGORY_TITEL_KEY));

        return mappingList;
    }

    private Line buildLine(String... input) {
        DelimiterInterface delimiter = new Comma();

        return new Line(
                String.join(delimiter.getDelimiter(), CategoryParser.CATEGORY_TITEL_KEY),
                String.join(delimiter.getDelimiter(), input),
                delimiter
        );
    }
}