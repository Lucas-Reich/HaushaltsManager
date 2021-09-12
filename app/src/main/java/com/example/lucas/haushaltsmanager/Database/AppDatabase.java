package com.example.lucas.haushaltsmanager.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.Converters;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.Booking.BookingWithoutCategory;
import com.example.lucas.haushaltsmanager.entities.Category;

@Database(entities = {Account.class, Category.class, BookingWithoutCategory.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AccountDAO accountDAO();

    public abstract CategoryDAO categoryDAO();

    public abstract ExpenseDAO bookingDAO();
}
