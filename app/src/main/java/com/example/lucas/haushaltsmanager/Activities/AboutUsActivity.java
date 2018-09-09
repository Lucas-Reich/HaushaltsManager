package com.example.lucas.haushaltsmanager.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Assets.ReleaseHistory;
import com.example.lucas.haushaltsmanager.BuildConfig;
import com.example.lucas.haushaltsmanager.Dialogs.LicensesWrapperDialog;
import com.example.lucas.haushaltsmanager.R;
import com.lucas.changelogdialog.ChangelogDialog;

public class AboutUsActivity extends AppCompatActivity {
    private static final String SUPPORT_MAIL = "support@Haushaltsmanager.de";
    private static final String FEATURE_MAIL = "feature@Haushaltsmanager.de";

    private LinearLayout mSupportLayout, mImproveLayout, mFeatureLayout, mVersionLayout, mRateLayout, mLicenseLayout;
    private TextView mAppVersionTxt;
    private ImageButton mBackArrow;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mBackArrow = findViewById(R.id.back_arrow);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

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

                ChangelogDialog changelogDialog = new com.lucas.changelogdialog.ChangelogDialog();
                changelogDialog.createBuilder(AboutUsActivity.this);
                changelogDialog.setTitle(getString(R.string.changelog_title));
                changelogDialog.setReleaseHistory2(new ReleaseHistory());
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
