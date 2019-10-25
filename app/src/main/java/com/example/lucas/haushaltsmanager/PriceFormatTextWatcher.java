package com.example.lucas.haushaltsmanager;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Utils.MoneyUtils;

import java.util.Locale;

public class PriceFormatTextWatcher implements TextWatcher {
    private EditText editText;
    private Currency defaultCurrency;
    private Locale defaultLocale;

    private int cursorOffset = 0;

    public PriceFormatTextWatcher(EditText editText, Currency defaultCurrency, Locale defaultLocale) {
        this.editText = editText;
        this.defaultCurrency = defaultCurrency;
        this.defaultLocale = defaultLocale;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        cursorOffset = start;
        // Do nothing
    }

    @Override
    public void afterTextChanged(Editable moneyEditable) {
        String unformattedMoneyString = moneyEditable.toString();

        String formattedMoneyString = formatMoneyString(unformattedMoneyString);

        showFormattedMoneyString(formattedMoneyString);

        moveCursorToPosition(determineCursorPosition(cursorOffset, unformattedMoneyString, formattedMoneyString));
    }

    private int determineCursorPosition(int currentOffset, String unformattedText, String formattedText) {
        if (unformattedText.length() == 1 || unformattedText.length() == formattedText.length()) {
            return currentOffset + 1;
        }

        return currentOffset + 2;
    }

    private void moveCursorToPosition(int position) {
        editText.setSelection(position);
    }

    private void showFormattedMoneyString(String formattedMoneyString) {
        editText.removeTextChangedListener(this);
        editText.setText(formattedMoneyString);
        editText.addTextChangedListener(this);
    }

    private String formatMoneyString(String moneyString) {
        Price price = new Price(
                moneyString,
                defaultCurrency,
                defaultLocale
        );

        return MoneyUtils.formatHumanReadable(price, defaultLocale);
    }
}
