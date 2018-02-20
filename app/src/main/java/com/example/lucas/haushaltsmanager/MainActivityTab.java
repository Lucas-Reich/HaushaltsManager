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
import android.view.View;
import android.widget.Toast;

public class MainActivityTab extends AppCompatActivity implements ChooseAccountsDialogFragment.OnSelectedAccount {

    private String TAG = MainActivityTab.class.getSimpleName();
    TabLayout mTabLayout;
    int mTest;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO die shared preferences in den dafür vorgehenen bereich bringen
        SharedPreferences settings = getSharedPreferences("UserSettings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("activeAccount", 1);
        editor.putString("mainCurrency", "€");
        editor.putLong("mainCurrencyIndex", 32);

        editor.apply();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_main_mit_nav_drawer);


        FloatingActionButton testService = (FloatingActionButton) findViewById(R.id.service_fab);
        testService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent serviceIntent = new Intent(getBaseContext(), ExchangeRateService.class);
                startService(serviceIntent);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the mReportAdapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections mReportAdapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);


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
                        //do smth
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
                    case R.id.course://Verlauf
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
                    case R.id.about:

                        Intent testPieIntent = new Intent(MainActivityTab.this, TestPieChart.class);
                        MainActivityTab.this.startActivity(testPieIntent);
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

                ChooseAccountsDialogFragment chooseAccountFragment = new ChooseAccountsDialogFragment();
                chooseAccountFragment.show(getFragmentManager(), "alterVisibleAccounts");
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

                    return new TabOneBookings();
                case 1:

                    return new TabTwoMonthlyReports();
                case 2:

                    return new TabThree();
            }
            return null;
        }

        @Override
        public int getCount() {

            return 3;//show 3 pages
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

    /**
     * Anleitung von: https://stackoverflow.com/questions/27204409/android-calling-a-function-inside-a-fragment-from-a-custom-action-bar
     * <p>
     * Der User hat im ChooseAccountDialogFragment ein Konto angewählt.
     *
     * @param accountId Id des angewählten Kontos.
     * @param isChecked Status des Kontos (aktiv - true oder inaktiv - false)
     */
    public void onAccountSelected(long accountId, boolean isChecked) {

        int visibleTabPosition = mTabLayout.getSelectedTabPosition();

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + visibleTabPosition);

        if (fragment != null) {

            switch (visibleTabPosition) {

                case 0:
                    ((TabOneBookings) fragment).refreshListOnAccountSelected(accountId, isChecked);
                    break;
                case 1:
                    ((TabTwoMonthlyReports) fragment).refreshListOnAccountSelected(accountId, isChecked);
                    break;
                case 3:
                    //todo ((TabThree) fragment).refreshListOnAccountSelected(accountId, isChecked);
                    break;
            }
        }
    }
}
