package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.Template;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ExpenseRepositoryTest {
    private Account account;
    private Category category;
    private Currency currency;
    private AccountRepository mAccountRepo;
    private TemplateRepository mTemplateRepo;
    private ChildCategoryRepository mChildCategoryRepo;
    private ExpenseRepository mBookingRepo;
    private CurrencyRepository mCurrencyRepo;

    @Before
    public void setup() {

        mAccountRepo = new AccountRepository(RuntimeEnvironment.application);
        mTemplateRepo = new TemplateRepository(RuntimeEnvironment.application);
        mChildCategoryRepo = new ChildCategoryRepository(RuntimeEnvironment.application);
        mBookingRepo = new ExpenseRepository(RuntimeEnvironment.application);
        mCurrencyRepo = new CurrencyRepository(RuntimeEnvironment.application);

        Category parentCategoryMock = mock(Category.class);
        when(parentCategoryMock.getIndex()).thenReturn(100L);

        category = new Category("Kategorie", new Color("#000000"), ExpenseType.income(), new ArrayList<Category>());
        category = mChildCategoryRepo.insert(parentCategoryMock, category);

        currency = new Currency("Credits", "CRD", "C");
        currency = mCurrencyRepo.insert(currency);

        account = new Account("Konto", new Price(100, currency));
        account = mAccountRepo.insert(account);
    }

    @Test
    public void testExistsWithExistingExpenseShouldSucceed() {
        ExpenseObject expense = mBookingRepo.insert(getSimpleExpense());

        boolean exists = mBookingRepo.exists(expense);
        assertTrue("Buchung konnte nicht gefunden werden", exists);
    }

    @Test
    public void testExistsWithNotExistingExpenseShouldFail() {
        ExpenseObject expense = getSimpleExpense();

        boolean exists = mBookingRepo.exists(expense);
        assertFalse("Nicht existierende Buchung wurde gefunden", exists);
    }

    @Test
    public void testGetWithExistingExpenseShouldSucceed() {
        ExpenseObject expectedExpense = mBookingRepo.insert(getSimpleExpense());

        try {
            ExpenseObject fetchedExpense = mBookingRepo.get(expectedExpense.getIndex());
            assertEquals(expectedExpense, fetchedExpense);

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Buchung wurde nicht gefunden");
        }
    }

    @Test
    public void testGetWithNotExistingExpenseShouldThrowExpenseNotFoundException() {
        long notExistingExpenseId = 313L;

        try {
            mBookingRepo.get(notExistingExpenseId);
            Assert.fail("Nicht existierende Buchung wurde gefunden");

        } catch (ExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Booking with id %s.", notExistingExpenseId), e.getMessage());
        }
    }

    @Test
    public void testGetAllShouldNotIncludeHiddenBookings() {
        ExpenseObject visibleExpense1 = getSimpleExpense();
        visibleExpense1 = mBookingRepo.insert(visibleExpense1);
        assertTrue("Ausgabe 1 wurde nicht gefunden", mBookingRepo.exists(visibleExpense1));

        ExpenseObject visibleExpense2 = getSimpleExpense();
        visibleExpense2 = mBookingRepo.insert(visibleExpense2);
        assertTrue("Ausgabe 2 wurde nicht gefunden", mBookingRepo.exists(visibleExpense2));

        ExpenseObject hiddenExpense = getSimpleExpense();
        hiddenExpense = mBookingRepo.insert(hiddenExpense);
        assertTrue("Ausgabe 3 wurde nicht gefunden", mBookingRepo.exists(hiddenExpense));

        try {
            mBookingRepo.hide(hiddenExpense);
            List<ExpenseObject> expenses = mBookingRepo.getAll();

            assertEquals("Es wurde zu viele Buchungen aus der Datenbank geholt", 2, expenses.size());
            assertFalse("Die vesteckte Buchung wurde aus der Datenbank geholt", expenses.contains(hiddenExpense));
            assertTrue("Die erste sichtbare Buchung wurde nicht aus der Datenbank geholt", expenses.contains(visibleExpense1));
            assertTrue("Die zweite sichtbare Buchung wurde nicht aus der Datenbank geholt", expenses.contains(visibleExpense2));
        } catch (ExpenseNotFoundException e) {

            Assert.fail("Buchung konnte nicht versteckt werden");
        }
    }

    @Test
    public void testGetAllShouldNotReturnChildBookings() {
        ExpenseObject parentExpense = getExpenseWithChildren();
        parentExpense = mBookingRepo.insert(parentExpense);

        ExpenseObject expense = getSimpleExpense();
        expense = mBookingRepo.insert(expense);

        List<ExpenseObject> expenses = mBookingRepo.getAll();

        assertEquals("Es wurden zu viele Ausgaben aus der Datenbank geholt", 2, expenses.size());
        assertTrue("Die ParentExpense wurde nicht aus der Datenbank geholt", expenses.contains(parentExpense));
        assertTrue("Die Ausgabe wurde nicht aus der Datenbank geholt", expenses.contains(expense));
    }

    @Test
    public void testGetAllInSpecifiedTimeFrameShouldReturnBookingsInThisTimeFrame() {
        //fixme
        //todo den Test noch einmal ausführen, wenn es einen Test für ExpenseObject.setDateTime() und ExpenseObject.getDate() gibt
        //das Verhalten vom Calendar ist unerwartet
        Calendar date = Calendar.getInstance();
        ExpenseObject expenseBeforeTimeFrame = getSimpleExpense();
        date.setTimeInMillis(1527803999000L);//31.05.2018 23:59:59
        expenseBeforeTimeFrame.setDateTime(date);
        expenseBeforeTimeFrame = mBookingRepo.insert(expenseBeforeTimeFrame);

        ExpenseObject expenseWithinTimeFrame1 = getSimpleExpense();
        date.setTimeInMillis(1527804000000L);//01.06.2018 00:00:00
        expenseWithinTimeFrame1.setDateTime(date);
        expenseWithinTimeFrame1 = mBookingRepo.insert(expenseWithinTimeFrame1);

        ExpenseObject expenseWithinTimeFrame2 = getSimpleExpense();
        date.setTimeInMillis(1527867176000L);//01.06.2018 17:32:56
        expenseWithinTimeFrame2.setDateTime(date);
        expenseWithinTimeFrame2 = mBookingRepo.insert(expenseWithinTimeFrame2);

        ExpenseObject expenseAfterTimeFrame = getSimpleExpense();
        date.setTimeInMillis(1527890400000L);//02.06.2018 00:00:00
        expenseAfterTimeFrame.setDateTime(date);
        expenseAfterTimeFrame = mBookingRepo.insert(expenseAfterTimeFrame);

        List<ExpenseObject> expenses = mBookingRepo.getAll(1527804000000L, 1527890399000L);//01.06.2018 00:00:00 - 01.06.2018 23:59:59

        assertFalse("Buchung die vor dem angegebenen Zeitraum ist wurde aus der Datenbank geholt", expenses.contains(expenseBeforeTimeFrame));
        assertTrue("Buchung 1 die innerhalb des Zeitraums ist wurde nicht aus der Datenbank geholt", expenses.contains(expenseWithinTimeFrame1));
        assertTrue("Buchung 2 die innerhalb des Zeitraums ist wurde nicht aus der Datenbank geholt", expenses.contains(expenseWithinTimeFrame2));
        assertFalse("Buchung die nach dem angegebenen Zeitraum ist wurde aus der Datenbank geholt", expenses.contains(expenseAfterTimeFrame));
    }

    @Test
    public void testInsertWithValidBookingThatHasNoChildrenOrTagsShouldSucceed() {
        ExpenseObject expectedExpense = mBookingRepo.insert(getSimpleExpense());

        try {
            ExpenseObject fetchedExpense = mBookingRepo.get(expectedExpense.getIndex());

            assertEquals(expectedExpense, fetchedExpense);
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + expectedExpense.getSignedPrice(),
                    account
            );

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Gerade erstellte Ausgabe konnte nicht gefunden werden");
        }

    }

    @Test
    public void testInsertWithValidBookingThatHasChildrenShouldSucceedAndChildrenShouldExist() {
        ExpenseObject expectedExpenseWithChildren = mBookingRepo.insert(getExpenseWithChildren());

        try {
            ExpenseObject fetchedExpense = mBookingRepo.get(expectedExpenseWithChildren.getIndex());

            assertEquals(expectedExpenseWithChildren, fetchedExpense);
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + expectedExpenseWithChildren.getChildren().get(0).getSignedPrice() + expectedExpenseWithChildren.getChildren().get(1).getSignedPrice() + expectedExpenseWithChildren.getChildren().get(2).getSignedPrice(),
                    account
            );

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Gerade erstellte Buchung wurde nicht gefunden");
        }

    }

    @Test
    public void testDeleteWithExistingBookingThatIsNotATemplateOrRecurringShouldSucceed() {
        ExpenseObject expense = mBookingRepo.insert(getSimpleExpense());

        try {
            mBookingRepo.delete(expense);

            assertFalse("Buchung wurde nicht gelöscht", mBookingRepo.exists(expense));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account
            );

        } catch (CannotDeleteExpenseException e) {

            Assert.fail("Buchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingBookingThatIsATemplateShouldSucceedAndChangeBookingVisibilityToHidden() {
        ExpenseObject expense = mBookingRepo.insert(getSimpleExpense());
        mTemplateRepo.insert(new Template(expense));

        try {
            mBookingRepo.delete(expense);

            assertTrue("Versteckte Buchung wurde aus der Datenbank geholt", mBookingRepo.isHidden(expense));
            assertTrue("Buchung wurde nicht als Versteckt markiert", mBookingRepo.exists(expense));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account
            );

        } catch (CannotDeleteExpenseException e) {

            Assert.fail("Buchung konnte nicht gelöscht werden");
        } catch (ExpenseNotFoundException e) {

            Assert.fail("Buchung wurde nicht gefunden");
        }
    }

    @Test
    public void testDeleteWithExistingBookingThatIsARecurringShouldSucceedAndChangeBookingVisibilityToHidden() {
        //fixme Test ausführen, wenn Buchungen als versteckt markiert werden wenn sie noch einen Wiederkehrende Buchung sind
        ExpenseObject expense = mBookingRepo.insert(getSimpleExpense());

        try {
            //TODO die recurringBookingExists methode welche in der delete methode aufgerufen wird sollte true zurückgeben
            mBookingRepo.delete(expense);

            assertFalse("Versteckte Buchung wurde aus der Datenbank geholt", mBookingRepo.getAll().contains(expense));
            assertTrue("Buchung wurde nicht als Versteckt markiert", mBookingRepo.exists(expense));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account
            );

        } catch (CannotDeleteExpenseException e) {

            Assert.fail("Buchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithNotExistingBookingShouldSucceed() {
        ExpenseObject expense = getSimpleExpense();

        try {
            mBookingRepo.delete(expense);
            assertFalse("Buchung wurde nicht gelöscht", mBookingRepo.exists(expense));

        } catch (CannotDeleteExpenseException e) {

            Assert.fail("Nicht existierende Buchung konnte nicht geöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingBookingThatIsAttachedToChildBookingsShouldThrowCannotDeleteExpenseException() {
        ExpenseObject parentExpense = mBookingRepo.insert(getExpenseWithChildren());

        try {
            mBookingRepo.delete(parentExpense);
            Assert.fail("Buchung mit Kindern konnte gelöscht werden");

        } catch (CannotDeleteExpenseException e) {

            assertEquals(String.format("Booking %s is attached to a child expense and cannot be deleted.", parentExpense.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testUpdateWithExistingBookingShouldSucceed() {
        ExpenseObject expectedExpense = mBookingRepo.insert(getSimpleExpense());

        try {
            expectedExpense.setTitle("New Booking Name");
            expectedExpense.setPrice(new Price(30, expectedExpense.getPrice().isNegative(), expectedExpense.getPrice().getCurrency()));
            mBookingRepo.update(expectedExpense);

            ExpenseObject fetchedExpense = mBookingRepo.get(expectedExpense.getIndex());

            assertEquals(expectedExpense, fetchedExpense);
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + expectedExpense.getSignedPrice(),
                    account
            );

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Buchung konnte nicht geupdated werden");
        }
    }

    @Test
    public void testUpdateWithNotExistingBookingShouldThrowExpenseNotFoundException() {
        ExpenseObject expense = getSimpleExpense();

        try {
            mBookingRepo.update(expense);
            Assert.fail("Nicht existierende Buchung konnte geupdated werden");

        } catch (ExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Booking with id %s.", expense.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testHideWithValidExpenseShouldSucceed() {
        ExpenseObject expense = mBookingRepo.insert(getSimpleExpense());

        try {
            mBookingRepo.hide(expense);
            assertTrue("Buchung wurde gelöscht", mBookingRepo.exists(expense));
            assertFalse("Versteckte Buchung wurde aus der Datenbank geholt", mBookingRepo.getAll().contains(expense));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account
            );

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Buchung wurde nicht gefunden");
        }
    }

    @Test
    public void testHideWithNotExistingExpenseShouldThrowExpenseNotFoundException() {
        ExpenseObject expense = getSimpleExpense();

        try {
            mBookingRepo.hide(expense);
            Assert.fail("Nicht existierende Ausgabe konnte versteckt werden");

        } catch (ExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Booking with id %s.", expense.getIndex()), e.getMessage());
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account
            );
        }
    }

    @Test
    public void testIsHiddenWithExistingExpenseShouldSucceed() {
        ExpenseObject expense = mBookingRepo.insert(getSimpleExpense());

        try {
            boolean isHidden = mBookingRepo.isHidden(expense);
            assertFalse("Buchung ist versteckt", isHidden);

            mBookingRepo.hide(expense);
            isHidden = mBookingRepo.isHidden(expense);
            assertTrue("Buchung is nich versteckt", isHidden);

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Buchung wurde nicht gefunden");
        }
    }

    @Test
    public void testIsHiddenWithNotExistingExpenseShouldThrowExpenseNotFoundException() {
        ExpenseObject expense = getSimpleExpense();

        try {
            mBookingRepo.isHidden(expense);
            Assert.fail("Buchung wurde gefunden");

        } catch (ExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Booking with id %s.", expense.getIndex()), e.getMessage());
        }
    }

    private ExpenseObject getSimpleExpense() {
        return new ExpenseObject(
                "Ausgabe",
                new Price(new Random().nextInt(1000), true, getDefaultCurrency()),
                category,
                account.getIndex(),
                currency
        );
    }

    private ExpenseObject getExpenseWithChildren() {
        ExpenseObject parentExpense = getSimpleExpense();
        ExpenseObject childExpense = getSimpleExpense();

        childExpense.setPrice(new Price(33, false, getDefaultCurrency()));
        parentExpense.addChild(childExpense);

        childExpense.setPrice(new Price(768, false, getDefaultCurrency()));
        parentExpense.addChild(childExpense);

        childExpense.setPrice(new Price(324, true, getDefaultCurrency()));
        parentExpense.addChild(childExpense);

        return parentExpense;
    }

    private Currency getDefaultCurrency() {
        return null;
    }

    private void assertEqualAccountBalance(double expectedAmount, Account account) {

        try {
            double actualBalance = mAccountRepo.get(account.getIndex()).getBalance().getSignedValue();
            assertEquals("Konto wurde nicht geupdated", expectedAmount, actualBalance);

        } catch (AccountNotFoundException e) {

            Assert.fail("Konto wurde nicht gefunden");
        }
    }
}
