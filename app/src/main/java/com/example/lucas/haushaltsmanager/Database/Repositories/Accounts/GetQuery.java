package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;

import java.util.UUID;

class GetQuery implements QueryInterface {
    private final UUID accountId;

    public GetQuery(UUID accountId) {
        this.accountId = accountId;
    }

    @Override
    public String sql() {
        return "SELECT id, name, balance FROM ACCOUNTS WHERE id = '%s'";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                accountId.toString()
        };
    }
}
