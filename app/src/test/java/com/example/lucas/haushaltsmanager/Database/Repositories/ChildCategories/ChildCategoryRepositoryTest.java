package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.CannotDeleteChildCategoryException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.ChildCategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
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
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ChildCategoryRepositoryTest {

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.application;
        ExpensesDbHelper dbHelper = new ExpensesDbHelper(context);
        DatabaseManager.initializeInstance(dbHelper);
    }

    @Test
    public void testExistsWithExistingChildCategoryShouldSucceed() {
        Category parent = mock(Category.class);
        when(parent.getIndex()).thenReturn(100L);

        Category childCategory1 = new Category("ChildCategory1", "#000000", false, new ArrayList<Category>());
        childCategory1 = ChildCategoryRepository.insert(parent, childCategory1);

        boolean exists = ChildCategoryRepository.exists(childCategory1);
        assertTrue("Die KindKategorie wurde nicht in der Datenbank gefunden", exists);
    }

    @Test
    public void testExistsWithNotExistingChildCategoryShouldFail() {
        Category notExistingChildCategory = new Category("Kategorie", "#121212", true, new ArrayList<Category>());

        boolean exists = ChildCategoryRepository.exists(notExistingChildCategory);
        assertFalse("Die KindKategorie wurde in der Datenbank gefunden", exists);
    }

    @Test
    public void testGetWithExistingChildCategoryShouldSucceed() {
        Category parent = mock(Category.class);
        when(parent.getIndex()).thenReturn(101L);

        Category actualChildCategory = new Category("ChildCategory Get 1", "#121212", true, new ArrayList<Category>());
        actualChildCategory = ChildCategoryRepository.insert(parent, actualChildCategory);

        try {
            Category fetchedChildCategory = ChildCategoryRepository.get(actualChildCategory.getIndex());
            assertSameChildCategories(actualChildCategory, fetchedChildCategory);

        } catch (ChildCategoryNotFoundException e) {

            Assert.fail("KindKategorie wurde nicht gefunden");
        }
    }

    @Test
    public void testGetWithNotExistingChildCategoryShouldThrowCategoryNotFoundException() {
        long notExistingChildCategoryId = 1337;

        try {
            ChildCategoryRepository.get(notExistingChildCategoryId);
            Assert.fail("Nicht existierende KindKategorie wurde gefunden");

        } catch (ChildCategoryNotFoundException e) {

            assertEquals(String.format("Could not find Child Category with index %s.", notExistingChildCategoryId), e.getMessage());
        }
    }

    @Test
    public void testGetAllShouldNotReturnHiddenCategory() {
        Category parent = mock(Category.class);
        when(parent.getIndex()).thenReturn(106L);

        Category visibleChildCategory1 = new Category("Sichtbare Kategorie 1", "#000000", false, new ArrayList<Category>());
        visibleChildCategory1 = ChildCategoryRepository.insert(parent, visibleChildCategory1);

        Category visibleChildCategory2 = new Category("Sichtbare Kategorie 2", "#000000", true, new ArrayList<Category>());
        visibleChildCategory2 = ChildCategoryRepository.insert(parent, visibleChildCategory2);

        try {
            Category invisibleChildCategory1 = new Category("Unsichtbare Kategorie 1", "#000000", true, new ArrayList<Category>());
            invisibleChildCategory1 = ChildCategoryRepository.insert(parent, invisibleChildCategory1);
            ChildCategoryRepository.hide(invisibleChildCategory1);

            List<Category> children = ChildCategoryRepository.getAll(parent.getIndex());

            assertTrue("Sichtbare Kind Kategorie 1 wurde nicht aus der Datenbank geholt", children.contains(visibleChildCategory1));
            assertTrue("Sichtbare Kind Kategorie 2 wurde nicht aus der Datenbank geholt", children.contains(visibleChildCategory2));
            assertFalse("Versteckte Kind Kategorie 1 wurde aus der Datenbank geholt", children.contains(invisibleChildCategory1));
        } catch (ChildCategoryNotFoundException e) {

            Assert.fail("ChildCategory konnte nicht veresteckt werden");
        }
    }

    @Test
    public void testInsertWithValidInputShouldSucceed() {
        Category parent = mock(Category.class);
        when(parent.getIndex()).thenReturn(102L);

        Category actualChildCategory = new Category("Kategorie", "#121212", false, new ArrayList<Category>());
        actualChildCategory = ChildCategoryRepository.insert(parent, actualChildCategory);

        try {
            Category fetchedChildCategory = ChildCategoryRepository.get(actualChildCategory.getIndex());
            assertSameChildCategories(actualChildCategory, fetchedChildCategory);

        } catch (ChildCategoryNotFoundException e) {

            Assert.fail("Gerade erstellte KindKategorie konnte nicht gefunden werden");
        }
    }

    @Test
    public void testInsertWithInvalidInputShouldFail() {
        //todo was sollte passieren wenn eine KindKategorie nicht richtig initialisiert wurde, zb kein name
    }

    @Test
    public void testDeleteWithExistingChildCategoryShouldSucceed() {
        Category parent = mock(Category.class);
        when(parent.getIndex()).thenReturn(103L);

        //Es muss 2 Kinder mit dem Parent geben, sodass die letzte Kategorie nach dem Löschen nicht automatisch in eine ParentKategorie umgewandelt wird
        Category childCategory1 = new Category("ChildCategory", "#121212", true, new ArrayList<Category>());
        childCategory1 = ChildCategoryRepository.insert(parent, childCategory1);

        Category childCategory2 = new Category("ChildCategory", "#121212", true, new ArrayList<Category>());
        ChildCategoryRepository.insert(parent, childCategory2);

        try {
            ChildCategoryRepository.delete(childCategory1);

        } catch (CannotDeleteChildCategoryException e) {

            Assert.fail("ChildCategory die zu keiner Buchung zugeordnet ist kann nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingChildCategoryThatIsLastVisibleChildOfParentShouldSucceed() {
        Category parentCategory = new Category("Parent Category", "#000000", false, new ArrayList<Category>());
        parentCategory = CategoryRepository.insert(parentCategory);

        Category visibleChildCategory = new Category("Visible Child Category", "#000000", false, new ArrayList<Category>());
        visibleChildCategory = ChildCategoryRepository.insert(parentCategory, visibleChildCategory);

        Category invisibleChildCategory = new Category("Invisible Child Category", "#000000", false, new ArrayList<Category>());
        invisibleChildCategory = ChildCategoryRepository.insert(parentCategory, invisibleChildCategory);

        try {
            ChildCategoryRepository.hide(invisibleChildCategory);
            ChildCategoryRepository.delete(visibleChildCategory);

            boolean exists = ChildCategoryRepository.exists(visibleChildCategory);
            assertFalse("Kind Kategorie wurde nicht gelöscht", exists);

            exists = CategoryRepository.exists(parentCategory);
            assertTrue("Parent Category wurde auch gelöscht, obwohl es noch eine versteckte Kategorie mit ihr gibt", exists);
        } catch (ChildCategoryNotFoundException e) {

            Assert.fail("Gerade erstellte ChildCategory konnte nicht versteckt werden");
        } catch (CannotDeleteChildCategoryException e) {

            Assert.fail("Gerade erstellte Kind Kategorie konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingChildCategoryThatIsLastChildOfParentShouldSucceedAndParentShouldBeDeleted() {
        Category parentCategory = new Category("ParentKategorie", "#121212", true, new ArrayList<Category>());
        parentCategory = CategoryRepository.insert(parentCategory);

        Category childCategory = new Category("ChildCategory", "#121212", false, new ArrayList<Category>());
        childCategory = ChildCategoryRepository.insert(parentCategory, childCategory);

        try {
            ChildCategoryRepository.delete(childCategory);

            boolean exists = CategoryRepository.exists(parentCategory);
            assertFalse("ParentCategory wurde nicht gelöscht obwohl das gelöschte Kind das letzte des Parents war", exists);

        } catch (CannotDeleteChildCategoryException e) {

            Assert.fail("ChildCategory konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingChildCategoryAttachedToBookingShouldThrowCannotDeleteChildCategoryException() {
        Category parent = mock(Category.class);
        when(parent.getIndex()).thenReturn(104L);

        Category childCategory = new Category("ChildCategory", "#121212", false, new ArrayList<Category>());
        childCategory = ChildCategoryRepository.insert(parent, childCategory);

        ExpenseObject expense = new ExpenseObject("Ausgabe", 100, true, childCategory, new Account("Konot 1", 100, new Currency("Währung", "WÄH", "W")));
        ExpenseRepository.insert(expense);

        try {
            ChildCategoryRepository.delete(childCategory);
            Assert.fail("Konnte die KindKategorie löschen obwohl sie einer Buchung zugeordnet ist");

        } catch (CannotDeleteChildCategoryException e) {

            assertEquals(String.format("Child category %s is attached to a ParentExpense and cannot be deleted.", childCategory.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithExistingChildCategoryAttachedToChildBookingShouldThrowCannotDeleteChildCategoryException() {
        Category parentCategory = mock(Category.class);
        when(parentCategory.getIndex()).thenReturn(104L);

        Category childCategory = new Category("ChildCategory", "#121212", false, new ArrayList<Category>());
        childCategory = ChildCategoryRepository.insert(parentCategory, childCategory);

        ExpenseObject parentExpense = mock(ExpenseObject.class);
        when(parentExpense.getIndex()).thenReturn(100L);

        ExpenseObject childExpense = new ExpenseObject("Ausgabe", 100, false, childCategory, new Account("Konot 1", 100, new Currency("Währung", "WÄH", "W")));
        ChildExpenseRepository.insert(parentExpense, childExpense);
        try {
            ChildCategoryRepository.delete(childCategory);
            Assert.fail("Konnte die KindKategorie löschen obwohl sie einer KindBuchung zugeordnet ist");

        } catch (CannotDeleteChildCategoryException e) {

            assertEquals(String.format("Child category %s is attached to a ChildExpense and cannot be deleted.", childCategory.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithNotExistingChildCategoryShouldSucceed() {
        Category childCategory = new Category("Kategorie", "#000000", false, new ArrayList<Category>());

        try {
            ChildCategoryRepository.delete(childCategory);

        } catch (CannotDeleteChildCategoryException e) {

            Assert.fail("Nicht existierende KindKategorie konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testUpdateWithExistingChildCategoryShouldSucceed() {
        Category parent = mock(Category.class);
        when(parent.getIndex()).thenReturn(105L);

        Category childCategory = new Category("Kategorie", "#000000", true, new ArrayList<Category>());
        childCategory = ChildCategoryRepository.insert(parent, childCategory);

        try {
            childCategory.setName("Updated Category Name");
            ChildCategoryRepository.update(childCategory);

            Category fetchedChildCategory = ChildCategoryRepository.get(childCategory.getIndex());
            assertSameChildCategories(childCategory, fetchedChildCategory);

        } catch (ChildCategoryNotFoundException e) {

            Assert.fail("Gerade erstellte KindKategorie konnte nicht geupdated werden");
        }
    }

    @Test
    public void testUpdateWithNotExistingChildCategoryShouldThrowChildCategoryNotFoundException() {
        Category childCategory = new Category(
                1337,
                "Kategorie",
                "#120012",
                true,
                new ArrayList<Category>()
        );

        try {
            ChildCategoryRepository.update(childCategory);
            Assert.fail("Nicht existierende KindKategorie konnte geupdated werden");

        } catch (ChildCategoryNotFoundException e) {

            assertEquals(String.format("Could not find Child Category with index %s.", childCategory.getIndex()), e.getMessage());
        }

    }

    @Test
    public void testCursorToChildCategoryWithValidCursorShouldSucceed() {
        Category expectedChildCategory = new Category(
                1337,
                "Kategorie",
                "#000012",
                false,
                new ArrayList<Category>()
        );

        String[] columns = new String[]{
                ExpensesDbHelper.CHILD_CATEGORIES_COL_ID,
                ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME,
                ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR,
                ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{expectedChildCategory.getIndex(), expectedChildCategory.getTitle(), expectedChildCategory.getColorString(), expectedChildCategory.getDefaultExpenseType() ? 1 : 0});
        cursor.moveToFirst();

        try {
            Category actualChildCategory = ChildCategoryRepository.cursorToChildCategory(cursor);
            assertSameChildCategories(expectedChildCategory, actualChildCategory);

        } catch (CursorIndexOutOfBoundsException e) {

            Assert.fail("KindKategorie konnte nicht aus einem Cursor wiederhergestellt werden, obwohl alles benötigte da ist");
        }
    }

    @Test
    public void testCursorToChildCategoryWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        Category expectedChildCategory = new Category(
                1337,
                "Kategorie",
                "#000012",
                false,
                new ArrayList<Category>()
        );

        String[] columns = new String[]{
                ExpensesDbHelper.CHILD_CATEGORIES_COL_ID,
                ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME,
                //Die Farbe ist nicht im Cursor
                ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{expectedChildCategory.getIndex(), expectedChildCategory.getTitle(), expectedChildCategory.getDefaultExpenseType()});
        cursor.moveToFirst();

        try {
            ChildCategoryRepository.cursorToChildCategory(cursor);
            Assert.fail("Kategorie konnte aus einem Fehlerhaften Cursor wiederhergstellt werden");

        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }

    private void assertSameChildCategories(Category expected, Category actual) {
        assertEquals(expected, actual);
    }
}
