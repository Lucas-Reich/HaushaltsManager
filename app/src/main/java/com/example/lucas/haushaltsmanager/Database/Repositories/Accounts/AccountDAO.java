package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.lucas.haushaltsmanager.Entities.Account;

import java.util.List;
import java.util.UUID;

@Dao
public interface AccountDAO {
    @Query("SELECT * FROM accounts WHERE id = :id")
    Account get(UUID id);

    @Query("SELECT * FROM accounts")
    List<Account> getAll();

    @Insert
    void insert(Account entity);

    @Delete
    void delete(Account account);

    @Update
    void update(Account account);
}
