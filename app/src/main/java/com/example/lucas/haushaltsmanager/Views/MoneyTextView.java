package com.example.lucas.haushaltsmanager.Views;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;

import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Utils.MoneyUtils;

import java.util.Locale;

public class MoneyTextView extends android.support.v7.widget.AppCompatTextView {

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
        return String.format("%s %s", getHumanReadablePrice(price), getCurrencySymbol(price));
    }

    private String getCurrencySymbol(Price price) {
        return price.getCurrency().getSymbol();
    }

    private String getHumanReadablePrice(Price price) {
        return MoneyUtils.formatHumanReadable(price, Locale.getDefault());
    }

    @ColorInt
    private int getColor(@ColorRes int color) {
        return getContext().getResources().getColor(color);
    }
}
