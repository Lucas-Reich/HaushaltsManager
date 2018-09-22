package com.example.lucas.haushaltsmanager.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ErrorAlertDialog;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseObjectExporter;
import com.example.lucas.haushaltsmanager.FileAdapter;
import com.example.lucas.haushaltsmanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportExportActivity extends AppCompatActivity {

    private List<File> mSelectableFileList;
    private ListView mListView;
    private File mSelectedFile;
    private FloatingActionButton mAddExportFab;
    private Button mSelectDirectoryBtn;
    private File mSelectedDirectory;
    private ExpenseRepository mBookingRepo;

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
        setContentView(R.layout.activity_import_export);

        mBookingRepo = new ExpenseRepository(this);

        mSelectableFileList = new ArrayList<>();

        mListView = (ListView) findViewById(R.id.activity_import_importable_files_list);
        mAddExportFab = (FloatingActionButton) findViewById(R.id.activity_import_add_export_btn);
        mSelectDirectoryBtn = (Button) findViewById(R.id.activity_import_directory_picker);

        initializeToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSelectDirectoryBtn.setHint(R.string.hint_choose_directory);
        mSelectDirectoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReadExternalStoragePermission();

                StorageChooser choose = new StorageChooser.Builder()
                        .withActivity(ImportExportActivity.this)
                        .withFragmentManager(getFragmentManager())
                        .withMemoryBar(true)
                        .allowAddFolder(true)
                        .allowCustomPath(true)
                        .setType(StorageChooser.DIRECTORY_CHOOSER)
                        .build();
                choose.show();
                choose.setOnSelectListener(new StorageChooser.OnSelectListener() {

                    @Override
                    public void onSelect(String directory) {
                        File file = new File(directory);
                        mSelectDirectoryBtn.setText(file.getName());
                        mSelectedDirectory = file;

                        mSelectableFileList.clear();
                        mSelectableFileList.addAll(getImportableFilesInDirectory(file));

                        updateListView();
                    }
                });
            }
        });

        TextView emptyListViewText = new TextView(this);
        emptyListViewText.setText(R.string.no_files_to_import);
        mListView.setEmptyView(emptyListViewText);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectedFile = mSelectableFileList.get(position);

                Bundle bundle = new Bundle();
                bundle.putString(ConfirmationDialog.TITLE, getString(R.string.confirmation_dialog_title));
                bundle.putString(ConfirmationDialog.CONTENT, getString(R.string.import_bookings_confirmation));

                ConfirmationDialog confirmationDialog = new ConfirmationDialog();
                confirmationDialog.setArguments(bundle);
                confirmationDialog.setOnConfirmationListener(new ConfirmationDialog.OnConfirmationResult() {
                    @Override
                    public void onConfirmationResult(boolean result) {

                        // todo aktivieren
                        // ExpenseObjectImporter fileImporter = new ExpenseObjectImporter(mSelectedFile, this);
                        // fileImporter.readAndSaveExpenseObjects();

                        Toast.makeText(ImportExportActivity.this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
                    }
                });
                confirmationDialog.show(getFragmentManager(), "import_confirm_import");
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

                        ExpenseObjectExporter fileExporter = new ExpenseObjectExporter(mSelectedDirectory);
                        fileExporter.convertAndExportExpenses(getAllExpenses());

                        getImportableFilesInDirectory(mSelectedDirectory);
                        updateListView();
                    }
                });
                confirmationDialog.show(getFragmentManager(), "import_confirm_export");
            }
        });

        getImportableFilesInDirectory(new File(getFilesDir().toString()));
        updateListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Methode um eine Toolbar anzuzeigen die den Titel und einen Zurückbutton enthält.
     */
    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);

        //schatten der toolbar
        if (Build.VERSION.SDK_INT >= 21)
            toolbar.setElevation(10.f);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Methode um die Berechtigung zum lesen und schreiben des externen Speichers zu erhalten
     */
    private void getReadExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 0);
        }
    }

    /**
     * Methode um alle Datein in einem Verzeichniss zu bekommen, die importiert werden können.
     *
     * @param path Pfad, welcher überprüft werden soll
     * @return Liste mit den unterstützten Datein
     */
    private List<File> getImportableFilesInDirectory(File path) {
        mSelectableFileList.clear();
        if (path.listFiles() != null) {
            for (File file : path.listFiles()) {
                for (SupportedFileExtensions fileExtension : SupportedFileExtensions.values()) {
                    if (file.getName().contains(fileExtension.toString())) {
                        mSelectableFileList.add(file);
                        break;
                    }
                }

            }
        }
        return new ArrayList<>();
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
     * Methode um alle Buchungen aus der Datenbank abzufragen.
     * <p>
     * TODO: 23.06.2018 Den User fragen welche Buchungen er genau exportieren möchte
     *
     * @return Alle Buchungen
     */
    private List<ExpenseObject> getAllExpenses() {
        return mBookingRepo.getAll();
    }
}
