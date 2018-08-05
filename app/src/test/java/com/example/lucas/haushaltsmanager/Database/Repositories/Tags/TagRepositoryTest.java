package com.example.lucas.haushaltsmanager.Database.Repositories.Tags;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags.BookingTagRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Exceptions.CannotDeleteTagException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Exceptions.TagNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class TagRepositoryTest {

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.application;
        ExpensesDbHelper dbHelper = new ExpensesDbHelper(context);
        DatabaseManager.initializeInstance(dbHelper);
    }

    private Tag getSimpleTag() {
        return new Tag(
                "Tag"
        );
    }

    @Test
    public void testExistsWithExistingTagShouldSucceed() {
        Tag tag = TagRepository.insert(getSimpleTag());

        boolean exists = TagRepository.exists(tag);
        assertTrue("Das Tag wurde nicht in der Datenbank gefunden", exists);
    }

    @Test
    public void testExistsWithNotExistingTagShouldFail() {
        Tag tag = getSimpleTag();

        boolean exists = TagRepository.exists(tag);
        assertFalse("Das Tag wurde in der Datenbank gefunden", exists);
    }

    @Test
    public void testGetWithExistingTagShouldSucceed() {
        Tag expectedTag = TagRepository.insert(getSimpleTag());

        try {
            Tag fetchedTag = TagRepository.get(expectedTag.getIndex());
            assertEquals(expectedTag, fetchedTag);

        } catch (TagNotFoundException e) {

            Assert.fail("Tag wurde nicht gefunden");
        }
    }

    @Test
    public void testGetWithNotExistingTagShouldThrowTagNotFoundException() {
        long notExistingTagId = 313;

        try {
            TagRepository.get(notExistingTagId);

            Assert.fail("Nicht existierendes Tag wurde in der Datenbank gefunden.");

        } catch (TagNotFoundException e) {

            assertEquals(String.format("Could not find Tag with id %s.", notExistingTagId), e.getMessage());
        }
    }

    @Test
    public void testInsertWithValidTagShouldSucceed() {
        Tag expectedTag = TagRepository.insert(getSimpleTag());

        try {
            Tag fetchedTag = TagRepository.get(expectedTag.getIndex());
            assertEquals(expectedTag, fetchedTag);

        } catch (TagNotFoundException e) {

            Assert.fail("Tag konnte nicht in die Datenbank geschrieben werden");
        }
    }

    @Test
    public void testInsertWithInvalidTagShouldFail() {
        //todo was sollte passieren wenn ein Tag nicht richtig initialisiert wurde, zb kein namen hat
    }

    @Test
    public void testDeleteWithExistingTagShouldSucceed() {
        Tag tag = TagRepository.insert(getSimpleTag());

        try {
            TagRepository.delete(tag);
            assertFalse("Tag wurde nicht aus der Datenbank gelöscht", TagRepository.exists(tag));

        } catch (CannotDeleteTagException e) {

            assertTrue("Tag wurde nicht gelöscht", TagRepository.exists(tag));
            Assert.fail("Tag konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithNotExistingTagShouldSucceed() {
        Tag tag = getSimpleTag();

        try {
            TagRepository.delete(tag);
            assertFalse("Tag wurde nicht as der Datenbank gelöscht", TagRepository.exists(tag));

        } catch (CannotDeleteTagException e) {

            Assert.fail("Nicht existierenden Tag konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingTagAttachedToBookingShouldThrowCannotDeleteTagException() {
        ExpenseObject expenseObjectMock = mock(ExpenseObject.class);
        when(expenseObjectMock.getIndex()).thenReturn(100L);
        when(expenseObjectMock.getExpenseType()).thenReturn(ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE);

        Tag tag = TagRepository.insert(getSimpleTag());

        BookingTagRepository.insert(expenseObjectMock.getIndex(), tag, expenseObjectMock.getExpenseType());

        try {
            TagRepository.delete(tag);
            Assert.fail("Tag konnte gelöscht werden obwohl es noch einer Buchung zugeordnet ist.");

        } catch (CannotDeleteTagException e) {

            assertEquals(String.format("Tag %s could not be deleted.", tag.getName()), e.getMessage());
        }
    }

    @Test
    public void testUpdateWithExistingTagShouldSucceed() {
        Tag expectedTag = TagRepository.insert(getSimpleTag());

        try {
            expectedTag.setName("New Tag Name");
            TagRepository.update(expectedTag);
            Tag fetchedTag = TagRepository.get(expectedTag.getIndex());

            assertEquals(expectedTag, fetchedTag);

        } catch (TagNotFoundException e) {

            Assert.fail("Tag konnte nicht gefunden werden");
        }
    }

    @Test
    public void testUpdateWithNotExistingTagShouldThrowTagNotFoundException() {
        Tag tag = getSimpleTag();

        try {
            TagRepository.update(tag);
            Assert.fail("Nicht existierendes Tag gefunden");

        } catch (TagNotFoundException e) {

            assertEquals(String.format("Could not find Tag with id %s.", tag.getIndex()), e.getMessage());
        }
    }

    @Test
    public void testCursorToTagWithValidCursorShouldSucceed() {
        Tag expectedTag = getSimpleTag();

        String[] columns = new String[]{
                ExpensesDbHelper.TAGS_COL_ID,
                ExpensesDbHelper.TAGS_COL_NAME
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{expectedTag.getIndex(), expectedTag.getName()});
        cursor.moveToFirst();

        try {
            Tag fetchedTag = TagRepository.cursorToTag(cursor);
            assertEquals(expectedTag, fetchedTag);

        } catch (CursorIndexOutOfBoundsException e) {

            Assert.fail("Konnte keinen Tag aus dem Cursor erstellen");
        }
    }

    @Test
    public void testCursorToTagWithInvalidCursorThrowCursorIndexOutOfBoundsException() {
        Tag expectedTag = getSimpleTag();

        String[] columns = new String[]{
                ExpensesDbHelper.TAGS_COL_ID
                //Der Name des Tags wurde nicht mit abgefragt
        };

        MatrixCursor cursor = new MatrixCursor(columns);
        cursor.addRow(new Object[]{expectedTag.getIndex()});
        cursor.moveToFirst();

        try {
            TagRepository.cursorToTag(cursor);
            Assert.fail("Tag konnte trotz fehlender attribute erstellt werden");

        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }
}
