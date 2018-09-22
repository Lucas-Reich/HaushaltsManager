package com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class BookingTagsRepositoryTest {
    private BookingTagRepository mBookingTagRepo;

    @Before
    public void setup() {

        mBookingTagRepo = new BookingTagRepository(RuntimeEnvironment.application);
    }

    public void testGetWithExistingBookingTagsShouldSucceed() {

        //todo
    }

    public void testGetWithExistingBookingIdButNotExistingExpenseTypeShouldSucceed() {

        //todo
    }

    public void testGetWithNotExistingBookingIdAndExistingExpenseTypeShouldSucceed() {

        //todo
    }
}
