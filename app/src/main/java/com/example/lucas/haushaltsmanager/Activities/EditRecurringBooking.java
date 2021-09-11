package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions.RecurringBookingCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Dialogs.DatePickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.FrequencyInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.PriceInputDialog;
import com.example.lucas.haushaltsmanager.Entities.Booking.IBooking;
import com.example.lucas.haushaltsmanager.Entities.Frequency;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Utils.CalendarUtils;
import com.example.lucas.haushaltsmanager.Views.SaveFloatingActionButton;
import com.example.lucas.haushaltsmanager.Worker.WorkRequestBuilder;

import java.util.Calendar;

public class EditRecurringBooking extends AbstractAppCompatActivity {
    public static final String INTENT_BOOKING = "recurringBooking";

    private RecurringBookingRepository recurringBookingRepository;
    private Button mStartDateBtn, mEndDateBtn, mFrequencyBtn;
    private SaveFloatingActionButton mSaveFab;
    private RecurringBookingBuilder recurringBookingBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recurring_booking);

        recurringBookingBuilder = new RecurringBookingBuilder();
        setRecurringBookingExpense(new BundleUtils(getIntent().getExtras()));

        mSaveFab = findViewById(R.id.edit_recurring_booking_save);
        mStartDateBtn = findViewById(R.id.edit_recurring_booking_from_date);
        mEndDateBtn = findViewById(R.id.edit_recurring_booking_end_date);
        mFrequencyBtn = findViewById(R.id.edit_recurring_booking_frequency);

        recurringBookingRepository = new RecurringBookingRepository(this);

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

        mFrequencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(PriceInputDialog.TITLE, getString(R.string.input_frequency));

                FrequencyInputDialog frequencyDialog = new FrequencyInputDialog();
                frequencyDialog.setArguments(bundle);
                frequencyDialog.setOnFrequencySet(new FrequencyInputDialog.OnFrequencySelected() {
                    @Override
                    public void onFrequencySet(Frequency frequency, String frequencyText) {
                        setFrequency(frequency, frequencyText);
                    }
                });
                frequencyDialog.show(getFragmentManager(), "edit_recurring_booking_frequency");
            }
        });

        mEndDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong(DatePickerDialog.MIN_DATE_IN_MILLIS, Calendar.getInstance().getTimeInMillis());
                bundle.putLong(DatePickerDialog.CURRENT_DAY_IN_MILLIS, Calendar.getInstance().getTimeInMillis());

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

        mSaveFab.setOnClickListener(new SaveFloatingActionButton.OnClickListener() {
            @Override
            public void onCrossClick() {
                Toast.makeText(EditRecurringBooking.this, R.string.cannot_create_recurring_booking, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCheckClick() {
                saveRecurringBooking();

                finish();
            }
        });
    }

    private void setRecurringBookingExpense(BundleUtils bundle) {
        if (!bundle.hasMapping(INTENT_BOOKING)) {
            throw new IllegalArgumentException("Could not start EditRecurringBookingScreen without associated Booking");
        }

        IBooking booking = (IBooking) bundle.getParcelable(INTENT_BOOKING, null);
        recurringBookingBuilder.setBooking(booking);
    }

    private void setFrequency(Frequency frequency, String text) {
        recurringBookingBuilder.setFrequency(frequency);
        mFrequencyBtn.setText(String.format("%s", text));

        enableFabIfBookingIsSaveable();
    }

    private void setEndDate(Calendar endDate) {
        recurringBookingBuilder.setEnd(endDate);
        mEndDateBtn.setText(CalendarUtils.formatHumanReadable(endDate));

        enableFabIfBookingIsSaveable();
    }

    private void setStartDate(Calendar startDate) {
        recurringBookingBuilder.setStart(startDate);
        mStartDateBtn.setText(CalendarUtils.formatHumanReadable(startDate));

        enableFabIfBookingIsSaveable();
    }

    private void saveRecurringBooking() {
        try {
            RecurringBooking recurringBooking = recurringBookingBuilder.build();

            recurringBookingRepository.insert(recurringBooking);

            scheduleWorkRequestFor(recurringBooking);
        } catch (RecurringBookingCouldNotBeCreatedException e) {
            Toast.makeText(this, getString(R.string.cannot_create_recurring_booking), Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleWorkRequestFor(RecurringBooking recurringBooking) {
        WorkRequest recurringBookingWorkRequest = WorkRequestBuilder.from(recurringBooking);
        WorkManager.getInstance(this).enqueue(recurringBookingWorkRequest);
    }

    private void enableFabIfBookingIsSaveable() {
        if (recurringBookingBuilder.recurringBookingCanBeCreated()) {
            mSaveFab.enable();
        } else {
            mSaveFab.disable();
        }
    }
}
