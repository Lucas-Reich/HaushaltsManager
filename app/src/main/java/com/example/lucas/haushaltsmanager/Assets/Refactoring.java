package com.example.lucas.haushaltsmanager.Assets;

import com.example.lucas.changelogdialog.ChangelogItem;

public class Refactoring implements ChangelogItem {
    private static final String REFACTORING = "Refactoring";

    private final String mDescription;

    public Refactoring(String additionalInformation) {
        mDescription = additionalInformation != null ? additionalInformation : "";
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public String getType() {
        return REFACTORING;
    }
}
