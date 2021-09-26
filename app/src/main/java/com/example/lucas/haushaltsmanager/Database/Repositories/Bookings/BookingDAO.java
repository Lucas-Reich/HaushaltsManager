package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.lucas.haushaltsmanager.entities.Booking.BookingWithCategory;
import com.example.lucas.haushaltsmanager.entities.Booking.BookingWithoutCategory;

import java.util.List;
import java.util.UUID;

@Dao
public abstract class BookingDAO {
    @Query("SELECT * FROM bookings WHERE id = :id")
    public abstract BookingWithoutCategory get(UUID id);

    @Transaction
    @Query("SELECT * FROM bookings WHERE id = :id")
    public abstract BookingWithCategory getBookingWithCategory(UUID id);

    @Transaction
    @Query("SELECT * FROM bookings")
    public abstract List<BookingWithCategory> getBookingsWithCategory();

    @Query("SELECT * FROM bookings")
    public abstract List<BookingWithoutCategory> getAll();

    @Insert
    public abstract void insert(BookingWithoutCategory expense);

    @Delete
    public abstract void delete(BookingWithoutCategory expense);

    @Update
    public abstract void update(BookingWithoutCategory expense);

    public void insert(BookingWithCategory booking) {
        insert(booking.getBooking());
    }

    public void update(BookingWithCategory booking) {
        update(booking.getBooking());
    }
//
//    void insert(ParentBooking booking);
}
