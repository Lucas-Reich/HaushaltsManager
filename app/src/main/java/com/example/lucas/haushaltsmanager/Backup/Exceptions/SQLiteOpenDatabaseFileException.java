package com.example.lucas.haushaltsmanager.Backup.Exceptions;

import java.io.File;

public class SQLiteOpenDatabaseFileException extends RuntimeException {
    private SQLiteOpenDatabaseFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public static SQLiteOpenDatabaseFileException generic(File file) {
        return new SQLiteOpenDatabaseFileException(String.format(
                "Something went wrong while handling database %s.",
                file.getName()
        ), null);
    }

    public static SQLiteOpenDatabaseFileException invalidVersion(int expectedVersion, int actualVersion) {
        return new SQLiteOpenDatabaseFileException(String.format(
                "Could not open database with invalid version. Expected: %s, Actual: %s.",
                expectedVersion,
                actualVersion
        ), null);
    }

    public static SQLiteOpenDatabaseFileException invalidSchema(File file) {
        return new SQLiteOpenDatabaseFileException(String.format(
                "Could not open %s as the schema is incompatible with the current database.",
                file.getName()
        ), null);
    }

    public static SQLiteOpenDatabaseFileException invalidFile(File file) {
        return new SQLiteOpenDatabaseFileException(String.format(
                "Could not open database from file: %s.",
                file.getName()
        ), null);
    }
}
