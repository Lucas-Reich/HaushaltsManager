package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.lucas.haushaltsmanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryPickerDialog extends DialogFragment {
    private static final String TAG = DirectoryPickerDialog.class.getSimpleName();

    OnDirectorySelected mCallback;
    String mDialogTitle;
    Context mContext;
    ListView mListView;
    ArrayAdapter<String> mAdapter;
    File mCurrentDir;
    List<String> mSubDirectories;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            mCallback = (OnDirectorySelected) context;
            mContext = context;
        } catch (ClassCastException e) {

            throw new ClassCastException(context.toString() + " must implement OnDirectorySelected");
        }
    }

    //sollte ich noch einen "go up" button einfügen, um ein directory zurückzugehen??
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mCurrentDir = mContext.getFilesDir(); // das start verzeichniss sollte noch weiter unten sein als in files

        Bundle args = getArguments();
        mDialogTitle = args.getString("title") != null ? args.getString("title") : "";

        mListView = new ListView(mContext);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1) {

                    mCurrentDir = mCurrentDir.getParentFile();
                } else {

                    File picketSubDir = new File(mCurrentDir + mSubDirectories.get(position));
                    if (picketSubDir.isDirectory()) {

                        mCurrentDir = picketSubDir;
                    }
                }

                refreshListView(mCurrentDir);
            }
        });

        refreshListView(mCurrentDir);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

        dialog.setTitle(mDialogTitle);

        dialog.setView(mListView);

        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                mCallback.onDirectorySelected(mCurrentDir, getTag());
            }
        });

        dialog.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dismiss();
            }
        });

        return dialog.create();
    }

    /**
     * Methode um die Unterverzeichnisse zu dem angegebenen Verzeichniss zu bekommen.
     * Außerdem wird am Anfang der liste noch ein "/.." eingefügt, sodass man ein Verzeichniss hoch gehen kann.
     *
     * @param directory aktuelles Verzeichniss
     *///todo bei lese gesperrten parent verzeichnissen das /.. nicht anzeigen
    private List<String> getSubDirectories(File directory) {

        List<String> subDirectories = new ArrayList<>();

        subDirectories.add("/..");

        if (directory.listFiles() != null) {

            for (File subDir : directory.listFiles()) {

                subDirectories.add("/" + subDir.getName());
            }
        }

        return subDirectories;
    }

    /**
     * Methode um die Unterverzeichnisse zu einem Verzeichniss in einer ListView anzeigt
     *
     * @param currentDir Das aktuelle Verzeichniss
     */
    private void refreshListView(File currentDir) {

        mSubDirectories = getSubDirectories(currentDir);

        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, mSubDirectories);

        mListView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    public interface OnDirectorySelected {

        void onDirectorySelected(File file, String tag);
    }
}
