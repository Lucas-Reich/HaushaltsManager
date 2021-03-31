package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Price;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class CachedInsertAccountRepositoryDecoratorTest {
    private AccountRepositoryInterface mockAccountRepository;
    private CachedInsertAccountRepositoryDecorator accountRepositoryDecorator;

    @Before
    public void setUp() {
        mockAccountRepository = mock(AccountRepositoryInterface.class);

        accountRepositoryDecorator = new CachedInsertAccountRepositoryDecorator(mockAccountRepository);
    }

    @Test
    public void whenAccountIsCachedNoCallToDatabaseIsMade() throws Exception {
        // SetUp
        Account account = mockAccount();
        injectAccountToCache(account);

        // Act
        accountRepositoryDecorator.insert(account);

        // Assert
        verifyZeroInteractions(mockAccountRepository);
    }

    @Test
    public void whenAccountIsNotCachedCreateWillBeCalled() throws AccountCouldNotBeCreatedException {
        // SetUp
        Account account = mock(Account.class);

        // Act
        accountRepositoryDecorator.insert(account);

        //Assert
        verify(mockAccountRepository, times(1)).insert(account);
    }

    private Account mockAccount() {
        return new Account(
                "any string",
                new Price(1234)
        );
    }

    private void injectAccountToCache(final Account account) throws Exception {
        Field accounts = CachedInsertAccountRepositoryDecorator.class.getDeclaredField("cachedAccounts");

        accounts.setAccessible(true);
        accounts.set(accountRepositoryDecorator, new ArrayList() {{
            add(account);
        }});
    }
}