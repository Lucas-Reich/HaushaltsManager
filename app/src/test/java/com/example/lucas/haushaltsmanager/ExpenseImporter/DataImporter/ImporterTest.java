package com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidLineException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.FileReader.IFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.ISub;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.ISaver;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Transformer.ITransformer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        IImporter importer = new Importer(reader, mock(ISaver.class), mock(ITransformer.class));


        // Assert
        assertEquals(expectedStepCount, importer.totalSteps());
    }

    @Test
    public void readingStopsWhenAborted() {
        IFileReader reader = mock(IFileReader.class);
        when(reader.moveToNext()).thenReturn(true);

        ISaver saver = mock(ISaver.class);
        ISub sub = mock(ISub.class);

        IImporter importer = new Importer(reader, saver, mock(ITransformer.class));
        importer.addSub(sub);
        importer.abort();
        importer.run();


        verify(saver, times(1)).revert();
        verify(saver, times(2)).finish(); // Once for .abort(), second for finishing .run()

        verify(reader, times(2)).close(); // Once for .abort(), second for finishing .run()

        verifyZeroInteractions(sub);
    }

    @Test
    public void subsAreNotifiedOnFailure() {
        // Set Up
        ITransformer transformer = mock(ITransformer.class);
        when(transformer.transform(null)).thenThrow(InvalidLineException.class);

        IFileReader reader = mock(IFileReader.class);
        when(reader.moveToNext())
                .thenReturn(true)
                .thenReturn(false);

        ISub sub = mock(ISub.class);


        // Act
        IImporter importer = new Importer(reader, mock(ISaver.class), transformer);
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
        when(reader.moveToNext()).thenReturn(true).thenReturn(false);

        ITransformer transformer = mock(ITransformer.class);
        when(transformer.transform(null)).thenReturn(mock(Line.class));

        ISaver saver = mock(ISaver.class);
        when(saver.save(any(Line.class))).thenReturn(true);


        // Act
        IImporter importer = new Importer(reader, saver, transformer);
        importer.addSub(sub);
        importer.run();


        // Assert
        verify(sub, times(1)).notifySuccess();
    }

    @Test
    public void resourcesAreReleasedAfterFinish() {
        // Set Up
        IFileReader reader = mock(IFileReader.class);
        ISaver saver = mock(ISaver.class);


        // Act
        IImporter importer = new Importer(reader, saver, mock(ITransformer.class));
        importer.run();


        // Assert
        verify(reader, times(1)).close();
        verify(saver, times(1)).finish();
    }
}
