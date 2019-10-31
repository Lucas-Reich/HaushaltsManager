package com.example.lucas.haushaltsmanager.Assets;

import android.support.annotation.StringRes;

import com.example.lucas.changelogdialog.Bug;
import com.example.lucas.changelogdialog.ChangelogItem;
import com.example.lucas.changelogdialog.Feature;
import com.example.lucas.changelogdialog.Improvement;
import com.example.lucas.changelogdialog.Release;
import com.example.lucas.changelogdialog.ReleaseHistory;
import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.R;

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
    public List<Release> getHistory() {
        List<Release> releases = new ArrayList<>();
        releases.add(getRelease100());
        releases.add(getRelease110());
        releases.add(getRelease120());
        releases.add(getRelease130());
        releases.add(getRelease131());
        releases.add(getRelease132());
        releases.add(getRelease133());
        releases.add(getRelease14());

        return releases;
    }

    /**
     * Methode, welche die Änderungen der Version 1.0.0 zurückgibt.
     *
     * @return Version 1.0.0
     */
    private Release getRelease100() {
        return new Release(
                1,
                null,
                null,
                getDate(1, Calendar.JANUARY, 2019),
                getString(R.string.release_notes_100),
                new ArrayList<ChangelogItem>()
        );
    }

    /**
     * Methode, welche die Änderungen der Version 1.1.0 zurückgibt.
     *
     * @return Version 1.1.0
     */
    private Release getRelease110() {
        List<ChangelogItem> changelog = new ArrayList<>();
        changelog.add(new Feature(getString(R.string.release_notes_110_tab_one_feature_recycler_view)));
        changelog.add(new Feature(getString(R.string.release_notes_110_tab_one_feature_animations)));
        changelog.add(new Improvement(getString(R.string.release_notes_110_code_improvements)));

        return new Release(
                1,
                1,
                null,
                getDate(1, Calendar.MARCH, 2019),
                getString(R.string.release_notes_110),
                changelog
        );
    }

    /**
     * Methode, welche die Änderungen der Version 1.2.0 zurückgibt.
     *
     * @return Version 1.2.0
     */
    private Release getRelease120() {
        List<ChangelogItem> changelog = new ArrayList<>();
        changelog.add(new Bug(getString(R.string.release_notes_120_correct_scheduling)));
        changelog.add(new Improvement(getString(R.string.release_notes_120_improved_scheduling))); // RecurringBookings lassen sich nun einfacher planen


        return new Release(
                1,
                2,
                0,
                getDate(1, Calendar.APRIL, 2019),
                getString(R.string.release_notes_120),
                changelog
        );
    }

    /**
     * Methode, welche die Änderungen der Version 1.3.0 zurückgibt.
     *
     * @return Version 1.3.0
     */
    private Release getRelease130() {
        List<ChangelogItem> changelog = new ArrayList<>();
        changelog.add(new Improvement(getString(R.string.release_notes_130_list_improvements)));
        changelog.add(new Improvement(getString(R.string.release_notes_130_tab_two_space)));

        return new Release(
                1,
                3,
                0,
                getDate(30, Calendar.JUNE, 2019),
                getString(R.string.release_notes_130),
                changelog
        );
    }

    /**
     * Methode, welche die Änderungen der Version 1.3.1 zurückgibt.
     *
     * @return Version 1.3.1
     */
    private Release getRelease131() {
        List<ChangelogItem> changelog = new ArrayList<>();
        changelog.add(new Bug(getString(R.string.release_notes_131_cent_fix)));

        return new Release(
                1,
                3,
                1,
                getDate(25, Calendar.OCTOBER, 2019),
                "",
                changelog
        );
    }

    /**
     * Methode, welche die Änderungen der Version 1.3.2 zurückgibt.
     *
     * @return Version 1.3.2
     */
    private Release getRelease132() {
        List<ChangelogItem> changelog = new ArrayList<>();
        changelog.add(new Improvement(getString(R.string.release_notes_132_fixed_strings)));

        return new Release(
                1,
                3,
                2,
                getDate(25, Calendar.OCTOBER, 2019),
                "",
                changelog
        );
    }

    /**
     * Methode, welche die Änderungen der Version 1.3.3 zurückgibt.
     *
     * @return Version 1.3.3
     */
    private Release getRelease133() {
        List<ChangelogItem> changelog = new ArrayList<>();
        changelog.add(new Bug(getString(R.string.release_notes_133_infinite_scroll_fix)));

        return new Release(
                1,
                3,
                3,
                getDate(25, Calendar.OCTOBER, 2019),
                "",
                changelog
        );
    }

    /**
     * Methode, welche die Änderungen der Version 1.4 zurückgibt.
     *
     * @return Version 2.4
     */
    private Release getRelease14() {
        List<ChangelogItem> changelog = new ArrayList<>();
        changelog.add(new Feature(getString(R.string.release_notes_14_data_importer)));

        return new Release(
                1,
                4,
                0,
                getDate(1, Calendar.NOVEMBER, 2019),
                getString(R.string.release_notes_14),
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
