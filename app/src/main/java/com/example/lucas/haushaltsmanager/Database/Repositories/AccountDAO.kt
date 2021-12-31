package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.*
import com.example.lucas.haushaltsmanager.entities.Account
import java.util.*

@Dao
interface AccountDAO {
    @Query("SELECT accounts.id, accounts.name, COALESCE(SUM(bookings.price), 0) AS balance FROM accounts LEFT JOIN bookings ON accounts.id = bookings.account_id WHERE accounts.id = :id")
    fun get(id: UUID): Account

    @Query("SELECT accounts.id, accounts.name, COALESCE(SUM(bookings.price), 0) AS balance FROM accounts LEFT JOIN bookings ON accounts.id = bookings.account_id GROUP BY accounts.id")
    fun getAll(): List<Account>

    @Insert
    fun insert(account: Account)

    @Query("SELECT accounts.id, accounts.name, COALESCE(SUM(bookings.price), 0) AS balance FROM accounts LEFT JOIN bookings ON accounts.id = bookings.account_id WHERE accounts.name = :accountName")
    fun getByName(accountName: String): Account

    @Delete
    fun delete(account: Account)

    @Update
    fun update(account: Account)
}