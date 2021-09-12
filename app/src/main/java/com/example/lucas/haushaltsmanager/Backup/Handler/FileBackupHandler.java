package com.example.lucas.haushaltsmanager.Backup.Handler;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.entities.Directory;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;

import java.io.File;

import javax.annotation.Nullable;

public class FileBackupHandler {
    public static final String BACKUP_EXTENSION = "em_bkp";

    public File backup(File file, Directory targetDir, @Nullable String backupName) {
        // TODO: Ich sollte überprüfen ob file und targetDir valid sind
        return FileUtils.copy(
                file,
                targetDir,
                createBackupFileName(file, backupName)
        );
    }

    /**
     * @param backup           Backup Datei (.em_bkp Endung), welche wiederhergestellt werden soll
     * @param targetDir        Verzeichnis in welchen die wiederhergestellte Datei gespeichert werden soll
     * @param restoredFileName Muss bereits die neue Dateiendung enthalten
     */
    public File restore(File backup, Directory targetDir, @NonNull String restoredFileName) throws InvalidFileException {
        guardAgainstInvalidFile(backup);

        return FileUtils.copy(
                backup,
                targetDir,
                restoredFileName
        );
    }

    private void guardAgainstInvalidFile(File file) throws InvalidFileException {
        if (null == file) {
            throw InvalidFileException.nullGiven();
        }

        String fileType = FileUtils.getType(file);

        if (!BACKUP_EXTENSION.equals(fileType)) {
            throw InvalidFileException.invalidType(BACKUP_EXTENSION, file.getPath());
        }
    }

    private String createBackupFileName(File file, @Nullable String backupName) {
        if (null == backupName) {
            backupName = file.getName();
        }

        return String.format(
                "%s.%s",
                backupName,
                BACKUP_EXTENSION
        );
    }
}
