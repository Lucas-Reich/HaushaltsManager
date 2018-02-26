package com.example.lucas.haushaltsmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class TabTwoExport extends Fragment {

    String TAG = TabTwoExport.class.getSimpleName();
    final String exportDirectory = "/Exports";
    final String mFileExtension = ".csv";
    ArrayList<ExpenseObject> mExpenses;
    ExpensesDataSource mDatabase;
    BasicDialog inputNameDialog;
    Calendar mCalendar;
    String mSeparator;
    String mFileName;
    String mRootDir;
    File mFile;

    Button mCreateFileBtn, mChoosePathBtn, mChooseNameBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRootDir = getActivity().getFilesDir().toString();
        mFile = new File(mRootDir + exportDirectory);

        //check if default Directory exists if not create it
        if (!mFile.exists()) {

            if (!mFile.mkdir())
                //throw new Exception("Failed to create new Directory!");

                Log.d(TAG, "Created new Directory " + mFile.toString());
        }

        mCalendar = Calendar.getInstance();
        mSeparator = ",";
        mFileName = "Export_" + mCalendar.get(Calendar.YEAR) + "_" + mCalendar.get(Calendar.DAY_OF_MONTH) + "_" + (mCalendar.get(Calendar.MONTH) + 1) + mFileExtension;

        mDatabase = new ExpensesDataSource(getContext());
        mDatabase.open();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        inputNameDialog.dismiss();
        mDatabase.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_two_exports, container, false);
        //todo auf der seite sollen auch noch alle alten exporte angezeigt werden

        mCreateFileBtn = (Button) rootView.findViewById(R.id.tab_two_exports_create_export);
        mCreateFileBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                createFile();
            }
        });

        mChooseNameBtn = (Button) rootView.findViewById(R.id.tab_two_exports_create_file_name);
        mChooseNameBtn.setText(mFileName);
        mChooseNameBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", getResources().getString(R.string.choose_new_file_name));

                inputNameDialog = new BasicDialog();
                inputNameDialog.setArguments(bundle);
                inputNameDialog.show(getActivity().getFragmentManager(), "input_file_name");
            }
        });

        mChoosePathBtn = (Button) rootView.findViewById(R.id.tab_two_exports_file_path);
        mChoosePathBtn.setText(String.format("%s%s", mRootDir, exportDirectory));
        mChoosePathBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //wähle den pfad aus todo
                //pfad kann nur aus bereits existierenden strukturen ausgewählt werden
            }
        });

        return rootView;
    }

    /**
     * Methode um den angebenen Pfad auf Komplikationen zu überpfüfen.
     *
     * @param file Dateipfad
     */
    private void validateFileName(File file, String fileName) {

        //todo anpassen
        //check ob bereits existent
        if (file.exists())
            createErrorAlertDialog();
        else {

            mFile = file;
            mFileName = fileName;
            mChooseNameBtn.setText(String.format("%s%s",fileName,mFileExtension));
        }

        //check ob man zugriff auf den Ordner hat todo
    }

    /**
     * Methode um einen AlertDialog zu erstellen, um den User auf einen Nameskonflikt hinzuweisen.
     */
    private void createErrorAlertDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        dialog.setTitle(R.string.error);

        dialog.setMessage(R.string.file_already_existing_err_msg);

        dialog.setPositiveButton(R.string.overwrite_existing_file, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                createFile();
            }
        });

        dialog.setNegativeButton(R.string.abort_file_writing, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //do nothing
            }
        });

        dialog.setNeutralButton(R.string.choose_new_file_name, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Bundle bundle = new Bundle();
                bundle.putString("title", getResources().getString(R.string.input_file_name));

                inputNameDialog = new BasicDialog();
                inputNameDialog.setArguments(bundle);
                inputNameDialog.show(getActivity().getFragmentManager(), "input_file_name");
            }
        });

        dialog.create().show();
    }

    /**
     * Methode um die Buchungen in eine Datei zu schreiben und diese Datei dann zu speichern
     */
    private void createFile() {

        mExpenses = mDatabase.getBookings();

        //zeige via ladezeiten an wie lange die operation noch dauert

        throw new UnsupportedOperationException("Datein können noch nicht erstellt werden");//todo
    }

    /**
     * Methode, welche einen Neuen FileName durch den BasicDialog erhält.
     *
     * @param fileName Neuer FileName
     * @param tag      Tag des BasiDialogs
     */
    public void onFileNameChanged(String fileName, String tag) {

        if (tag.equals("input_file_name")) {

            validateFileName(new File(mFile.toString() + "/" + fileName + mFileExtension), fileName);
        }
    }
}
