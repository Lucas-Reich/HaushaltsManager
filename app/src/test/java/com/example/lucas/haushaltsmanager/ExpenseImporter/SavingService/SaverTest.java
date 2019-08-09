package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SaverTest {
    private AccountRepositoryInterface mockAccountRepository;
    private ChildCategoryRepositoryInterface mockChildCategoryRepository;
    private ExpenseRepository mockExpenseRepository;
    private ActiveAccountsPreferences accountPreferences;
    private BackupService backupService;

    private ISaver saver;
    private IParser parser;

    @Before
    public void setUp() {
        mockAccountRepository = mock(AccountRepositoryInterface.class);
        mockChildCategoryRepository = mock(ChildCategoryRepositoryInterface.class);
        mockExpenseRepository = mock(ExpenseRepository.class);
        accountPreferences = mock(ActiveAccountsPreferences.class);
        backupService = mock(BackupService.class);

        parser = mock(IParser.class);

        saver = new Saver(
                mockAccountRepository,
                mockChildCategoryRepository,
                mockExpenseRepository,
                accountPreferences,
                parser,
                backupService,
                mock(Category.class)
        );
    }

    @Test
    public void expenseIsSavedWithNewlyCreatedAccountAndCategory2() {
        // SetUp
        long expectedIndex = 714L;
        accountRepositoryShouldReturnAccount(mockAccount(expectedIndex));

        Category expectedCategory = mock(Category.class);
        childCategoryRepositoryShouldReturnCategory(expectedCategory);

        ExpenseObject booking = createRealExpense(mock(Category.class));
        when(parser.parseBooking(any(Line.class))).thenReturn(booking);
        when(parser.parseAccount(any(Line.class))).thenReturn(mock(Account.class));


        // Act
        Line line = mock(Line.class);
        boolean success = saver.save(line);


        // Assert
        assertTrue(success);

        verify(backupService, times(1)).createBackup();

        verify(accountPreferences, times(1)).addAccount(any(Account.class));

        verify(mockExpenseRepository, times(1)).insert(booking);

        assertEquals(expectedCategory, getSavedExpense(mockExpenseRepository).getCategory());
        assertEquals(expectedIndex, getSavedExpense(mockExpenseRepository).getAccountId());
    }

    @Test
    public void revertRestoresFiles() {
        saver.revert();

        verify(backupService, times(1)).restoreBackup();
    }

    @Test
    public void finishRemovesUnusedResources() {
        saver.finish();

        verify(backupService, times(1)).removeBackups();
    }

    private void childCategoryRepositoryShouldReturnCategory(Category category) {
        when(mockChildCategoryRepository.insert(any(Category.class), any(Category.class)))
                .thenReturn(category);
    }

    private void accountRepositoryShouldReturnAccount(Account account) {
        when(mockAccountRepository.create(any(Account.class)))
                .thenReturn(account);
    }

    private ExpenseObject getSavedExpense(ExpenseRepository expenseRepository) {
        ArgumentCaptor<ExpenseObject> captor = ArgumentCaptor.forClass(ExpenseObject.class);
        Mockito.verify(expenseRepository).insert(captor.capture());

        return captor.getValue();
    }

    private ExpenseObject createRealExpense(Category category) {
        return new ExpenseObject(
                "any String passes",
                mock(Price.class),
                category,
                ExpensesDbHelper.INVALID_INDEX,
                mock(Currency.class)
        );
    }

    private Account mockAccount(long index) {
        Account accountMock = mock(Account.class);
        when(accountMock.getIndex()).thenReturn(index);

        return accountMock;
    }
}