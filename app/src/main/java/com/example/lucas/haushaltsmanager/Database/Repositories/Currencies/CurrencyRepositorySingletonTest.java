package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import com.example.lucas.haushaltsmanager.Database.BaseRepository;
import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotDeleteEntityException;
import com.example.lucas.haushaltsmanager.Database.Exceptions.CouldNotUpdateEntityException;
import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import java.util.List;

public class CurrencyRepositorySingletonTest implements BaseRepository<Currency> {

    private static CurrencyRepositorySingletonTest instance;

    public static CurrencyRepositorySingletonTest getInstance() {
        if (instance == null)
            instance = new CurrencyRepositorySingletonTest();

        return instance;
    }

    @Override
    public Currency create(Currency entity) {
        return null;
    }

    @Override
    public Currency get(long entityId) throws EntityNotExistingException {
        return null;
    }

    @Override
    public List<Currency> getAll() {
        return null;
    }

    @Override
    public boolean update(Currency entity) throws CouldNotUpdateEntityException {
        return false;
    }

    @Override
    public void delete(Currency entity) throws CouldNotDeleteEntityException {

    }

    @Override
    public boolean exists(Currency object) {
        return false;
    }
}
