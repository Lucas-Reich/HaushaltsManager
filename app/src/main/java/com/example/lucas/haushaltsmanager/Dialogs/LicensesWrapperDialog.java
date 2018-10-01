package com.example.lucas.haushaltsmanager.Dialogs;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Assets.MozillaPublicLicenseV2;
import com.example.lucas.haushaltsmanager.R;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class LicensesWrapperDialog {

    private Context mContext;

    public LicensesWrapperDialog(Context context) {

        mContext = context;
    }

    public LicensesDialog create() {
        LicensesDialog.Builder builder = new LicensesDialog.Builder(mContext);
        builder.setNotices(getNotices());
        builder.setTitle(R.string.licenses_title);

        return builder.build();
    }

    private Notices getNotices() {
        Notices notices = new Notices();
        notices.addNotice(getLicensesDialogNotice());
        notices.addNotice(getStorageChooserNotice());
        notices.addNotice(getAndroidColorPickerNotice());
        notices.addNotice(getAndroidChangelogDialogNotice());
        notices.addNotice(getWorkManagerNotice());
        notices.addNotice(getFABToolbarNotice());

        return notices;
    }

    /**
     * Methode um den Lizenzen Eintrag für das OpenSource Projekt FABToolbar zu bekommen.
     * Quelle: https://github.com/fafaldo/FABToolbar
     *
     * @return FABToolbar Notiz Eintrag
     */
    private Notice getFABToolbarNotice() {
        return new Notice(
                "FABToolbar",
                "https://github.com/fafaldo/FABToolbar",
                "Copyright (c) 2015 Rafał Tułaza",
                new MITLicense()
        );
    }

    /**
     * Methode um den Lizenzen Eintrag für das OpenSource Projekt Andorid-WorkManager zu bekommen.
     * Quelle: https://github.com/googlecodelabs/android-workmanager
     *
     * @return Android-WorkManager Notiz Eintrag
     */
    private Notice getWorkManagerNotice() {
        return new Notice(
                "Android-WorkManager",
                "https://github.com/googlecodelabs/android-workmanager",
                "Copyright 2018 Google, Inc.",
                new ApacheSoftwareLicense20()
        );
    }

    /**
     * Methode um den Lizenzen Eintrag für das OpenSource Projekt LicensesDialog zu bekommen.
     * Quelle: https://github.com/PSDev/LicensesDialog
     *
     * @return LicensesDialog Notiz Eintrag
     */
    private Notice getLicensesDialogNotice() {
        return new Notice(
                "LicensesDialog",
                "http://psdev.de",
                "Copyright 2013 Philip Schiffer <admin@psdev.de>",
                new ApacheSoftwareLicense20()
        );
    }

    /**
     * Methode um den Lizenz Eintrag für das OpenSource Projekt Storage Choose zu bekommen.
     * Quelle: https://github.com/codekidX/storage-chooser
     *
     * @return Storage Choose Notiz Eintrag
     */
    private Notice getStorageChooserNotice() {
        return new Notice(
                "Storage Chooser",
                "https://github.com/codekidX/storage-chooser",
                "Copyright 2013 Chiral Code",
                new MozillaPublicLicenseV2()
        );
    }

    /**
     * Methode um den Lizenz Eitnrag für das OpenSource Projekt Android-Color-Picker zu bekommen.
     * Quelle: https://github.com/chiralcode/Android-Color-Picker
     *
     * @return Android-Color-Picker Notiz Eintrag
     */
    private Notice getAndroidColorPickerNotice() {
        return new Notice(
                "Android-Color-Picker",
                "https://github.com/chiralcode/Android-Color-Picker",
                "Copyright 2013 Piotr Adamus",
                new ApacheSoftwareLicense20()
        );
    }

    /**
     * Methode um den Lizenzeintrag für das OpenSource Projekt Android-ChangelogDialog zu bekommen.
     * Quelle: https://github.com/LabberToasT/Android-ChangelogDialog
     *
     * @return Android-ChangelogDialog Notiz Eintrag
     */
    private Notice getAndroidChangelogDialogNotice() {
        return new Notice(
                "Android-ChangelogDialog",
                "https://github.com/LabberToasT/Android-ChangelogDialog",
                "Copyright 2018 Lucas Reich",
                new MITLicense()
        );
    }
}
