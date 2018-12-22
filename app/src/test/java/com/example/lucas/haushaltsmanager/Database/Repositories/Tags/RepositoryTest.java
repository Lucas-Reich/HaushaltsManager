package com.example.lucas.haushaltsmanager.Database.Repositories.Tags;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.DefaultDatabase;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RepositoryTest {
    private TagRepositoryInterface mRepo;

    @Before
    public void setup() {
        DatabaseManager.initializeInstance(new ExpensesDbHelper());
        mRepo = new Repository(new DefaultDatabase(DatabaseManager.getInstance().openDatabase()));
    }

    @After
    public void teardown() {
        DatabaseManager.getInstance().closeDatabase();
        mRepo = null;
    }

    @Test
    public void testInsert() {
        Tag expectedTag = getSimpleTag();

        Tag actualTag = mRepo.save(expectedTag);

        assertEquals(expectedTag, actualTag);
    }

    private Tag getSimpleTag() {
        return new Tag(
                -1,
                "Mein Tag"
        );
    }
}
