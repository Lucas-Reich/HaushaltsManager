package com.example.lucas.haushaltsmanager;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class PriceInputDialogFragment2 extends DialogFragment {


    private double price = 0.0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Bundle args = getArguments();
        final Activity activity = getActivity();


        LayoutInflater inflater = activity.getLayoutInflater();
        View priceInputView = inflater.inflate(R.layout.price_input, null);
        Button btn = (Button) priceInputView.findViewById(R.id.price_input_one);
        Log.d("Test", "bound an click listener to one");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calcPrice(0.0);
                Log.d("Test", "hier habe ich ein richtig geile test nachricht um zu gucken ob das hier überhaupt funktioniert");
            }
        });

        priceInputView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Test", "hier habe ich ein richtig geile test nachricht um zu gucken ob das hier überhaupt funktioniert");
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(args.getString("original_title"));

        builder.setView(R.layout.price_input);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

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

    private void calcPrice(double number) {

        price += number;
        Log.d("ExpenseScreen", "Hallo aus der calcPrice Methode, um den preis einzugeben");
    }
}
