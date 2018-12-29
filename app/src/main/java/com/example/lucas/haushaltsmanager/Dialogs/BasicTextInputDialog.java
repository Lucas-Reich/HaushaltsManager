package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Utils.ViewUtils;

public class BasicTextInputDialog extends DialogFragment {
    public static final String TITLE = "title";
    public static final String HINT = "hint";

    private OnTextInput mCallback;
    private String mDialogHint;
    private EditText mTextInput;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BundleUtils args = new BundleUtils(getArguments());

        mDialogHint = args.getString(HINT, "");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString(TITLE, ""));

        builder.setView(prepareLayout());

        builder.setPositiveButton(R.string.btn_ok, null);

        builder.setNegativeButton(R.string.btn_cancel, null);

        return builder.create();
    }

    /**
     * Methode um das Layout des AlertDialogs zu erstellen.
     * Dies wird gemacht, damit man dem TextInput Feld padding geben kann.
     *
     * @return vorbereitetes LinearLayout
     */
    private LinearLayout prepareLayout() {

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setPadding(ViewUtils.dpToPx(23), 0, 0, 0);
        linearLayout.addView(prepareTextInput());

        return linearLayout;
    }

    /**
     * Methode um den EditText vorzubereiten.
     *
     * @return Vorbereiteter EditText
     */
    private EditText prepareTextInput() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        EditText editText = new EditText(getActivity());
        editText.setLayoutParams(lp);
        editText.setMaxLines(1);
        editText.setHint(mDialogHint);
        // Sollte ich noch eine ContentDescription hinzufügen, sodass das InputFeld auch duch ScreemReader lesbar ist?
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    performButtonClickOk();
                    return true;
                }

                return false;
            }
        });

        //Wenn die Anfrage vom CreateAccountActivity kommt, dann müssen Zahlen anstatt Buchstaben im keyboard angezeigt werden
        if (getTag().equals("accountBalance"))
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        else
            editText.setInputType(InputType.TYPE_CLASS_TEXT);

        mTextInput = editText;
        return editText;
    }

    /**
     * Methode um nach der Dialog erstellung den OK onClickListener zu setzen.
     * Das geschieht hier, damit der Dialog nicht automatisch geschlossen wird, wenn der User auf den OK Button klickt.
     * Quelle: https://stackoverflow.com/a/10661281
     */
    @Override
    public void onResume() {
        super.onResume();

        AlertDialog d = (AlertDialog) getDialog();
        Button okButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performButtonClickOk();
            }
        });
    }

    /**
     * Methode welche die Logik hinter dem OK klick des Users implementiert.
     */
    private void performButtonClickOk() {
        if (mTextInput.getText().toString().length() == 0) {

            if (mDialogHint.length() == 0) {

                Toast.makeText(getActivity(), getString(R.string.error_name_missing), Toast.LENGTH_SHORT).show();
            } else {

                if (mCallback != null)
                    mCallback.onTextInput(mDialogHint);

                dismiss();
            }
        } else {

            if (mCallback != null)
                mCallback.onTextInput(mTextInput.getText().toString());

            dismiss();
        }
    }

    /**
     * Methode um einen Listener zu registrieren, welcher aufgerufen wird, wenn der User den Text einegegeben hat.
     *
     * @param listener Listener, welcher aufgerufen werden soll
     */
    public void setOnTextInputListener(OnTextInput listener) {
        mCallback = listener;
    }

    public interface OnTextInput {
        void onTextInput(String textInput);
    }
}