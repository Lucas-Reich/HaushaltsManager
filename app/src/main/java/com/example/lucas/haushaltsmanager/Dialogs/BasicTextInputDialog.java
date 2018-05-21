package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Views.ViewWrapper;

public class BasicTextInputDialog extends DialogFragment {
    private static String TAG = BasicTextInputDialog.class.getSimpleName();

    BasicDialogCommunicator mCallback;
    Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mCallback = (BasicDialogCommunicator) context;
            mContext = context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString() + " must implement BasicDialogCommunicator");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        String dialogTitle = bundle.getString("title") != null ? bundle.getString("title") : "";
        final EditText mTextInput = createInputView();
        mTextInput.setMaxLines(1);
        mTextInput.setInputType(InputType.TYPE_CLASS_TEXT);
        mTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mCallback.onTextInput(mTextInput.getText().toString(), getTag());
                    dismiss();

                    return true;
                }

                return false;
            }
        });

        //wrapper für die text eingabe, sodass dieser eine padding gegeben werden kann
        //Quelle: http://android.pcsalt.com/create-alertdialog-with-custom-layout-programmatically/
        LinearLayout layout = new LinearLayout(mContext);
        layout.setPadding(ViewWrapper.dpToPx(23), 0, 0, 0);
        layout.addView(mTextInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(dialogTitle);

        builder.setView(layout);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mCallback.onTextInput(mTextInput.getText().toString(), getTag());
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
     * Methode um den TextInput vorzubereiten
     *
     * @return EditText
     */
    private EditText createInputView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        EditText input = new EditText(mContext);
        input.setLayoutParams(lp);

        if (getTag().equals("accountBalance"))
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        return input;
    }

    public interface BasicDialogCommunicator {

        void onTextInput(String textInput, String tag);
    }
}