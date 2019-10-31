package com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImportStrategies.IImportStrategy;
import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImportStrategies.ImportBookingStrategy;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.IFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.ISub;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.AccountParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.CategoryParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.DateParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.RequiredFields.Date;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.PriceParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields.Type;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields.Value;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.BookingParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.RequiredFields.Title;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.ISaver;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class ImporterTest {
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
    public void readingStopsWhenAborted() throws Exception {
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
        verify(sub, times(1)).notifyFailure();
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
                new BookingParser(new PriceParser(mock(Currency.class)), new CategoryParser(), new DateParser(), mock(Currency.class)),
                new AccountParser(mock(Currency.class)),
                mock(ISaver.class)
        ));


        // Act
        List<IRequiredField> requiredFields = importer.getRequiredFields();


        // Assert
        assertEquals(6, requiredFields.size());
        assertTrue(requiredFields.get(0) instanceof Title);
        assertTrue(requiredFields.get(1) instanceof Value);
        assertTrue(requiredFields.get(2) instanceof Type);
        assertTrue(requiredFields.get(3) instanceof com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.RequiredFields.Title);
        assertTrue(requiredFields.get(4) instanceof Date);
        assertTrue(requiredFields.get(5) instanceof com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.RequiredFields.Title);
    }
}
