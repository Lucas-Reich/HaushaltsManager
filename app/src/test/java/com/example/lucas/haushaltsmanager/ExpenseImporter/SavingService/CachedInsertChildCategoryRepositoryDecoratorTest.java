package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class CachedInsertChildCategoryRepositoryDecoratorTest {
    private CategoryRepositoryInterface repository;
    private CachedInsertCategoryRepositoryDecorator decorator;

    @Before
    public void setUp() {
        repository = mock(CategoryRepositoryInterface.class);
        decorator = new CachedInsertCategoryRepositoryDecorator(repository);
    }

    @Test
    public void whenChildCategoryIsCachedNoDatabaseCallIsMade() throws Exception {
        // SetUp
        Category expectedCategory = createCategory();
        injectToCache(expectedCategory);

        // Act
        decorator.insert(expectedCategory);

        // Assert
        verifyZeroInteractions(repository);
    }

    @Test
    public void whenChildCategoryIsNotCachedADatabaseCallIsMade() throws CategoryCouldNotBeCreatedException {
        // SetUp
        Category category = mock(Category.class);

        // Act
        decorator.insert(category);

        // Assert
        verify(repository, times(1)).insert(any(Category.class));
    }

    private Category createCategory() {
        return new Category(
                "any string",
                mock(Color.class),
                ExpenseType.expense()
        );
    }

    private void injectToCache(final Category category) throws Exception {
        Field accounts = CachedInsertCategoryRepositoryDecorator.class.getDeclaredField("cachedCategories");

        accounts.setAccessible(true);
        accounts.set(decorator, new ArrayList<Category>() {{
            add(category);
        }});
    }
}
