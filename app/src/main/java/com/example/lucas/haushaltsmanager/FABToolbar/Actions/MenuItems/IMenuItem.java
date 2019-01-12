package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;

import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/**
 * Dieses Interface dient dazu Die action handler für die FABToolbar zu definieren.
 * Klassen die dieses Interface implementieren können der FABToolbar hinzugefügt werden
 */
public interface IMenuItem {
    /**
     * The icon which will be shown for this MenuItem.
     *
     * @return Icon of MenuItem
     */
    @DrawableRes
    int getIconRes();

    /**
     * The title is the visible name of the MenuItem.
     *
     * @return Title of MenuItem
     */
    String getTitle();

    /**
     * The hint will be used for accessibility applications.
     *
     * @return Hint of MenuItem
     */
    @StringRes
    int getHintRes();

    /**
     * Key that identifies the concrete MenuItem.
     * This key should be unique compared to other MenuItems.
     *
     * @return ActionKey of MenuItem
     */
    IActionKey getActionKey();

    /**
     * This Method defines the behaviour of the MenuItem when its clicked.
     *
     * @param actionPayload Payload which contains Data to process
     * @param context       Context
     */
    void handleClick(ActionPayload actionPayload, Context context);
}
