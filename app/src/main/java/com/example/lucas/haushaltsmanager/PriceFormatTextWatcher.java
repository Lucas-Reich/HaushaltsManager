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

    public PriceFormatTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Do nothing
    }

    @Override
    public void afterTextChanged(Editable moneyEditable) {
        String unformattedMoneyString = moneyEditable.toString();

        String formattedMoneyString = formatMoneyString(unformattedMoneyString);

        showFormattedMoneyString(formattedMoneyString);

        moveCursorToPosition(formattedMoneyString.length());
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
        Price mPrice = new Price(
                moneyString,
                getDefaultCurrency(),
                getDefaultLocale()
        );

        return MoneyUtils.formatHumanReadableOmitCents(mPrice, getDefaultLocale());
    }

    private Currency getDefaultCurrency() {
        return Currency.getDefault(editText.getContext());
    }

    private Locale getDefaultLocale() {
        return Locale.getDefault();
    }
}
