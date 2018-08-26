package com.example.lucas.haushaltsmanager.Dialogs.ChangelogDialog;

import android.support.annotation.Nullable;

public class Bug {
    private String mIdentifier;
    private String mAdditionalInformation;

    public Bug(String identifier, @Nullable String additionalInformation) {
        mIdentifier = identifier;
        mAdditionalInformation = additionalInformation != null ? additionalInformation : "";
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public String getAdditionalInformation() {
        return mAdditionalInformation;
    }
}
