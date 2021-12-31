package com.example.lucas.haushaltsmanager.ExpenseImporter.Line;

import static org.junit.Assert.assertEquals;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LineTest {
    private static final IDelimiter DEFAULT_DELIMITER = new Comma();

    @Test
    public void lineCanBeCreatedFromValidInput() {
        for (String[] input : validLineInputDataProvider()) {
            // Act
            new Line(String.join(DEFAULT_DELIMITER.getDelimiter(), input), DEFAULT_DELIMITER);
        }
    }

    @Test
    public void getAsStringReturnsStringValue() {
        for (String[] validInput : validLineInputDataProvider()) {
            // Act
            Line line = new Line(String.join(DEFAULT_DELIMITER.getDelimiter(), validInput), DEFAULT_DELIMITER);

            // Assert
            for (int i = 0; i < validInput.length; i++) {
                assertEquals(validInput[i], line.getAsString(i));
            }
        }
    }

    private List<String[]> validLineInputDataProvider() {
        return new ArrayList<String[]>() {{
            add(new String[]{"31-12-2015", "0", "Title Of Booking", "51.89", "Bank Account 1"});
            add(new String[]{"31-12-2015", "0", "", "-51.89", ""});
            add(new String[]{"", "", "", "", ""});
        }};
    }

    @Test
    public void lineThrowsExceptionWhenGettingValueWithInvalidIndex() {
        // Arrange
        Line line = new Line("", new Comma());

        for (int invalidIndex : invalidIndexDataProvider()) {
            try {
                // Act
                line.getAsString(invalidIndex);
            } catch (IndexOutOfBoundsException e) {
                // Assert
                String expectedErrorMessage = String.format(
                        "Could not retrieve value from index at position '%d'. Line only has '1' values",
                        invalidIndex
                );
                assertEquals(expectedErrorMessage, e.getMessage());
            }
        }
    }

    private List<Integer> invalidIndexDataProvider() {
        return new ArrayList<Integer>() {{
            add(-1);
            add(1000);
        }};
    }
}
