package com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.InsertStrategy;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.DateItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.FileItem;
import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileListInsertStrategyTest {
    private FileListInsertStrategy insertStrategy = new FileListInsertStrategy();

    @Test
    public void cannotInsertItemOfWrongClass() {
        DateItem wrongClassItem = new DateItem(Calendar.getInstance());

        try {

            insertStrategy.insert(wrongClassItem, new ArrayList<IRecyclerItem>());
            Assert.fail("Could add item of wrong class to FileList");
        } catch (IllegalArgumentException e) {

            assertEquals("FileListInsertStrategy requires FileItems. Class given: DateItem", e.getMessage());
        }
    }

    @Test
    public void insertItemIntoEmptyList() {
        List<IRecyclerItem> items = new ArrayList<>();

        FileItem fileItem = new FileItem(getDummyFile(12, 0, 0));
        int insertIndex = insertStrategy.insert(fileItem, items);

        assertEquals(0, insertIndex);
        assertEquals(fileItem, items.get(insertIndex));
    }

    // insertItemAtCorrectPosition
    // Den Test kann ich nicht machen, da ich den timestamp für lastModified bei einem CSVFileReader nicht richtig ändern kann.

    private File getDummyFile(int hour, int minute, int second) {
        File file = new File("the/path/does/not/matter");
        file.setLastModified(dateToLong(10, Calendar.JUNE, 2019, hour, minute, second));

        return file;
    }

    private long dateToLong(int day, int month, int year, int hour, int minute, int second) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day, hour, minute, second);

        return date.getTimeInMillis();
    }
}

