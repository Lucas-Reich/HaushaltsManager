package com.example.lucas.haushaltsmanager.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.changelogdialog.ChangelogDialog;
import com.example.lucas.haushaltsmanager.Assets.AppReleaseHistory;
import com.example.lucas.haushaltsmanager.BuildConfig;
import com.example.lucas.haushaltsmanager.Dialogs.LicensesWrapperDialog;
import com.example.lucas.haushaltsmanager.R;

public class AboutUsActivity extends AppCompatActivity {
    private static final String SUPPORT_MAIL = "ausgabenmanager@outlook.com";
    private static final String FEATURE_MAIL = "ausgabenmanager@outlook.com";

    private LinearLayout mSupportLayout, mImproveLayout, mFeatureLayout, mVersionLayout, mRateLayout, mLicenseLayout;
    private TextView mAppVersionTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        mSupportLayout = findViewById(R.id.about_us_contact_support_wrapper);
        mImproveLayout = findViewById(R.id.about_us_contact_improve_wrapper);
        mFeatureLayout = findViewById(R.id.about_us_contact_feature_wrapper);
        mVersionLayout = findViewById(R.id.about_us_version_wrapper);
        mRateLayout = findViewById(R.id.about_us_rating_wrapper);
        mLicenseLayout = findViewById(R.id.about_us_licenses_wrapper);

        mAppVersionTxt = findViewById(R.id.about_us_version_number_txt);

        initializeToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSupportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMail(SUPPORT_MAIL, "");
            }
        });

        mImproveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMail(SUPPORT_MAIL, "");
            }
        });

        mFeatureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMail(FEATURE_MAIL, "I got an Idea for new Features");
            }
        });

        mAppVersionTxt.setText(getAppVersion());
        mVersionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChangelogDialog changelogDialog = new ChangelogDialog();
                changelogDialog.createBuilder(AboutUsActivity.this);
                changelogDialog.setTitle(getString(R.string.changelog_title));
                changelogDialog.setCloseBtnText(R.string.btn_dismiss);
                changelogDialog.setReleaseHistory(new AppReleaseHistory());
                changelogDialog.show(getFragmentManager(), "about_us_changelog");
            }
        });

        mRateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openPlayStore(getPackageName());
            }
        });

        mLicenseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LicensesWrapperDialog licenseDialog = new LicensesWrapperDialog(AboutUsActivity.this);
                licenseDialog.create().show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Methode um eine Toolbar anzuzeigen die den Titel und einen Zurückbutton enthält.
     */
    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        //schatten der toolbar
        if (Build.VERSION.SDK_INT >= 21)
            toolbar.setElevation(10.f);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Methode um die Übersichtsseite einer App im GooglePlayStore zu öffnen.
     *
     * @param packageName App welche geöffnet werden soll
     */
    private void openPlayStore(String packageName) {

        try {

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    /**
     * Methode um eine E-Mail App aufzufordern eine E-Mail für mich zu senden.
     * Quelle: https://stackoverflow.com/a/2197841
     *
     * @param recipient Empfänger
     * @param subject   Betreff
     */
    private void sendMail(String recipient, String subject) {

        Intent featureEmailIntent = new Intent(Intent.ACTION_SEND);
        featureEmailIntent.setType("message/rfc822");
        featureEmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        featureEmailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        try {

            startActivity(Intent.createChooser(featureEmailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException e) {

            Toast.makeText(AboutUsActivity.this, R.string.error_no_mail_clients, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Methode welche die Aktuelle App Version zurückgibt.
     *
     * @return App Version
     */
    private String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }
}
