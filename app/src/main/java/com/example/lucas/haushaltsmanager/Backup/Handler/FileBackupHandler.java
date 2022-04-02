package com.example.lucas.haushaltsmanager.Backup.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;
import com.example.lucas.haushaltsmanager.entities.Directory;

import java.io.File;

public class FileBackupHandler {
    public static final String BACKUP_EXTENSION = "em_bkp";

    public File backup(@NonNull File file, @NonNull Directory targetDir, @Nullable String backupName) {
        /**
         * TODO: Do I need to check if file and targetDir are valid?
         */
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
    public File restore(@Nullable File backup, @NonNull Directory targetDir, @NonNull String restoredFileName) throws InvalidFileException {
        guardAgainstInvalidFile(backup);

        return FileUtils.copy(
                backup,
                targetDir,
                restoredFileName
        );
    }

    private void guardAgainstInvalidFile(@Nullable File file) throws InvalidFileException {
        if (null == file) {
            throw InvalidFileException.nullGiven();
        }

        String fileType = FileUtils.getType(file);

        if (!BACKUP_EXTENSION.equals(fileType)) {
            throw InvalidFileException.invalidType(BACKUP_EXTENSION, file.getPath(), fileType);
        }
    }

    private String createBackupFileName(@NonNull File file, @Nullable String backupName) {
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
