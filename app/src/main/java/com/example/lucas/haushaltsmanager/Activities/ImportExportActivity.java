package com.example.lucas.haushaltsmanager.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codekidlabs.storagechooser.StorageChooser;
import com.example.lucas.haushaltsmanager.CsvBookingExporter;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ErrorAlertDialog;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.AdditionalFunctionality.RecyclerItemClickListener;
import com.example.lucas.haushaltsmanager.RecyclerView.ItemCreator.ItemCreator;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.FileItem.FileItem;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.IRecyclerItem;
import com.example.lucas.haushaltsmanager.RecyclerView.ListAdapter.FileListRecyclerViewAdapter;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;
import com.example.lucas.haushaltsmanager.entities.Directory;
import com.example.lucas.haushaltsmanager.entities.booking.IBooking;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;

public class ImportExportActivity extends AbstractAppCompatActivity implements RecyclerItemClickListener.OnRecyclerItemClickListener {
    private static final String IMPORTABLE_FILE_CSV_REGEX = ".*.csv";

    private FloatingActionButton exportBookingsFab;
    private Button selectDirectoryBtn;
    private Directory selectedDirectory;

    private RecyclerView recyclerView;
    private FileListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        recyclerView = findViewById(R.id.activity_import_export_recycler_view);
        recyclerView.setLayoutManager(LayoutManagerFactory.vertical(this));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));

        exportBookingsFab = findViewById(R.id.activity_import_export_add_export_btn);
        selectDirectoryBtn = findViewById(R.id.activity_import_export_directory_picker);

        initializeToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        selectDirectoryBtn.setHint(R.string.hint_choose_directory);
        selectDirectoryBtn.setOnClickListener(v -> {
            if (!hasFilePermission()) {
                getFilePermission();
            }

            StorageChooser storageChooser = new StorageChooser.Builder()
                    .withActivity(ImportExportActivity.this)
                    .withFragmentManager(getFragmentManager())
                    .withMemoryBar(true)
                    .allowAddFolder(true)
                    .allowCustomPath(true)
                    .setType(StorageChooser.DIRECTORY_CHOOSER)
                    .build();

            storageChooser.show();
            storageChooser.setOnSelectListener(directory -> {
                selectedDirectory = new Directory(directory);
                selectDirectoryBtn.setText(selectedDirectory.getName());

                updateListView(selectedDirectory);
            });
        });

        exportBookingsFab.setOnClickListener(v -> {
            if (selectedDirectory == null) {

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
            confirmationDialog.setOnConfirmationListener(actionConfirmed -> {
                if (!actionConfirmed) {
                    return;
                }

                CsvBookingExporter fileExporter = new CsvBookingExporter(selectedDirectory, ImportExportActivity.this);
                File createdFile = fileExporter.writeToFile(getAllExpenses());

                adapter.insertItem(new FileItem(createdFile));
            });
            confirmationDialog.show(getFragmentManager(), "import_confirm_export");
        });

        updateListView(new Directory(getFilesDir().toString()));
    }

    @Override
    public void onClick(View v, IRecyclerItem item, int position) {
        Intent expenseImporterIntent = new Intent(ImportExportActivity.this, ExpenseImporterActivity.class);
        expenseImporterIntent.putExtras(createBundleWithSelectedFile(item));

        ImportExportActivity.this.startActivity(expenseImporterIntent);
    }

    @Override
    public void onLongClick(View v, IRecyclerItem item, int position) {
        // If item (importable file) is long clicked nothing should happen
    }

    private boolean hasFilePermission() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        int result = ContextCompat.checkSelfPermission(this, permission);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private Bundle createBundleWithSelectedFile(IRecyclerItem item) {
        Bundle bundle = new Bundle();
        bundle.putString(ExpenseImporterActivity.INTENT_FILE_PATH, item.getContent().toString());

        return bundle;
    }

    /**
     * Methode um die Berechtigung zum lesen und schreiben des externen Speichers zu erhalten
     */
    private void getFilePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    private void updateListView(Directory selectedDirectory) {
        adapter = new FileListRecyclerViewAdapter(
                loadData(selectedDirectory)
        );

        recyclerView.setAdapter(adapter);
    }

    private List<IRecyclerItem> loadData(Directory selectedDirectory) {
        List<File> importableFileList = getImportableFilesInDirectory(selectedDirectory);

        return ItemCreator.createFileItems(importableFileList);
    }

    private List<File> getImportableFilesInDirectory(Directory dir) {
        return FileUtils.listFiles(
                dir,
                false,
                IMPORTABLE_FILE_CSV_REGEX
        );
    }

    private List<IBooking> getAllExpenses() {
        // IMPROVEMENT: Den User fragen, welche Buchung genau exportiert werden sollen (welches Konto, Zeitraum, ...)
        return AppDatabase.getDatabase(this).bookingDAO().getAll();
    }
}
