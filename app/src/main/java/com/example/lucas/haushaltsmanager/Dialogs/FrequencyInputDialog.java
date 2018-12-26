package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.R;

public class FrequencyInputDialog extends DialogFragment {
    private static final String TAG = FrequencyInputDialog.class.getSimpleName();
    public static final String TITLE = "title";

    private OnFrequencySet mCallback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BundleUtils args = new BundleUtils(getArguments());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View frequencyInput = inflater.inflate(R.layout.frequency_input, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString("title", ""));

        builder.setView(frequencyInput);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (mCallback != null)
                    mCallback.onFrequencySet(getFrequencyFromInput(frequencyInput));

                dismiss();
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
            }
        });

        return builder.create();
    }

    /**
     * Methode um die vom user eingegebene Häufigkeit aus dem Inputfeld auszulesen
     *
     * @param view View, in dem sich das Inputfeld befindet
     * @return Häufigkeit in Stunden
     */
    private int getFrequencyFromInput(View view) {

        EditText numberInput = view.findViewById(R.id.input_number);
        String frequency = numberInput.getText().toString();

        return Integer.parseInt(frequency) * 24;
    }

    /**
     * Methode um einen Listener zu registrieren, welcher aufgerufen wird, wenn der User eine Häufigkeit angegeben hat.
     *
     * @param listener Listener
     */
    public void setOnFrequencySet(FrequencyInputDialog.OnFrequencySet listener) {
        mCallback = listener;
    }

    public interface OnFrequencySet {
        void onFrequencySet(int frequencyInHours);
    }
}
