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
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
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
import java.util.Random;

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
        currency = mCurrencyRepo.create(currency);

        account = new Account("Konto 1", 2453, currency);
        account = mAccountRepo.create(account);

        Category parentCategory = mock(Category.class);
        when(parentCategory.getIndex()).thenReturn(453L);

        category = new Category("Kategorie", "#323211", false, new ArrayList<Category>());
        category = mChildCategoryRepo.insert(parentCategory, category);
    }

    private Template getSimpleTemplate() {
        ExpenseObject expense = mBookingRepo.insert(new ExpenseObject("Ausgabe", new Random().nextInt(1000), true, category, account.getIndex(), currency));

        return new Template(
                expense
        );
    }

    @Test
    public void testExistsWithExistingTemplateShouldSucceed() {
        Template template = mTemplateRepo.insert(getSimpleTemplate());

        boolean exists = mTemplateRepo.exists(template);
        assertTrue("Die Templatebuchung konnte nicht gefunden werden.", exists);
    }

    @Test
    public void testExistsWithNotExistingTemplateShouldFail() {
        Template template = getSimpleTemplate();

        boolean exists = mTemplateRepo.exists(template);
        assertFalse("Nicht existierende Templatebuchung wurde gefunden", exists);
    }

    @Test
    public void testGetWithWithExistingTemplateShouldSucceed() {
        Template expectedTemplate = mTemplateRepo.insert(getSimpleTemplate());

        try {
            Template actualTemplate = mTemplateRepo.get(expectedTemplate.getIndex());
            assertEquals(expectedTemplate, actualTemplate);

        } catch (TemplateNotFoundException e) {

            Assert.fail("Das Template wurde nicht in der Datenbank gefunden.");
        }
    }

    @Test
    public void testGetWithNotExistingTemplateShouldThrowTemplateNotFoundException() {
        Template template = getSimpleTemplate();

        try {
            mTemplateRepo.get(template.getIndex());
            Assert.fail("Nicht existierendes Template wurde gefunden");

        } catch (TemplateNotFoundException e) {

            assertEquals(String.format("Cannot find Template with id %s.", template.getIndex()), e.getMessage());
        }
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

    @Test
    public void testInsertWithValidTemplateShouldSucceed() {
        Template template = mTemplateRepo.insert(getSimpleTemplate());
        assertTrue("Das Template wurde nicht in der Datenbank gefunden", mTemplateRepo.exists(template));
    }

    @Test
    public void testDeleteWithExistingTemplateShouldSucceed() {
        Template template = mTemplateRepo.insert(getSimpleTemplate());

        try {
            mTemplateRepo.delete(template);
            assertFalse("Gelöschtes Template wurde gefunden", mTemplateRepo.exists(template));

        } catch (CannotDeleteTemplateException e) {

            Assert.fail("Template konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithNotExistingTemplateShouldSucceed() {
        Template template = getSimpleTemplate();

        try {
            mTemplateRepo.delete(template);
            assertFalse("Gelöschtes Template wurde gefunden", mTemplateRepo.exists(template));

        } catch (CannotDeleteTemplateException e) {

            Assert.fail("Nicht existierendes Template konnte nicht gelösch werden");
        }
    }

    @Test
    public void testDeleteWithExistingTemplateAndReferencedExpenseIsHiddenShouldSucceedAndExpenseShouldBeDeleted() {
        ExpenseObject relatedParentExpense = mBookingRepo.insert(new ExpenseObject("Ausgabe", 897, true, category, account.getIndex(), currency));
        Template template = mTemplateRepo.insert(new Template(relatedParentExpense));

        try {
            mBookingRepo.hide(relatedParentExpense);
            assertTrue("Ausgabe wurde nicht in eine Versteckte Buchung umgewandelt", mBookingRepo.isHidden(relatedParentExpense));

            mTemplateRepo.delete(template);
            assertFalse("Template wurde nicht gelöscht", mTemplateRepo.exists(template));
            assertFalse("Ausgabe wurde nicht gelöscht", mBookingRepo.exists(relatedParentExpense));
        } catch (ExpenseNotFoundException e) {

            Assert.fail("Buchung wurde nicht gefunden.");
        } catch (CannotDeleteTemplateException e) {

            Assert.fail("Template konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingTemplateAndReferencesChildExpenseIsHiddenShouldSucceedAndChildExpenseShouldBeDeleted() {
        // REFACTOR: dieser test ist irgendwie kaputt gegangen
        ExpenseObject parentExpense = mock(ExpenseObject.class);
        when(parentExpense.getIndex()).thenReturn(465L);
        ExpenseObject relatedChildExpense = mChildExpenseRepo.insert(parentExpense, new ExpenseObject("Ausgabe", 897, true, category, account.getIndex(), currency));
        Template template = mTemplateRepo.insert(new Template(relatedChildExpense));

        try {
            mChildExpenseRepo.hide(relatedChildExpense);
            assertTrue("Ausgabe wurde nicht in eine Versteckte Buchung umgewandelt", mChildExpenseRepo.isHidden(relatedChildExpense));

            mTemplateRepo.delete(template);
            assertFalse("Template wurde nicht gelöscht", mTemplateRepo.exists(template));
            assertFalse("Ausgabe wurde nicht gelöscht", mChildExpenseRepo.exists(relatedChildExpense));
        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("Buchung wurde nicht gefunden.");
        } catch (CannotDeleteTemplateException e) {

            Assert.fail("Template konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testUpdateWithExistingTemplateShouldSucceed() {
        Template expectedTemplate = mTemplateRepo.insert(getSimpleTemplate());

        try {
            ExpenseObject newTemplateExpense = mBookingRepo.insert(new ExpenseObject(
                    "Neue Ausgabe",
                    312,
                    true,
                    category,
                    account.getIndex(),
                    currency
            ));
            expectedTemplate.setTemplate(newTemplateExpense);
            mTemplateRepo.update(expectedTemplate);
            Template fetchedTemplate = mTemplateRepo.get(expectedTemplate.getIndex());

            assertEquals(expectedTemplate, fetchedTemplate);

        } catch (TemplateNotFoundException e) {

            Assert.fail("Template wurde nicht gefunden");
        }
    }

    @Test
    public void testUpdateWithNotExistingTemplateShouldThrowTemplateNotFoundException() {
        Template template = getSimpleTemplate();

        try {
            mTemplateRepo.update(template);
            Assert.fail("Nicht existierenden Template wurde geupdated");

        } catch (TemplateNotFoundException e) {

            assertEquals(String.format("Cannot find Template with id %s.", template.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testCursorToTemplateWithValidCursorShouldSucceed() {
        Template expectedTemplate = getSimpleTemplate();

        String[] columns = new String[]{
                ExpensesDbHelper.TEMPLATE_COL_ID,
                ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{
                expectedTemplate.getIndex(),
                expectedTemplate.getTemplate().getIndex()
        });
        cursor.moveToFirst();

        try {
            Template actualTemplate = TemplateRepository.cursorToTemplate(cursor);
            assertEquals(expectedTemplate, actualTemplate);

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Ausgabe wurde nicht gefunden");
        } catch (CursorIndexOutOfBoundsException e) {

            Assert.fail("Template konnte nicht aus einem vollständigen Cursor wiederhergestellt werden");
        }
    }

    @Test
    public void testCursorToTemplateWithInvalidCursorShouldThrowCursorIndexOutOfBoundsException() {
        Template expectedTemplate = getSimpleTemplate();

        String[] columns = new String[]{
                //Der Index des Templates wurde nicht mit abgefragt
                ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{
                expectedTemplate.getTemplate().getIndex()
        });
        cursor.moveToFirst();

        try {
            TemplateRepository.cursorToTemplate(cursor);
            Assert.fail("Template konnte aus einem unvollständigen Cursor wiederhergestellt werden");

        } catch (ExpenseNotFoundException e) {

            Assert.fail("Ausgabe wurde nicht gefunden");
        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }
}
