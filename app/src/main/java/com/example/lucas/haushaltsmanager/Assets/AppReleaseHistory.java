package com.example.lucas.haushaltsmanager.Assets;

import android.support.annotation.StringRes;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.R;
import com.lucas.changelogdialog.Bug;
import com.lucas.changelogdialog.ChangelogItem;
import com.lucas.changelogdialog.Release;
import com.lucas.changelogdialog.ReleaseHistory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// Ich kann die History auch in einem XML file speichert
// Quelle: https://android-arsenal.com/details/1/81
//Then, you need a XML file with change log in res/raw folder. It automatically searches for res/raw/changelog.xml but you can customize filename.
//
//        <?xml version="1.0" encoding="utf-8"?>
//        <changelog bulletedList="true">
//
//        <changelogversion versionName="1.0" changeDate="Aug 26,2013">
//        <changelogtext>Initial release.</changelogtext>
//        </changelogversion>
//
//        <changelogversion versionName="0.9" changeDate="Aug 11,2013">
//        <changelogtext>[b]New![/b] Add new attrs to customize header and row layout</changelogtext>
//        <changelogtext>Fixed log while parsing </changelogtext>
//        <changelogtext>Add support for html markup</changelogtext>
//        <changelogtext>Add bullet point in </changelogtext>
//        <changelogtext>Support for customized xml filename</changelogtext>
//        </changelogversion>
//
//        </changelog>
//        Last, if you would like a multi language changelog, you just have to put the translated files changelog.xml in the appropriate folders res/raw-xx/.
public class AppReleaseHistory implements ReleaseHistory {

    @Override
    public List<com.lucas.changelogdialog.Release> getHistory() {
        List<Release> releases = new ArrayList<>();
        releases.add(getRealRelease100());
        releases.add(getRealRelease601());
        releases.add(getRelease602());
        releases.add(getRelease603());

        return releases;
    }

    /**
     * Methode welche die Änderungen der Version 1.0.0 zurückgibt.
     *
     * @return Version 1.0.0
     */
    private Release getRealRelease100() {
        return new Release(
                1,
                null,
                null,
                getDate(1, 1, 2019),
                getString(R.string.release_notes_100),
                new ArrayList<ChangelogItem>()
        );
    }

    private Release getRealRelease601() {
        List<ChangelogItem> changelog = new ArrayList<>();
        changelog.add(new Bug("Fehler behoben, welcher zum Absturz der App beim öffnen fürte"));

        return new Release(
                6,
                null,
                1,
                getDate(25, 12, 2018),
                "Kleinere Fehlerbehebungen",
                changelog
        );
    }

    private Release getRelease602() {
        List<ChangelogItem> changelog = new ArrayList<>();
        changelog.add(new Bug("Fehler behoben, welcher die App zum Absturz gebracht hat, wenn das letzte Konto gelöscht wurde"));

        return new Release(
                6,
                null,
                2,
                getDate(26, 12, 2018),
                null,
                changelog
        );
    }

    private Release getRelease603() {
        List<ChangelogItem> changelog = new ArrayList<>();
        changelog.add(new Bug("Fehler behoben, welcher die App aucf alten Geräten zum abstürzen bringt, wenn ein neues Backup erstellt wurde"));

        return new Release(
                6,
                null,
                3,
                getDate(26, 12, 2018),
                null,
                changelog
        );
    }

    private String getString(@StringRes int stringId) {
        return app.getContext().getString(stringId);
    }

    private Calendar getDate(int day, int month, int year) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day, 0, 0, 0);

        return date;
    }
}
