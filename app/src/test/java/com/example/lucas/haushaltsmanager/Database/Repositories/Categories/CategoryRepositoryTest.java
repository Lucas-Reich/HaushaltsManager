package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Category;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class CategoryRepositoryTest {

    @Before
    public void setup() {

        Context context = RuntimeEnvironment.application;
        ExpensesDbHelper dbHelper = new ExpensesDbHelper(context);
        DatabaseManager.initializeInstance(dbHelper);
    }

    @Test
    public void testExistsWithExistingCategoryShouldReturnTrue() {
        Category category = new Category("Kategorie", "#121212", false, new ArrayList<Category>());
        category = CategoryRepository.insert(category);

        boolean exists = CategoryRepository.exists(category);
        assertTrue("Kategorie wurde nicht gefunden", exists);
    }

    @Test
    public void testExistsWithNotExistingCategoryShouldReturnFalse() {

    }

    @Test
    public void testGetWithExistingCategoryShouldSucceed() {

    }

    @Test
    public void testGetWithNozExistingCategoryShouldThrowCategoryNotFoundException() {

    }

    @Test
    public void testInsertWithValidCategoryShouldSucceed() {

    }

    @Test
    public void testInsertWithInvalidCategoryShouldFail() {
        //todo was sollte passieren wenn die Kategorie nicht richtig initialisiert wurde, zb kein name
    }

    @Test
    public void testDeleteWithWithExistingCurrencyShouldSucceed() {

    }

    @Test
    public void testDeleteWithNotExistingCategoryShouldSucceed() {

    }

    @Test
    public void testDeleteWithExistingCategoryAttachedToChildCategoriesSholdThrowCannotDeleteCategoryException() {

    }

    @Test
    public void testUpdateWithWithExistingCategoryShouldSucceed() {

    }

    @Test
    public void testUpdateWithNotExistingCategoryShouldThrowCategoryNotFoundException() {

    }

    @Test
    public void testCursorToCategoryWithValidCursorShouldSucceed() {

    }

    @Test
    public void testCursorToCategoryWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {

    }

    private void assertSameCategories(Category expected, Category actual) {
        assertTrue(expected.equals(actual));
    }
}
