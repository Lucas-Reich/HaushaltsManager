package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.*
import com.example.lucas.haushaltsmanager.entities.Account
import java.util.*

@Dao
interface AccountDAO {
    @Query("SELECT accounts.id, accounts.name, SUM(bookings.price) AS balance FROM accounts JOIN bookings ON accounts.id = bookings.account_id WHERE accounts.id = :id")
    fun get(id: UUID): Account

    @Query("SELECT accounts.id, accounts.name, SUM(bookings.price) AS balance FROM accounts JOIN bookings ON accounts.id = bookings.account_id GROUP BY accounts.id")
    fun getAll(): List<Account>

    @Insert
    fun insert(account: Account)

    @Delete
    fun delete(account: Account)

    @Update
    fun update(account: Account)
}