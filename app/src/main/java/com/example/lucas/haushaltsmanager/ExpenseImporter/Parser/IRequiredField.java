package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser;

import androidx.annotation.StringRes;

public interface IRequiredField {
    @StringRes
    int getTranslationKey();

    // TODO: Sollte ich noch eine Methode einfügen mit der ein String überprüft werden kann,
    //  ob dieser auch den Anforderungen des RequiredFields entspricht?
    //  .
    //  z.B.: boolean isValidInput(String input);
}
