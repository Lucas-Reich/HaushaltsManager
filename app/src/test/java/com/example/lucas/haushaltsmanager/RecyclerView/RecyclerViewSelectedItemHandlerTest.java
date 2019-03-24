package com.example.lucas.haushaltsmanager.RecyclerView;

import android.support.v7.widget.RecyclerView;

import com.example.lucas.haushaltsmanager.RecyclerView.RecyclerViewItems.IRecyclerItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;

import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
public class RecyclerViewSelectedItemHandlerTest {
    private ExpenseListRecyclerViewAdapter mItemHandler;

    @Before
    public void setUp() {
        mItemHandler = new ExpenseListRecyclerViewAdapter(new ArrayList<IRecyclerItem>());

        RecyclerView rView = new RecyclerView(RuntimeEnvironment.application);
        rView.setAdapter(mItemHandler);
    }

    @After
    public void teardown() {
        mItemHandler = null;
    }

    @Test
    public void testSelectItem() {
        // TODO: Implement
        fail("Test is not implement");
    }

    @Test
    public void testUnselectItem() {
        // TODO: Implement
        fail("Test is not implement");
    }
}
