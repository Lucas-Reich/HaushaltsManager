package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.example.lucas.haushaltsmanager.Database.Repositories.CategoryDAO;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Color;
import com.example.lucas.haushaltsmanager.entities.booking.ExpenseType;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class CachedInsertChildCategoryRepositoryDecoratorTest {
    private CategoryDAO repository;
    private CachedCategoryReadRepositoryDecorator decorator;

    @Before
    public void setUp() {
        repository = mock(CategoryDAO.class);
        decorator = new CachedCategoryReadRepositoryDecorator(repository);
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
    public void whenChildCategoryIsNotCachedADatabaseCallIsMade() {
        // SetUp
        Category category = createCategory();

        // Act
        decorator.insert(category);

        // Assert
        verify(repository, times(1)).insert(category);
    }

    private Category createCategory() {
        return new Category(
                "any string",
                Color.random(),
                ExpenseType.expense()
        );
    }

    private void injectToCache(final Category category) throws Exception {
        Field accounts = CachedCategoryReadRepositoryDecorator.class.getDeclaredField("cache");

        accounts.setAccessible(true);
        accounts.set(decorator, new ArrayList<Category>() {{
            add(category);
        }});
    }
}
