package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Utils.ViewUtils;
import com.example.lucas.haushaltsmanager.Views.MoneyEditText;
import com.example.lucas.haushaltsmanager.entities.Price;

import java.util.Locale;

public class PriceInputDialog extends DialogFragment {
    public static final String TITLE = "title";
    public static final String HINT = "hint";

    private OnPriceSelected mCallback;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // TODO: Als User kann ich kein Kommata eingeben, weil das irgendwie geblockt ist.
        //  Wenn ich diesen Bug fixe, dann muss ich auch die Locale bei createPrice(Price price) fixen, da der User als separator beides eingeben kann
        BundleUtils args = new BundleUtils(getArguments());

        final EditText input = createInputView((Price) args.getParcelable(HINT, null));

        //wrapper für die text eingabe, sodass dieser eine padding gegeben werden kann
        //Source: http://android.pcsalt.com/create-alertdialog-with-custom-layout-programmatically/
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setPadding(ViewUtils.dpToPx(23), 0, 0, 0);
        layout.addView(input);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString(TITLE, ""));

        builder.setView(layout);

        builder.setPositiveButton(R.string.btn_ok, (dialog, which) -> {
            String priceString = input.getText().toString();

            if (null != mCallback) {
                mCallback.onPriceSelected(createPrice(priceString));
            }

        });

        builder.setNegativeButton(R.string.btn_cancel, (dialog, which) -> dismiss());

        //when user clicks ok on keyboard input gets send to activity
        input.setOnEditorActionListener((v, actionId, event) -> {
            if (hasUserConfirmed(event, actionId)) {
                String priceString = input.getText().toString();

                mCallback.onPriceSelected(createPrice(priceString));
                dismiss();
            }

            return false;
        });

        return builder.create();
    }

    public void setOnPriceSelectedListener(PriceInputDialog.OnPriceSelected listener) {
        mCallback = listener;
    }

    private boolean hasUserConfirmed(KeyEvent keyEvent, int actionId) {
        return (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE;
    }

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
                Locale.US // TODO: Locale auf Locale.getDefault() wechseln, wenn ich den PriceFormatTextWatcher gefixt habe.
        );
    }

    public interface OnPriceSelected {
        void onPriceSelected(Price price);
    }
}
