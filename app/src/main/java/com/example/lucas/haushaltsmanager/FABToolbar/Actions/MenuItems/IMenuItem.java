package com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.example.lucas.haushaltsmanager.FABToolbar.Actions.ActionPayload;
import com.example.lucas.haushaltsmanager.FABToolbar.Actions.MenuItems.ActionKey.IActionKey;

/**
 * Dieses Interface dient dazu die ActionHandler für die FABToolbar zu definieren.
 * Klassen die dieses Interface implementieren können der FABToolbar hinzugefügt werden
 */
public interface IMenuItem {
    /**
     * Das Icon, welches für dieses MenuItem angezeigt wird.
     *
     * @return Icon des MenuItem
     */
    @DrawableRes
    int getIconRes();

    /**
     * Der Anzeigename für dieses MenuItem. Er wird unter dem Icon angezeigt.
     *
     * @return Anzeigenamen des MenuItem
     */
    String getTitle();

    /**
     * Ein Hinweis, welcher für Zugänglichkeitsapplikationen genutzt wird.
     *
     * @return Hint of MenuItem
     */
    @StringRes
    int getHintRes();

    /**
     * Key, welcher diese konkrete MenuItem identifiziert.
     * Dieses Key muss, verglichen mit den anderen MenuItems, einzigartig sein.
     *
     * @return ActionKey des MenuItem
     */
    IActionKey getActionKey();

    /**
     * In dieser Methode wird das Verhalten des MenuItem definiert.
     * Sie wird aufgerufen, sobald der User auf das MenuItem klickt.
     *
     * @param actionPayload Payload, welche die zu verarbeitenden Daten enthält
     * @param context       Kontext
     */
    void handleClick(ActionPayload actionPayload, Context context);
}
