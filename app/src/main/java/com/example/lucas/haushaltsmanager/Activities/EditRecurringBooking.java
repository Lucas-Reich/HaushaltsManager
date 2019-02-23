package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.R;

import java.util.Calendar;

public class EditRecurringBooking extends AbstractAppCompatActivity {
    public static final String INTENT_BOOKING = "recurringBooking";

    private ExpenseObject mExpense;
    private Calendar mStartDate, mEndDate;
    private int mFrequency;
    private Button mSaveBtn, mStartDateBtn, mEndDateBtn, mFrequencyBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recurring_booking);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle)
            mExpense = bundle.getParcelable(INTENT_BOOKING);

        mSaveBtn = findViewById(R.id.edit_recurring_booking_save);
        mStartDateBtn = findViewById(R.id.edit_recurring_booking_from_date);
        mEndDateBtn = findViewById(R.id.edit_recurring_booking_end_date);
        mFrequencyBtn = findViewById(R.id.edit_recurring_booking_frequency);

        initializeToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setStartDate(Calendar.getInstance());
        mStartDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePicker = new DatePickerDialog();
                datePicker.setOnDateSelectedListener(new DatePickerDialog.OnDateSelected() {
                    @Override
                    public void onDateSelected(Calendar date) {
                        setStartDate(date);
                    }
                });
                datePicker.show(getFragmentManager(), "edit_recurring_booking_from");
            }
        });

        setFrequency(1);
        mFrequencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(PriceInputDialog.TITLE, getString(R.string.input_frequency));
                bundle.putString(PriceInputDialog.HINT, getString(R.string.frequency_in_days));

                PriceInputDialog frequencyInput = new PriceInputDialog();
                frequencyInput.setArguments(bundle);
                frequencyInput.setOnPriceSelectedListener(new PriceInputDialog.OnPriceSelected() {
                    @Override
                    public void onPriceSelected(double price) {
                        setFrequency((int) price);
                    }
                });
                frequencyInput.show(getFragmentManager(), "edit_recurring_booking_frequency");
            }
        });

        mEndDate = Calendar.getInstance();
        mEndDate.add(Calendar.DAY_OF_WEEK, 7);
        setEndDate(mEndDate);
        mEndDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong(DatePickerDialog.MIN_DATE_IN_MILLIS, mStartDate.getTimeInMillis());
                bundle.putLong(DatePickerDialog.CURRENT_DAY_IN_MILLIS, mEndDate.getTimeInMillis());

                DatePickerDialog datePicker = new DatePickerDialog();
                datePicker.setArguments(bundle);
                datePicker.setOnDateSelectedListener(new DatePickerDialog.OnDateSelected() {
                    @Override
                    public void onDateSelected(Calendar date) {
                        setEndDate(date);
                    }
                });
                datePicker.show(getFragmentManager(), "edit_recurring_booking_end");
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!saveRecurringBooking())
                    Toast.makeText(EditRecurringBooking.this, R.string.could_not_save_recurring_booking, Toast.LENGTH_SHORT).show();
                else
                    finish();
            }
        });
    }

    private void setFrequency(int frequency) {

        mFrequency = frequency;
        mFrequencyBtn.setText(String.format("%s", frequency));
    }

    private void setEndDate(Calendar endDate) {

        mEndDate = endDate;
        mEndDateBtn.setText(DateUtils.formatDateTime(
                this,
                endDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE)
        );
    }

    private void setStartDate(Calendar startDate) {
        // TODO: Sollte ich einen Fehler ausgeben, wenn der User versucht das StartDate nach dem EndDate zu setzen?

        mStartDate = startDate;
        mStartDateBtn.setText(DateUtils.formatDateTime(
                this,
                startDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE)
        );
    }

    private boolean saveRecurringBooking() {
        if (!isRecurringBookingSavable())
            return false;

        RecurringBookingRepository recurringBookingRepo = new RecurringBookingRepository(this);
        recurringBookingRepo.create(new RecurringBooking(
                mStartDate,
                mEndDate,
                mFrequency,
                mExpense
        ));

        return true;
    }

    private boolean isRecurringBookingSavable() {
        return mStartDate != null && mEndDate != null && mEndDate.after(mStartDate);
    }
}
