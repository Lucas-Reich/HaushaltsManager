package com.example.lucas.haushaltsmanager.Database.Migrations;

public class MigrationHelper {
    public static IMigration[] getMigrations() {
        return new IMigration[]{
                new V1__Initial_Database_Creation()
        };
    }
}
