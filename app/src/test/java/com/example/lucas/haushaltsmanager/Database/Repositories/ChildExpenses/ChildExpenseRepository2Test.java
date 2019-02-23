package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ChildExpenseRepository2Test {
    private ChildExpenseRepositoryInterface mChildRepo;

    @Before
    public void setup() {

        //open new Database connnection
    }

    @After
    public void teardown() {

        //clear database
    }

    private ExpenseObject getSimpleExpense() {
        return new ExpenseObject(
                "KindBuchung",
                37.25,
                true,
                mock(Category.class),
                1L,
                mock(Currency.class)
        );
    }

    @Test
    public void addingExpenseToExpenseShouldSucceed() {

    }

    @Test
    public void addingExpenseToParentExpenseShouldSucceed() {
        ExpenseRepository mockExpenseRepo = mock(ExpenseRepository.class);
        when(mockExpenseRepo.exists(any(ExpenseObject.class))).thenReturn(true);
        injectMock(mChildRepo, mockExpenseRepo, "mBookingRepo");

        try {
            mChildRepo.create(getSimpleExpense(), any(ExpenseObject.class));

            // IMPROVEMENT: Sollte ich überprüfen ob der Kontostand richtig geupdatet wurde?
            // TODO: Der Rückgabewert von mChildRepo.create() sollte mit dem child übereinstimmen
        } catch (ExpenseNotFoundException e) {

            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void addingExpenseToChildExpenseShouldFailWithCannotAddChildToChildException() {

    }

    @Test
    public void testCreateWithNotExistingParentShouldThrowExpenseNotFoundException() {
        ExpenseRepository mockExpenseRepo = mock(ExpenseRepository.class);
        when(mockExpenseRepo.exists(any(ExpenseObject.class))).thenReturn(false);
        injectMock(mChildRepo, mockExpenseRepo, "mBookingRepo");

        try {
            mChildRepo.create(getSimpleExpense(), any(ExpenseObject.class));

            Assert.fail("Could save ChildExpense with invalid Parent");
        } catch (ExpenseNotFoundException e) {

            assertEquals("Could not find Booking with id ", e.getMessage());// IMPROVEMENT: Welche Id hat die ParentExpense
        }
    }

    @Test
    public void testGetWithExistingChildExpenseShouldSucceed() {
        ExpenseRepository mockExpenseRepo = mock(ExpenseRepository.class);
        when(mockExpenseRepo.exists(any(ExpenseObject.class))).thenReturn(false);
        injectMock(mChildRepo, mockExpenseRepo, "mBookingRepo");

        try {
            ExpenseObject expectedChildExpense = mChildRepo.create(getSimpleExpense(), any(ExpenseObject.class));
            ExpenseObject actualChildExpense = mChildRepo.get(expectedChildExpense.getIndex());

            assertSame("Actual ChildExpense does not match expected ChildExpense", expectedChildExpense, actualChildExpense);
        } catch (ExpenseNotFoundException e) {

            Assert.fail(e.getMessage());
        } catch (ChildExpenseNotFoundException e) {

            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {
        try {
            mChildRepo.get(1337L);

            Assert.fail("Found not existing ChildExpense");
        } catch (ChildExpenseNotFoundException e) {

            assertEquals("Could not find ChildExpense with id 1337.", e.getMessage());
        }
    }

//    testGetWithNotExistingParentExpenseShouldThrowExpenseNotFoundException IMPROVEMENT: Brauche ich diesen Test?

    @Test
    public void testGetAllShouldReturnAllVisibleChildrenOfParent() {

    }


    /**
     * Methode um ein Feld einer Klasse durch ein anderes, mit injection, auszutauschen.
     *
     * @param obj       Objekt welches angepasst werden soll
     * @param value     Neuer Wert des Felds
     * @param fieldName Name des Feldes
     */
    private void injectMock(Object obj, Object value, String fieldName) {
        try {
            Class cls = obj.getClass();

            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {

            Assert.fail(String.format("Could not find field %s in class %s", fieldName, obj.getClass().getSimpleName()));
        }
    }
}
