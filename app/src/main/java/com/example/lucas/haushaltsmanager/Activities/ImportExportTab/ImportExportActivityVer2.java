package com.example.lucas.haushaltsmanager.Activities.ImportExportTab;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.example.lucas.haushaltsmanager.Dialogs.DirectoryPickerDialog;
import com.example.lucas.haushaltsmanager.R;

import java.io.File;

public class ImportExportActivityVer2 extends AppCompatActivity implements DirectoryPickerDialog.OnDirectorySelected {
    private static final String TAG = ImportExportActivityVer2.class.getSimpleName();

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ImageButton mBackArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export_ver2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    return new TabImport();
                case 1:
                    return new TabExport();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getString(R.string.tab_import_title);
                case 1:
                    return getString(R.string.tab_export_title);
                default:
                    return "";
            }
        }
    }

    /**
     * Callbackmethode die aufgerufen wird, wenn der User ein neues Verzeichniss ausgewählt hat.
     *
     * @param file Pfad vom Verzeichniss
     * @param tag  Mitgesendetes Tag
     */
    @Override
    public void onDirectorySelected(File file, String tag) {
        int visibleTabPosition = mTabLayout.getSelectedTabPosition();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + visibleTabPosition);

        if (fragment != null) {
            switch (visibleTabPosition) {
                case 0:
                    ((TabImport) fragment).onDirectorySelected(file);
                default:
                    break;
            }
        }
    }
}
