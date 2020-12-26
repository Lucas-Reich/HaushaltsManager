package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepositoryInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

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
        Account expectedAccount = mockAccount();
        injectAccountToCache(expectedAccount);


        // Act
        Account actualAccount = accountRepositoryDecorator.insert(expectedAccount);


        // Assert
        verifyZeroInteractions(mockAccountRepository);
        assertEquals(expectedAccount, actualAccount);
    }

    @Test
    public void whenAccountIsNotCachedCreateWillBeCalled() {
        // SetUp
        Account expectedAccount = mock(Account.class);
        accountRepositoryWillFindAccount(expectedAccount);


        // Act
        Account actualAccount = accountRepositoryDecorator.insert(mock(Account.class));


        //Assert
        verify(mockAccountRepository, times(1)).insert(any(Account.class));
        assertEquals(expectedAccount, actualAccount);
    }

    private Account mockAccount() {
        return new Account(
                "any string",
                new Price(1234, mock(Currency.class))
        );
    }

    private void injectAccountToCache(final Account account) throws Exception {
        Field accounts = CachedInsertAccountRepositoryDecorator.class.getDeclaredField("cachedAccounts");

        accounts.setAccessible(true);
        accounts.set(accountRepositoryDecorator, new ArrayList() {{
            add(account);
        }});
    }

    private void accountRepositoryWillFindAccount(Account account) {
        when(mockAccountRepository.insert(any(Account.class)))
                .thenReturn(account);
    }
}