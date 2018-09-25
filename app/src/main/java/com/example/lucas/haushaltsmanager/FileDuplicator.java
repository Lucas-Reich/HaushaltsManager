package com.example.lucas.haushaltsmanager;

import android.util.Log;

import com.example.lucas.haushaltsmanager.Entities.Directory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileDuplicator {
    private static final String TAG = FileDuplicator.class.getSimpleName();

    private File mFile;
    private Directory mDestDir;

    public FileDuplicator(File file, Directory desDir) {

        mFile = file;
        mDestDir = desDir;
        //todo sollte ich auch überprüfen ob das dir existiert?
    }

    //todo check if read/write permission is granted

    public boolean copy(String newFileName) {
        if (!hasFilePermission())
            getFilePermission();

        try {
            InputStream in = new FileInputStream(mFile);
            OutputStream out = new FileOutputStream(mDestDir.toString() + "/" + newFileName);

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

            Log.e(TAG, mFile.toString() + " does not exist.", e);
            return false;
        } catch (IOException e) {

            //wenn eine io exception ausgelöst wird weiß ich eh nicht genau was der Grund dafür war und kann auch einfach FALSE zurückgeben
            Log.e(TAG, "Something went wrong while copying", e);
            return false;
        }
    }

    private boolean hasFilePermission() {
        return mDestDir.canWrite() && mFile.canRead();
    }

    private void getFilePermission() {
        //todo request permission to write file
    }
}
