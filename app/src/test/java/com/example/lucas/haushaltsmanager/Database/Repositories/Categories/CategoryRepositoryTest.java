package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CannotDeleteCategoryException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Entities.Category;

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

@RunWith(RobolectricTestRunner.class)
public class CategoryRepositoryTest {

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
                false,
                new ArrayList<Category>()
        );
    }

    private Category getCategoryWithChild() {
        Category parentCategory = getSimpleCategory();
        parentCategory.addChild(getSimpleCategory());

        return parentCategory;
    }

    @Test
    public void testExistsWithExistingCategoryShouldReturnTrue() {
        Category category = CategoryRepository.insert(getSimpleCategory());

        boolean exists = CategoryRepository.exists(category);
        assertTrue("Kategorie wurde nicht gefunden", exists);
    }

    @Test
    public void testExistsWithNotExistingCategoryShouldReturnFalse() {
        Category category = getSimpleCategory();

        boolean exists = CategoryRepository.exists(category);
        assertFalse("Nich existierende Kategorie wurde gefunden", exists);
    }

    @Test
    public void testGetWithExistingCategoryShouldSucceed() {
        Category expectedCategory = CategoryRepository.insert(getSimpleCategory());

        try {
            Category fetchedCategory = CategoryRepository.get(expectedCategory.getIndex());
            assertEquals(expectedCategory, fetchedCategory);

        } catch (CategoryNotFoundException e) {

            Assert.fail("Kategorie wurde nicht gefunden");
        }
    }

    @Test
    public void testGetWithExistingCategoryThatHasChildrenShouldSucceed() {
        Category expectedCategory = CategoryRepository.insert(getCategoryWithChild());

        try {
            Category fetchedCategory = CategoryRepository.get(expectedCategory.getIndex());
            assertEquals(expectedCategory, fetchedCategory);

        } catch (CategoryNotFoundException e) {

            Assert.fail("Kategorie wurde nicht gefunden");
        }
    }

    @Test
    public void testGetWithNotExistingCategoryShouldThrowCategoryNotFoundException() {
        long notExistingCategoryId = 1337;

        try {
            CategoryRepository.get(notExistingCategoryId);
            Assert.fail("Nicht existierende Kategorie wurde gefunden");

        } catch (CategoryNotFoundException e) {

            assertEquals(String.format("Could not find Category with index %s.", notExistingCategoryId), e.getMessage());
        }
    }

    @Test
    public void testInsertWithValidCategoryShouldSucceed() {
        Category expectedCategory = CategoryRepository.insert(getSimpleCategory());

        try {
            Category fetchedCategory = CategoryRepository.get(expectedCategory.getIndex());
            assertEquals(expectedCategory, fetchedCategory);

        } catch (CategoryNotFoundException e) {

            Assert.fail("Gerade erstellte Kategorie wurde nicht gefunden");
        }
    }

    @Test
    public void testInsertWithValidCategoryThatHasChildrenShouldSucceed() {
        Category expectedCategory = CategoryRepository.insert(getCategoryWithChild());

        try {
            Category fetchedCategory = CategoryRepository.get(expectedCategory.getIndex());

            assertEquals(expectedCategory, fetchedCategory);
            assertTrue("Kind der Kategorie wurde nicht gefunden", ChildCategoryRepository.exists(expectedCategory.getChildren().get(0)));

        } catch (CategoryNotFoundException e) {

            Assert.fail("Kategorie wurde nicht gefunden");
        }
    }

    @Test
    public void testInsertWithInvalidCategoryShouldFail() {
        //todo was sollte passieren wenn die Kategorie nicht richtig initialisiert wurde, zb kein name
    }

    @Test
    public void testDeleteWithWithExistingCategoryShouldSucceed() {
        Category category = CategoryRepository.insert(getSimpleCategory());

        try {
            CategoryRepository.delete(category);
            assertFalse("Kategorie wurde nicht gelöscht", CategoryRepository.exists(category));

        } catch (CannotDeleteCategoryException e) {

            Assert.fail("Kategorie konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithNotExistingCategoryShouldSucceed() {
        Category category = getSimpleCategory();

        try {
            CategoryRepository.delete(category);
            assertFalse("Nicht existierende Kategorie wurde in der Datenbank gefunden", CategoryRepository.exists(category));

        } catch (CannotDeleteCategoryException e) {

            Assert.fail("Nicht existierende Kategorie konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingCategoryAttachedToChildCategoriesShouldThrowCannotDeleteCategoryException() {
        Category category = CategoryRepository.insert(getCategoryWithChild());

        try {
            CategoryRepository.delete(category);
            Assert.fail("Kategorie konnte gelöscht werden, obwohl es noch Kinder zu dieser Kategorie gibt");

        } catch (CannotDeleteCategoryException e) {

            assertTrue("Kategorie wurde gelöscht, obwohl es Kinder gibt", CategoryRepository.exists(category));
            assertEquals(String.format("Category %s cannot be deleted.", category.getTitle()), e.getMessage());
        }
    }

    @Test
    public void testUpdateWithWithExistingCategoryShouldSucceed() {
        Category expectedCategory = CategoryRepository.insert(getSimpleCategory());

        try {
            expectedCategory.setName("New Category Name");
            CategoryRepository.update(expectedCategory);
            Category fetchedCategory = CategoryRepository.get(expectedCategory.getIndex());

            assertEquals(expectedCategory, fetchedCategory);

        } catch (CategoryNotFoundException e) {

            Assert.fail("Gerade erstellte Kategorie konnte nicht gefunden werden");
        }
    }

    @Test
    public void testUpdateWithNotExistingCategoryShouldThrowCategoryNotFoundException() {
        Category category = getSimpleCategory();

        try {
            CategoryRepository.update(category);
            Assert.fail("Nicht existierende Kategorie konnte geupdated werden");

        } catch (CategoryNotFoundException e) {

            assertEquals(String.format("Could not find Category with index %s.", category.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testCursorToCategoryWithValidCursorShouldSucceed() {
        Category expectedCategory = getSimpleCategory();

        String[] columns = new String[]{
                ExpensesDbHelper.CATEGORIES_COL_ID,
                ExpensesDbHelper.CATEGORIES_COL_NAME,
                ExpensesDbHelper.CATEGORIES_COL_COLOR,
                ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{expectedCategory.getIndex(), expectedCategory.getTitle(), expectedCategory.getColorString(), expectedCategory.getDefaultExpenseType() ? 1 : 0});
        cursor.moveToFirst();

        try {
            Category fetchedCategory = CategoryRepository.cursorToCategory(cursor);
            assertEquals(expectedCategory, fetchedCategory);

        } catch (CursorIndexOutOfBoundsException e) {

            Assert.fail("Kategorie konnte nicht aus einem vollständingen Cursor hergestellt werden");
        }
    }

    @Test
    public void testCursorToCategoryWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        Category expectedCategory = getSimpleCategory();

        String[] columns = new String[]{
                ExpensesDbHelper.CATEGORIES_COL_ID,
                ExpensesDbHelper.CATEGORIES_COL_NAME,
                //Die Farbe ist nicht mit im Cursor
                ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{expectedCategory.getIndex(), expectedCategory.getTitle(), expectedCategory.getDefaultExpenseType()});
        cursor.moveToFirst();

        try {
            CategoryRepository.cursorToCategory(cursor);
            Assert.fail("Kategorie konnte aus einem unvollständigen Cursor wiederhergestellt werden");

        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }
}
