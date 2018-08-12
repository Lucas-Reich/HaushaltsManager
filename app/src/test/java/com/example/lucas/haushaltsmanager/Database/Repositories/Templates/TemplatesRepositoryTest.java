package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import android.content.Context;
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

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.application;
        ExpensesDbHelper dbHelper = new ExpensesDbHelper(context);
        DatabaseManager.initializeInstance(dbHelper);

        currency = new Currency("Euro", "EUR", "€");
        currency = CurrencyRepository.insert(currency);

        account = new Account("Konto 1", 2453, currency);
        account = AccountRepository.insert(account);

        Category parentCategory = mock(Category.class);
        when(parentCategory.getIndex()).thenReturn(453L);

        category = new Category("Kategorie", "#323211", false, new ArrayList<Category>());
        category = ChildCategoryRepository.insert(parentCategory, category);
    }

    private Template getSimpleTemplate() {
        ExpenseObject expense = ExpenseRepository.insert(new ExpenseObject("Ausgabe", new Random().nextInt(1000), true, category, account.getIndex(), currency));

        return new Template(
                expense
        );
    }

    @Test
    public void testExistsWithExistingTemplateShouldSucceed() {
        Template template = TemplateRepository.insert(getSimpleTemplate());

        boolean exists = TemplateRepository.exists(template);
        assertTrue("Die Templatebuchung konnte nicht gefunden werden.", exists);
    }

    @Test
    public void testExistsWithNotExistingTemplateShouldFail() {
        Template template = getSimpleTemplate();

        boolean exists = TemplateRepository.exists(template);
        assertFalse("Nicht existierende Templatebuchung wurde gefunden", exists);
    }

    @Test
    public void testGetWithWithExistingTemplateShouldSucceed() {
        Template expectedTemplate = TemplateRepository.insert(getSimpleTemplate());

        try {
            Template actualTemplate = TemplateRepository.get(expectedTemplate.getIndex());
            assertEquals(expectedTemplate, actualTemplate);

        } catch (TemplateNotFoundException e) {

            Assert.fail("Das Template wurde nicht in der Datenbank gefunden.");
        }
    }

    @Test
    public void testGetWithNotExistingTemplateShouldThrowTemplateNotFoundException() {
        Template template = getSimpleTemplate();

        try {
            TemplateRepository.get(template.getIndex());
            Assert.fail("Nicht existierendes Template wurde gefunden");

        } catch (TemplateNotFoundException e) {

            assertEquals(String.format("Cannot find Template with id %s.", template.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testGetAllShouldReturnAllTemplates() {
        Template template1 = TemplateRepository.insert(getSimpleTemplate());
        Template template2 = TemplateRepository.insert(getSimpleTemplate());
        Template template3 = TemplateRepository.insert(getSimpleTemplate());
        Template template4 = TemplateRepository.insert(getSimpleTemplate());

        List<Template> templates = TemplateRepository.getAll();

        assertTrue("Template 1 wurde nicht aus der Datenbank geholt", templates.contains(template1));
        assertTrue("Template 2 wurde nicht aus der Datenbank geholt", templates.contains(template2));
        assertTrue("Template 3 wurde nicht aus der Datenbank geholt", templates.contains(template3));
        assertTrue("Template 4 wurde nicht aus der Datenbank geholt", templates.contains(template4));
        assertEquals("Es wurden mehr/weniger Templates aus der Datenbank geholt", 4, templates.size());
    }

    @Test
    public void testInsertWithValidTemplateShouldSucceed() {
        Template template = TemplateRepository.insert(getSimpleTemplate());
        assertTrue("Das Template wurde nicht in der Datenbank gefunden", TemplateRepository.exists(template));
    }

    @Test
    public void testDeleteWithExistingTemplateShouldSucceed() {
        Template template = TemplateRepository.insert(getSimpleTemplate());

        try {
            TemplateRepository.delete(template);
            assertFalse("Gelöschtes Template wurde gefunden", TemplateRepository.exists(template));

        } catch (CannotDeleteTemplateException e) {

            Assert.fail("Template konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithNotExistingTemplateShouldSucceed() {
        Template template = getSimpleTemplate();

        try {
            TemplateRepository.delete(template);
            assertFalse("Gelöschtes Template wurde gefunden", TemplateRepository.exists(template));

        } catch (CannotDeleteTemplateException e) {

            Assert.fail("Nicht existierendes Template konnte nicht gelösch werden");
        }
    }

    @Test
    public void testDeleteWithExistingTemplateAndReferencedExpenseIsHiddenShouldSucceedAndExpenseShouldBeDeleted() {
        ExpenseObject relatedParentExpense = ExpenseRepository.insert(new ExpenseObject("Ausgabe", 897, true, category, account.getIndex(), currency));
        Template template = TemplateRepository.insert(new Template(relatedParentExpense));

        try {
            ExpenseRepository.hide(relatedParentExpense);
            assertTrue("Ausgabe wurde nicht in eine Versteckte Buchung umgewandelt", ExpenseRepository.isHidden(relatedParentExpense));

            TemplateRepository.delete(template);
            assertFalse("Template wurde nicht gelöscht", TemplateRepository.exists(template));
            assertFalse("Ausgabe wurde nicht gelöscht", ExpenseRepository.exists(relatedParentExpense));
        } catch (ExpenseNotFoundException e) {

            Assert.fail("Buchung wurde nicht gefunden.");
        } catch (CannotDeleteTemplateException e) {

            Assert.fail("Template konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingTemplateAndReferencesChildExpenseIsHiddenShouldSucceedAndChildExpenseShouldBeDeleted() {
        ExpenseObject parentExpense = mock(ExpenseObject.class);
        when(parentExpense.getIndex()).thenReturn(465L);
        ExpenseObject relatedChildExpense = ChildExpenseRepository.insert(parentExpense, new ExpenseObject("Ausgabe", 897, true, category, account.getIndex(), currency));
        Template template = TemplateRepository.insert(new Template(relatedChildExpense));

        try {
            ChildExpenseRepository.hide(relatedChildExpense);
            assertTrue("Ausgabe wurde nicht in eine Versteckte Buchung umgewandelt", ChildExpenseRepository.isHidden(relatedChildExpense));

            TemplateRepository.delete(template);
            assertFalse("Template wurde nicht gelöscht", TemplateRepository.exists(template));
            assertFalse("Ausgabe wurde nicht gelöscht", ChildExpenseRepository.exists(relatedChildExpense));
        } catch (ChildExpenseNotFoundException e) {

            Assert.fail("Buchung wurde nicht gefunden.");
        } catch (CannotDeleteTemplateException e) {

            Assert.fail("Template konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testUpdateWithExistingTemplateShouldSucceed() {
        Template expectedTemplate = TemplateRepository.insert(getSimpleTemplate());

        try {
            ExpenseObject newTemplateExpense = ExpenseRepository.insert(new ExpenseObject(
                    "Neue Ausgabe",
                    312,
                    true,
                    category,
                    account.getIndex(),
                    currency
            ));
            expectedTemplate.setTemplate(newTemplateExpense);
            TemplateRepository.update(expectedTemplate);
            Template fetchedTemplate = TemplateRepository.get(expectedTemplate.getIndex());

            assertEquals(expectedTemplate, fetchedTemplate);

        } catch (TemplateNotFoundException e) {

            Assert.fail("Template wurde nicht gefunden");
        }
    }

    @Test
    public void testUpdateWithNotExistingTemplateShouldThrowTemplateNotFoundException() {
        Template template = getSimpleTemplate();

        try {
            TemplateRepository.update(template);
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
