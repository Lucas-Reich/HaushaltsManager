package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationAlertDialog;
import com.example.lucas.haushaltsmanager.Dialogs.DirectoryPickerDialog;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.FileAdapter;
import com.example.lucas.haushaltsmanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportExportActivityVer3 extends AppCompatActivity implements DirectoryPickerDialog.OnDirectorySelected, ConfirmationAlertDialog.OnConfirmationResult {

    private List<File> mSelectableFileList;
    private ListView mListView;
    private File mSelectedFile;
    private FloatingActionButton mAddExportFab;
    private Button mSelectDirectoryBtn;
    private Toolbar mToolbar;

    private enum SupportedFileExtensions {
        CSV,
        CUSTOM_FILE_EXTENSION;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export_ver3);

        mSelectableFileList = new ArrayList<>();

        mListView = (ListView) findViewById(R.id.activity_import_importable_files_list);
        mAddExportFab = (FloatingActionButton) findViewById(R.id.activity_import_add_export_btn);
        mSelectDirectoryBtn = (Button) findViewById(R.id.activity_import_directory_picker);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSelectDirectoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", "Title");// todo change
                bundle.putString("search_mode", DirectoryPickerDialog.SEARCH_MODE_DIRECTORY);

                DirectoryPickerDialog directoryPicker = new DirectoryPickerDialog();
                directoryPicker.setArguments(bundle);
                directoryPicker.show(getFragmentManager(), "import_select_directory");
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectedFile = mSelectableFileList.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.confirmation_dialog_title));
                bundle.putString("message", getString(R.string.import_bookings_confirmation));

                ConfirmationAlertDialog confirmationDialog = new ConfirmationAlertDialog();
                confirmationDialog.setArguments(bundle);
                confirmationDialog.show(getFragmentManager(), "import_confirm_import");
            }
        });

        mAddExportFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", "Erstelle Export");// todo change
                bundle.putString("message", "Möchtest du wirklich deine Daten in das aktuelle Verzeichniss Exportieren?");// todo change

                ConfirmationAlertDialog confirmationDialog = new ConfirmationAlertDialog();
                confirmationDialog.setArguments(bundle);
                confirmationDialog.show(getFragmentManager(), "import_confirm_export");
            }
        });

        getImportableFilesInDirectory(new File(getFilesDir().toString()));
        updateListView();
    }

    /**
     * Methode um alle Datein in einem Verzeichniss zu bekommen, die importiert werden können.
     *
     * @param path Pfad, welcher überprüft werden soll
     * @return Liste mit den unterstützten Datein
     */
    private List<File> getImportableFilesInDirectory(File path) {
        for (File file : path.listFiles()) {
            for (SupportedFileExtensions fileExtension : SupportedFileExtensions.values()) {
                if (file.getName().contains(fileExtension.toString())) {
                    mSelectableFileList.add(file);
                    break;
                }
            }

        }
        return new ArrayList<>();
    }

    /**
     * Methode um eine komplette Buchung in einen String zu konvertieren.
     * todo Überlegen ob es sinn macht eine toJson methode in die Entities einzubauen, welche die aufgabe übernimmt
     *
     * @param expenseString String welcher zu einer Buchung konvertiert werden soll
     * @return Buchung
     */
    private ExpenseObject stringToExpense(String expenseString) {

        return ExpenseObject.createDummyExpense(this);
    }

    /**
     * Methode um die ListView nach einer Änderung anzuzeigen.
     */
    private void updateListView() {

        FileAdapter fileAdapter = new FileAdapter(mSelectableFileList, this);

        mListView.setAdapter(fileAdapter);

        fileAdapter.notifyDataSetChanged();
    }

    /**
     * Methode die von der ParentActivity aufgerufen wird, wenn der User ein neues Directory ausgewählt hat.
     *
     * @param file Pfad zum neuen Directory
     */
    @Override
    public void onDirectorySelected(File file, String tag) {
        if (tag.equals("import_select_directory") && file.isDirectory()) {

            mSelectableFileList.clear();
            mSelectableFileList.addAll(getImportableFilesInDirectory(file));

            updateListView();
        }
    }

    /**
     * Methode die den Callback des ConfirmationAlertDialogs implementiert.
     *
     * @param result Von user gegebene Antwort.
     * @param tag    mitgesendetes Tag
     */
    @Override
    public void onConfirmationResult(boolean result, String tag) {
        if (!result)
            return;

        switch (tag) {
            case "import_confirm_import":
                //todo import prozedur starten
                // soll noch der dateityp geprüft werden (getFileExtension)? also nur csvs erlauben oder so
                mSelectedFile.getName();
                Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
                break;
            case "import_confirm_export":
                //todo rexport prozedur starten
                Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
                break;
            default:
                return;
        }
    }

    /**
     * Methode, welche den Dateityp einer Datei extrahiert
     *
     * @param file Datei
     * @return Dateityp der angegebenen Datei
     */
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.indexOf(".");

        return fileName.substring(dotIndex + 1);
    }
}
