package com.example.lucas.haushaltsmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.Toast;

public class MainActivityTab extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    FloatingActionButton fab, fabDelete, fabCombine;
    Animation test, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false, combOpen = false, delOpen = false, fabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO die shared preferences in den dafür vorgehenen bereich bringen
        SharedPreferences settings = getSharedPreferences("UserSettings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("activeAccount", 1);
        editor.putString("mainCurrency", "€");

        editor.commit();


        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_tab);
        setContentView(R.layout.tab_main_mit_nav_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the reportAdapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections reportAdapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_2);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.

                switch (item.getItemId()) {

                    case R.id.desktop:

                        Intent testIntent = new Intent(MainActivityTab.this, PieChartTest.class);
                        MainActivityTab.this.startActivity(testIntent);
                        break;
                    case R.id.categories:

                        Intent categoryIntent = new Intent(MainActivityTab.this, Categories.class);
                        MainActivityTab.this.startActivity(categoryIntent);
                        break;
                    case R.id.templates:

                        Intent templateIntent = new Intent(MainActivityTab.this, BookingTemplates.class);
                        MainActivityTab.this.startActivity(templateIntent);
                        break;
                    case R.id.no_category:
                        //do smth
                        break;
                    case R.id.course:
                        //do smth
                        break;
                    case R.id.standing_orders:

                        Intent recurringIntent = new Intent(MainActivityTab.this, RecurringBookings.class);
                        MainActivityTab.this.startActivity(recurringIntent);
                        break;
                    case R.id.transfers:
                        //do smth
                        break;
                    case R.id.backup:
                        //do smth
                        break;
                    case R.id.import_export:
                        //do smth
                        break;
                    case R.id.store:
                        //do smth
                        break;
                    case R.id.preferences:
                        //do smth
                        break;
                    case R.id.follow_us:
                        //do smth
                        break;
                    case R.id.rate_the_app:

                        Intent testListViewIntent = new Intent(MainActivityTab.this, ExpandableListViewTest.class);
                        MainActivityTab.this.startActivity(testListViewIntent);
                        break;
                    case R.id.about:
                        //do smth
                        break;
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_2);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_2);
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_settings:

                Toast.makeText(this, "replace", Toast.LENGTH_SHORT).show();
                break;

            case R.id.choose_account:

                ChooseAccountsDialogFragment test = new ChooseAccountsDialogFragment();
                test.show(getFragmentManager(), "test");
                break;

            default:

                Toast.makeText(this, "This should never happen!", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:

                    return new TabOneBookingsVer2();
                case 1:

                    return new TabTwoMonthlyReports();
                case 2:

                    return new TabThree();
                default:

                    return null;
            }
        }

        @Override
        public int getCount() {

            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {

                case 0:

                    return getString(R.string.tab_one_title);
                case 1:

                    return getString(R.string.tab_two_title);
                case 2:

                    return getString(R.string.tab_three_title);
            }
            return null;
        }
    }
}
