package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Utils.ViewUtils;
import com.example.lucas.haushaltsmanager.Views.MoneyEditText;

import java.util.Locale;

public class PriceInputDialog extends DialogFragment {
    public static final String TITLE = "title";
    public static final String HINT = "hint";

    private OnPriceSelected mCallback;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO: Als User kann ich kein Kommata eingeben, weil das irgendwie geblockt ist.
        //  Wenn ich diesen Bug fixe, dann muss ich auch die Locale bei createPrice(Price price) fixen, da der User als separator beides eingeben kann
        BundleUtils args = new BundleUtils(getArguments());

        final EditText input = createInputView((Price) args.getParcelable(HINT, null));

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
                String priceString = input.getText().toString();

                if (null != mCallback) {
                    mCallback.onPriceSelected(createPrice(priceString));
                }

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
                if (hasUserConfirmed(event, actionId)) {
                    String priceString = input.getText().toString();

                    mCallback.onPriceSelected(createPrice(priceString));
                    dismiss();
                }

                return false;
            }
        });

        return builder.create();
    }

    /**
     * Methode um einen Listener zu registrieren, welcher aufgerufen wird, wenn der User einen Preis eingegeben hat.
     *
     * @param listener Listener
     */
    public void setOnPriceSelectedListener(PriceInputDialog.OnPriceSelected listener) {
        mCallback = listener;
    }

    private boolean hasUserConfirmed(KeyEvent keyEvent, int actionId) {
        return (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE;
    }

    /**
     * Methode um ein EditText mit einer margin links und rechts von 75 pixeln zu erstellen.
     *
     * @return EditText
     */
    private EditText createInputView(Price priceHint) {
        MoneyEditText editText = new MoneyEditText(getActivity(), priceHint);
        editText.setLayoutParams(createLayoutParams());

        return editText;
    }

    private LinearLayout.LayoutParams createLayoutParams() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    private Price createPrice(String value) {
        return new Price(
                value,
                getDefaultCurrency(),
                Locale.US // TODO: Locale auf Locale.getDefault() wechseln, wenn ich den PriceFormatTextWatcher gefixt habe.
        );
    }

    private Currency getDefaultCurrency() {
        return Currency.getDefault(getActivity());
    }

    public interface OnPriceSelected {
        void onPriceSelected(Price price);
    }
}
