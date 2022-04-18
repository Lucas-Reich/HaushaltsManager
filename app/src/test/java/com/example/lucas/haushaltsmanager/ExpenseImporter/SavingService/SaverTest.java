package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.lucas.haushaltsmanager.Backup.Handler.Decorator.DataImporterBackupHandler;
import com.example.lucas.haushaltsmanager.Database.Repositories.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.category.Category;
import com.example.lucas.haushaltsmanager.entities.Color;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.ExpenseType;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class SaverTest {
    private AccountDAO mockAccountRepository;
    private BookingDAO mockExpenseRepository;
    private CategoryDAO mockCategoryRepository;

    private ActiveAccountsPreferences accountPreferences;
    private DataImporterBackupHandler backupHandler;

    @Before
    public void setUp() {
        mockAccountRepository = mock(AccountDAO.class);
        mockExpenseRepository = mock(BookingDAO.class);
        mockCategoryRepository = mock(CategoryDAO.class);
        accountPreferences = mock(ActiveAccountsPreferences.class);
        backupHandler = mock(DataImporterBackupHandler.class);
    }

    @Test
    public void backupIsCreatedWhenSaverIsInitialized() {
        // Act
        buildSaver();

        // Assert
        verify(backupHandler, times(1)).backup();
    }

    @Test
    public void bookingIsSavedWithNewlyCreatedAccountAndCategory() {
        // Arrange
        Account account = createDummyAccount();
        when(mockAccountRepository.getByName(account.getName())).thenReturn(account);

        Category category = createDummyCategory();
        when(mockCategoryRepository.getByName(category.getName())).thenReturn(category);

        Booking booking = createDummyBooking();

        // Act
        buildSaver().persist(booking, account, category);

        // Assert
        assertThatAccountIsCorrectlyPersisted(account);
        assertThatCategoryIsCorrectlyPersisted(category);

        assertEquals(account.getId(), booking.getAccountId());
        assertEquals(category.getId(), booking.getCategoryId());
        verify(mockExpenseRepository, times(1)).insert(booking);
    }

    @Test
    public void revertRestoresFiles() {
        buildSaver().revert();

        verify(backupHandler, times(1)).restore();
    }

    @Test
    public void finishRemovesUnusedResources() {
        buildSaver().finish();

        verify(backupHandler, times(1)).remove();
    }

    private void assertThatCategoryIsCorrectlyPersisted(Category category) {
        verify(mockCategoryRepository, times(1)).insert(category);
    }

    private void assertThatAccountIsCorrectlyPersisted(Account account) {
        verify(mockAccountRepository, times(1)).insert(account);
        verify(accountPreferences, times(1)).addAccount(account);
    }

    private ISaver buildSaver() {
        return new Saver(
                mockAccountRepository,
                mockCategoryRepository,
                mockExpenseRepository,
                accountPreferences,
                backupHandler
        );
    }

    private Booking createDummyBooking() {
        return new Booking(
                "Booking Title",
                new Price(0),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
    }

    private Category createDummyCategory() {
        return new Category(
                "Category 1",
                Color.white(),
                ExpenseType.expense()
        );
    }

    private Account createDummyAccount() {
        return new Account(
                "Bank Account 1",
                new Price(0)
        );
    }
}