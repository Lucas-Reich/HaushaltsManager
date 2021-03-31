package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions.TemplateCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.TemplateBooking;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ExpenseRepositoryTest {
    private Account account;
    private AccountRepository mAccountRepo;
    private TemplateRepository mTemplateRepo;
    private ExpenseRepository mBookingRepo;

    @Before
    public void setup() throws AccountCouldNotBeCreatedException {

        mAccountRepo = new AccountRepository(RuntimeEnvironment.application);
        mTemplateRepo = new TemplateRepository(RuntimeEnvironment.application);
        mBookingRepo = new ExpenseRepository(RuntimeEnvironment.application);

        account = new Account("Konto", new Price(100));
        mAccountRepo.insert(account);
    }

    @Test
    public void testGetWithExistingExpenseShouldSucceed() {
        ExpenseObject expectedExpense = getSimpleExpense();
        mBookingRepo.insert(expectedExpense);

        try {
            ExpenseObject fetchedExpense = mBookingRepo.get(expectedExpense.getId());
            assertEquals(expectedExpense, fetchedExpense);

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Buchung wurde nicht gefunden");
        }
    }

    @Test(expected = ExpenseNotFoundException.class)
    public void testGetWithNotExistingExpenseShouldThrowExpenseNotFoundException() throws ExpenseNotFoundException {
        mBookingRepo.get(UUID.randomUUID());
    }

    @Test
    public void testGetAllShouldNotIncludeHiddenBookings() {
        ExpenseObject visibleExpense1 = getSimpleExpense();
        mBookingRepo.insert(visibleExpense1);
        assertBookingExists(visibleExpense1);

        ExpenseObject visibleExpense2 = getSimpleExpense();
        mBookingRepo.insert(visibleExpense2);
        assertBookingExists(visibleExpense2);

        ExpenseObject hiddenExpense = getSimpleExpense();
        mBookingRepo.insert(hiddenExpense);
        assertBookingExists(hiddenExpense);

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
        mBookingRepo.insert(parentExpense);

        ExpenseObject expense = getSimpleExpense();
        mBookingRepo.insert(expense);

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
        expenseBeforeTimeFrame.setDate(date);
        mBookingRepo.insert(expenseBeforeTimeFrame);

        ExpenseObject expenseWithinTimeFrame1 = getSimpleExpense();
        date.setTimeInMillis(1527804000000L);//01.06.2018 00:00:00
        expenseWithinTimeFrame1.setDate(date);
        mBookingRepo.insert(expenseWithinTimeFrame1);

        ExpenseObject expenseWithinTimeFrame2 = getSimpleExpense();
        date.setTimeInMillis(1527867176000L);//01.06.2018 17:32:56
        expenseWithinTimeFrame2.setDate(date);
        mBookingRepo.insert(expenseWithinTimeFrame2);

        ExpenseObject expenseAfterTimeFrame = getSimpleExpense();
        date.setTimeInMillis(1527890400000L);//02.06.2018 00:00:00
        expenseAfterTimeFrame.setDate(date);
        mBookingRepo.insert(expenseAfterTimeFrame);

        List<ExpenseObject> expenses = mBookingRepo.getAll(1527804000000L, 1527890399000L);//01.06.2018 00:00:00 - 01.06.2018 23:59:59

        assertFalse("Buchung die vor dem angegebenen Zeitraum ist wurde aus der Datenbank geholt", expenses.contains(expenseBeforeTimeFrame));
        assertTrue("Buchung 1 die innerhalb des Zeitraums ist wurde nicht aus der Datenbank geholt", expenses.contains(expenseWithinTimeFrame1));
        assertTrue("Buchung 2 die innerhalb des Zeitraums ist wurde nicht aus der Datenbank geholt", expenses.contains(expenseWithinTimeFrame2));
        assertFalse("Buchung die nach dem angegebenen Zeitraum ist wurde aus der Datenbank geholt", expenses.contains(expenseAfterTimeFrame));
    }

    @Test
    public void testInsertWithValidBookingThatHasNoChildrenOrTagsShouldSucceed() {
        ExpenseObject expenseObject = getSimpleExpense();
        mBookingRepo.insert(expenseObject);

        try {
            ExpenseObject fetchedExpense = mBookingRepo.get(expenseObject.getId());

            assertEquals(expenseObject, fetchedExpense);
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue() + expenseObject.getSignedPrice(),
                    account
            );

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Gerade erstellte Ausgabe konnte nicht gefunden werden");
        }

    }

    @Test
    public void testInsertWithValidBookingThatHasChildrenShouldSucceedAndChildrenShouldExist() {
        ExpenseObject expectedExpenseWithChildren = getExpenseWithChildren();
        mBookingRepo.insert(expectedExpenseWithChildren);

        try {
            ExpenseObject fetchedExpense = mBookingRepo.get(expectedExpenseWithChildren.getId());

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
        ExpenseObject expense = getSimpleExpense();
        mBookingRepo.insert(expense);

        try {
            mBookingRepo.delete(expense);

            assertBookingNotExisting(expense);
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account
            );

        } catch (CannotDeleteExpenseException e) {

            Assert.fail("Buchung konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingBookingThatIsATemplateShouldSucceedAndChangeBookingVisibilityToHidden() throws TemplateCouldNotBeCreatedException {
        ExpenseObject expense = getSimpleExpense();
        mBookingRepo.insert(expense);
        mTemplateRepo.insert(new TemplateBooking(expense));

        try {
            mBookingRepo.delete(expense);

            assertTrue("Versteckte Buchung wurde aus der Datenbank geholt", mBookingRepo.isHidden(expense));
            assertBookingExists(expense);
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
        ExpenseObject expense = getSimpleExpense();
        mBookingRepo.insert(expense);

        try {
            //TODO die recurringBookingExists methode welche in der delete methode aufgerufen wird sollte true zurückgeben
            mBookingRepo.delete(expense);

            assertFalse("Versteckte Buchung wurde aus der Datenbank geholt", mBookingRepo.getAll().contains(expense));
            assertBookingExists(expense);
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
            assertBookingNotExisting(expense);

        } catch (CannotDeleteExpenseException e) {

            Assert.fail("Nicht existierende Buchung konnte nicht geöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingBookingThatIsAttachedToChildBookingsShouldThrowCannotDeleteExpenseException() {
        ExpenseObject parentExpense = getExpenseWithChildren();
        mBookingRepo.insert(parentExpense);

        try {
            mBookingRepo.delete(parentExpense);
            Assert.fail("Buchung mit Kindern konnte gelöscht werden");

        } catch (CannotDeleteExpenseException e) {

            assertEquals(String.format("Booking %s is attached to a child expense and cannot be deleted.", parentExpense.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testUpdateWithExistingBookingShouldSucceed() {
        ExpenseObject expectedExpense = getSimpleExpense();
        mBookingRepo.insert(expectedExpense);

        try {
            expectedExpense.setTitle("New Booking Name");
            expectedExpense.setPrice(new Price(30, expectedExpense.getPrice().isNegative()));
            mBookingRepo.update(expectedExpense);

            ExpenseObject fetchedExpense = mBookingRepo.get(expectedExpense.getId());

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

            assertEquals(String.format("Could not find Booking with id %s.", expense.getId().toString()), e.getMessage());
        }
    }

    @Test
    public void testHideWithValidExpenseShouldSucceed() {
        ExpenseObject expense = getSimpleExpense();
        mBookingRepo.insert(expense);

        try {
            mBookingRepo.hide(expense);
            assertBookingExists(expense);
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

            assertEquals(String.format("Could not find Booking with id %s.", expense.getId().toString()), e.getMessage());
            assertEqualAccountBalance(
                    account.getBalance().getSignedValue(),
                    account
            );
        }
    }

    @Test
    public void testIsHiddenWithExistingExpenseShouldSucceed() {
        ExpenseObject expense = getSimpleExpense();
        mBookingRepo.insert(expense);

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

            assertEquals(String.format("Could not find Booking with id %s.", expense.getId().toString()), e.getMessage());
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
                new Price(new Random().nextInt(1000), true),
                category,
                account.getId()
        );
    }

    private ExpenseObject getExpenseWithChildren() {
        ExpenseObject parentExpense = getSimpleExpense();
        ExpenseObject childExpense = getSimpleExpense();

        childExpense.setPrice(new Price(33, false));
        parentExpense.addChild(childExpense);

        childExpense.setPrice(new Price(768, false));
        parentExpense.addChild(childExpense);

        childExpense.setPrice(new Price(324, true));
        parentExpense.addChild(childExpense);

        return parentExpense;
    }

    private void assertEqualAccountBalance(double expectedAmount, Account account) {

        try {
            double actualBalance = mAccountRepo.get(account.getId()).getBalance().getSignedValue();
            assertEquals("Konto wurde nicht geupdated", expectedAmount, actualBalance);

        } catch (AccountNotFoundException e) {

            Assert.fail("Konto wurde nicht gefunden");
        }
    }
}
