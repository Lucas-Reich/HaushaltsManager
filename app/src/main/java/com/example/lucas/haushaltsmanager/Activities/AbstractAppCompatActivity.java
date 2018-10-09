package com.example.lucas.haushaltsmanager.Activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.lucas.haushaltsmanager.R;

public abstract class AbstractAppCompatActivity extends AppCompatActivity {
    //kann ich eine Abstrakt Klasse erstellen die von allen Actvities verwendet wird?
    // macht das sinn?

    /**
     * Methode um eine Toolbar anzuzeigen die den Titel und einen Zurückbutton enthält.
     */
    public void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        //schatten der toolbar
        if (Build.VERSION.SDK_INT >= 21)
            toolbar.setElevation(10.f);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
