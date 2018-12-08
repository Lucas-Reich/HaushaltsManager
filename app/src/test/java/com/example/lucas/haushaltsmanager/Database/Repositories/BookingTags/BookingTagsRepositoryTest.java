package com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags;

import org.junit.Before;
import org.robolectric.RuntimeEnvironment;

public class BookingTagsRepositoryTest {
    // IMPROVEMENT: BookingTagRepositoryTest erstellen
    private BookingTagRepository mBookingTagRepo;

    @Before
    public void setup() {

        mBookingTagRepo = new BookingTagRepository(RuntimeEnvironment.application);
    }
}
