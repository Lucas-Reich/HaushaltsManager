package com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImportStrategies.IImportStrategy;
import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImportStrategies.ImportBookingStrategy;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.IFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.ISub;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.AccountParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.RequiredFields.AccountTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.CategoryParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.RequiredFields.CategoryTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.DateParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.RequiredFields.Date;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.AbsDoubleParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.RequiredFields.PriceValue;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.NumericPriceTypeParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.RequiredFields.PriceType;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.BookingParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.RequiredFields.BookingTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.PriceParser.PriceParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.ISaver;

import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalAnswers;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;

public class ImporterTest {
    private static final IDelimiter DEFAULT_DELIMITER = new Comma();

    private IFileReader reader;
    private IImportStrategy importStrategy;

    @Before
    public void setUp() {
        reader = mock(IFileReader.class);
        importStrategy = mock(IImportStrategy.class);
    }

    @Test
    public void importStrategyWillNotBeCalledForEmptyFile() {
        // Arrange
        fileReaderWillReturnTheFollowingLines(createListOfLines(0));

        // Act
        buildImporter().run();

        // Assert
        verify(importStrategy, times(0)).handle(
                any(Line.class),
                any(MappingList.class)
        );
        assertThatResourcesAreReleased();
    }

    @Test
    public void importStrategyWillBeCalledForEachLineFromFileReader() {
        // Arrange
        int amountOfLinesInReader = 2;
        List<Line> lines = createListOfLines(amountOfLinesInReader);
        fileReaderWillReturnTheFollowingLines(lines);

        // Act
        buildImporter().run();

        // Assert
        verify(importStrategy, times(amountOfLinesInReader)).handle(
                any(Line.class),
                any(MappingList.class)
        );
        assertThatResourcesAreReleased();
    }

    @Test
    public void returnsCorrectTotalStepCount() {
        // Set up
        int expectedStepCount = 100;

        IFileReader reader = mock(IFileReader.class);
        when(reader.getLineCount()).thenReturn(expectedStepCount);


        // Act
        IImporter importer = new Importer(reader, mock(ImportBookingStrategy.class));


        // Assert
        assertEquals(expectedStepCount, importer.totalSteps());
    }

    @Test
    public void readingStopsWhenAborted() {
        IFileReader reader = mock(IFileReader.class);

        ISub sub = mock(ISub.class);
        IImportStrategy importStrategy = mock(IImportStrategy.class);

        IImporter importer = new Importer(reader, importStrategy);
        importer.addSub(sub);
        importer.abort();
        importer.run();

        verify(importStrategy, times(1)).abort();
        verify(reader, times(3)).close(); // Twice for .abort(), third for finishing .run()

        verifyZeroInteractions(sub);
    }

    @Test
    public void subsAreNotifiedOnFailure() {
        // Set Up
        IFileReader reader = mock(IFileReader.class);
        when(reader.moveToNext())
                .thenReturn(true)
                .thenReturn(false);

        ISub sub = mock(ISub.class);

        IImportStrategy importStrategy = mock(IImportStrategy.class);
        doThrow(NoMappingFoundException.class)
                .when(importStrategy)
                .handle(null, null); // handle will receive null because of Mockito mocks


        // Act
        IImporter importer = new Importer(reader, importStrategy);
        importer.addSub(sub);
        importer.run();


        // Assert
        verify(sub, times(1)).notifyFailure(any());
    }

    @Test
    public void subsAreNotifiedOnSuccess() {
        // Set Up
        ISub sub = mock(ISub.class);

        IFileReader reader = mock(IFileReader.class);
        when(reader.moveToNext())
                .thenReturn(true)
                .thenReturn(false);


        // Act
        IImporter importer = new Importer(reader, mock(IImportStrategy.class));
        importer.addSub(sub);
        importer.run();


        // Assert
        verify(sub, times(1)).notifySuccess();
    }

    @Test
    public void resourcesAreReleasedAfterFinish() {
        // Set Up
        IFileReader reader = mock(IFileReader.class);
        IImportStrategy importStrategy = mock(IImportStrategy.class);


        // Act
        IImporter importer = new Importer(reader, importStrategy);
        importer.run();


        // Assert
        verify(reader, times(1)).close();
        verify(importStrategy, times(1)).finish();
    }

    @Test
    public void returnsExpectedRequiredFields() {
        // Set Up
        IImporter importer = new Importer(mock(IFileReader.class), new ImportBookingStrategy(
                new BookingParser(new PriceParser(new AbsDoubleParser(), new NumericPriceTypeParser()), new DateParser()),
                new AccountParser(),
                new CategoryParser(),
                mock(ISaver.class)
        ));


        // Act
        List<IRequiredField> requiredFields = importer.getRequiredFields();


        // Assert
        assertEquals(6, requiredFields.size());
        assertTrue(requiredFields.get(0) instanceof BookingTitle);
        assertTrue(requiredFields.get(1) instanceof PriceValue);
        assertTrue(requiredFields.get(2) instanceof PriceType);
        assertTrue(requiredFields.get(3) instanceof Date);
        assertTrue(requiredFields.get(4) instanceof AccountTitle);
        assertTrue(requiredFields.get(5) instanceof CategoryTitle);
    }

    private void assertThatResourcesAreReleased() {
        verify(reader, times(1)).close();
        verify(importStrategy, times(1)).finish();
    }

    private List<Line> createListOfLines(int amount) {
        return Collections.nCopies(amount, new Line("", DEFAULT_DELIMITER));
//        ArrayList<Line> lines = new ArrayList<>();
//        for (int i = 0; i < amount; i++) {
//            lines.add(new Line("", DEFAULT_DELIMITER));
//        }
//
//        return lines;
    }

    private IImporter buildImporter() {
        Importer importer = new Importer(
                reader,
                importStrategy
        );
        importer.setMapping(new MappingList());

        return importer;
    }

    private void fileReaderWillReturnTheFollowingLines(List<Line> lines) {
        when(reader.getLineCount()).thenReturn(lines.size());

        when(reader.getCurrentLine()).thenAnswer(AdditionalAnswers.returnsElementsOf(lines));

        ArrayList<Boolean> list = new ArrayList<>();
        for (int i = 0; i <= lines.size(); i++) {
            list.add(i != lines.size());
        }
        when(reader.moveToNext()).thenAnswer(AdditionalAnswers.returnsElementsOf(list));
    }
}
