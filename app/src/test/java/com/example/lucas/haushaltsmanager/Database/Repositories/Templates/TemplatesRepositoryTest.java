package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions.CannotDeleteTemplateException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions.TemplateNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.Template;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class TemplatesRepositoryTest {
    private Currency currency;
    private Account account;
    private Category category;
    private AccountRepository mAccountRepo;
    private TemplateRepository mTemplateRepo;
    private CurrencyRepository mCurrencyRepo;
    private ChildExpenseRepository mChildExpenseRepo;
    private ChildCategoryRepository mChildCategoryRepo;
    private ExpenseRepository mBookingRepo;

    @After
    public void teardown() {

        DatabaseManager.getInstance().closeDatabase();
    }

    @Before
    public void setup() {

        mAccountRepo = new AccountRepository(RuntimeEnvironment.application);
        mTemplateRepo = new TemplateRepository(RuntimeEnvironment.application);
        mCurrencyRepo = new CurrencyRepository(RuntimeEnvironment.application);
        mChildExpenseRepo = new ChildExpenseRepository(RuntimeEnvironment.application);
        mChildCategoryRepo = new ChildCategoryRepository(RuntimeEnvironment.application);
        mBookingRepo = new ExpenseRepository(RuntimeEnvironment.application);

        currency = new Currency("Euro", "EUR", "€");
        currency = mCurrencyRepo.insert(currency);

        account = new Account("Konto 1", new Price(2453, currency));
        account = mAccountRepo.insert(account);

        Category parentCategory = mock(Category.class);
        when(parentCategory.getIndex()).thenReturn(453L);

        category = new Category("Kategorie", new Color("#323211"), ExpenseType.income(), new ArrayList<Category>());
        category = mChildCategoryRepo.insert(parentCategory, category);
    }

    private Template getSimpleTemplate() {
        ExpenseObject expense = mBookingRepo.insert(getSimpleExpense());

        return new Template(
                expense
        );
    }

    @Test
    public void testGetAllShouldReturnAllTemplates() {
        Template template1 = mTemplateRepo.insert(getSimpleTemplate());
        Template template2 = mTemplateRepo.insert(getSimpleTemplate());
        Template template3 = mTemplateRepo.insert(getSimpleTemplate());
        Template template4 = mTemplateRepo.insert(getSimpleTemplate());

        List<Template> templates = mTemplateRepo.getAll();

        assertTrue("Template 1 wurde nicht aus der Datenbank geholt", templates.contains(template1));
        assertTrue("Template 2 wurde nicht aus der Datenbank geholt", templates.contains(template2));
        assertTrue("Template 3 wurde nicht aus der Datenbank geholt", templates.contains(template3));
        assertTrue("Template 4 wurde nicht aus der Datenbank geholt", templates.contains(template4));
        assertEquals("Es wurden mehr/weniger Templates aus der Datenbank geholt", 4, templates.size());
    }

    private ExpenseObject getSimpleExpense() {
        return new ExpenseObject(
                "Ausgabe",
                new Price(1337, true, currency),
                category,
                account.getIndex(),
                currency
        );
    }
}
