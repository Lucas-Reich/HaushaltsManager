package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;

class GetAllAccountsQuery implements QueryInterface {
    @Override
    public String sql() {
        return "SELECT id, name, balance FROM ACCOUNTS";
    }

    @Override
    public Object[] values() {
        return new Object[0];
    }
}
