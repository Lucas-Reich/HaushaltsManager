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

    private Category getSimpleCategory() {
        return new Category(
                "Kategorie",
                "#000000",
                true,
                new ArrayList<Category>()
        );
    }

    private Category getCategoryWithChild() {
        Category parent = getSimpleCategory();
        parent.addChild(getSimpleCategory());

        return parent;
    }

    @Test
    public void testExistsWithExistingChildCategoryShouldSucceed() {
        Category parent = getCategoryWithChild();
        Category childCategory = ChildCategoryRepository.insert(parent, parent.getChildren().get(0));

        boolean exists = ChildCategoryRepository.exists(childCategory);
        assertTrue("Die KindKategorie wurde nicht in der Datenbank gefunden", exists);
    }

    @Test
    public void testExistsWithNotExistingChildCategoryShouldFail() {
        Category notExistingChildCategory = getSimpleCategory();

        boolean exists = ChildCategoryRepository.exists(notExistingChildCategory);
        assertFalse("Die KindKategorie wurde in der Datenbank gefunden", exists);
    }

    @Test
    public void testGetWithExistingChildCategoryShouldSucceed() {
        Category parentCategory = getCategoryWithChild();
        Category expectedChildCategory = ChildCategoryRepository.insert(parentCategory, parentCategory.getChildren().get(0));

        try {
            Category fetchedChildCategory = ChildCategoryRepository.get(expectedChildCategory.getIndex());
            assertEquals(expectedChildCategory, fetchedChildCategory);

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
        Category parentCategory = mock(Category.class);
        when(parentCategory.getIndex()).thenReturn(106L);

        Category visibleChildCategory1 = new Category("Sichtbare Kategorie 1", "#000000", false, new ArrayList<Category>());
        visibleChildCategory1 = ChildCategoryRepository.insert(parentCategory, visibleChildCategory1);

        Category visibleChildCategory2 = new Category("Sichtbare Kategorie 2", "#000000", true, new ArrayList<Category>());
        visibleChildCategory2 = ChildCategoryRepository.insert(parentCategory, visibleChildCategory2);

        try {
            Category invisibleChildCategory1 = new Category("Unsichtbare Kategorie 1", "#000000", true, new ArrayList<Category>());
            invisibleChildCategory1 = ChildCategoryRepository.insert(parentCategory, invisibleChildCategory1);
            ChildCategoryRepository.hide(invisibleChildCategory1);

            List<Category> children = ChildCategoryRepository.getAll(parentCategory.getIndex());

            assertTrue("Sichtbare Kind Kategorie 1 wurde nicht aus der Datenbank geholt", children.contains(visibleChildCategory1));
            assertTrue("Sichtbare Kind Kategorie 2 wurde nicht aus der Datenbank geholt", children.contains(visibleChildCategory2));
            assertFalse("Versteckte Kind Kategorie 1 wurde aus der Datenbank geholt", children.contains(invisibleChildCategory1));
        } catch (ChildCategoryNotFoundException e) {

            Assert.fail("ChildCategory konnte nicht veresteckt werden");
        }
    }

    @Test
    public void testInsertWithValidInputShouldSucceed() {
        Category parentCategory = getCategoryWithChild();
        Category expectedChildCategory = ChildCategoryRepository.insert(parentCategory, parentCategory.getChildren().get(0));

        try {
            Category fetchedChildCategory = ChildCategoryRepository.get(expectedChildCategory.getIndex());
            assertEquals(expectedChildCategory, fetchedChildCategory);

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
        Category parentCategory = getCategoryWithChild();
        parentCategory.addChild(getSimpleCategory());

        Category childCategory1 = ChildCategoryRepository.insert(parentCategory, parentCategory.getChildren().get(0));
        Category childCategory2 = ChildCategoryRepository.insert(parentCategory, parentCategory.getChildren().get(1));

        try {
            ChildCategoryRepository.delete(childCategory1);

            assertFalse("Kategorie 1 wurde nicht gelöscht", ChildCategoryRepository.exists(childCategory1));
            assertTrue("Kategorie 2 wurde gelöscht", ChildCategoryRepository.exists(childCategory2));

        } catch (CannotDeleteChildCategoryException e) {

            Assert.fail("KindKategorie konnte nicht gelöscht werden");
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
        Category parentCategory = CategoryRepository.insert(getCategoryWithChild());
        Category childCategory = parentCategory.getChildren().get(0);

        try {
            ChildCategoryRepository.delete(childCategory);

            assertFalse("KindKategorie wurde nicht gelöscht", ChildCategoryRepository.exists(childCategory));
            assertFalse("ParentCategory wurde nicht gelöscht", CategoryRepository.exists(parentCategory));

        } catch (CannotDeleteChildCategoryException e) {

            Assert.fail("ChildCategory konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingChildCategoryAttachedToBookingShouldThrowCannotDeleteChildCategoryException() {
        Category parentCategory = getCategoryWithChild();
        Category childCategory = ChildCategoryRepository.insert(parentCategory, parentCategory.getChildren().get(0));

        ExpenseObject expense = new ExpenseObject("Ausgabe", 100, true, childCategory, -1, new Currency("Währung", "WÄH", "W"));
        ExpenseRepository.insert(expense);

        try {
            ChildCategoryRepository.delete(childCategory);
            Assert.fail("Konnte die KindKategorie löschen obwohl sie einer Buchung zugeordnet ist");

        } catch (CannotDeleteChildCategoryException e) {

            assertTrue("Kategorie konnte gelöscht werden", ChildCategoryRepository.exists(childCategory));
            assertEquals(String.format("Child category %s is attached to a ParentExpense and cannot be deleted.", childCategory.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithExistingChildCategoryAttachedToChildBookingShouldThrowCannotDeleteChildCategoryException() {
        Category parentCategory = getCategoryWithChild();
        Category childCategory = ChildCategoryRepository.insert(parentCategory, parentCategory.getChildren().get(0));

        ExpenseObject parentExpense = mock(ExpenseObject.class);
        when(parentExpense.getIndex()).thenReturn(100L);

        ExpenseObject childExpense = new ExpenseObject("Ausgabe", 100, false, childCategory, -1, new Currency("Euro", "EUR", "€"));
        ChildExpenseRepository.insert(parentExpense, childExpense);
        try {
            ChildCategoryRepository.delete(childCategory);
            Assert.fail("Konnte die KindKategorie löschen obwohl sie einer KindBuchung zugeordnet ist");

        } catch (CannotDeleteChildCategoryException e) {

            assertTrue("Kategorie konnte gelöscht werden", ChildCategoryRepository.exists(childCategory));
            assertEquals(String.format("Child category %s is attached to a ChildExpense and cannot be deleted.", childCategory.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithNotExistingChildCategoryShouldSucceed() {
        Category childCategory = getSimpleCategory();

        try {
            ChildCategoryRepository.delete(childCategory);

        } catch (CannotDeleteChildCategoryException e) {

            Assert.fail("Nicht existierende KindKategorie konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testUpdateWithExistingChildCategoryShouldSucceed() {
        Category parent = getCategoryWithChild();
        Category childCategory = ChildCategoryRepository.insert(parent, parent.getChildren().get(0));

        try {
            childCategory.setName("Updated Category Name");
            ChildCategoryRepository.update(childCategory);
            Category fetchedChildCategory = ChildCategoryRepository.get(childCategory.getIndex());

            assertEquals(childCategory, fetchedChildCategory);

        } catch (ChildCategoryNotFoundException e) {

            Assert.fail("Kategorie wurde nicht gefunden");
        }
    }

    @Test
    public void testUpdateWithNotExistingChildCategoryShouldThrowChildCategoryNotFoundException() {
        Category childCategory = getSimpleCategory();

        try {
            ChildCategoryRepository.update(childCategory);
            Assert.fail("Nicht existierende KindKategorie konnte geupdated werden");

        } catch (ChildCategoryNotFoundException e) {

            assertEquals(String.format("Could not find Child Category with index %s.", childCategory.getIndex()), e.getMessage());
        }

    }

    @Test
    public void testCursorToChildCategoryWithValidCursorShouldSucceed() {
        Category expectedChildCategory = getSimpleCategory();

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
            assertEquals(expectedChildCategory, actualChildCategory);

        } catch (CursorIndexOutOfBoundsException e) {

            Assert.fail("KindKategorie konnte nicht aus einem Cursor wiederhergestellt werden, obwohl alles benötigte da ist");
        }
    }

    @Test
    public void testCursorToChildCategoryWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        Category expectedChildCategory = getSimpleCategory();

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
}
