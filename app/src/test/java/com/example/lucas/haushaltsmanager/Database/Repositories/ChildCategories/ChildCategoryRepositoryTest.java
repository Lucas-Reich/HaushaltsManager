package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories;

import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.CannotDeleteChildCategoryException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.ChildCategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;

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

    private ChildExpenseRepository mChildExpenseRepo;
    private ChildCategoryRepository mChildCategoryRepo;
    private CategoryRepository mCategoryRepo;
    private ExpenseRepository mBookingRepo;

    @Before
    public void setup() {

        mChildExpenseRepo = new ChildExpenseRepository(RuntimeEnvironment.application);
        mChildCategoryRepo = new ChildCategoryRepository(RuntimeEnvironment.application);
        mCategoryRepo = new CategoryRepository(RuntimeEnvironment.application);
        mBookingRepo = new ExpenseRepository(RuntimeEnvironment.application);
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
        Category childCategory = mChildCategoryRepo.insert(parent, parent.getChildren().get(0));

        boolean exists = mChildCategoryRepo.exists(childCategory);
        assertTrue("Die KindKategorie wurde nicht in der Datenbank gefunden", exists);
    }

    @Test
    public void testExistsWithNotExistingChildCategoryShouldFail() {
        Category notExistingChildCategory = getSimpleCategory();

        boolean exists = mChildCategoryRepo.exists(notExistingChildCategory);
        assertFalse("Die KindKategorie wurde in der Datenbank gefunden", exists);
    }

    @Test
    public void testGetWithExistingChildCategoryShouldSucceed() {
        Category parentCategory = getCategoryWithChild();
        Category expectedChildCategory = mChildCategoryRepo.insert(parentCategory, parentCategory.getChildren().get(0));

        try {
            Category fetchedChildCategory = mChildCategoryRepo.get(expectedChildCategory.getIndex());
            assertEquals(expectedChildCategory, fetchedChildCategory);

        } catch (ChildCategoryNotFoundException e) {

            Assert.fail("KindKategorie wurde nicht gefunden");
        }
    }

    @Test
    public void testGetWithNotExistingChildCategoryShouldThrowCategoryNotFoundException() {
        long notExistingChildCategoryId = 1337;

        try {
            mChildCategoryRepo.get(notExistingChildCategoryId);
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
        visibleChildCategory1 = mChildCategoryRepo.insert(parentCategory, visibleChildCategory1);

        Category visibleChildCategory2 = new Category("Sichtbare Kategorie 2", "#000000", true, new ArrayList<Category>());
        visibleChildCategory2 = mChildCategoryRepo.insert(parentCategory, visibleChildCategory2);

        try {
            Category invisibleChildCategory1 = new Category("Unsichtbare Kategorie 1", "#000000", true, new ArrayList<Category>());
            invisibleChildCategory1 = mChildCategoryRepo.insert(parentCategory, invisibleChildCategory1);
            mChildCategoryRepo.hide(invisibleChildCategory1);

            List<Category> children = mChildCategoryRepo.getAll(parentCategory.getIndex());

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
        Category expectedChildCategory = mChildCategoryRepo.insert(parentCategory, parentCategory.getChildren().get(0));

        try {
            Category fetchedChildCategory = mChildCategoryRepo.get(expectedChildCategory.getIndex());
            assertEquals(expectedChildCategory, fetchedChildCategory);

        } catch (ChildCategoryNotFoundException e) {

            Assert.fail("Gerade erstellte KindKategorie konnte nicht gefunden werden");
        }
    }

    @Test
    public void testInsertWithInvalidInputShouldFail() {
        // IMPROVEMENT: Was sollte passieren wenn eine KindKategorie nicht richtig initialisiert wurde, zb kein name
    }

    @Test
    public void testDeleteWithExistingChildCategoryShouldSucceed() {
        Category parentCategory = getCategoryWithChild();
        parentCategory.addChild(getSimpleCategory());

        Category childCategory1 = mChildCategoryRepo.insert(parentCategory, parentCategory.getChildren().get(0));
        Category childCategory2 = mChildCategoryRepo.insert(parentCategory, parentCategory.getChildren().get(1));

        try {
            mChildCategoryRepo.delete(childCategory1);

            assertFalse("Kategorie 1 wurde nicht gelöscht", mChildCategoryRepo.exists(childCategory1));
            assertTrue("Kategorie 2 wurde gelöscht", mChildCategoryRepo.exists(childCategory2));

        } catch (CannotDeleteChildCategoryException e) {

            Assert.fail("KindKategorie konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingChildCategoryThatIsLastVisibleChildOfParentShouldSucceed() {
        Category parentCategory = new Category("Parent Category", "#000000", false, new ArrayList<Category>());
        parentCategory = mCategoryRepo.insert(parentCategory);

        Category visibleChildCategory = new Category("Visible Child Category", "#000000", false, new ArrayList<Category>());
        visibleChildCategory = mChildCategoryRepo.insert(parentCategory, visibleChildCategory);

        Category invisibleChildCategory = new Category("Invisible Child Category", "#000000", false, new ArrayList<Category>());
        invisibleChildCategory = mChildCategoryRepo.insert(parentCategory, invisibleChildCategory);

        try {
            mChildCategoryRepo.hide(invisibleChildCategory);
            mChildCategoryRepo.delete(visibleChildCategory);

            boolean exists = mChildCategoryRepo.exists(visibleChildCategory);
            assertFalse("Kind Kategorie wurde nicht gelöscht", exists);

            exists = mCategoryRepo.exists(parentCategory);
            assertTrue("Parent Category wurde auch gelöscht, obwohl es noch eine versteckte Kategorie mit ihr gibt", exists);
        } catch (ChildCategoryNotFoundException e) {

            Assert.fail("Gerade erstellte ChildCategory konnte nicht versteckt werden");
        } catch (CannotDeleteChildCategoryException e) {

            Assert.fail("Gerade erstellte Kind Kategorie konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingChildCategoryThatIsLastChildOfParentShouldSucceedAndParentShouldBeDeleted() {
        Category parentCategory = mCategoryRepo.insert(getCategoryWithChild());
        Category childCategory = parentCategory.getChildren().get(0);

        try {
            mChildCategoryRepo.delete(childCategory);

            assertFalse("KindKategorie wurde nicht gelöscht", mChildCategoryRepo.exists(childCategory));
            assertFalse("ParentCategory wurde nicht gelöscht", mCategoryRepo.exists(parentCategory));

        } catch (CannotDeleteChildCategoryException e) {

            Assert.fail("ChildCategory konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingChildCategoryAttachedToBookingShouldThrowCannotDeleteChildCategoryException() {
        Category parentCategory = getCategoryWithChild();
        Category childCategory = mChildCategoryRepo.insert(parentCategory, parentCategory.getChildren().get(0));

        ExpenseObject expense = getSimpleExpense(childCategory);
        mBookingRepo.insert(expense);

        try {
            mChildCategoryRepo.delete(childCategory);
            Assert.fail("Konnte die KindKategorie löschen obwohl sie einer Buchung zugeordnet ist");

        } catch (CannotDeleteChildCategoryException e) {

            assertTrue("Kategorie konnte gelöscht werden", mChildCategoryRepo.exists(childCategory));
            assertEquals(String.format("Child category %s is attached to a ParentExpense and cannot be deleted.", childCategory.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithExistingChildCategoryAttachedToChildBookingShouldThrowCannotDeleteChildCategoryException() {
        //fixme
        Category parentCategory = getCategoryWithChild();
        Category childCategory = mChildCategoryRepo.insert(parentCategory, parentCategory.getChildren().get(0));

        ExpenseObject parentExpense = mock(ExpenseObject.class);
        when(parentExpense.getIndex()).thenReturn(100L);

        ExpenseObject childExpense = getSimpleExpense(childCategory);
        mChildExpenseRepo.insert(parentExpense, childExpense);
        try {
            mChildCategoryRepo.delete(childCategory);
            Assert.fail("Konnte die KindKategorie löschen obwohl sie einer KindBuchung zugeordnet ist");

        } catch (CannotDeleteChildCategoryException e) {

            assertTrue("Kategorie konnte gelöscht werden", mChildCategoryRepo.exists(childCategory));
            assertEquals(String.format("Child category %s is attached to a ChildExpense and cannot be deleted.", childCategory.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testDeleteWithNotExistingChildCategoryShouldSucceed() {
        Category childCategory = getSimpleCategory();

        try {
            mChildCategoryRepo.delete(childCategory);

        } catch (CannotDeleteChildCategoryException e) {

            Assert.fail("Nicht existierende KindKategorie konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testUpdateWithExistingChildCategoryShouldSucceed() {
        Category parent = getCategoryWithChild();
        Category childCategory = mChildCategoryRepo.insert(parent, parent.getChildren().get(0));

        try {
            childCategory.setName("Updated Category Name");
            mChildCategoryRepo.update(childCategory);
            Category fetchedChildCategory = mChildCategoryRepo.get(childCategory.getIndex());

            assertEquals(childCategory, fetchedChildCategory);

        } catch (ChildCategoryNotFoundException e) {

            Assert.fail("Kategorie wurde nicht gefunden");
        }
    }

    @Test
    public void testUpdateWithNotExistingChildCategoryShouldThrowChildCategoryNotFoundException() {
        Category childCategory = getSimpleCategory();

        try {
            mChildCategoryRepo.update(childCategory);
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
        cursor.addRow(new Object[]{expectedChildCategory.getIndex(), expectedChildCategory.getTitle(), expectedChildCategory.getColor().getColorString(), expectedChildCategory.getDefaultExpenseType() ? 1 : 0});
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

    private ExpenseObject getSimpleExpense(Category category) {
        Currency currency = new Currency("Euro", "EUR", "€");

        return new ExpenseObject(
                "Ausgabe",
                new Price(100, false, currency),
                category,
                ExpensesDbHelper.INVALID_INDEX,
                currency
        );
    }
}
