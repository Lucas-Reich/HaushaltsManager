package com.example.lucas.haushaltsmanager.Database.Migrations;

public class MigrationHelper {
    public static IMigration[] getMigrations() {
        return new IMigration[]{
                new V1__Initial_Database_Creation(),
                new V2__Add_New_Columns_To_Recurring_Bookings_Table(),
                new V3__Add_Foreign_Keys_To_Accounts_Table(),
                new V4__Add_Foreign_Keys_To_Child_Categories(),
                new V5__Add_Foreign_Key_Support_To_Bookings_Table(),
                new V6__Recreate_Template_Bookings_Table()
        };
    }
}
