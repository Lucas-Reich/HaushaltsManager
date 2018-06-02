package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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

import com.example.lucas.haushaltsmanager.Activities.CategoryListActivity;
import com.example.lucas.haushaltsmanager.Activities.CourseActivity;
import com.example.lucas.haushaltsmanager.Activities.CreateBackupActivity;
import com.example.lucas.haushaltsmanager.Activities.ImportExportActivity;
import com.example.lucas.haushaltsmanager.Activities.RecurringBookingsActivity;
import com.example.lucas.haushaltsmanager.Activities.TestActivity;
import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ChangeAccounts.ChooseAccountsDialogFragment;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.MockDataCreator;
import com.example.lucas.haushaltsmanager.MyAlarmReceiver;
import com.example.lucas.haushaltsmanager.R;

public class TabParentActivity extends AppCompatActivity implements ChooseAccountsDialogFragment.OnSelectedAccount, BasicTextInputDialog.BasicDialogCommunicator {
    private static final String TAG = TabParentActivity.class.getSimpleName();

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_main_mit_nav_drawer);

        //todo in die user einstellungen verlagern
        setSharedPreferencesProperties();

        //Methode die jeden Tag einmal den BackupService laufen lässt
        scheduleBackupServiceAlarm();


        //TODO den test button removen
        FloatingActionButton testService = (FloatingActionButton) findViewById(R.id.service_fab);
        testService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MockDataCreator test = new MockDataCreator(TabParentActivity.this);
                test.createBookings(100);
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

                    case R.id.categories:

                        Intent categoryIntent = new Intent(TabParentActivity.this, CategoryListActivity.class);
                        TabParentActivity.this.startActivity(categoryIntent);
                        break;
                    case R.id.course:

                        Intent courseIntent = new Intent(TabParentActivity.this, CourseActivity.class);
                        TabParentActivity.this.startActivity(courseIntent);
                        break;
                    case R.id.standing_orders:

                        Intent recurringBookingIntent = new Intent(TabParentActivity.this, RecurringBookingsActivity.class);
                        TabParentActivity.this.startActivity(recurringBookingIntent);
                        break;
                    case R.id.backup:

                        Intent backupIntent = new Intent(TabParentActivity.this, CreateBackupActivity.class);
                        TabParentActivity.this.startActivity(backupIntent);
                        break;
                    case R.id.import_export:

                        Intent importExportIntent = new Intent(TabParentActivity.this, ImportExportActivity.class);
                        TabParentActivity.this.startActivity(importExportIntent);
                        break;
                    case R.id.preferences:

                        //todo show preferences activity
                        break;
                    case R.id.about:

                        Intent testPieIntent = new Intent(TabParentActivity.this, TestActivity.class);//todo zeige die AboutActivity
                        TabParentActivity.this.startActivity(testPieIntent);
                        break;
                    default:

                        Toast.makeText(TabParentActivity.this, "Ups, da hast du wohl etwas entdeckt was du eigentlich noch gar nicht sehen solltest.", Toast.LENGTH_SHORT).show();
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_2);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    /**
     * Methode um die Hauptwährung und das Symbol der Hauptwährung in die SharedPreferences zu schreiben.
     */
    private void setSharedPreferencesProperties() {
        //todo das setzen der Hauptwährung sollte in den einstellungen passieren

        ExpensesDataSource database = new ExpensesDataSource(this);
        Currency mainCurrency = database.getCurrencyById("EUR");

        SharedPreferences preferences = this.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        preferences.edit().putLong("mainCurrencyIndex", mainCurrency.getIndex()).apply();
        preferences.edit().putString("mainCurrencySymbol", mainCurrency.getSymbol()).apply();
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
        //todo wenn in einem Tab die Ausgaben angepasst werden (konto wird an oder abgewählt), dann nehmen die anderen tabs diese aänderung nicht mit an

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

    @Override
    public void onTextInput(String textInput, String tag) {

        int visibleTabPosition = mTabLayout.getSelectedTabPosition();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + visibleTabPosition);

        if (tag.equals("tab_one_combine_bookings")) {

            ((TabOneBookings) fragment).onCombinedTitleSelected(textInput);
        }
    }

    /**
     * Methode um meinen BackupService periodische jeden Tag einmal laufen zu lassen.
     * <p>
     * Anleitung siehe: https://guides.codepath.com/android/Starting-Background-Services#using-with-alarmmanager-for-periodic-tasks
     *///todo es sollte nicht jedes mal ein backup erstellt werden wenn die app aufgerufen wird
    private void scheduleBackupServiceAlarm() {

        Intent backupServiceIntent = new Intent(getApplicationContext(), MyAlarmReceiver.class);

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE, backupServiceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long startInMillis = System.currentTimeMillis();

        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, startInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
