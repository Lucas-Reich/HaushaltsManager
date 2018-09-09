package com.example.lucas.haushaltsmanager.Assets;

import com.lucas.changelogdialog.ChangelogItem;
import com.lucas.changelogdialog.Feature;
import com.lucas.changelogdialog.Release;

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
public class ReleaseHistory implements com.lucas.changelogdialog.ReleaseHistory {

    @Override
    public List<com.lucas.changelogdialog.Release> getHistory() {
        List<Release> releases = new ArrayList<>();
        releases.add(getRelease100());
        releases.add(getRelease200());
        releases.add(getRelease300());
        releases.add(getRelease400());
        releases.add(getRelease500());

        return releases;
    }

    /**
     * Methode welche die Änderungen der Version 1.0.0 zurückgibt.
     *
     * @return Version 1.0.0
     */
    private Release getRelease100() {
        //zu diesem Release gibt es keine Information, da dieser geschah, bevor ich den ChangelogDialog implementiert habe

        return new Release(
                1,
                null,
                null,
                Calendar.getInstance(),
                "",
                new ArrayList<ChangelogItem>()
        );
    }

    /**
     * Methode welche die Änderungen der Version 2.0.0 zurückgibt.
     *
     * @return Version 2.0.0
     */
    private Release getRelease200() {
        //zu diesem Release gibt es keine Information, da dieser geschah, bevor ich den ChangelogDialog implementiert habe

        return new Release(
                2,
                null,
                null,
                Calendar.getInstance(),
                "",
                new ArrayList<ChangelogItem>()
        );
    }

    /**
     * Methode welche die Änderungen der Version 3.0.0 zurückgibt.
     *
     * @return Version 3.0.0
     */
    private Release getRelease300() {
        //zu diesem Release gibt es keine Information, da dieser geschah, bevor ich den ChangelogDialog implementiert habe

        return new Release(
                3,
                null,
                null,
                Calendar.getInstance(),
                "",
                new ArrayList<ChangelogItem>()
        );
    }

    /**
     * Methode welche die Änderungen der Version 4.0.0 zurückgibt.
     *
     * @return Version 4.0.0
     */
    private Release getRelease400() {
        //zu diesem Release gibt es keine Information, da dieser geschah, bevor ich den ChangelogDialog implementiert habe

        return new Release(
                4,
                null,
                null,
                Calendar.getInstance(),
                "",
                new ArrayList<ChangelogItem>()
        );
    }

    /**
     * Methode welche die Änderungen der Version 5.0.0 zurückgibt.
     *
     * @return Version 5.0.0
     */
    private Release getRelease500() {
        List<ChangelogItem> items = new ArrayList<>();
        items.add(new Feature("Über Uns Activity hinzugefügt"));

        return new Release(
                4,
                null,
                null,
                Calendar.getInstance(),
                "Tolle neuigkeiten. Du kannst dir nun Information über uns und die App angucken. Besuche dafür einfach die \"Über Uns\" Seite in den Einstellungen.",
                items
        );
    }
}
