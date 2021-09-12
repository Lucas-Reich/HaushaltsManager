package com.example.lucas.haushaltsmanager.Activities;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MenuItem;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.example.lucas.haushaltsmanager.R;

public abstract class AbstractAppCompatActivity extends AppCompatActivity {

    /**
     * Methode um eine Toolbar anzuzeigen die den Titel und einen Zurückbutton enthält.
     */
    public void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        assertActivityHasToolbar(toolbar);

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
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @ColorInt
    public int getColorRes(@ColorRes int colorId) {
        return getResources().getColor(colorId);
    }

    public Drawable getDrawableRes(@DrawableRes int drawableId) {
        return ResourcesCompat.getDrawable(getResources(), drawableId, null);
    }
}
