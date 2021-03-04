package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;

import junit.framework.Assert;

import org.junit.After;
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

@RunWith(RobolectricTestRunner.class)
public class CategoryRepositoryTest {

    private ChildCategoryRepository mChildCategoryRepo;
    private CategoryRepository mCategoryRepo;

    @Before
    public void setup() {

        mChildCategoryRepo = new ChildCategoryRepository(RuntimeEnvironment.application);
        mCategoryRepo = new CategoryRepository(RuntimeEnvironment.application);
    }

    @After
    public void teardown() {

        DatabaseManager.getInstance().closeDatabase();
    }

    @Test
    public void testInsertWithValidCategoryShouldSucceed() {
        Category expectedCategory = mCategoryRepo.insert(getSimpleCategory());

        Category fetchedCategory = getCategoryWithId(expectedCategory.getIndex());
        assertEquals(expectedCategory, fetchedCategory);
    }

    @Test
    public void testInsertWithValidCategoryThatHasChildrenShouldSucceed() {
        Category expectedCategory = mCategoryRepo.insert(getCategoryWithChild());

        Category fetchedCategory = getCategoryWithId(expectedCategory.getIndex());

        assertEquals(expectedCategory, fetchedCategory);
        assertTrue("Kind der Kategorie wurde nicht gefunden", mChildCategoryRepo.exists(expectedCategory.getChildren().get(0)));
    }

    @Test
    public void testUpdateWithWithExistingCategoryShouldSucceed() {
        Category expectedCategory = mCategoryRepo.insert(getSimpleCategory());

        try {
            expectedCategory.setName("New Category Name");
            mCategoryRepo.update(expectedCategory);
            Category fetchedCategory = getCategoryWithId(expectedCategory.getIndex());

            assertEquals(expectedCategory, fetchedCategory);

        } catch (CategoryNotFoundException e) {

            Assert.fail("Gerade erstellte Kategorie konnte nicht gefunden werden");
        }
    }

    @Test
    public void testUpdateWithNotExistingCategoryShouldThrowCategoryNotFoundException() {
        Category category = getSimpleCategory();

        try {
            mCategoryRepo.update(category);
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
        cursor.addRow(new Object[]{expectedCategory.getIndex(), expectedCategory.getTitle(), expectedCategory.getColor().getColorString(), expectedCategory.getDefaultExpenseType().value() ? 1 : 0});
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
        cursor.addRow(new Object[]{expectedCategory.getIndex(), expectedCategory.getTitle(), expectedCategory.getDefaultExpenseType().value()});
        cursor.moveToFirst();

        try {
            CategoryRepository.cursorToCategory(cursor);
            Assert.fail("Kategorie konnte aus einem unvollständigen Cursor wiederhergestellt werden");

        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }

    private Category getSimpleCategory() {
        return new Category(
                "Kategorie",
                Color.black(),
                ExpenseType.income(),
                new ArrayList<Category>()
        );
    }

    private Category getCategoryWithChild() {
        Category parentCategory = getSimpleCategory();
        parentCategory.addChild(getSimpleCategory());

        return parentCategory;
    }

    private Category getCategoryWithId(long id) {
        List<Category> categories = mCategoryRepo.getAll();

        for (Category category : categories) {
            if (category.getIndex() != id) {
                continue;
            }

            return category;
        }

        return null;
    }
}
