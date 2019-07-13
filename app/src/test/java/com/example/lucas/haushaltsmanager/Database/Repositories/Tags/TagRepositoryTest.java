package com.example.lucas.haushaltsmanager.Database.Repositories.Tags;

import android.database.CursorIndexOutOfBoundsException;
import android.database.MatrixCursor;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags.BookingTagRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Exceptions.CannotDeleteTagException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Exceptions.TagNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.TagRepository;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Field;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class TagRepositoryTest {
    private TagRepository mTagRepo;

    /**
     * Manager welcher die Datenbank verbindungen hält
     */
    private DatabaseManager mDatabaseManagerInstance;

    @Before
    public void setup() {

        mTagRepo = new TagRepository(RuntimeEnvironment.application);
        mDatabaseManagerInstance = DatabaseManager.getInstance();
    }

    @After
    public void teardown() {

        // Keine Ahnung warum das so funktioniert aber irgendwie tut es das
        // Angepasste Quelle: https://stackoverflow.com/questions/34742685/robolectric-running-multiple-tests-fails
        mTagRepo.closeDatabase();
        mDatabaseManagerInstance.closeDatabase();
    }

    private Tag getSimpleTag() {
        return new Tag(
                "Tag"
        );
    }

    @Test
    public void testExistsWithExistingTagShouldSucceed() {
        Tag tag = mTagRepo.create(getSimpleTag());

        boolean exists = mTagRepo.exists(tag);
        assertTrue("Das Tag wurde nicht in der Datenbank gefunden", exists);
    }

    @Test
    public void testExistsWithNotExistingTagShouldFail() {
        Tag tag = getSimpleTag();

        boolean exists = mTagRepo.exists(tag);
        assertFalse("Das Tag wurde in der Datenbank gefunden", exists);
    }

    @Test
    public void testGetWithExistingTagShouldSucceed() {
        Tag expectedTag = mTagRepo.create(getSimpleTag());

        try {
            Tag fetchedTag = mTagRepo.get(expectedTag.getIndex());
            assertEquals(expectedTag, fetchedTag);

        } catch (TagNotFoundException e) {

            Assert.fail("Tag wurde nicht gefunden");
        }
    }

    @Test
    public void testGetWithNotExistingTagShouldThrowTagNotFoundException() {
        long notExistingTagId = 313;

        try {
            mTagRepo.get(notExistingTagId);

            Assert.fail("Nicht existierendes Tag wurde in der Datenbank gefunden.");

        } catch (TagNotFoundException e) {

            assertEquals(String.format("Could not find Tag with id %s.", notExistingTagId), e.getMessage());
        }
    }

    @Test
    public void testInsertWithValidTagShouldSucceed() {
        Tag expectedTag = mTagRepo.create(getSimpleTag());

        try {
            Tag fetchedTag = mTagRepo.get(expectedTag.getIndex());
            assertEquals(expectedTag, fetchedTag);

        } catch (TagNotFoundException e) {

            Assert.fail("Tag konnte nicht in die Datenbank geschrieben werden");
        }
    }

    @Test
    public void testDeleteWithExistingTagShouldSucceed() {
        Tag tag = mTagRepo.create(getSimpleTag());

        try {
            mTagRepo.delete(tag);
            assertFalse("Tag wurde nicht aus der Datenbank gelöscht", mTagRepo.exists(tag));

        } catch (CannotDeleteTagException e) {

            assertTrue("Tag wurde nicht gelöscht", mTagRepo.exists(tag));
            Assert.fail("Tag konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithNotExistingTagShouldSucceed() {
        Tag tag = getSimpleTag();

        try {
            mTagRepo.delete(tag);
            assertFalse("Tag wurde nicht as der Datenbank gelöscht", mTagRepo.exists(tag));

        } catch (CannotDeleteTagException e) {

            Assert.fail("Nicht existierenden Tag konnte nicht gelöscht werden");
        }
    }

    @Test
    public void testDeleteWithExistingTagAttachedToBookingShouldThrowCannotDeleteTagException() {
        Tag tag = mTagRepo.create(getSimpleTag());

        //Mocking the BookingTagRepository and the isTagAssignedToBooking Method
        BookingTagRepository mockBookingTagRepo = mock(BookingTagRepository.class);
        when(mockBookingTagRepo.isTagAssignedToBooking(tag)).thenReturn(true);

        injectMock(mTagRepo, mockBookingTagRepo, "mBookingTagsRepo");

        try {
            mTagRepo.delete(tag);
            Assert.fail("Tag konnte gelöscht werden obwohl es noch einer Buchung zugeordnet ist.");

        } catch (CannotDeleteTagException e) {

            assertEquals(String.format("Tag %s could not be deleted.", tag.getName()), e.getMessage());
        }
    }

    @Test
    public void testUpdateWithExistingTagShouldSucceed() {
        Tag expectedTag = mTagRepo.create(getSimpleTag());

        try {
            expectedTag.setName("New Tag Name");
            mTagRepo.update(expectedTag);
            Tag fetchedTag = mTagRepo.get(expectedTag.getIndex());

            assertEquals(expectedTag, fetchedTag);

        } catch (TagNotFoundException e) {

            Assert.fail("Tag konnte nicht gefunden werden");
        }
    }

    @Test
    public void testUpdateWithNotExistingTagShouldThrowTagNotFoundException() {
        Tag tag = getSimpleTag();

        try {
            mTagRepo.update(tag);
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
            Tag fetchedTag = TagRepository.fromCursor(cursor);
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
            TagRepository.fromCursor(cursor);
            Assert.fail("Tag konnte trotz fehlender attribute erstellt werden");

        } catch (CursorIndexOutOfBoundsException e) {

            //do nothing
        }
    }

    /**
     * Methode um ein Feld einer Klasse durch ein anderes, mit injection, auszutauschen.
     *
     * @param obj       Objekt welches angepasst werden soll
     * @param value     Neuer Wert des Felds
     * @param fieldName Name des Feldes
     */
    private void injectMock(Object obj, Object value, String fieldName) {
        try {
            Class cls = obj.getClass();

            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {

            Assert.fail(String.format("Could not find field %s in class %s", fieldName, obj.getClass().getSimpleName()));
        }
    }
}
