package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class CachedInsertChildCategoryRepositoryDecoratorTest {
    private ChildCategoryRepositoryInterface repository;
    private CachedInsertChildCategoryRepositoryDecorator decorator;

    @Before
    public void setUp() {
        repository = mock(ChildCategoryRepositoryInterface.class);
        decorator = new CachedInsertChildCategoryRepositoryDecorator(repository);
    }

    @Test
    public void whenChildCategoryIsCachedNoDatabaseCallIsMade() throws Exception {
        // SetUp
        Category expectedCategory = createCategory();
        Category parent = createCategory();
        injectToCache(parent, expectedCategory);

        // Act
        Category actualCategory = decorator.insert(parent, expectedCategory);


        // Assert
        verifyZeroInteractions(repository);
        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    public void whenChildCategoryIsNotCachedADatabaseCallIsMade() {
        // SetUp
        Category expectedCategory = mock(Category.class);
        childCategoryRepositoryWillFindCategory(expectedCategory);


        // Act
        Category actualCategory = decorator.insert(mock(Category.class), mock(Category.class));


        // Assert
        verify(repository, times(1)).insert(any(Category.class), any(Category.class));
        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    public void whenChildCategoryIsNotCachedADatabaseCallIsMade2() throws Exception {
        // SetUp
        Category expectedCategory = mock(Category.class);
        childCategoryRepositoryWillFindCategory(expectedCategory);

        injectToCache(mock(Category.class), mock(Category.class));


        // Act
        Category actualCategory = decorator.insert(mock(Category.class), mock(Category.class));


        // Assert
        verify(repository, times(1)).insert(any(Category.class), any(Category.class));
        assertEquals(expectedCategory, actualCategory);
    }

    private void childCategoryRepositoryWillFindCategory(Category category) {
        when(repository.insert(any(Category.class), any(Category.class)))
                .thenReturn(category);
    }

    private Category createCategory() {
        return new Category(
                "any string",
                mock(Color.class),
                true,
                new ArrayList<Category>()
        );
    }

    private void injectToCache(final Category parent, final Category child) throws Exception {
        Field accounts = CachedInsertChildCategoryRepositoryDecorator.class.getDeclaredField("cachedCategories");

        accounts.setAccessible(true);
        accounts.set(decorator, new HashMap<Category, List<Category>>() {{
            put(parent, new ArrayList<Category>() {{
                add(child);
            }});
        }});
    }
}
