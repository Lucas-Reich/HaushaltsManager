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

import com.example.lucas.haushaltsmanager.R;

public class FrequencyAlertDialog extends DialogFragment {

    private OnFrequencySet mCallback;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mCallback = (OnFrequencySet) context;
            mContext = context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString() + " must implement OnFrequencySelected!");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View frequencyInput = inflater.inflate(R.layout.frequency_input, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(args.getString("title"));

        builder.setView(frequencyInput);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mCallback.onFrequencySet(getFrequencyFromInput(frequencyInput), getTag());
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

        EditText numberInput = (EditText) view.findViewById(R.id.input_number);
        String frequency = numberInput.getText().toString();

        return Integer.parseInt(frequency) * 24;
    }

    public interface OnFrequencySet {
        void onFrequencySet(int frequencyInHours, String tag);
    }
}
