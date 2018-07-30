package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ChildExpenseRepositoryTest {

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.application;
        ExpensesDbHelper dbHleper = new ExpensesDbHelper(context);
        DatabaseManager.initializeInstance(dbHleper);
    }

    @Test
    public void testExistsWithValidChildExpenseShouldSucceed() {

    }

    @Test
    public void testExistsWithInvalidChildExpenseShouldFail() {

    }

    @Test
    public void testAddChildToBookingWithExistingParentThatHasChildrenShouldSucceed() {
        //todo den kontostand überprüfen
    }

    @Test
    public void testAddChildToBookingWithExistingParentThatHAsNoChildrenShouldSucceed() {
        //todo den kontostand überprüfen
    }

    @Test
    public void testAddChildToBookingWithParentCategoryIsChildShouldThrowAddChildToExpenseException() {

    }

    @Test
    public void testCombineExpensesWithParentExpensesShouldSucceed() {

    }

    @Test
    public void testExtractChildFromBookingShouldSucceed() {

    }

    @Test
    public void testExtractChildFromBookingWithNotExistingChildBookingShouldThrowChildExpenseNotFoundException() {

    }

    @Test
    public void testGetWithExistingChildExpenseShouldSucceed() {

    }

    @Test
    public void testGetWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {

    }

    @Test
    public void testUpdateWithExistingChildExpenseShouldSucceed() {
        //todo den kontostand überprüfen
    }

    @Test
    public void testUpdateWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {

    }

    @Test
    public void testDeleteWithExistingChildExpenseShouldSucceed() {
        //todo den kontostand überprüfen
    }

    @Test
    public void testDeleteWithNotExistingChildExpenseShouldSucceed() {

    }

    @Test
    public void testDeleteWithChildExpenseIsLastOfParentShouldDeleteParentAsWell() {

    }

    @Test
    public void testGetParentWithExistingChildExpenseShouldSucceed() {

    }

    @Test
    public void testGetParentWithNotExistingChildExpenseShouldThrowChildExpenseNotFoundException() {

    }

    @Test
    public void testGetParentWithNotExistingParentExpenseShouldThrowExpenseNotFoundException() {

    }

    @Test
    public void testCursorToChildBookingWithValidCursorShouldSucceed() {

    }

    @Test
    public void testCursorToChildBookingWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {

    }

    private void assertSameChildEpenses(ExpenseObject expected, ExpenseObject actual) {
        assertEquals(expected, actual);
    }
}
