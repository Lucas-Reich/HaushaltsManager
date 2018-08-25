package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.lucas.haushaltsmanager.BundleUtils;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

public class CurrencyPicker extends DialogFragment {
    private Context mContext;
    private OnCurrencySelected mCallback;
    private Currency mSelectedCurrency;
    private List<Currency> mCurrencies = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BundleUtils args = new BundleUtils(getArguments());

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(args.getString("title", ""));

        builder.setSingleChoiceItems(convertToString(mCurrencies), -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedCurrency = mCurrencies.get(i);
            }
        });

        builder.setPositiveButton(R.string.btn_choose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (mCallback != null)
                    mCallback.onCurrencySelected(mSelectedCurrency);
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    /**
     * Methode um dem AlertDialog die Anzuzeigenden Währungen zu übergeben
     *
     * @param currencies Anzuzeigende Währungen
     */
    public void setCurrencies(List<Currency> currencies) {
        mCurrencies = currencies;
    }

    /**
     * Methode um eine Liste der Währungsbezeichnungen zu bekommen.
     *
     * @param currencies Liste von Währungen von denen der Name extrahiert werden soll
     * @return Array mit dem Namen der angegeben Währungen
     */
    private String[] convertToString(List<Currency> currencies) {
        String[] currencyNames = new String[currencies.size()];

        for (int i = 0; i < currencies.size(); i++) {
            currencyNames[i] = currencies.get(i).getName();
        }

        return currencyNames;
    }

    /**
     * Methode um einen Listener zu registrieren.
     *
     * @param listener Listener
     */
    public void setOnCurrencySelectedListener(CurrencyPicker.OnCurrencySelected listener) {
        mCallback = listener;
    }

    public interface OnCurrencySelected {
        void onCurrencySelected(Currency currency);
    }
}
