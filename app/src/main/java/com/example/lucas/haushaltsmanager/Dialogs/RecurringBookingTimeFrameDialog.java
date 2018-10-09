package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.R;

import java.util.Calendar;

public class RecurringBookingTimeFrameDialog extends DialogFragment {
    public static final String TITLE = "title";

    private OnConfirmationListener mListener;
    private Context mContext;
    private int mFrequency;
    private Calendar mEndDate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BundleUtils args = new BundleUtils(getArguments());

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(args.getString(TITLE, ""));

        builder.setView(getDialogView());

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null)
                    mListener.onConfirmation(
                            getStartInMillis(),
                            getFrequency(),
                            getEndInMillis()
                    );
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

    private LinearLayout getDialogView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.frequency_dialog_input, null);

        return ll;
    }

    private long getEndInMillis() {
        return mEndDate.getTimeInMillis();
    }

    private int getFrequency() {
        return mFrequency;
    }

    private long getStartInMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public void setOnConfirmationListener(OnConfirmationListener listener) {
        mListener = listener;
    }

    public interface OnConfirmationListener {
        void onConfirmation(long start, int frequency, long end);
    }
}
