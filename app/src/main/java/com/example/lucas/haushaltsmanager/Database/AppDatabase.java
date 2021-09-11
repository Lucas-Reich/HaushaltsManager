package com.example.lucas.haushaltsmanager.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.Converters;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Booking.BookingWithoutCategory;
import com.example.lucas.haushaltsmanager.Entities.Category;

@Database(entities = {Account.class, Category.class, BookingWithoutCategory.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AccountDAO accountDAO();

    public abstract CategoryDAO categoryDAO();

    public abstract ExpenseDAO bookingDAO();
}
