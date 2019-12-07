package com.example.lucas.haushaltsmanager.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.IImporter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.ISub;
import com.example.lucas.haushaltsmanager.R;

public class ProgressBarDialog extends AlertDialog implements ISub {
    private ProgressBar progressBar;
    private TextView secondaryTitle;
    private IImporter observableAction;

    public ProgressBarDialog(@NonNull Context context, final IImporter observableAction) {
        super(context);

        View view = getLayoutInflater().inflate(R.layout.progress_bar_dialog_layout, null);
        setView(view);

        progressBar = view.findViewById(R.id.progress_bar_dialog_layout_bar);

        secondaryTitle = view.findViewById(R.id.progress_bar_dialog_sub_title);

        this.observableAction = observableAction;

        setTitle(context.getString(R.string.import_file_title));

        setSubTitle(context.getString(R.string.import_file_sub_title_reading_content));

        setButton(BUTTON_NEGATIVE, context.getString(R.string.btn_abort), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                observableAction.abort();

                dialog.dismiss();
            }
        });


        setCanceledOnTouchOutside(false);
        attachAlertDialogTo(observableAction);
    }

    @Override
    protected void onStop() {
        super.onStop();

        observableAction.removeSub(this);
    }

    @Override
    public void notifySuccess() {
        increaseProgressBarProgress();

        log(true);
    }

    @Override
    public void notifyFailure() {
        increaseProgressBarProgress();

        log(false);
    }

    public void setSubTitle(String subTitle) {
        secondaryTitle.setText(subTitle);
    }

    private void increaseProgressBarProgress() {
        int currentProgress = progressBar.getProgress();

        progressBar.setProgress(currentProgress + 1);

        if (progressBar.getProgress() == progressBar.getMax()) {
            dismiss();
        }
    }

    private void log(boolean result) {
        Log.i(ProgressBarDialog.class.getSimpleName(), String.format(
                "Progress %s/%s, %s.",
                progressBar.getProgress(),
                progressBar.getMax(),
                result ? "Success" : "Failure"
        ));
    }

    private void attachAlertDialogTo(IImporter observableAction) {
        observableAction.addSub(this);

        progressBar.setMax(observableAction.totalSteps());
    }
}
