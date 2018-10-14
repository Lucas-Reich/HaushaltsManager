package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags.BookingTagRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.TagRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;
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
    private TagRepository mTagRepo;
    private RecurringBookingRepository mRecurringBookingRepo;
    private ChildCategoryRepository mChildCategoryRepo;
    private BookingTagRepository mBookingTagRepo;
    private ExpenseRepository mBookingRepo;
    private CurrencyRepository mCurrencyRepo;

    @Before
    public void setup() {

        mAccountRepo = new AccountRepository(RuntimeEnvironment.application);
        mTemplateRepo = new TemplateRepository(RuntimeEnvironment.application);
        mRecurringBookingRepo = new RecurringBookingRepository(RuntimeEnvironment.application);
        mChildCategoryRepo = new ChildCategoryRepository(RuntimeEnvironment.application);
        mBookingTagRepo = new BookingTagRepository(RuntimeEnvironment.application);
        mBookingRepo = new ExpenseRepository(RuntimeEnvironment.application);
        mTagRepo = new TagRepository(RuntimeEnvironment.application);
        mCurrencyRepo = new CurrencyRepository(RuntimeEnvironment.application);

        Category parentCategoryMock = mock(Category.class);
        when(parentCategoryMock.getIndex()).thenReturn(100L);

        category = new Category("Kategorie", "#000000", false, new ArrayList<Category>());
        category = mChildCategoryRepo.insert(parentCategoryMock, category);

        currency = new Currency("Credits", "CRD", "C");
        currency = mCurrencyRepo.create(currency);

        account = new Account("Konto", 100, currency);
        account = mAccountRepo.create(account);
    }

    private ExpenseObject getSimpleExpense() {
        return new ExpenseObject(
                "Ausgabe",
                new Random().nextInt(1000),
                true,
                category,
                account.getIndex(),
                currency
        );
    }

    private ExpenseObject getExpenseWithChildren() {
        ExpenseObject parentExpense = getSimpleExpense();
        ExpenseObject childExpense = getSimpleExpense();

        childExpense.setExpenditure(false);
        childExpense.setPrice(33);
        parentExpense.addChild(childExpense);

        childExpense.setExpenditure(false);
        childExpense.setPrice(768);
        parentExpense.addChild(childExpense);

        childExpense.setExpenditure(true);
        childExpense.setPrice(324);
        parentExpense.addChild(childExpense);

        return parentExpense;
    }

    private ExpenseObject getExpenseWithTags() {
        Tag tag1 = new Tag("Tag 1");
        tag1 = mTagRepo.create(tag1);
        Tag tag2 = new Tag("Tag 2");
        tag2 = mTagRepo.create(tag2);
        Tag tag3 = new Tag("Tag 3");
        tag3 = mTagRepo.create(tag3);


        ExpenseObject expenseWithTags = getSimpleExpense();
        expenseWithTags.addTag(tag1);
        expenseWithTags.addTag(tag2);
        expenseWithTags.addTag(tag3);

        return expenseWithTags;
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

            assertEquals(String.format("Could not find Expense with id %s.", notExistingExpenseId), e.getMessage());
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
        //todo den Test noch einmal ausführen, wenn es einen Test für ExpenseObject.setDateTime() und ExpenseObject.getDateTime() gibt
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
                    account.getBalance() + expectedExpense.getSignedPrice(),
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
                    account.getBalance() + expectedExpenseWithChildren.getChildren().get(0).getSignedPrice() + expectedExpenseWithChildren.getChildren().get(1).getSignedPrice() + expectedExpenseWithChildren.getChildren().get(2).getSignedPrice(),
                    account
            );

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Gerade erstellte Buchung wurde nicht gefunden");
        }

    }

    @Test
    public void testInsertWithValidBookingThatHasTagsShouldSucceedAndTagsShouldExists() {
        //fixme Test noch einmal ausführen, wenn das BookingTagRepository mit Tests versehen ist
        ExpenseObject expectedExpenseWithTags = mBookingRepo.insert(getExpenseWithTags());

        try {
            ExpenseObject fetchedExpenseWithTags = mBookingRepo.get(expectedExpenseWithTags.getIndex());

            assertEquals(expectedExpenseWithTags, fetchedExpenseWithTags);
            assertTrue("Tag 1 wurde der Buchung nicht zugewiesen", mBookingTagRepo.exists(expectedExpenseWithTags, expectedExpenseWithTags.getTags().get(0)));
            assertTrue("Tag 2 wurde der Buchung nicht zugewiesen", mBookingTagRepo.exists(expectedExpenseWithTags, expectedExpenseWithTags.getTags().get(1)));
            assertTrue("Tag 3 wurde der Buchung nicht zugewiesen", mBookingTagRepo.exists(expectedExpenseWithTags, expectedExpenseWithTags.getTags().get(2)));
            assertEqualAccountBalance(
                    account.getBalance() + expectedExpenseWithTags.getSignedPrice(),
                    account
            );

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Ausgabe wurde nicht gefunden");
        }
    }

    @Test
    public void testDeleteWithExistingBookingThatIsNotATemplateOrRecurringShouldSucceed() {
        ExpenseObject expense = mBookingRepo.insert(getSimpleExpense());

        try {
            mBookingRepo.delete(expense);

            assertFalse("Buchung wurde nicht gelöscht", mBookingRepo.exists(expense));
            assertEqualAccountBalance(
                    account.getBalance(),
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
                    account.getBalance(),
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
                    account.getBalance(),
                    account
            );

        } catch (CannotDeleteExpenseException e) {

            Assert.fail("Buchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingBookingThatHasTagsAssignedShouldDeleteBookingAndTagRelations() {
        //fixme Test noch einmal ausführen, wenn das BookingTagRepository mit Tests versehen ist
        ExpenseObject expenseWithTags = mBookingRepo.insert(getExpenseWithTags());

        try {
            mBookingRepo.delete(expenseWithTags);

            assertTrue("Die Relation zu Tag 1 wurde nicht gelöscht", mBookingTagRepo.exists(expenseWithTags, expenseWithTags.getTags().get(0)));
            assertTrue("Die Relation zu Tag 2 wurde nicht gelöscht", mBookingTagRepo.exists(expenseWithTags, expenseWithTags.getTags().get(1)));
            assertTrue("Die Relation zu Tag 3 wurde nicht gelöscht", mBookingTagRepo.exists(expenseWithTags, expenseWithTags.getTags().get(2)));
            assertEqualAccountBalance(
                    account.getBalance(),
                    account
            );
        } catch (CannotDeleteExpenseException e) {

            Assert.fail("Buchung kann nicht gelöscht werden");
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

            assertEquals(String.format("Expense %s is attached to a child expense and cannot be deleted.", parentExpense.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testUpdateWithExistingBookingShouldSucceed() {
        ExpenseObject expectedExpense = mBookingRepo.insert(getSimpleExpense());

        try {
            expectedExpense.setTitle("New Expense Name");
            expectedExpense.setPrice(30);
            mBookingRepo.update(expectedExpense);

            ExpenseObject fetchedExpense = mBookingRepo.get(expectedExpense.getIndex());

            assertEquals(expectedExpense, fetchedExpense);
            assertEqualAccountBalance(
                    account.getBalance() + expectedExpense.getSignedPrice(),
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

            assertEquals(String.format("Could not find Expense with id %s.", expense.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testCursorToExpenseWithValidCursorShouldSucceed() {
        ExpenseObject expectedExpense = getSimpleExpense();

        String[] columns = new String[]{
                ExpensesDbHelper.BOOKINGS_COL_ID,
                ExpensesDbHelper.BOOKINGS_COL_DATE,
                ExpensesDbHelper.BOOKINGS_COL_TITLE,
                ExpensesDbHelper.BOOKINGS_COL_PRICE,
                ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE,
                ExpensesDbHelper.BOOKINGS_COL_NOTICE,
                ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE,
                ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID,
                ExpensesDbHelper.CURRENCIES_COL_ID,
                ExpensesDbHelper.CURRENCIES_COL_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SYMBOL,
                ExpensesDbHelper.CHILD_CATEGORIES_COL_ID,
                ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME,
                ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR,
                ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{
                expectedExpense.getIndex(),
                expectedExpense.getDateTime().getTimeInMillis(),
                expectedExpense.getTitle(),
                expectedExpense.getUnsignedPrice(),
                expectedExpense.isExpenditure() ? 1 : 0,
                expectedExpense.getNotice(),
                expectedExpense.getExpenseType().name(),
                expectedExpense.getAccountId(),
                expectedExpense.getCurrency().getIndex(),
                expectedExpense.getCurrency().getName(),
                expectedExpense.getCurrency().getShortName(),
                expectedExpense.getCurrency().getSymbol(),
                expectedExpense.getCategory().getIndex(),
                expectedExpense.getCategory().getTitle(),
                expectedExpense.getCategory().getColorString(),
                expectedExpense.getCategory().getDefaultExpenseType() ? 1 : 0
        });
        cursor.moveToFirst();

        try {
            ExpenseObject actualExpense = ExpenseRepository.cursorToExpense(cursor);
            assertEquals(expectedExpense, actualExpense);

        } catch (CursorIndexOutOfBoundsException e) {

            Assert.fail("Ausgabe konnte nicht aus einem vollständigen Cursor wiederhergestellt werden");
        }
    }

    @Test
    public void testCursorToExpenseWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        ExpenseObject expectedExpense = getSimpleExpense();

        String[] columns = new String[]{
                ExpensesDbHelper.BOOKINGS_COL_ID,
                ExpensesDbHelper.BOOKINGS_COL_DATE,
                ExpensesDbHelper.BOOKINGS_COL_TITLE,
                //Der Preis der Buchung wurde nicht mit abgefragt
                ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE,
                ExpensesDbHelper.BOOKINGS_COL_NOTICE,
                ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE,
                ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID,
                ExpensesDbHelper.CURRENCIES_COL_ID,
                ExpensesDbHelper.CURRENCIES_COL_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SYMBOL,
                ExpensesDbHelper.CHILD_CATEGORIES_COL_ID,
                ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME,
                ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR,
                ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{
                expectedExpense.getIndex(),
                expectedExpense.getDateTime().getTimeInMillis(),
                expectedExpense.getTitle(),
                expectedExpense.isExpenditure() ? 1 : 0,
                expectedExpense.getNotice(),
                expectedExpense.getExpenseType().name(),
                expectedExpense.getAccountId(),
                expectedExpense.getCurrency().getIndex(),
                expectedExpense.getCurrency().getName(),
                expectedExpense.getCurrency().getShortName(),
                expectedExpense.getCurrency().getSymbol(),
                expectedExpense.getCategory().getIndex(),
                expectedExpense.getCategory().getTitle(),
                expectedExpense.getCategory().getColorString(),
                expectedExpense.getCategory().getDefaultExpenseType() ? 1 : 0
        });
        cursor.moveToFirst();

        try {
            ExpenseRepository.cursorToExpense(cursor);
            Assert.fail("Buchung konnte aus einem Fehlerhaften Cursor wiederhegestellt werden");

        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }

    @Test
    public void testAssertSavableExpenseWithSavableExpenseShouldSucceed() {
        ExpenseObject savableExpense = getSimpleExpense();

        savableExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE);
        mBookingRepo.assertSavableExpense(savableExpense);

        savableExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE);
        mBookingRepo.assertSavableExpense(savableExpense);

        savableExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.CHILD_EXPENSE);
        mBookingRepo.assertSavableExpense(savableExpense);

    }

    @Test
    public void testAssertSavableExpenseWithNotSavableExpenseShouldThrowUnsupportedOperationException() {
        ExpenseObject savableExpense = getSimpleExpense();

        savableExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.DATE_PLACEHOLDER);
        try {
            mBookingRepo.assertSavableExpense(savableExpense);
            Assert.fail("Konnte nicht speicherbare Buchung speichern");

        } catch (UnsupportedOperationException e) {

            assertEquals("Booking type cannot be saved.", e.getMessage());
        }

        savableExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.TRANSFER_EXPENSE);
        try {
            mBookingRepo.assertSavableExpense(savableExpense);
            Assert.fail("Konnte nicht speicherbare Buchung speichern");

        } catch (UnsupportedOperationException e) {

            assertEquals("Booking type cannot be saved.", e.getMessage());
        }

        savableExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.DUMMY_EXPENSE);
        try {
            mBookingRepo.assertSavableExpense(savableExpense);
            Assert.fail("Konnte nicht speicherbare Buchung speichern");

        } catch (UnsupportedOperationException e) {

            assertEquals("Booking type cannot be saved.", e.getMessage());
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
                    account.getBalance(),
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

            assertEquals(String.format("Could not find Expense with id %s.", expense.getIndex()), e.getMessage());
            assertEqualAccountBalance(
                    account.getBalance(),
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

            assertEquals(String.format("Could not find Expense with id %s.", expense.getIndex()), e.getMessage());
        }
    }

    private void assertEqualAccountBalance(double expectedAmount, Account account) {

        try {
            double actualBalance = mAccountRepo.get(account.getIndex()).getBalance();
            assertEquals("Konto wurde nicht geupdated", expectedAmount, actualBalance);

        } catch (AccountNotFoundException e) {

            Assert.fail("Konto wurde nicht gefunden");
        }
    }
}
