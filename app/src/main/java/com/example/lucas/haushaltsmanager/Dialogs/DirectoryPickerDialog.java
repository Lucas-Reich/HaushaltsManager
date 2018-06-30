package com.example.lucas.haushaltsmanager.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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

    private OnDirectorySelected mCallback;
    private Context mContext;
    private ListView mListView;
    private File mCurrentDir;
    private List<String> mSubDirectories;
    private String mSearchMode;// todo funktionalität implementieren

    public static final String SEARCH_MODE_DIRECTORY = "directory";
    public static final String SEARCH_MODE_FILE = "file";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    //todo einen "go up" button einfügen, welcher einen ein verzeichniss nach oben bringt
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        getReadExternalStoragePermission();

        mCurrentDir = Environment.getExternalStorageDirectory();

        Bundle args = getArguments();
        String mDialogTitle = args.getString("title") != null ? args.getString("title") : "";
        mSearchMode = args.getString("search_mode") != null ? args.getString("search_mode") : SEARCH_MODE_FILE;

        prepareListView();

        refreshListView(mCurrentDir);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

        dialog.setTitle(mDialogTitle);

        dialog.setView(mListView);

        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (mCallback != null)
                    mCallback.onDirectorySelected(mCurrentDir, "");
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
     * Methode um die ListView vorzubereiten.
     */
    private void prepareListView() {

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
    }

    /**
     * Methode um die Berechtigung zum lesen und schreiben des externen Speichers zu erhalten
     */
    private void getReadExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 0);
        }
    }

    /**
     * Methode um die Unterverzeichnisse zu einem Verzeichniss in einer ListView anzeigt
     *
     * @param currentDir Das aktuelle Verzeichniss
     */
    private void refreshListView(File currentDir) {

        mSubDirectories = getSubDirectories(currentDir);

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, mSubDirectories);

        mListView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    /**
     * Methode um einen listener auf das Directory choosen event zu setzen.
     *
     * @param listener Listener der aufgerufen werden soll
     */
    public void setDirectoryChosenListener(DirectoryPickerDialog.OnDirectorySelected listener) {

        mCallback = listener;
    }

    public interface OnDirectorySelected {

        void onDirectorySelected(File file, String tag);
    }
}
