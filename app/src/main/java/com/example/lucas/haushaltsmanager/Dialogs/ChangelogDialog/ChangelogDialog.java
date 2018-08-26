package com.example.lucas.haushaltsmanager.Dialogs.ChangelogDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.lucas.haushaltsmanager.Assets.ReleaseHistory;
import com.example.lucas.haushaltsmanager.R;

public class ChangelogDialog extends DialogFragment {
    //todo Klasse in eine Bibliothek extrahieren
    private AlertDialog.Builder mBuilder;

    public void createBuilder(Context context) {

        mBuilder = new AlertDialog.Builder(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ReleaseHistory releaseHistory = new ReleaseHistory();

        View view = getActivity().getLayoutInflater().inflate(R.layout.changelog_dialog, null, false);
        RecyclerView recyclerView = view.findViewById(R.id.changelog_recycler_view);

        mBuilder.setView(view);

        mBuilder.setPositiveButton(R.string.btn_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dismiss();
            }
        });

        return mBuilder.create();
    }

    /**
     * Methode um den Titel des ChangelogDialogs anzupassen.
     *
     * @param title ChangelogDialog Title
     */
    public void setTitle(String title) {

        mBuilder.setTitle(title);
    }
}
