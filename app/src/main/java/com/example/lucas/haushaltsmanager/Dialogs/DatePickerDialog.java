package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;

import java.util.Calendar;

public class DatePickerDialog extends DialogFragment implements DatePicker.OnDateChangedListener {
    private static final String TAG = DatePickerDialog.class.getSimpleName();
    public static final String TITLE = "title";
    public static final String CURRENT_DAY_IN_MILLIS = "current_day";
    public static final String MIN_DATE_IN_MILLIS = "min_date";

    private OnDateSelected mCallback;
    private Calendar mCalendar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCalendar = Calendar.getInstance();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BundleUtils args = new BundleUtils(getArguments());

        mCalendar.setTimeInMillis(args.getLong(CURRENT_DAY_IN_MILLIS, Calendar.getInstance().getTimeInMillis()));

        DatePicker datePicker = new DatePicker(getActivity());
        datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
        datePicker.setMinDate(args.getLong(MIN_DATE_IN_MILLIS, 0L));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(args.getString(TITLE, ""));

        builder.setView(datePicker);

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (mCallback != null)
                    mCallback.onDateSelected(mCalendar);

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
     * Methode um einen Listener zu setzen, welcher aufegrufen wird, wenn der User ein Datum ausgewählt hat.
     *
     * @param listener Listener
     */
    public void setOnDateSelectedListener(DatePickerDialog.OnDateSelected listener) {
        mCallback = listener;
    }

    @Override
    public void onDateChanged(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        mCalendar.set(year, monthOfYear, dayOfMonth);
    }

    public interface OnDateSelected {
        void onDateSelected(Calendar date);
    }
}
