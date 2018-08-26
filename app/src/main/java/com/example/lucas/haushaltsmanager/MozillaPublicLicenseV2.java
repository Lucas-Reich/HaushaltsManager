package com.example.lucas.haushaltsmanager;

import android.content.Context;

import de.psdev.licensesdialog.licenses.License;

public class MozillaPublicLicenseV2 extends License {
    @Override
    public String getName() {
        return "Mozilla Software License v2";
    }

    @Override
    public String readSummaryTextFromResources(Context context) {
        return context.getResources().getString(R.string.mozilla_software_license_summary);
    }

    @Override
    public String readFullTextFromResources(Context context) {
        return context.getResources().getString(R.string.mozilla_software_license_full);
    }

    @Override
    public String getVersion() {
        return "2.0";
    }

    @Override
    public String getUrl() {
        return "https://www.mozilla.org/media/MPL/2.0/index.815ca599c9df.txt";
    }
}
