package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Database.ExpensesDataSource;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationAlertDialog;
import com.example.lucas.haushaltsmanager.Dialogs.DirectoryPickerDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ErrorAlertDialog;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseObjectExporter;
import com.example.lucas.haushaltsmanager.FileAdapter;
import com.example.lucas.haushaltsmanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportExportActivity extends AppCompatActivity implements ConfirmationAlertDialog.OnConfirmationResult {

    private List<File> mSelectableFileList;
    private ListView mListView;
    private File mSelectedFile;
    private FloatingActionButton mAddExportFab;
    private Button mSelectDirectoryBtn;
    private ImageButton mBackArrow;
    private File mSelectedDirectory;

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

        mSelectableFileList = new ArrayList<>();

        mListView = (ListView) findViewById(R.id.activity_import_importable_files_list);
        mAddExportFab = (FloatingActionButton) findViewById(R.id.activity_import_add_export_btn);
        mSelectDirectoryBtn = (Button) findViewById(R.id.activity_import_directory_picker);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBackArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        mSelectDirectoryBtn.setHint(R.string.hint_choose_directory);
        mSelectDirectoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.choose_directory));
                bundle.putString("search_mode", DirectoryPickerDialog.SEARCH_MODE_DIRECTORY);

                DirectoryPickerDialog directoryPicker = new DirectoryPickerDialog();
                directoryPicker.setArguments(bundle);
                directoryPicker.setDirectoryChosenListener(new DirectoryPickerDialog.OnDirectorySelected() {
                    @Override
                    public void onDirectorySelected(File file, String tag) {

                        mSelectDirectoryBtn.setText(file.getName());
                        mSelectedDirectory = file;

                        mSelectableFileList.clear();
                        mSelectableFileList.addAll(getImportableFilesInDirectory(file));

                        updateListView();
                    }
                });
                directoryPicker.show(getFragmentManager(), "import_select_directory");
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

                if (mSelectedDirectory == null) {

                    Bundle bundle = new Bundle();
                    bundle.putString("title", getString(R.string.error));
                    bundle.putString("message", getString(R.string.error_no_directory_selected));

                    ErrorAlertDialog errorDialog = new ErrorAlertDialog();
                    errorDialog.setArguments(bundle);
                    errorDialog.show(getFragmentManager(), "import_error_export");
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.create_export));
                bundle.putString("message", getString(R.string.export_directory_confirmation));

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

                // todo aktivieren
                // ExpenseObjectImporter fileImporter = new ExpenseObjectImporter(mSelectedFile, this);
                // fileImporter.readAndSaveExpenseObjects();

                Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
                break;
            case "import_confirm_export":

                ExpenseObjectExporter fileExporter = new ExpenseObjectExporter(mSelectedDirectory);
                fileExporter.convertAndExportExpenses(getAllExpenses());

                getImportableFilesInDirectory(mSelectedDirectory);
                updateListView();
                break;
        }
    }

    /**
     * Methode um alle Buchungen aus der Datenbank abzufragen.
     * <p>
     * TODO: 23.06.2018 Den User fragen welche Buchungen er genau exportieren möchte
     *
     * @return Alle Buchungen
     */
    private ArrayList<ExpenseObject> getAllExpenses() {
        ExpensesDataSource database = new ExpensesDataSource(this);
        database.open();
        ArrayList<ExpenseObject> expenses = database.getBookings();
        database.close();

        return expenses;
    }
}
