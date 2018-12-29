package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Utils.PriceUtils;
import com.example.lucas.haushaltsmanager.Utils.ViewUtils;

public class PriceInputDialog extends DialogFragment {
    public static final String TITLE = "title";
    public static final String HINT = "hint";

    private OnPriceSelected mCallback;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BundleUtils args = new BundleUtils(getArguments());

        final EditText input = createInputView(args.getDouble(HINT, 0D));

        //wrapper für die text eingabe, sodass dieser eine padding gegeben werden kann
        //Quelle: http://android.pcsalt.com/create-alertdialog-with-custom-layout-programmatically/
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(ViewUtils.dpToPx(23), 0, 0, 0);
        layout.addView(input);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString(TITLE, ""));

        builder.setView(layout);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String price = input.getText().toString();
                if (price.isEmpty())
                    mCallback.onPriceSelected(0);
                else
                    mCallback.onPriceSelected(Double.parseDouble(price));

            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
            }
        });

        //when user clicks ok on keyboard input gets send to activity
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE) {

                    mCallback.onPriceSelected(Double.parseDouble(input.getText().toString()));
                    dismiss();
                    return false;
                }

                return false;
            }
        });


        return builder.create();
    }

    /**
     * Methode um ein EditText mit einer margin links und rechts von 75 pixeln zu erstellen.
     *
     * @return EditText
     */
    private EditText createInputView(double hint) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        EditText input = new EditText(getActivity());
        input.setHint(PriceUtils.toHumanReadablePrice(hint));
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        return input;
    }

    /**
     * Methode um einen Listener zu registrieren, welcher aufgerufen wird, wenn der User einen Preis eingegeben hat.
     *
     * @param listener Listener
     */
    public void setOnPriceSelectedListener(PriceInputDialog.OnPriceSelected listener) {
        mCallback = listener;
    }

    public interface OnPriceSelected {
        void onPriceSelected(double price);
    }
}
