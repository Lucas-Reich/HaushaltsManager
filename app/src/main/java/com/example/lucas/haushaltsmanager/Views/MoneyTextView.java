package com.example.lucas.haushaltsmanager.Views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;

import com.example.lucas.haushaltsmanager.entities.Currency;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.Utils.MoneyUtils;

import java.util.Locale;

public class MoneyTextView extends androidx.appcompat.widget.AppCompatTextView {

    public MoneyTextView(Context context) {
        super(context);
    }

    public MoneyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoneyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bind(Price price) {
        setText(formatString(price));
        setTextColor(getColor(price.getColor()));

        invalidate();
    }

    private String formatString(Price price) {
        return String.format(
                "%s %s",
                getHumanReadablePrice(price),
                new Currency().getSymbol()
        );
    }

    private String getHumanReadablePrice(Price price) {
        return MoneyUtils.formatHumanReadable(price, Locale.getDefault());
    }

    @ColorInt
    private int getColor(@ColorRes int color) {
        return getContext().getResources().getColor(color);
    }
}
