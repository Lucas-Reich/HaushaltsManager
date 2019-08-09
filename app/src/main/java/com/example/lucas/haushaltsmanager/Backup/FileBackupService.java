package com.example.lucas.haushaltsmanager.Backup;

import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;

import java.io.File;

public class FileBackupService {
    private static final String BACKUP_EXTENSION = "f_bkp";

    /**
     * Methode sichert die angegebene Datei in dem angegebenen Verzeichnis.
     *
     * @param file    Datei die gesichert werden soll
     * @param destDir Verzeichnis in dem die Sicherheitskopie gespeichert werden soll
     */
    public void create(File file, Directory destDir) {
        FileUtils.copy(
                file,
                destDir,
                createBackupFileName(file)
        );
    }

    /**
     * Methode sucht in dem angegebenen Verzeichnis nach einer Sicherungsdatei für die angegebene Datei.
     * Wird eine Sicherheitskopie gefunden, wird die aktuelle Datei mit der Sicherheitskopie überschrieben.
     *
     * @param file      Wiederherzustellende Datei
     * @param sourceDir Verzeichnis indem die Sicherheitskopie liegt
     */
    public void restore(File file, Directory sourceDir) {
        FileUtils.copy(
                new File(String.format("%s/%s", sourceDir.getPath(), createBackupFileName(file))),
                sourceDir,
                file.getName()
        );
    }

    /**
     * Methode um alle Sicherheitskopien für die angegebene Datei zu löschen.
     *
     * @param file      Datei deren Sicherheitskopien gelöscht werden sollen
     * @param sourceDir Verzeichnis indem nach den Sicherheitskopien gesucht werden soll
     */
    public void remove(File file, Directory sourceDir) {
        FileUtils.remove(
                createBackupFileName(file),
                sourceDir
        );
    }

    private String createBackupFileName(File file) {
        return String.format(
                "%s.%s",
                file.getName(),
                BACKUP_EXTENSION
        );
    }
}
