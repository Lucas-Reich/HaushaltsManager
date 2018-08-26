package com.example.lucas.haushaltsmanager.Dialogs;

import android.content.Context;

import com.example.lucas.haushaltsmanager.MozillaPublicLicenseV2;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
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

        return builder.build();
    }

    private Notices getNotices() {
        Notices notices = new Notices();
        notices.addNotice(getLicensesDialogNotice());
        notices.addNotice(getStorageChooserNotice());
        notices.addNotice(getAndroidColorPickerNotice());

        return notices;
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
}
