package com.example.lucas.haushaltsmanager.Database.Repositories

import androidx.room.*
import com.example.lucas.haushaltsmanager.entities.Account
import java.util.*

@Dao
interface AccountDAO {
    @Query("SELECT * FROM accounts WHERE id = :id")
    fun get(id: UUID): Account

    @Query("SELECT * FROM accounts")
    fun getAll(): List<Account>

    @Insert
    fun insert(account: Account)

    @Delete
    fun delete(account: Account)

    @Update
    fun update(account: Account)
}