package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import android.content.Context;
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
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ChildExpenseRepositoryTest {
    private Account account;
    private Category category;

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.application;
        ExpensesDbHelper dbHelper = new ExpensesDbHelper(context);
        DatabaseManager.initializeInstance(dbHelper);

        Category parentCategory = mock(Category.class);
        when(parentCategory.getIndex()).thenReturn(107L);

        category = new Category("Kategorie", "#121212", true, new ArrayList<Category>());
        category = ChildCategoryRepository.insert(parentCategory, category);

        Currency currency = new Currency("Euro", "EUR", "€");
        currency = CurrencyRepository.insert(currency);

        account = new Account("Konto", 70, currency);
        account = AccountRepository.insert(account);
    }

    private ExpenseObject getSimpleExpense() {

        return new ExpenseObject(
                "Ausgabe",
                100,
                false,
                category,
                account
        );
    }

    private ExpenseObject getParentExpenseWithChildren() {
        ExpenseObject parentExpense = getSimpleExpense();
        parentExpense.addChild(getSimpleExpense());
        parentExpense.addChild(getSimpleExpense());

        return parentExpense;
    }

    @Test
    public void testExistsWithValidChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = mock(ExpenseObject.class);
        when(parentExpense.getIndex()).thenReturn(77L);
        ExpenseObject childExpense = ChildExpenseRepository.insert(parentExpense, getSimpleExpense());

        boolean exists = ChildExpenseRepository.exists(childExpense);
        assertTrue("Kindbuchung wurde nicht gefunden", exists);
    }

    @Test
    public void testExistsWithInvalidChildExpenseShouldFail() {
        ExpenseObject childExpense = getSimpleExpense();

        boolean exists = ChildExpenseRepository.exists(childExpense);
        assertFalse("Nicht existierende KindBuchung wurde gefunden", exists);

    }

    @Test
    public void testAddChildToBookingWithExistingParentThatHasChildrenShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        parentExpense = ExpenseRepository.insert(parentExpense);

        ExpenseObject childExpense = getSimpleExpense();
        childExpense.setExpenditure(false);
        childExpense.setPrice(133);

        try {
            ExpenseObject actualExpense = ChildExpenseRepository.addChildToBooking(childExpense, parentExpense);
            assertEquals(parentExpense.getChildren().get(0), actualExpense.getChildren().get(0));
            assertEquals(parentExpense.getChildren().get(1), actualExpense.getChildren().get(1));
            assertEquals(parentExpense.getChildren().get(2), actualExpense.getChildren().get(2));

            assertEqualAccountBalance(
                    account.getBalance() + parentExpense.getChildren().get(0).getSignedPrice() + parentExpense.getChildren().get(1).getSignedPrice() + childExpense.getSignedPrice(),
                    account
            );
        } catch (AddChildToChildException e) {

            Assert.fail("KindBuchung konnte nicht zu einem Parent hinzugefügt werden");
        }
        //todo den kontostand überprüfen
    }

    @Test
    public void testAddChildToBookingWithExistingParentThatHasNoChildrenShouldSucceed() {
        ExpenseObject parentExpense = getSimpleExpense();
        parentExpense.setExpenditure(true);
        parentExpense.setPrice(144);
        parentExpense = ExpenseRepository.insert(parentExpense);

        ExpenseObject childExpense = getSimpleExpense();
        childExpense.setExpenditure(true);
        childExpense.setPrice(177);

        try {
            ExpenseObject actualParentExpense = ChildExpenseRepository.addChildToBooking(childExpense, parentExpense);
            assertEquals(parentExpense, actualParentExpense.getChildren().get(0));
            assertEquals(childExpense, actualParentExpense.getChildren().get(1));

            assertEqualAccountBalance(
                    account.getBalance() + parentExpense.getSignedPrice() + childExpense.getSignedPrice(),
                    actualParentExpense.getChildren().get(0).getAccount()
            );

        } catch (AddChildToChildException e) {

            Assert.fail("ParentBuchung ist keine KindBuchung");
        }
        //todo den kontostand überprüfen
    }

    @Test
    public void testAddChildToBookingWithParentBookingIsChildShouldThrowAddChildToExpenseException() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        parentExpense = ExpenseRepository.insert(parentExpense);

        ExpenseObject childExpense = getSimpleExpense();

        try {
            ChildExpenseRepository.addChildToBooking(childExpense, parentExpense.getChildren().get(0));
            Assert.fail("KindBuchung konnte zu einer KindBuchung hinzugefügt werden");

        } catch (AddChildToChildException e) {

            assertEquals("It is not possible to add children to a ChildExpense.", e.getMessage());
        }
    }

    @Test
    public void testCombineExpensesWithParentExpensesShouldSucceed() {
        ArrayList<ExpenseObject> expenses = new ArrayList<>();

        ExpenseObject parentExpense1 = getSimpleExpense();
        parentExpense1 = ExpenseRepository.insert(parentExpense1);
        expenses.add(parentExpense1);

        ExpenseObject parentExpense2 = getSimpleExpense();
        parentExpense2 = ExpenseRepository.insert(parentExpense2);
        expenses.add(parentExpense2);

        ChildExpenseRepository.combineExpenses(expenses);
        assertTrue("Erste Parent Expense wurde nicht als ChildExpense gespeichert", ChildExpenseRepository.exists(parentExpense1));
        assertTrue("Zweite Parent Expense wurde nicht als ChildExpense gespeichert", ChildExpenseRepository.exists(parentExpense2));
    }

    @Test
    public void testExtractChildFromBookingShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        parentExpense = ExpenseRepository.insert(parentExpense);
        //todo wenn ein Kind extrahiert wird dann wird der falsche betrag dem Konto zugeschrieben

        try {
            ExpenseObject extractedChildExpense = ChildExpenseRepository.extractChildFromBooking(parentExpense.getChildren().get(0));
            assertTrue("Die KindBuchung wurde nicht zu einer ParentBuchung konvertiert", ExpenseRepository.exists(extractedChildExpense));
            assertFalse("Die extrahierte KindBuchung wurde nicht gelöscht", ChildExpenseRepository.exists(extractedChildExpense));

            //todo noch ein assertEqualAccountBalance einfügen

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("Existierende KindBuchung konnt enicht extrahiert werden");
        }
    }

    @Test
    public void testExtractLastChildFromBookingShouldSucceedAndParentBookingShouldBeRemoved() {
        ExpenseObject parentExpense = getSimpleExpense();
        parentExpense.addChild(getSimpleExpense());
        parentExpense = ExpenseRepository.insert(parentExpense);

        try {
            ExpenseObject extractedChildExpense = ChildExpenseRepository.extractChildFromBooking(parentExpense.getChildren().get(0));

            assertTrue("KindBuchung wurde nicht in eine ParentBuchung konvertiert", ExpenseRepository.exists(extractedChildExpense));
            assertFalse("ParentBuchung ohne Kinder wurde nicht gelöscht", ExpenseRepository.exists(parentExpense));

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("KindBuchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testExtractChildFromBookingWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {
        ExpenseObject childExpense = getSimpleExpense();

        try {
            ChildExpenseRepository.extractChildFromBooking(childExpense);
            Assert.fail("Nicht existierende KindBuchung konnte extrahiert werden");

        } catch (ChildExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Child Expense with id %s.", childExpense.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testExtractChildFromBookingWithNotExistingChildBookingShouldThrowChildExpenseNotFoundException() {
        ExpenseObject childExpense = getSimpleExpense();

        try {
            ChildExpenseRepository.extractChildFromBooking(childExpense);
            Assert.fail("Nicht existierende KindBuchung wurde gefunden");

        } catch (ChildExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Child Expense with id %s.", childExpense.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testGetWithExistingChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = mock(ExpenseObject.class);
        when(parentExpense.getIndex()).thenReturn(876L);

        ExpenseObject expectedChildExpense = getSimpleExpense();
        expectedChildExpense.setExpenseType(ExpenseObject.EXPENSE_TYPES.CHILD_EXPENSE);
        expectedChildExpense = ChildExpenseRepository.insert(parentExpense, expectedChildExpense);

        try {
            ExpenseObject actualChildExpense = ChildExpenseRepository.get(expectedChildExpense.getIndex());
            assertEquals(expectedChildExpense, actualChildExpense);

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("KindBuchung konnte nicht gefunden werden");
        }
    }

    @Test
    public void testGetWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {
        long notExistingChildExpenseId = 524L;

        try {
            ChildExpenseRepository.get(notExistingChildExpenseId);
            Assert.fail("Nicht existierende KindBuchung wurde gefunden");

        } catch (ChildExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Child Expense with id %s.", notExistingChildExpenseId), e.getMessage());
        }
    }

    @Test
    public void testUpdateWithExistingChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        parentExpense = ExpenseRepository.insert(parentExpense);

        try {
            ExpenseObject expectedChildExpense = parentExpense.getChildren().get(1);
            expectedChildExpense.setPrice(13);
            expectedChildExpense.setExpenditure(true);

            ChildExpenseRepository.update(expectedChildExpense);
            ExpenseObject actualChildExpense = ChildExpenseRepository.get(expectedChildExpense.getIndex());
            assertEquals(expectedChildExpense, actualChildExpense);

            assertEqualAccountBalance(
                    account.getBalance() + parentExpense.getChildren().get(0).getSignedPrice() + expectedChildExpense.getSignedPrice(),
                    account
            );
        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("KindBuchung konnte nicht gefunden werden");
        }
    }

    @Test
    public void testUpdateWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {
        ExpenseObject childExpense = getSimpleExpense();

        try {
            ChildExpenseRepository.update(childExpense);
            Assert.fail("Nicht existierende KindBuchung konnte geupdated werden");

        } catch (ChildExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Child Expense with id %s.", childExpense.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithExistingChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        parentExpense = ExpenseRepository.insert(parentExpense);

        try {
            ExpenseObject childExpense = parentExpense.getChildren().get(0);
            ChildExpenseRepository.delete(childExpense);
            assertFalse("Buchung wurde nicht gelöscht", ChildExpenseRepository.exists(childExpense));

            assertEqualAccountBalance(
                    account.getBalance() + parentExpense.getChildren().get(0).getSignedPrice() + parentExpense.getChildren().get(1).getSignedPrice(),
                    childExpense.getAccount()
            );

        } catch (CannotDeleteChildExpenseException e) {

            Assert.fail("KindBuchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithNotExistingChildExpenseShouldSucceed() {
        ExpenseObject childExpense = getSimpleExpense();

        try {
            ChildExpenseRepository.delete(childExpense);
            assertFalse("Nicht existierende KindBuchung wurde gefunden", ChildExpenseRepository.exists(childExpense));

        } catch (Exception e) {

            Assert.fail("Nicht existierende KindBuchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithChildExpenseIsLastOfParentShouldDeleteParentAsWell() {
        ExpenseObject parentExpense = getSimpleExpense();
        parentExpense.addChild(getSimpleExpense());

        parentExpense = ExpenseRepository.insert(parentExpense);
        assertTrue("ParentExpense wurde nicht erstellt", ExpenseRepository.exists(parentExpense));

        try {
            ExpenseObject childExpense = parentExpense.getChildren().get(0);
            ChildExpenseRepository.delete(childExpense);

            assertFalse("KindBuchung wurde nicht gelöscht", ChildExpenseRepository.exists(childExpense));
            assertFalse("ParentBuchung wurde nicht gelöscht", ExpenseRepository.exists(parentExpense));

        } catch (Exception e) {

            Assert.fail("KindBuchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testGetParentWithExistingChildExpenseShouldSucceed() {
        ExpenseObject expectedParentExpense = getParentExpenseWithChildren();
        expectedParentExpense = ExpenseRepository.insert(expectedParentExpense);

        try {
            ExpenseObject actualParentExpense = ChildExpenseRepository.getParent(expectedParentExpense.getChildren().get(0));
            assertEquals(expectedParentExpense, actualParentExpense);

        } catch (Exception e) {

            Assert.fail("Existierende ParentBuchung wurde nicht gefunden");
        }
    }

    @Test
    public void testGetParentWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {
        ExpenseObject childExpense = getSimpleExpense();

        try {
            ChildExpenseRepository.getParent(childExpense);
            Assert.fail("Konnte einen Parent zu einer nicht existierenden KindBuchung finden");

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Existierende ParentBuchung konnte nicht gefunden werden");
        } catch (ChildExpenseNotFoundException e) {

            assertEquals(String.format("Could not find Child Expense with id %s.", childExpense.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testGetParentWithNotExistingParentExpenseShouldThrowExpenseNotFoundException() {
        ExpenseObject parentExpense = mock(ExpenseObject.class);
        when(parentExpense.getIndex()).thenReturn(777L);

        ExpenseObject childExpense = getSimpleExpense();
        childExpense = ChildExpenseRepository.insert(parentExpense, childExpense);

        try {
            ChildExpenseRepository.getParent(childExpense);
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
                ExpensesDbHelper.CHILD_BOOKINGS_COL_ID,
                ExpensesDbHelper.CHILD_BOOKINGS_COL_DATE,
                ExpensesDbHelper.CHILD_BOOKINGS_COL_TITLE,
                ExpensesDbHelper.CHILD_BOOKINGS_COL_PRICE,
                ExpensesDbHelper.CHILD_BOOKINGS_COL_EXPENDITURE,
                ExpensesDbHelper.CHILD_BOOKINGS_COL_NOTICE,
                ExpensesDbHelper.CATEGORIES_COL_ID,
                ExpensesDbHelper.CATEGORIES_COL_NAME,
                ExpensesDbHelper.CATEGORIES_COL_COLOR,
                ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE,
                ExpensesDbHelper.ACCOUNTS_COL_ID,
                ExpensesDbHelper.ACCOUNTS_COL_NAME,
                ExpensesDbHelper.ACCOUNTS_COL_BALANCE,
                ExpensesDbHelper.CURRENCIES_COL_ID,
                ExpensesDbHelper.CURRENCIES_COL_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SYMBOL
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{
                expectedChildExpense.getIndex(),
                expectedChildExpense.getDateTime().getTimeInMillis(),
                expectedChildExpense.getTitle(),
                expectedChildExpense.getUnsignedPrice(),
                expectedChildExpense.isExpenditure() ? 1 : 0,
                expectedChildExpense.getNotice(),
                expectedChildExpense.getCategory().getIndex(),
                expectedChildExpense.getCategory().getTitle(),
                expectedChildExpense.getCategory().getColorString(),
                expectedChildExpense.getCategory().getDefaultExpenseType() ? 1 : 0,
                expectedChildExpense.getAccount().getIndex(),
                expectedChildExpense.getAccount().getTitle(),
                expectedChildExpense.getAccount().getBalance(),
                expectedChildExpense.getAccount().getCurrency().getIndex(),
                expectedChildExpense.getAccount().getCurrency().getName(),
                expectedChildExpense.getAccount().getCurrency().getShortName(),
                expectedChildExpense.getAccount().getCurrency().getSymbol()
        });
        cursor.moveToFirst();

        try {
            ExpenseObject actualChildExpense = ChildExpenseRepository.cursorToChildBooking(cursor);
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
                ExpensesDbHelper.CHILD_BOOKINGS_COL_ID,
                ExpensesDbHelper.CHILD_BOOKINGS_COL_DATE,
                ExpensesDbHelper.CHILD_BOOKINGS_COL_TITLE,
                //Der Preis der KindBuchung wurde nicht mit abgefragt
                ExpensesDbHelper.CHILD_BOOKINGS_COL_EXPENDITURE,
                ExpensesDbHelper.CHILD_BOOKINGS_COL_NOTICE,
                ExpensesDbHelper.CATEGORIES_COL_ID,
                ExpensesDbHelper.CATEGORIES_COL_NAME,
                ExpensesDbHelper.CATEGORIES_COL_COLOR,
                ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE,
                ExpensesDbHelper.ACCOUNTS_COL_ID,
                ExpensesDbHelper.ACCOUNTS_COL_NAME,
                ExpensesDbHelper.ACCOUNTS_COL_BALANCE,
                ExpensesDbHelper.CURRENCIES_COL_ID,
                ExpensesDbHelper.CURRENCIES_COL_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME,
                ExpensesDbHelper.CURRENCIES_COL_SYMBOL
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{
                expectedChildExpense.getIndex(),
                expectedChildExpense.getDateTime().getTimeInMillis(),
                expectedChildExpense.getTitle(),
                expectedChildExpense.isExpenditure() ? 1 : 0,
                expectedChildExpense.getNotice(),
                expectedChildExpense.getCategory().getIndex(),
                expectedChildExpense.getCategory().getTitle(),
                expectedChildExpense.getCategory().getColorString(),
                expectedChildExpense.getCategory().getDefaultExpenseType() ? 1 : 0,
                expectedChildExpense.getAccount().getIndex(),
                expectedChildExpense.getAccount().getTitle(),
                expectedChildExpense.getAccount().getBalance(),
                expectedChildExpense.getAccount().getCurrency().getIndex(),
                expectedChildExpense.getAccount().getCurrency().getName(),
                expectedChildExpense.getAccount().getCurrency().getShortName(),
                expectedChildExpense.getAccount().getCurrency().getSymbol()
        });
        cursor.moveToFirst();

        try {
            ChildExpenseRepository.cursorToChildBooking(cursor);
            Assert.fail("KindBuchung konnte trotz eines Fehlerhaften Cursor widerhergestellt werden");

        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }

    private void assertEqualAccountBalance(double expectedAmount, Account account) {

        try {
            double actualBalance = AccountRepository.get(account.getIndex()).getBalance();
            assertEquals("Konto wurde nicht geupdated", expectedAmount, actualBalance);

        } catch (AccountNotFoundException e) {

            Assert.fail("Konto wurde nicht gefunden");
        }
    }
}
