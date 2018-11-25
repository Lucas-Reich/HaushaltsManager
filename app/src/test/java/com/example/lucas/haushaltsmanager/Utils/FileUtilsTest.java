package com.example.lucas.haushaltsmanager.Utils;

import android.content.res.Resources;

import com.example.lucas.haushaltsmanager.R;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileUtilsTest {

    @Test
    public void testGetOldestFile() {
        List<File> files = new ArrayList<>();
        files.add(getFileWithModificationDate(1514819040000L)); // 01.01.2018 15:04:00
        files.add(getFileWithModificationDate(1514818860000L)); // 01.01.2018 15:01:00
        files.add(getFileWithModificationDate(1514818920000L)); // 01.01.2018 15:02:00
        files.add(getFileWithModificationDate(1514819100000L)); // 01.01.2018 15:05:00
        files.add(getFileWithModificationDate(1514819160000L)); // 01.01.2018 15:06:00
        files.add(getFileWithModificationDate(1514818980000L)); // 01.01.2018 15:03:00

        File oldestFile = FileUtils.getOldestFile(files);

        assertEquals(1514818860000L, oldestFile.lastModified());
    }

    private File getFileWithModificationDate(long modificationTime) {
        File file = mock(File.class);
        when(file.lastModified()).thenReturn(modificationTime);

        return file;
    }
}
