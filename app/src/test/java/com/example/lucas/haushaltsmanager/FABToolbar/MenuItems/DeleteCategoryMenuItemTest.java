package com.example.lucas.haushaltsmanager.FABToolbar.MenuItems;

import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.ActionKey;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.DeleteCategoryMenuItem;
import com.example.lucas.haushaltsmanager.R;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeleteCategoryMenuItemTest {
    private DeleteCategoryMenuItem deleteItem;

    @Before
    public void setUp() {
        deleteItem = new DeleteCategoryMenuItem(null);
    }

    @Test
    public void menuItemHasCorrectActionKey() {
        assertEquals(
                new ActionKey(DeleteCategoryMenuItem.ACTION_KEY),
                deleteItem.getActionKey()
        );
    }

    @Test
    public void menuItemHasCorrectHint() {
        assertEquals(
                R.string.fab_menu_item_delete_category_hint,
                deleteItem.getHintRes()
        );
    }

    @Test
    public void menuItemHasCorrectIcon() {
        assertEquals(
                R.drawable.ic_delete_white,
                deleteItem.getIconRes()
        );
    }

    @Test
    public void menuItemHasCorrectTitle() {
        assertEquals(
                "",
                deleteItem.getTitle()
        );
    }

    // TODO: Can I somehow check the "handle()" method
}
