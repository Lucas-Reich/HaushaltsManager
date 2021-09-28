package com.example.lucas.haushaltsmanager.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.lucas.haushaltsmanager.Database.Repositories.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.Converters;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookingDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.TemplateBookingDAO;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.template_booking.TemplateBookingWithoutCategory;

@Database(entities = {Account.class, Booking.class, Category.class, TemplateBookingWithoutCategory.class, RecurringBooking.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase DATABASE = null;

    public static AppDatabase getDatabase(Context context) {
        if (DATABASE != null) {
            return DATABASE;
        }

        synchronized (AppDatabase.class) {
            if (DATABASE == null) {
                DATABASE = Room.databaseBuilder(context, AppDatabase.class, "user_database")
                        .allowMainThreadQueries() // TODO: Remove
                        .build();
            }
        }

        return DATABASE;
    }

    public abstract AccountDAO accountDAO();

    public abstract CategoryDAO categoryDAO();

    public abstract TemplateBookingDAO templateBookingDAO();

    public abstract RecurringBookingDAO recurringBookingDAO();
}
