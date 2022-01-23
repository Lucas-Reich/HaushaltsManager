package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService

import com.example.lucas.haushaltsmanager.Database.Repositories.AccountDAO
import com.example.lucas.haushaltsmanager.entities.Account
import java.util.*
import kotlin.collections.ArrayList

class CachedAccountReadRepositoryDecorator(
    private val repository: AccountDAO
) : AccountDAO {
    private val cache: MutableList<Account> = ArrayList()

    override fun get(id: UUID): Account {
        val foundAccount = cache.singleOrNull { it.id == id }

        if (null != foundAccount) {
            return foundAccount
        }

        return repository.get(id)
    }

    override fun getAll(): List<Account> {
        return repository.getAll()
    }

    override fun insert(account: Account) {
        val createdAccount = cache.singleOrNull { it.name == account.name && it.balance == account.balance }

        if (null != createdAccount) {
            return
        }

        repository.insert(account)
        cache.add(account)
    }

    override fun getByName(accountName: String): Account {
        val account = cache.singleOrNull { it.name == accountName }

        if (null != account) {
            return account
        }

        return repository.getByName(accountName)
    }

    override fun delete(account: Account) {
        repository.delete(account)
    }

    override fun update(account: Account) {
        repository.update(account)
    }
}