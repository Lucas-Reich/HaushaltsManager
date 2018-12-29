package com.example.lucas.haushaltsmanager.Activities;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.lucas.haushaltsmanager.R;

public abstract class AbstractAppCompatActivity extends AppCompatActivity {

    /**
     * Methode um eine Toolbar anzuzeigen die den Titel und einen Zurückbutton enthält.
     */
    public void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        assertActivityHasToolbar(toolbar);

        // Toolbarschatten ist vor Androidversion 21 nicht verfügbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            toolbar.setElevation(10.f);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void assertActivityHasToolbar(Toolbar toolbar) {
        if (null == toolbar)
            throw new IllegalStateException("Activity has to have Toolbar with id 'toolbar'");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @ColorInt
    public int getColorRes(@ColorRes int colorId) {
        return getResources().getColor(colorId);
    }

    public Drawable getDrawableRes(@DrawableRes int drawableId) {
        return getResources().getDrawable(drawableId);
    }
}
