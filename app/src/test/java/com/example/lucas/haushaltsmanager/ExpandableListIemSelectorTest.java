package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExpandableListIemSelectorTest {
    private ExpandableListAdapter mListAdapter;
    private ExpandableListItemSelector mItemSelector;

    @Before
    public void setup() {
        mListAdapter = mock(ExpandableListAdapter.class);
        mItemSelector = new ExpandableListItemSelector(mListAdapter);
    }

    @After
    public void teardown() {
        mListAdapter = null;
        mItemSelector = null;
    }


    @Test
    public void selectGroup() {
        ExpenseObject expectedGroup = getSimpleExpense(498);
        when(mListAdapter.getGroup(anyInt())).thenReturn(expectedGroup);

        boolean selectionStatus = mItemSelector.selectItem(1, -1);

        assertTrue("Auswählen der Buchung wurde nicht bestätigt", selectionStatus);

        assertEquals(expectedGroup, mItemSelector.getSelectedItems().get(0).getItem());
    }

    @Test
    public void selectGroupWithChildAlreadySelectedShouldFail() {
        ExpandableListItemSelector itemSelector = mock(ExpandableListItemSelector.class);
        when(itemSelector.getSelectedChildrenCount()).thenReturn(1);

        boolean selectionStatus = itemSelector.selectItem(1, -1);

        assertFalse("GroupBuchung konnte ausgewählt werden, obwohl ein Kind bereits ausgewählt ist.", selectionStatus);
    }

    @Test
    public void testSelectGroupWithNotExistingGroupPosition() {
        // TODO: Was soll passieren wenn ich ein Item mit einem nicht existierenden Index requeste
    }

    @Test
    public void unselectGroup() {
        ExpenseObject expectedGroup = getSimpleExpense(498);
        when(mListAdapter.getGroup(anyInt())).thenReturn(expectedGroup);

        mItemSelector.selectItem(1, -1);
        assertEquals(1, mItemSelector.getSelectedGroupsCount());

        mItemSelector.unselectItem(1, -1);
        assertEquals(0, mItemSelector.getSelectedItems().size());
    }

    @Test
    public void testIsItemSelectedWithGroup() {
        ExpenseObject expectedGroup = getSimpleExpense(498);
        when(mListAdapter.getGroup(1)).thenReturn(expectedGroup);

        mItemSelector.selectItem(1, -1);

        assertTrue(mItemSelector.isItemSelected(1, -1));
    }


    @Test
    public void selectChild() {
        ExpenseObject expectedParent = getSimpleExpense(123);
        ExpenseObject expectedChild = getSimpleExpense(456);
        expectedParent.addChild(expectedChild);
        when(mListAdapter.getGroup(anyInt())).thenReturn(expectedParent);
        when(mListAdapter.getChild(anyInt(), anyInt())).thenReturn(expectedChild);

        boolean selectionStatus = mItemSelector.selectItem(1, 1);

        assertTrue("Auswählen der Buchung wurde nicht bestätigt", selectionStatus);

        assertEquals("Ausgewählter Parent stimmt nicht mit dem erwarteten überein.", expectedParent, mItemSelector.getSelectedItems().get(0).getParent());
        assertEquals("Ausgewähltes Kind stimmtn icht mit dem erwarteten überein.", expectedChild, mItemSelector.getSelectedItems().get(0).getItem());
    }

    @Test
    public void selectChildWithAlreadySelectedParentShouldFail() {
        ExpandableListItemSelector itemSelector = mock(ExpandableListItemSelector.class);
        when(itemSelector.getSelectedGroupsCount()).thenReturn(1);

        boolean selectionStatus = itemSelector.selectItem(1, 1);

        assertFalse("KindBuchung konnte ausgewählt werden, obwohl eine Group bereits ausgewählt ist.", selectionStatus);
    }

    @Test
    public void testSelectChildWithNotExistingChildPosition() {
        // TODO: Was soll passieren wenn ich ein Item mit einem nicht existierenden Index requeste
    }

    @Test
    public void testSelectChildWithParentThatHasNoChildren() {
        // TODO: Was soll passieren wenn ich ein Item mit einem nicht existierenden Index requeste
    }

    @Test
    public void unselectChild() {
        when(mListAdapter.getGroup(anyInt())).thenReturn(getSimpleExpense(123));
        when(mListAdapter.getChild(anyInt(), anyInt())).thenReturn(getSimpleExpense(456));

        mItemSelector.selectItem(1, 1);
        assertEquals(1, mItemSelector.getSelectedChildrenCount());

        mItemSelector.unselectItem(1, 1);
        assertEquals(0, mItemSelector.getSelectedItems().size());
    }

    @Test
    public void testIsItemSelectedWithChild() {
        when(mListAdapter.getGroup(1)).thenReturn(getSimpleExpense(123));
        when(mListAdapter.getChild(1, 2)).thenReturn(getSimpleExpense(456));

        mItemSelector.selectItem(1, 2);

        assertTrue(mItemSelector.isItemSelected(1, 2));
    }


    @Test
    public void selectParent() {
        ExpenseObject expectedParent = getSimpleExpense(75);
        when(mListAdapter.getGroup(anyInt())).thenReturn(expectedParent);

        ExpenseObject expectedChild1 = getSimpleExpense(1);
        ExpenseObject expectedChild2 = getSimpleExpense(2);
        ExpenseObject expectedChild3 = getSimpleExpense(3);
        when(mListAdapter.getChild(anyInt(), anyInt())).thenReturn(expectedChild1, expectedChild2, expectedChild3);

        when(mListAdapter.getChildrenCount(anyInt())).thenReturn(3);

        boolean selectionStatus = mItemSelector.selectItem(1, -1);

        assertTrue(selectionStatus);

        assertEquals("Die ParentExpense der ersten KindBuchung stimmt nicht überein", expectedParent, mItemSelector.getSelectedItems().get(0).getParent());
        assertEquals("KindBuchung 1 stimmt nicht überein", expectedChild1, mItemSelector.getSelectedItems().get(0).getItem());

        assertEquals("Die ParentExpense der zweiten KindBuchung stimmt nicht überein", expectedParent, mItemSelector.getSelectedItems().get(1).getParent());
        assertEquals("KindBuchung 2 stimmt nicht überein", expectedChild2, mItemSelector.getSelectedItems().get(1).getItem());

        assertEquals("Die ParentExpense der dritten KindBuchung stimmt nicht überein", expectedParent, mItemSelector.getSelectedItems().get(2).getParent());
        assertEquals("KindBuchung 3 stimmt nicht überein", expectedChild3, mItemSelector.getSelectedItems().get(2).getItem());

        assertEquals(3, mItemSelector.getSelectedItems().size());
    }

    @Test
    public void unselectParent() {
        ExpenseObject expectedParent = getSimpleExpense(75);
        when(mListAdapter.getGroup(anyInt())).thenReturn(expectedParent);

        ExpenseObject expectedChild1 = getSimpleExpense(1);
        ExpenseObject expectedChild2 = getSimpleExpense(2);
        ExpenseObject expectedChild3 = getSimpleExpense(3);
        when(mListAdapter.getChild(anyInt(), anyInt())).thenReturn(expectedChild1, expectedChild2, expectedChild3);

        when(mListAdapter.getChildrenCount(anyInt())).thenReturn(3);

        mItemSelector.selectItem(1, -1);
        assertEquals(3, mItemSelector.getSelectedItemCount());

        mItemSelector.unselectItem(1, -1);
        assertEquals(0, mItemSelector.getSelectedItemCount());
    }

    @Test
    public void testIsItemSelectedWithParent() {
        ExpenseObject expectedParent = getSimpleExpense(75);
        when(mListAdapter.getGroup(1)).thenReturn(expectedParent);

        ExpenseObject expectedChild1 = getSimpleExpense(1);
        ExpenseObject expectedChild2 = getSimpleExpense(2);
        ExpenseObject expectedChild3 = getSimpleExpense(3);
        when(mListAdapter.getChild(anyInt(), anyInt())).thenReturn(expectedChild1, expectedChild2, expectedChild3);

        when(mListAdapter.getChildrenCount(1)).thenReturn(3);

        mItemSelector.selectItem(1, -1);

        assertTrue(mItemSelector.isItemSelected(1, -1));
    }


    private ExpenseObject getSimpleExpense(long index) {
        return new ExpenseObject(
                index,
                "Ausgabe",
                32,
                mock(Calendar.class),
                false,
                mock(Category.class),
                "",
                -1,
                ExpenseObject.EXPENSE_TYPES.NORMAL_EXPENSE,
                new ArrayList<Tag>(),
                new ArrayList<ExpenseObject>(),
                mock(Currency.class)
        );
    }
}
