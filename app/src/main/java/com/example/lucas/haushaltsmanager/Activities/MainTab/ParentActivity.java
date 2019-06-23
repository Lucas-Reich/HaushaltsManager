package com.example.lucas.haushaltsmanager.Activities.MainTab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Activities.AboutUsActivity;
import com.example.lucas.haushaltsmanager.Activities.BackupActivity;
import com.example.lucas.haushaltsmanager.Activities.CategoryList;
import com.example.lucas.haushaltsmanager.Activities.ImportExportActivity;
import com.example.lucas.haushaltsmanager.Activities.RecurringBookingList;
import com.example.lucas.haushaltsmanager.Activities.Settings;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Dialogs.ChangeAccounts.ChooseAccountsDialogFragment;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.PreferencesHelper.ActiveAccountsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseFilter;

import java.util.ArrayList;
import java.util.List;

public class ParentActivity extends AppCompatActivity implements ChooseAccountsDialogFragment.OnSelectedAccount {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<ExpenseObject> mExpenses = new ArrayList<>();
    private List<Long> mActiveAccounts = new ArrayList<>();
    private ExpenseRepository mBookingRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_main_mit_nav_drawer);

        mBookingRepo = new ExpenseRepository(this);

        initializeActiveAccounts();

        updateExpenses();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the mReportAdapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections mReportAdapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);


        DrawerLayout drawer = findViewById(R.id.drawer_layout_2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_2);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.categories:

                        Intent categoryIntent = new Intent(ParentActivity.this, CategoryList.class);
                        ParentActivity.this.startActivity(categoryIntent);
                        break;
                    case R.id.standing_orders:

                        Intent recurringBookingIntent = new Intent(ParentActivity.this, RecurringBookingList.class);
                        ParentActivity.this.startActivity(recurringBookingIntent);
                        break;
                    case R.id.backup:

                        Intent backupIntent = new Intent(ParentActivity.this, BackupActivity.class);
                        ParentActivity.this.startActivity(backupIntent);
                        break;
                    case R.id.import_export:

                        Intent importExportIntent = new Intent(ParentActivity.this, ImportExportActivity.class);
                        ParentActivity.this.startActivity(importExportIntent);
                        break;
                    case R.id.preferences:

                        Intent settingsIntent = new Intent(ParentActivity.this, Settings.class);
                        ParentActivity.this.startActivity(settingsIntent);
                        break;
                    case R.id.about:

                        Intent aboutUsIntent = new Intent(ParentActivity.this, AboutUsActivity.class);
                        ParentActivity.this.startActivity(aboutUsIntent);
                        break;
                    default:

                        Toast.makeText(ParentActivity.this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout_2);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_2);

        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choose_account:

                ChooseAccountsDialogFragment chooseAccountFragment = new ChooseAccountsDialogFragment();
                chooseAccountFragment.setOnAccountSelectedListener(this);
                chooseAccountFragment.show(getFragmentManager(), "alterVisibleAccounts");
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAccountSelected(long accountId, boolean isChecked) {
        if (mActiveAccounts.contains(accountId) == isChecked)
            return;

        if (mActiveAccounts.contains(accountId) && !isChecked)
            mActiveAccounts.remove(accountId);
        else
            mActiveAccounts.add(accountId);

        updateVisibleTab();
    }

    public void updateExpenses() {
        mExpenses = mBookingRepo.getAll();
    }

    public List<ExpenseObject> getVisibleExpenses() {
        return new ExpenseFilter().byAccountWithChildren(mExpenses, mActiveAccounts);
    }

    public List<ExpenseObject> getVisibleExpensesByOffsetWithParents(int offset, int batchSize) {
        List<ExpenseObject> visibleExpenses = new ExpenseFilter().byAccountWithParents(mExpenses, mActiveAccounts);

        if (visibleExpenses.size() <= offset) {
            return new ArrayList<>();
        }

        if (visibleExpenses.size() < (offset + batchSize)) {
            return visibleExpenses.subList(offset, visibleExpenses.size());
        }

        return visibleExpenses.subList(offset, batchSize);
    }

    private void initializeActiveAccounts() {
        ActiveAccountsPreferences preferences = new ActiveAccountsPreferences(this);

        mActiveAccounts = preferences.getActiveAccounts();
    }

    /**
     * Method to refresh data in the currently visible Tab.
     * Source: https://stackoverflow.com/a/27211004
     */
    private void updateVisibleTab() {

        int visibleTabPosition = mTabLayout.getSelectedTabPosition();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + mViewPager.getId() + ":" + visibleTabPosition);

        if (fragment != null) {

            switch (visibleTabPosition) {

                case 0:
                    ((TabOneBookings) fragment).updateView(fragment.getView());
                    break;
                case 1:
                    ((TabTwoMonthlyReports) fragment).updateView(fragment.getView());
                    break;
                case 2:
                    ((TabThreeYearlyReports) fragment).updateView(fragment.getView());
                    break;
            }
        }
    }

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
                    return new TabThreeYearlyReports();
                default:
                    return null;
            }
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
}
