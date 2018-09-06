package com.example.lucas.haushaltsmanager.Dialogs.ChangelogDialog;

public class Feature implements ChangelogItem {
    private static final String FEATURE = "Feature";

    private String mDescription;

    public Feature(String additionalInformation) {
        mDescription = additionalInformation;
    }

    @Override
    public String getDescription() {
        return mDescription;
    }

    @Override
    public String getType() {
        return FEATURE;
    }
}
