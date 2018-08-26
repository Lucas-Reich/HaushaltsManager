package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.R;

public class AboutUsActivity extends AppCompatActivity {

    private LinearLayout supportLayout, improveLayout, featureLayout, versionLayout, rateLayout, licenseLayout, donateLayout;
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
        donateLayout = findViewById(R.id.about_us_donate_wrapper);

        appVersionTxt = findViewById(R.id.about_us_version_number_txt);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
