package com.example.lucas.haushaltsmanager.Assets;

import com.example.lucas.haushaltsmanager.Dialogs.ChangelogDialog.Bug;
import com.example.lucas.haushaltsmanager.Dialogs.ChangelogDialog.Feature;
import com.example.lucas.haushaltsmanager.Dialogs.ChangelogDialog.Release;

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
public class ReleaseHistory {

    public List<Release> getReleaseHistory() {
        List<Release> releases = new ArrayList<>();
        releases.add(getRelease100());

        return releases;
    }

    /**
     * Methode welche die Änderungen der Version 1.0.0 zurückgibt.
     *
     * @return Version 1.0.0
     */
    private Release getRelease100() {
        List<Bug> bugs = new ArrayList<>();

        List<Feature> features = new ArrayList<>();
        features.add(new Feature(
                "Created About Us Page",
                "You can now find further Information about us under the 'About Us' Entry in the Drawer Menu"
        ));

        return new Release(
                1,
                null,
                null,
                Calendar.getInstance(),
                features,
                bugs,
                "Hier stehen Information die als Eigenständiger Text vor den ganzen Änderungen angezeigt werden sollen, diese Information werden auch in einer Seperaten Box angezeigt"
        );
    }
}
