package com.example.lucas.haushaltsmanager.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lucas.haushaltsmanager.Database.AppDatabase
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingDAO
import com.example.lucas.haushaltsmanager.Database.Repositories.ParentBookingDAO
import com.example.lucas.haushaltsmanager.TestUtil
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*


@RunWith(AndroidJUnit4::class)
class ParentBookingDAOTest {
    private lateinit var parentBookingDAO: ParentBookingDAO
    private lateinit var bookingDAO: BookingDAO
    private lateinit var db: AppDatabase
    private lateinit var testUtil: TestUtil

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()

        parentBookingDAO = db.parentBookingDAO()
        bookingDAO = db.bookingDAO()
        testUtil = TestUtil()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun deletesParentWithNoAttachedChildren() {
        // Arrange
        val parentId = UUID.randomUUID()

        val parentBooking = testUtil.createParentBooking(parentId)
        parentBookingDAO.insert(parentBooking, ArrayList())

        // Act
        parentBookingDAO.deleteParentWhenNotReferenced(parentId)

        // Assert
        assertThatParentNotExisting()
    }

    @Test
    fun doesNotDeleteParentWhenChildrenIsAttached() {
        // Arrange
        val parentId = UUID.randomUUID()

        val parentBooking = testUtil.createParentBookingWithChildren(parentId, 1)
        parentBookingDAO.insert(parentBooking, parentBooking.children)

        val bookings = bookingDAO.getAll()
        assertEquals(1, bookings.count())
        assertEquals(parentId, bookings.get(0).parentId)

        // Act
        parentBookingDAO.deleteParentWhenNotReferenced(parentId)

        // Assert
        assertThatParentIsExisting()
    }

    private fun assertThatParentIsExisting() {
        val existingParentBookings = parentBookingDAO.getAll()

        assertEquals(1, existingParentBookings.size)
    }

    private fun assertThatParentNotExisting() {
        val parentBookings = parentBookingDAO.getAll()

        assertEquals(0, parentBookings.size)
    }
}