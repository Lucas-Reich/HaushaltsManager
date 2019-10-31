package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Database.Common.DefaultDatabase;
import com.example.lucas.haushaltsmanager.Database.Common.IQueryResult;
import com.example.lucas.haushaltsmanager.Database.Common.ITransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Queries.DeleteQuery;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Queries.FindQuery;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Queries.InsertQuery;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Queries.UpdateQuery;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;

import javax.annotation.Nullable;

public class Repository implements IAccountRepository {
    private DefaultDatabase database;
    private ITransformer<Account> transformer;

    public Repository(DefaultDatabase database) {
        this.database = database;
        transformer = new AccountTransformer();
    }

    @Override
    public boolean exists(long accountId) {
        return find(accountId) != null;
    }

    @Nullable
    @Override
    public Account find(long accountId) {
        IQueryResult queryResult = database.query(new FindQuery(accountId));

        return transformer.transformAndClose(queryResult);
    }

    @NonNull
    @Override
    public Account get(long accountId) throws AccountNotFoundException {
        Account account = find(accountId);

        if (null == account) {
            throw new AccountNotFoundException(accountId);
        }

        return account;
    }

    @Override
    // TODO: delete sollte einen boolean Wert zurückgeben. Dieser ist:
    //  TRUE, wenn das Konto gelöscht werden konnte (egal ob es existiert oder nicht)
    //  FALSE, wenn noch Buchungen gibt, welches auf dieses Konto referenzieren (bzw. bei einem SQL fehler)
    public boolean delete(long accountId) {
        IQueryResult queryResult = database.query(new DeleteQuery(accountId));
        queryResult.close();

        return !exists(accountId);
    }

    @Override
    public boolean update(Account account) {
        IQueryResult queryResult = database.query(new UpdateQuery(account));
        queryResult.close();

        return exists(account.getIndex()); // TODO: Das Konto wird es immer geben, wenn man es auch updaten kann, da nur basierend auf dem Index nach dem Konto gesucht wird.
    }

    @Override
    public Account save(Account account) {
        IQueryResult queryResult = database.query(new InsertQuery(account));

        queryResult.close();

        return account; // TODO: Woher bekomme ich den Index des neuen Kontos?
    }
}
