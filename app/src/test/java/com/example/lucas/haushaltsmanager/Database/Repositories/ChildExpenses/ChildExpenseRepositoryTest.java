package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
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
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ChildExpenseRepositoryTest {
    private Account account;
    private AccountRepository mAccountRepo;
    private ChildExpenseRepository mChildExpenseRepo;
    private ExpenseRepository mBookingRepo;

    private DatabaseManager mDbManagerInstance;

    @Before
    public void setup() throws AccountCouldNotBeCreatedException {

        mChildExpenseRepo = new ChildExpenseRepository(RuntimeEnvironment.application);
        mDbManagerInstance = DatabaseManager.getInstance();

        mAccountRepo = new AccountRepository(RuntimeEnvironment.application);
        mBookingRepo = new ExpenseRepository(RuntimeEnvironment.application);

        account = new Account("Konto", new Price(70));
        mAccountRepo.insert(account);
    }

    @After
    public void teardown() {

        mChildExpenseRepo.closeDatabase();
        mDbManagerInstance.closeDatabase();
    }

    @Test
    public void testExistsWithValidChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        ExpenseObject childExpense = parentExpense.getChildren().get(0);
        mChildExpenseRepo.insert(parentExpense, childExpense);

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
        childExpense.setPrice(new Price(133, false));

        try {
            ExpenseObject actualExpense = mChildExpenseRepo.addChildToBooking(childExpense, parentExpense);

            assertEquals(parentExpense.getChildren().get(0), actualExpense.getChildren().get(0));
            assertEquals(parentExpense.getChildren().get(1), actualExpense.getChildren().get(1));
            assertEquals(parentExpense.getChildren().get(2), actualExpense.getChildren().get(2));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + childExpense.getSignedPrice(),
                    account.getId()
            );
        } catch (AddChildToChildException e) {

            Assert.fail("Could not addItem ChildExpense to ParentExpense");
        }
    }

    @Test
    public void testAddChildToBookingWithExistingParentThatHasNoChildrenShouldSucceed() {
        ExpenseObject parentExpense = getSimpleExpense();
        parentExpense.setPrice(new Price(144, true));
        mBookingRepo.insert(parentExpense);

        ExpenseObject childExpense = getSimpleExpense();
        childExpense.setPrice(new Price(177, true));

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
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        mBookingRepo.insert(parentExpense);
        ExpenseObject childExpense = getSimpleExpense();

        try {
            mChildExpenseRepo.addChildToBooking(childExpense, parentExpense.getChildren().get(0));
            Assert.fail("KindBuchung konnte zu einer KindBuchung hinzugefügt werden");

        } catch (AddChildToChildException e) {

            assertEquals("It's not possible to addItem Ausgabe to Ausgabe, since Ausgabe is already a ChildExpense", e.getMessage());
        }
    }

    @Test
    public void testExtractChildFromBookingShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        mBookingRepo.insert(parentExpense);

        try {
            ExpenseObject extractedChildExpense = mChildExpenseRepo.extractChildFromBooking(parentExpense.getChildren().get(0));

            assertBookingExists(extractedChildExpense);
            assertFalse("Die extrahierte KindBuchung wurde nicht gelöscht", mChildExpenseRepo.exists(extractedChildExpense));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + parentExpense.getChildren().get(0).getSignedPrice() + parentExpense.getChildren().get(1).getSignedPrice(),
                    account.getId()
            );

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("Existierende KindBuchung konnt enicht extrahiert werden");
        }
    }

    @Test
    public void testExtractLastChildFromBookingShouldSucceedAndParentBookingShouldBeRemoved() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        parentExpense.removeChild(parentExpense.getChildren().get(1));
        mBookingRepo.insert(parentExpense);

        try {
            ExpenseObject extractedChildExpense = mChildExpenseRepo.extractChildFromBooking(parentExpense.getChildren().get(0));

            assertBookingExists(extractedChildExpense);
            assertBookingNotExisting(parentExpense);
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + parentExpense.getChildren().get(0).getSignedPrice(),
                    account.getId()
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

            assertEquals(String.format("Could not find Child Booking with id %s.", childExpense.getId().toString()), e.getMessage());
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account.getId()
            );
        }
    }

    @Test
    public void testGetWithExistingChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        ExpenseObject expectedChildExpense = parentExpense.getChildren().get(0);
        mChildExpenseRepo.insert(parentExpense, expectedChildExpense);

        try {
            ExpenseObject actualChildExpense = mChildExpenseRepo.get(expectedChildExpense.getId());
            assertEquals(expectedChildExpense, actualChildExpense);

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("KindBuchung konnte nicht gefunden werden");
        }
    }

    @Test
    public void testGetWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {
        UUID notExistingChildExpenseId = UUID.randomUUID();

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
        mBookingRepo.insert(parentExpense);

        try {
            mChildExpenseRepo.hide(parentExpense.getChildren().get(0));
            List<ExpenseObject> fetchedChildren = mChildExpenseRepo.getAll(parentExpense.getId());

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
        ExpenseObject expectedChildExpense = parentExpense.getChildren().get(0);
        mChildExpenseRepo.insert(parentExpense, expectedChildExpense);

        try {
            expectedChildExpense.setPrice(new Price(13, true));

            mChildExpenseRepo.update(expectedChildExpense);
            ExpenseObject actualChildExpense = mChildExpenseRepo.get(expectedChildExpense.getId());

            assertEquals(expectedChildExpense, actualChildExpense);
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + expectedChildExpense.getSignedPrice(),
                    account.getId()
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

            assertEquals(String.format("Could not find Child Booking with id %s.", childExpense.getId().toString()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithExistingChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        mBookingRepo.insert(parentExpense);
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
        mBookingRepo.insert(parentExpense);
        assertBookingExists(parentExpense);

        try {
            mChildExpenseRepo.delete(parentExpense.getChildren().get(0));

            assertFalse("KindBuchung wurde nicht gelöscht", mChildExpenseRepo.exists(parentExpense.getChildren().get(0)));
            assertBookingNotExisting(parentExpense);

        } catch (Exception e) {

            Assert.fail("KindBuchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testGetParentWithExistingChildExpenseShouldSucceed() {
        ExpenseObject expectedParentExpense = getParentExpenseWithChildren();
        mBookingRepo.insert(expectedParentExpense);

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

            assertEquals(String.format("Could not find Child Booking with id %s.", childExpense.getId().toString()), e.getMessage());
        }
    }

    @Test
    public void testGetParentWithNotExistingParentExpenseShouldThrowExpenseNotFoundException() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        ExpenseObject childExpense = parentExpense.getChildren().get(0);
        mChildExpenseRepo.insert(parentExpense, childExpense);

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
    public void testHideWithExistingChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        mBookingRepo.insert(parentExpense);

        try {
            ExpenseObject hiddenChild = parentExpense.getChildren().get(0);
            mChildExpenseRepo.hide(hiddenChild);

            assertTrue("KindBuchung wurde gelöscht", mChildExpenseRepo.exists(hiddenChild));
            assertFalse("Versteckte Buchung wurde aus der Datenbank geholt", mChildExpenseRepo.getAll(parentExpense.getId()).contains(hiddenChild));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + parentExpense.getChildren().get(1).getSignedPrice(),
                    account.getId()
            );

        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("Kind Buchung wurde nicht gefunden");
        }
    }

    @Test
    public void testHideLastChildOfParentShouldHideParentAsWell() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        parentExpense.getChildren().remove(1);
        mBookingRepo.insert(parentExpense);

        try {
            ExpenseObject hiddenChildExpense = parentExpense.getChildren().get(0);
            mChildExpenseRepo.hide(hiddenChildExpense);

            assertTrue("ParentBuchung wurde nicht versteckt", mBookingRepo.isHidden(parentExpense));
            assertFalse("KindBuchung wurde nicht versteckt", mChildExpenseRepo.getAll(parentExpense.getId()).contains(hiddenChildExpense));
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account.getId()
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

            assertEquals(String.format("Could not find Child Booking with id %s.", childExpense.getId().toString()), e.getMessage());
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account.getId()
            );
        }
    }

    @Test
    public void testIsHiddenWithExistingChildExpenseShouldSucceed() {
        ExpenseObject parentExpense = getParentExpenseWithChildren();
        mBookingRepo.insert(parentExpense);

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

    private void assertBookingExists(ExpenseObject booking) {
        List<ExpenseObject> bookings = mBookingRepo.getAll();

        boolean bookingFound = false;
        for (ExpenseObject existingBooking : bookings) {
            if (!existingBooking.equals(booking)) {
                continue;
            }

            bookingFound = true;
            break;
        }

        assertTrue(bookingFound);
    }

    private void assertBookingNotExisting(ExpenseObject booking) {
        List<ExpenseObject> bookings = mBookingRepo.getAll();

        boolean bookingFound = false;
        for (ExpenseObject existingBooking : bookings) {
            if (!existingBooking.equals(booking)) {
                continue;
            }

            bookingFound = true;
            break;
        }

        assertFalse(bookingFound);
    }

    private ExpenseObject getSimpleExpense() {
        Category category = new Category(
                "Kategorie",
                Color.black(),
                ExpenseType.expense()
        );

        return new ExpenseObject(
                "Ausgabe",
                new Price(new Random().nextInt(1000), false),
                category,
                account.getId()
        );
    }

    private ExpenseObject getParentExpenseWithChildren() {
        ExpenseObject parentExpense = getSimpleExpense();
        parentExpense.setPrice(new Price(0));

        parentExpense.addChild(getSimpleExpense());
        parentExpense.addChild(getSimpleExpense());

        return parentExpense;
    }

    private void assertEqualAccountBalance(double expectedAmount, UUID accountId) {

        try {
            double actualBalance = mAccountRepo.get(accountId).getBalance().getSignedValue();
            assertEquals("Konto wurde nicht geupdated", expectedAmount, actualBalance);

        } catch (AccountNotFoundException e) {

            Assert.fail("Konto wurde nicht gefunden");
        }
    }
}