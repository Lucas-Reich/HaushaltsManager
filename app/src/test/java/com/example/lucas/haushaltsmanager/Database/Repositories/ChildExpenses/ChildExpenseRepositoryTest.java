package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ChildExpenseRepositoryTest {
    private Account account;
    private Category category;
    private AccountRepository mAccountRepo;
    private CurrencyRepository mCurrencyRepo;
    private ChildCategoryRepository mChildCategoryRepo;
    private ChildExpenseRepository mChildExpenseRepo;
    private ExpenseRepository mBookingRepo;

    private DatabaseManager mDbManagerInstance;

    @Before
    public void setup() {

        mChildExpenseRepo = new ChildExpenseRepository(RuntimeEnvironment.application);
        mDbManagerInstance = DatabaseManager.getInstance();

        mAccountRepo = new AccountRepository(RuntimeEnvironment.application);
        mCurrencyRepo = new CurrencyRepository(RuntimeEnvironment.application);
        mChildCategoryRepo = new ChildCategoryRepository(RuntimeEnvironment.application);
        mBookingRepo = new ExpenseRepository(RuntimeEnvironment.application);

        Category parentCategory = mock(Category.class);
        when(parentCategory.getIndex()).thenReturn(107L);

        category = new Category("Kategorie", "#121212", true, new ArrayList<Category>());
        category = mChildCategoryRepo.insert(parentCategory, category);

        Currency currency = new Currency("Euro", "EUR", "€");
        currency = mCurrencyRepo.create(currency);

        account = new Account("Konto", 70, currency);
        account = mAccountRepo.create(account);
    }

    @After
    public void teardown() {

        mChildExpenseRepo.closeDatabase();
        mDbManagerInstance.closeDatabase();
    }

    private ExpenseObject getSimpleExpense() {
        return new ExpenseObject(
                "Ausgabe",
                new Price(new Random().nextInt(1000), false, getDefaultCurrency()),
                category,
                account.getIndex(),
                getDefaultCurrency()
        );
    }

    private Currency getDefaultCurrency() {
        Currency currency = new Currency("Euro", "EUR", "€");
        return mCurrencyRepo.create(currency);
    }

    private ExpenseObject getParentExpenseWithChildren() {
        ExpenseObject parentExpense = getSimpleExpense();
        parentExpense.setPrice(new Price(0, getDefaultCurrency()));

        parentExpense.addChild(getSimpleExpense());
        parentExpense.addChild(getSimpleExpense());

        return parentExpense;
    }

    @Test
    public void testExistsWithValidChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        ExpenseObject childExpense = mChildExpenseRepo.insert(parentExpense, parentExpense.getChildren().get(0));

        boolean exists = mChildExpenseRepo.exists(childExpense);
        assertTrue("Kindbuchung wurde nicht gefunden", exists);
    }

    @Test
    public void testExistsWithInvalidChildExpenseShouldFail() {
        ExpenseObject childExpense = getSimpleExpense();

        boolean exists = mChildExpenseRepo.exists(childExpense);
        assertFalse("Nicht existierende KindBuchung wurde gefunden", exists);

    }

    @Test
    public void testAddChildToBookingWithExistingParentThatHasChildrenShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();

        ExpenseObject childExpense = getSimpleExpense();
        childExpense.setPrice(new Price(133, false, getDefaultCurrency()));

        try {
            ExpenseObject actualExpense = mChildExpenseRepo.addChildToBooking(childExpense, parentExpense);

            assertEquals(parentExpense.getChildren().get(0), actualExpense.getChildren().get(0));
            assertEquals(parentExpense.getChildren().get(1), actualExpense.getChildren().get(1));
            assertEquals(parentExpense.getChildren().get(2), actualExpense.getChildren().get(2));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + childExpense.getSignedPrice(),
                    account.getIndex()
            );
        } catch (AddChildToChildException e) {

            Assert.fail("Could not addItem ChildExpense to ParentExpense");
        }
    }

    @Test
    public void testAddChildToBookingWithExistingParentThatHasNoChildrenShouldSucceed() {
        ExpenseObject parentExpense = getSimpleExpense();
        parentExpense.setPrice(new Price(144, true, getDefaultCurrency()));
        parentExpense = mBookingRepo.insert(parentExpense);

        ExpenseObject childExpense = getSimpleExpense();
        childExpense.setPrice(new Price(177, true, getDefaultCurrency()));

        try {
            ExpenseObject actualParentExpense = mChildExpenseRepo.addChildToBooking(childExpense, parentExpense);

            assertEquals(parentExpense, actualParentExpense.getChildren().get(0));
            assertEquals(childExpense, actualParentExpense.getChildren().get(1));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + parentExpense.getSignedPrice() + childExpense.getSignedPrice(),
                    actualParentExpense.getChildren().get(0).getAccountId()
            );

        } catch (AddChildToChildException e) {

            Assert.fail("ParentBuchung ist keine KindBuchung");
        }
    }

    @Test
    public void testAddChildToBookingWithParentBookingIsChildShouldThrowAddChildToExpenseException() {
        ExpenseObject parentExpense = mBookingRepo.insert(getParentExpenseWithChildren());
        ExpenseObject childExpense = getSimpleExpense();

        try {
            mChildExpenseRepo.addChildToBooking(childExpense, parentExpense.getChildren().get(0));
            Assert.fail("KindBuchung konnte zu einer KindBuchung hinzugefügt werden");

        } catch (AddChildToChildException e) {

            assertEquals("It's not possible to addItem Ausgabe to Ausgabe, since Ausgabe is already a ChildExpense", e.getMessage());
        }
    }

    @Test
    public void testCombineExpensesWithParentExpensesShouldSucceed() {
        ArrayList<ExpenseObject> expenses = new ArrayList<>();
        expenses.add(mBookingRepo.insert(getSimpleExpense()));
        expenses.add(mBookingRepo.insert(getSimpleExpense()));

        ExpenseObject parentExpense = mChildExpenseRepo.combineExpenses(expenses);

        assertTrue("ParentExpense wurde nicht erstellt", mBookingRepo.exists(parentExpense));
        assertTrue("ChildExpense 1 wurde nicht erstellt", mChildExpenseRepo.exists(parentExpense.getChildren().get(0)));
        assertTrue("ChildExpense 2 wurde nicht erstellt", mChildExpenseRepo.exists(parentExpense.getChildren().get(1)));
        assertEqualAccountBalance(
                account.getBalance().getSignedValue() + expenses.get(0).getSignedPrice() + expenses.get(1).getSignedPrice(),
                account.getIndex()
        );
    }

    @Test
    public void testExtractChildFromBookingShouldSucceed() {
        ExpenseObject parentExpense = mBookingRepo.insert(getParentExpenseWithChildren());

        try {
            ExpenseObject extractedChildExpense = mChildExpenseRepo.extractChildFromBooking(parentExpense.getChildren().get(0));

            assertTrue("Die KindBuchung wurde nicht zu einer ParentBuchung konvertiert", mBookingRepo.exists(extractedChildExpense));
            assertFalse("Die extrahierte KindBuchung wurde nicht gelöscht", mChildExpenseRepo.exists(extractedChildExpense));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + parentExpense.getChildren().get(0).getSignedPrice() + parentExpense.getChildren().get(1).getSignedPrice(),
                    account.getIndex()
            );

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("Existierende KindBuchung konnt enicht extrahiert werden");
        }
    }

    @Test
    public void testExtractLastChildFromBookingShouldSucceedAndParentBookingShouldBeRemoved() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        parentExpense.removeChild(parentExpense.getChildren().get(1));
        parentExpense = mBookingRepo.insert(parentExpense);

        try {
            ExpenseObject extractedChildExpense = mChildExpenseRepo.extractChildFromBooking(parentExpense.getChildren().get(0));

            assertTrue("KindBuchung wurde nicht in eine ParentBuchung konvertiert", mBookingRepo.exists(extractedChildExpense));
            assertFalse("ParentBuchung ohne Kinder wurde nicht gelöscht", mBookingRepo.exists(parentExpense));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + parentExpense.getChildren().get(0).getSignedPrice(),
                    account.getIndex()
            );

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("KindBuchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testExtractChildFromBookingWithNotExistingChildBookingShouldThrowChildExpenseNotFoundException() {
        ExpenseObject childExpense = getSimpleExpense();

        try {
            mChildExpenseRepo.extractChildFromBooking(childExpense);
            Assert.fail("Nicht existierende KindBuchung wurde gefunden");

        } catch (ChildExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Child Booking with id %s.", childExpense.getIndex()), e.getMessage());
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account.getIndex()
            );
        }
    }

    @Test
    public void testGetWithExistingChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        ExpenseObject expectedChildExpense = mChildExpenseRepo.insert(parentExpense, parentExpense.getChildren().get(0));

        try {
            ExpenseObject actualChildExpense = mChildExpenseRepo.get(expectedChildExpense.getIndex());
            assertEquals(expectedChildExpense, actualChildExpense);

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("KindBuchung konnte nicht gefunden werden");
        }
    }

    @Test
    public void testGetWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {
        long notExistingChildExpenseId = 524L;

        try {
            mChildExpenseRepo.get(notExistingChildExpenseId);
            Assert.fail("Nicht existierende KindBuchung wurde gefunden");

        } catch (ChildExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Child Booking with id %s.", notExistingChildExpenseId), e.getMessage());
        }
    }

    @Test
    public void testGetAllShouldOnlyReturnChildrenThatAreNotHidden() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        parentExpense = mBookingRepo.insert(parentExpense);

        try {
            mChildExpenseRepo.hide(parentExpense.getChildren().get(0));
            List<ExpenseObject> fetchedChildren = mChildExpenseRepo.getAll(parentExpense.getIndex());

            assertEquals("Es wurden zu viele Kinder aus der Datenbank geholt", 1, fetchedChildren.size());
            assertFalse("Ein verstecktes Kind wurde aus der Datenbank geholt", fetchedChildren.contains(parentExpense.getChildren().get(0)));
            assertTrue("Ein nicht verstecktes Kind wurde aus der Datenbank geholt", fetchedChildren.contains(parentExpense.getChildren().get(1)));
        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("Ausgabe konnte nicht gefunden werden");
        }
    }

    @Test
    public void testUpdateWithExistingChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        ExpenseObject expectedChildExpense = mChildExpenseRepo.insert(parentExpense, parentExpense.getChildren().get(0));

        try {
            expectedChildExpense.setPrice(new Price(13, true, getDefaultCurrency()));

            mChildExpenseRepo.update(expectedChildExpense);
            ExpenseObject actualChildExpense = mChildExpenseRepo.get(expectedChildExpense.getIndex());

            assertEquals(expectedChildExpense, actualChildExpense);
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + expectedChildExpense.getSignedPrice(),
                    account.getIndex()
            );

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("KindBuchung konnte nicht gefunden werden");
        }
    }

    @Test
    public void testUpdateWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {
        ExpenseObject childExpense = getSimpleExpense();

        try {
            mChildExpenseRepo.update(childExpense);
            Assert.fail("Nicht existierende KindBuchung konnte geupdated werden");

        } catch (ChildExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Child Booking with id %s.", childExpense.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithExistingChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = mBookingRepo.insert(getParentExpenseWithChildren());
        ExpenseObject childExpense = parentExpense.getChildren().get(0);

        try {
            mChildExpenseRepo.delete(childExpense);

            assertFalse("Buchung wurde nicht gelöscht", mChildExpenseRepo.exists(childExpense));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + parentExpense.getChildren().get(1).getSignedPrice(),
                    childExpense.getAccountId()
            );

        } catch (CannotDeleteChildExpenseException e) {

            Assert.fail("KindBuchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithNotExistingChildExpenseShouldSucceed() {
        ExpenseObject childExpense = getSimpleExpense();

        try {
            mChildExpenseRepo.delete(childExpense);
            assertFalse("Nicht existierende KindBuchung wurde gefunden", mChildExpenseRepo.exists(childExpense));

        } catch (Exception e) {

            Assert.fail("Nicht existierende KindBuchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithChildExpenseIsLastOfParentShouldDeleteParentAsWell() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        parentExpense.getChildren().remove(parentExpense.getChildren().get(1));
        parentExpense = mBookingRepo.insert(parentExpense);
        assertTrue("ParentExpense wurde nicht erstellt", mBookingRepo.exists(parentExpense));

        try {
            mChildExpenseRepo.delete(parentExpense.getChildren().get(0));

            assertFalse("KindBuchung wurde nicht gelöscht", mChildExpenseRepo.exists(parentExpense.getChildren().get(0)));
            assertFalse("ParentBuchung wurde nicht gelöscht", mBookingRepo.exists(parentExpense));

        } catch (Exception e) {

            Assert.fail("KindBuchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testGetParentWithExistingChildExpenseShouldSucceed() {
        ExpenseObject expectedParentExpense = mBookingRepo.insert(getParentExpenseWithChildren());

        try {
            ExpenseObject actualParentExpense = mChildExpenseRepo.getParent(expectedParentExpense.getChildren().get(0));
            assertEquals(expectedParentExpense, actualParentExpense);

        } catch (Exception e) {

            Assert.fail("Existierende ParentBuchung wurde nicht gefunden");
        }
    }

    @Test
    public void testGetParentWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {
        ExpenseObject childExpense = getSimpleExpense();

        try {
            mChildExpenseRepo.getParent(childExpense);
            Assert.fail("Konnte einen Parent zu einer nicht existierenden KindBuchung finden");

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Existierende ParentBuchung konnte nicht gefunden werden");
        } catch (ChildExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Child Booking with id %s.", childExpense.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testGetParentWithNotExistingParentExpenseShouldThrowExpenseNotFoundException() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        ExpenseObject childExpense = mChildExpenseRepo.insert(parentExpense, parentExpense.getChildren().get(0));

        try {
            mChildExpenseRepo.getParent(childExpense);
            Assert.fail("Ein nicht existierenden Parent wurde gefunden");

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("KindBuchung wurde nicht gefunden");
        } catch (ExpenseNotFoundException e) {

            assertEquals(String.format("Could not find ParentExpense for ChildExpense %s.", childExpense.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testCursorToChildBookingWithValidCursorShouldSucceed() {
        ExpenseObject expectedChildExpense = getSimpleExpense();
        expectedChildExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.CHILD_EXPENSE);

        String[] columns = new String[]{
                ExpensesDbHelper.BOOKINGS_COL_ID,
                ExpensesDbHelper.BOOKINGS_COL_DATE,
                ExpensesDbHelper.BOOKINGS_COL_TITLE,
                ExpensesDbHelper.BOOKINGS_COL_PRICE,
                ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE,
                ExpensesDbHelper.BOOKINGS_COL_NOTICE,
                ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID,
                ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID,
                ExpensesDbHelper.CURRENCIES_COL_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SYMBOL,
                ExpensesDbHelper.CATEGORIES_COL_ID,
                ExpensesDbHelper.CATEGORIES_COL_NAME,
                ExpensesDbHelper.CATEGORIES_COL_COLOR,
                ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{
                expectedChildExpense.getIndex(),
                expectedChildExpense.getDate().getTimeInMillis(),
                expectedChildExpense.getTitle(),
                expectedChildExpense.getUnsignedPrice(),
                expectedChildExpense.isExpenditure() ? 1 : 0,
                expectedChildExpense.getNotice(),
                expectedChildExpense.getAccountId(),
                expectedChildExpense.getCurrency().getIndex(),
                expectedChildExpense.getCurrency().getName(),
                expectedChildExpense.getCurrency().getShortName(),
                expectedChildExpense.getCurrency().getSymbol(),
                expectedChildExpense.getCategory().getIndex(),
                expectedChildExpense.getCategory().getTitle(),
                expectedChildExpense.getCategory().getColor().getColorString(),
                expectedChildExpense.getCategory().getDefaultExpenseType() ? 1 : 0
        });
        cursor.moveToFirst();

        try {
            ExpenseObject actualChildExpense = mChildExpenseRepo.fromCursor(cursor);
            assertEquals(expectedChildExpense, actualChildExpense);

        } catch (CursorIndexOutOfBoundsException e) {

            Assert.fail("Ausgabe konnte nicht aus einem vollständigen Cursor wiederhergestellt werden");
        }
    }

    @Test
    public void testCursorToChildBookingWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        ExpenseObject expectedChildExpense = getSimpleExpense();
        expectedChildExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.CHILD_EXPENSE);

        String[] columns = new String[]{
                ExpensesDbHelper.BOOKINGS_COL_ID,
                ExpensesDbHelper.BOOKINGS_COL_DATE,
                ExpensesDbHelper.BOOKINGS_COL_TITLE,
                //Der Preis der KindBuchung wurde nicht mit abgefragt
                ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE,
                ExpensesDbHelper.BOOKINGS_COL_NOTICE,
                ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID,
                ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID,
                ExpensesDbHelper.CURRENCIES_COL_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SYMBOL,
                ExpensesDbHelper.CATEGORIES_COL_ID,
                ExpensesDbHelper.CATEGORIES_COL_NAME,
                ExpensesDbHelper.CATEGORIES_COL_COLOR,
                ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{
                expectedChildExpense.getIndex(),
                expectedChildExpense.getDate().getTimeInMillis(),
                expectedChildExpense.getTitle(),
                expectedChildExpense.isExpenditure() ? 1 : 0,
                expectedChildExpense.getNotice(),
                expectedChildExpense.getAccountId(),
                expectedChildExpense.getCurrency().getIndex(),
                expectedChildExpense.getCurrency().getName(),
                expectedChildExpense.getCurrency().getShortName(),
                expectedChildExpense.getCurrency().getSymbol(),
                expectedChildExpense.getCategory().getIndex(),
                expectedChildExpense.getCategory().getTitle(),
                expectedChildExpense.getCategory().getColor().getColorString(),
                expectedChildExpense.getCategory().getDefaultExpenseType() ? 1 : 0
        });
        cursor.moveToFirst();

        try {
            mChildExpenseRepo.fromCursor(cursor);
            Assert.fail("KindBuchung konnte trotz eines Fehlerhaften Cursor widerhergestellt werden");

        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }

    @Test
    public void testHideWithExistingChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = mBookingRepo.insert(getParentExpenseWithChildren());

        try {
            ExpenseObject hiddenChild = parentExpense.getChildren().get(0);
            mChildExpenseRepo.hide(hiddenChild);

            assertTrue("KindBuchung wurde gelöscht", mChildExpenseRepo.exists(hiddenChild));
            assertFalse("Versteckte Buchung wurde aus der Datenbank geholt", mChildExpenseRepo.getAll(parentExpense.getIndex()).contains(hiddenChild));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + parentExpense.getChildren().get(1).getSignedPrice(),
                    account.getIndex()
            );

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("Kind Buchung wurde nicht gefunden");
        }
    }

    @Test
    public void testHideLastChildOfParentShouldHideParentAsWell() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        parentExpense.getChildren().remove(1);
        parentExpense = mBookingRepo.insert(parentExpense);

        try {
            ExpenseObject hiddenChildExpense = parentExpense.getChildren().get(0);
            mChildExpenseRepo.hide(hiddenChildExpense);

            assertTrue("ParentBuchung wurde nicht versteckt", mBookingRepo.isHidden(parentExpense));
            assertFalse("KindBuchung wurde nicht versteckt", mChildExpenseRepo.getAll(parentExpense.getIndex()).contains(hiddenChildExpense));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account.getIndex()
            );
        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("KindBuchung wurde nicht gefunden");
        } catch (ExpenseNotFoundException e) {

            Assert.fail("ParetnBuchung wurde nicht gefunden");
        }
    }

    @Test
    public void testHideWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {
        ExpenseObject childExpense = getSimpleExpense();

        try {
            mChildExpenseRepo.hide(childExpense);
            Assert.fail("Nicht existierende ChildExpense konnte versteckt werden");

        } catch (ChildExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Child Booking with id %s.", childExpense.getIndex()), e.getMessage());
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account.getIndex()
            );
        }
    }

    @Test
    public void testIsHiddenWithExistingChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = mBookingRepo.insert(getParentExpenseWithChildren());

        try {
            ExpenseObject hiddenChildExpense = parentExpense.getChildren().get(0);

            boolean isHidden = mBookingRepo.isHidden(hiddenChildExpense);
            assertFalse("ChildExpense ist versteckt", isHidden);

            mChildExpenseRepo.hide(hiddenChildExpense);
            isHidden = mBookingRepo.isHidden(hiddenChildExpense);
            assertTrue("ChildExpense ist nicht versteckt", isHidden);

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("ChildExpense wurde nicht gefunden");
        } catch (ExpenseNotFoundException e) {

            Assert.fail("ParentExpense wurde nicht gefunden");
        }
    }

    @Test
    public void testIsHiddenWithNotExistingChildExpenseShouldThrowExpenseNotFoundException() {
        ExpenseObject childExpense = getSimpleExpense();

        try {
            mChildExpenseRepo.isHidden(childExpense);
            Assert.fail("ChildExpense wurde gefunden");

        } catch (ChildExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Child Booking with id %s.", childExpense.getIndex()), e.getMessage());
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account.getIndex()
            );
        }
    }

    private void assertEqualAccountBalance(double expectedAmount, long accountId) {

        try {
            double actualBalance = mAccountRepo.get(accountId).getBalance().getSignedValue();
            assertEquals("Konto wurde nicht geupdated", expectedAmount, actualBalance);

        } catch (AccountNotFoundException e) {

            Assert.fail("Konto wurde nicht gefunden");
        }
    }
}