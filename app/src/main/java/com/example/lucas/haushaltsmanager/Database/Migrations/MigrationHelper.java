package com.example.lucas.haushaltsmanager.Database.Migrations;

public class MigrationHelper {
    public static IMigration[] getMigrations() {
        return new IMigration[]{
                new InitialDatabaseCreation(),
                new Migration1(),
                new Migration2()
        };
    }
}
