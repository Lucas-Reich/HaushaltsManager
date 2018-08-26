package com.example.lucas.haushaltsmanager.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.BuildConfig;
import com.example.lucas.haushaltsmanager.Dialogs.ChangelogDialog.ChangelogDialog;
import com.example.lucas.haushaltsmanager.Dialogs.LicensesWrapperDialog;
import com.example.lucas.haushaltsmanager.R;

public class AboutUsActivity extends AppCompatActivity {
    private static final String SUPPORT_MAIL = "support@Haushaltsmanager.de";
    private static final String FEATURE_MAIL = "feature@Haushaltsmanager.de";

    private LinearLayout supportLayout, improveLayout, featureLayout, versionLayout, rateLayout, licenseLayout;
    private TextView appVersionTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        supportLayout = findViewById(R.id.about_us_contact_support_wrapper);
        improveLayout = findViewById(R.id.about_us_contact_improve_wrapper);
        featureLayout = findViewById(R.id.about_us_contact_feature_wrapper);
        versionLayout = findViewById(R.id.about_us_version_wrapper);
        rateLayout = findViewById(R.id.about_us_rating_wrapper);
        licenseLayout = findViewById(R.id.about_us_licenses_wrapper);

        appVersionTxt = findViewById(R.id.about_us_version_number_txt);
    }

    @Override
    protected void onStart() {
        super.onStart();

        supportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMail(SUPPORT_MAIL, "");
            }
        });

        improveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMail(SUPPORT_MAIL, "");
            }
        });

        featureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMail(FEATURE_MAIL, "I got an Idea for new Features");
            }
        });

        appVersionTxt.setText(getAppVersion());
        versionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChangelogDialog changelogDialog = new ChangelogDialog();
                changelogDialog.createBuilder(AboutUsActivity.this);
                changelogDialog.setTitle(getString(R.string.changelog_title));
                changelogDialog.show(getFragmentManager(), "about_us_changelog");
            }
        });

        rateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openPlayStore(getPackageName());
            }
        });

        licenseLayout.setOnClickListener(new View.OnClickListener() {
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
