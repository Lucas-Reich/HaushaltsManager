package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.Backup.Handler.Decorator.DataImporterBackupHandler;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryDAO;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SaverTest {
    private AccountDAO mockAccountRepository;
    private ExpenseRepository mockExpenseRepository;
    private ActiveAccountsPreferences accountPreferences;
    private DataImporterBackupHandler backupHandler;

    private ISaver saver;

    @Before
    public void setUp() {
        mockAccountRepository = mock(AccountDAO.class);
        mockExpenseRepository = mock(ExpenseRepository.class);
        accountPreferences = mock(ActiveAccountsPreferences.class);
        backupHandler = mock(DataImporterBackupHandler.class);

        saver = new Saver(
                mockAccountRepository,
                mock(CategoryDAO.class),
                mockExpenseRepository,
                accountPreferences,
                backupHandler
        );
    }

    @Test
    public void expenseIsSavedWithNewlyCreatedAccountAndCategory() {
        // SetUp
        Account account = mock(Account.class);

        Category expectedCategory = mock(Category.class);

        Booking booking = mock(Booking.class);
        when(booking.getCategory()).thenReturn(mock(Category.class));


        // Act
        saver.persist(booking, account);


        // Assert
        verify(backupHandler, times(1)).backup();


        verify(mockAccountRepository, times(1)).insert(account);
        verify(accountPreferences, times(1)).addAccount(account);
        verify(booking, times(1)).setAccount(account);


        verify(mockExpenseRepository, times(1)).insert(booking);
        verify(booking, times(1)).setCategory(expectedCategory);

    }

    @Test
    public void revertRestoresFiles() {
        saver.revert();

        verify(backupHandler, times(1)).restore();
    }

    @Test
    public void finishRemovesUnusedResources() {
        saver.finish();

        verify(backupHandler, times(1)).remove();
    }
}