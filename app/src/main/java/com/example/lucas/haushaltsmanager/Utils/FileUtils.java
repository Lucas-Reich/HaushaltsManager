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

import javax.annotation.RegEx;

public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static List<File> listFiles(Directory dir, boolean invertList, @RegEx String regex) {
        List<File> files = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.getName().matches(regex))
                files.add(file);
        }

        if (invertList)
            Collections.reverse(files);

        return files;
    }

    public static boolean copy(File file, Directory destDir, String newFileName) {
        try {
            InputStream in = new FileInputStream(file);
            OutputStream out = new FileOutputStream(destDir.toString() + "/" + newFileName);

            try {

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {

                    out.write(buf, 0, len);
                }

            } finally {

                out.close();
            }

            return true;
        } catch (FileNotFoundException e) {

            Log.e(TAG, file.toString() + " does not exist.", e);
            return false;
        } catch (IOException e) {

            //wenn eine io exception ausgelöst wird weiß ich eh nicht genau was der Grund dafür war und kann auch einfach FALSE zurückgeben
            Log.e(TAG, "Something went wrong while copying", e);
            return false;
        }
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
            if (file.lastModified() > oldestFile.lastModified())
                oldestFile = file;
        }

        return oldestFile;
    }
}
