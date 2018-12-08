package com.example.lucas.haushaltsmanager.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ErrorAlertDialog;
import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseObjectExporter;
import com.example.lucas.haushaltsmanager.FileAdapter;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;
import com.example.lucas.haushaltsmanager.R;

import java.io.File;
import java.util.List;

public class ImportExportActivity extends AbstractAppCompatActivity {
    private static final String TAG = ImportExportActivity.class.getSimpleName();
    private static final String IMPORTABLE_FILE_CSV_REGEX = ".*.csv";

    private ListView mListView;
    private FloatingActionButton mAddExportFab;
    private Button mSelectDirectoryBtn;
    private Directory mSelectedDirectory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        mListView = findViewById(R.id.activity_import_importable_files_list);
        mListView.setEmptyView(findViewById(R.id.empty_list_view));

        mAddExportFab = findViewById(R.id.activity_import_add_export_btn);
        mSelectDirectoryBtn = findViewById(R.id.activity_import_directory_picker);

        initializeToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSelectDirectoryBtn.setHint(R.string.hint_choose_directory);
        mSelectDirectoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasFilePermission())
                    getFilePermission();

                StorageChooser storageChooser = new StorageChooser.Builder()
                        .withActivity(ImportExportActivity.this)
                        .withFragmentManager(getFragmentManager())
                        .withMemoryBar(true)
                        .allowAddFolder(true)
                        .allowCustomPath(true)
                        .setType(StorageChooser.DIRECTORY_CHOOSER)
                        .build();

                storageChooser.show();
                storageChooser.setOnSelectListener(new StorageChooser.OnSelectListener() {

                    @Override
                    public void onSelect(String directory) {
                        mSelectedDirectory = new Directory(directory);
                        mSelectDirectoryBtn.setText(mSelectedDirectory.getName());

                        updateListView(mSelectedDirectory);
                    }
                });
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TODO: Die Importierfunktion wieder aktivieren
//                final File selectedFile = mSelectableFileList.get(position);
//
//                Bundle bundle = new Bundle();
//                bundle.putString(ConfirmationDialog.TITLE, getString(R.string.confirmation_dialog_title));
//                bundle.putString(ConfirmationDialog.CONTENT, getString(R.string.import_bookings_confirmation));
//
//                ConfirmationDialog confirmationDialog = new ConfirmationDialog();
//                confirmationDialog.setArguments(bundle);
//                confirmationDialog.setOnConfirmationListener(new ConfirmationDialog.OnConfirmationResult() {
//
//                    @Override
//                    public void onConfirmationResult(boolean importExpenses) {
//                        if (importExpenses) {
//
//                            ExpenseObjectImporter fileImporter = new ExpenseObjectImporter(selectedFile, ImportExportActivity.this);
//                            fileImporter.readAndSaveExpenseObjects();
//                        }
//                    }
//                });
//                confirmationDialog.show(getFragmentManager(), "import_confirm_import");

                Toast.makeText(ImportExportActivity.this, R.string.importing_not_yet_supported, Toast.LENGTH_SHORT).show();
            }
        });

        mAddExportFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSelectedDirectory == null) {

                    Bundle bundle = new Bundle();
                    bundle.putString(ErrorAlertDialog.TITLE, getString(R.string.error));
                    bundle.putString(ErrorAlertDialog.CONTENT, getString(R.string.error_no_directory_selected));

                    ErrorAlertDialog errorDialog = new ErrorAlertDialog();
                    errorDialog.setArguments(bundle);
                    errorDialog.show(getFragmentManager(), "import_error_export");

                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString(ConfirmationDialog.TITLE, getString(R.string.create_export));
                bundle.putString(ConfirmationDialog.CONTENT, getString(R.string.export_directory_confirmation));

                ConfirmationDialog confirmationDialog = new ConfirmationDialog();
                confirmationDialog.setArguments(bundle);
                confirmationDialog.setOnConfirmationListener(new ConfirmationDialog.OnConfirmationResult() {
                    @Override
                    public void onConfirmationResult(boolean result) {

                        ExpenseObjectExporter fileExporter = new ExpenseObjectExporter(mSelectedDirectory, ImportExportActivity.this);
                        fileExporter.convertAndExportExpenses(getAllExpenses());

                        updateListView(mSelectedDirectory);
                    }
                });
                confirmationDialog.show(getFragmentManager(), "import_confirm_export");
            }
        });

        updateListView(new Directory(getFilesDir().toString()));
    }

    private boolean hasFilePermission() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        int result = ContextCompat.checkSelfPermission(this, permission);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Methode um die Berechtigung zum lesen und schreiben des externen Speichers zu erhalten
     */
    private void getFilePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    private List<File> getImportableFilesInDirectory(Directory dir) {
        return FileUtils.listFiles(
                dir,
                false,
                IMPORTABLE_FILE_CSV_REGEX
        );
    }

    /**
     * Methode um die ListView nach einer Änderung anzuzeigen.
     */
    private void updateListView(Directory selectedDirectory) {
        Log.i(TAG, "Refreshing ListView");

        List<File> importableFileList = getImportableFilesInDirectory(selectedDirectory);

        FileAdapter fileAdapter = new FileAdapter(importableFileList, this);

        mListView.setAdapter(fileAdapter);

        fileAdapter.notifyDataSetChanged();
    }

    /**
     * Methode um alle Buchungen aus der Datenbank abzufragen.
     *
     * @return Alle Buchungen
     */
    private List<ExpenseObject> getAllExpenses() {
        // IMPROVEMENT: Den User fragen, welche Buchung genau exportiert werden sollen (welches Konto, Zeitraum, ...)
        ExpenseRepository repo = new ExpenseRepository(this);

        return repo.getAll();
    }
}
