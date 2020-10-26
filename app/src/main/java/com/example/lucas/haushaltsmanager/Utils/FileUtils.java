package com.example.lucas.haushaltsmanager.Utils;

import android.util.Log;

import com.example.lucas.haushaltsmanager.Entities.Directory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static List<File> listFiles(Directory dir, boolean invertList, String regex) {
        List<File> files = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.getName().matches(regex)) {
                files.add(file);
            }
        }

        if (invertList)
            Collections.reverse(files);

        return files;
    }

    public static File copy(File file, Directory destDir, String newFileName) {
        try {
            InputStream in = new FileInputStream(file);
            String path = String.format("%s/%s",
                    destDir.toString(),
                    newFileName
            );

            try (OutputStream out = new FileOutputStream(path, false)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }

            return new File(path);
        } catch (FileNotFoundException e) {

            Log.e(TAG, "Could not copy file. " + file.toString() + " does not exist.", e);
            return null;
        } catch (IOException e) {

            //Wenn eine IOException ausgelöst wird, kann ich den Grund eh nicht genau ermitteln und somit auch einfach FALSE zurückgeben
            Log.e(TAG, "Something went wrong while copying", e);
            return null;
        }
    }

    public static boolean remove(String fileName, Directory dir) {
        for (File file : dir.listFiles()) {
            if (file.getName().equals(fileName)) {
                return file.delete();
            }
        }

        return false;
    }


    /**
     * Methode um das älteste Backup aus einer Liste von Backups zu bekommen.
     *
     * @param fileList List mit Datein
     * @return Älteste Datei in der Liste
     */
    public static File getOldestFile(List<File> fileList) {
        if (fileList == null || fileList.isEmpty())
            throw new IllegalArgumentException();

        File oldestFile = fileList.get(0);
        for (File file : fileList) {
            if (file.lastModified() < oldestFile.lastModified())
                oldestFile = file;
        }

        return oldestFile;
    }

    public static String getType(File file) {
        String extension = "";
        String fileName = file.getName();

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }

        return extension;
    }
}
