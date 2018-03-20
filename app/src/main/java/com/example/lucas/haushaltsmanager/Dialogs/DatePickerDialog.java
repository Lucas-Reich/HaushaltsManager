package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import com.example.lucas.haushaltsmanager.R;

import java.util.Calendar;

public class DatePickerDialog extends DialogFragment implements DatePicker.OnDateChangedListener {

    //todo der date picker hat noch einen komischen kleinen rahmen auf der rechen seite welcher not entfernt werden muss

    private static String TAG = DatePickerDialog.class.getSimpleName();

    private OnDateSelected mCallback;
    private Context mContext;
    private Calendar mCalendar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mCallback = (OnDateSelected) context;
            mContext = context;
            mCalendar = Calendar.getInstance();
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString() + " must implement OnDateSelected");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null && args.getLong("current_day") != 0L)
            mCalendar.setTimeInMillis(args.getLong("current_day"));

        DatePicker datePicker = new DatePicker(mContext);
        datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(args.getString("title"));

        builder.setView(datePicker);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                mCallback.onDateSelected(mCalendar, getTag());
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

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDateChanged(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        mCalendar.set(year, monthOfYear, dayOfMonth);
    }

    public interface OnDateSelected {
        void onDateSelected(Calendar date, String tag);
    }
}
