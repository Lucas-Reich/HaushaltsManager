package com.example.lucas.haushaltsmanager.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.codekidlabs.storagechooser.StorageChooser;
import com.example.lucas.haushaltsmanager.AbstractAppCompatActivity;
import com.example.lucas.haushaltsmanager.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.FileDuplicator;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Services.BackupCreatorService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BackupActivity extends AbstractAppCompatActivity {
    private static final String TAG = BackupActivity.class.getSimpleName();

    private FloatingActionButton mCreateBackupFab;
    private Button mChooseDirectoryBtn;
    private Directory mBackupDirectory;
    private ListView mListView;

    //.SavedDataFile
    final String mBackupExtension = ".sdf";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_backup);

        initializeToolbar();

        mBackupDirectory = getBackupDirectory();

        mChooseDirectoryBtn = findViewById(R.id.create_backup_directory_btn);
        mListView = findViewById(R.id.create_backup_list_view);
        TextView emptyText = findViewById(R.id.empty_list_view);
        mListView.setEmptyView(emptyText);
        //todo die backups sollen so sortiert sein, dass der aktuellste eintrag an erster stelle steht

        mCreateBackupFab = findViewById(R.id.create_backup_create_backup_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateListView();

        mChooseDirectoryBtn.setHint(mBackupDirectory.getName());
        mChooseDirectoryBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StorageChooser storageChooser = new StorageChooser.Builder()
                        .withActivity(BackupActivity.this)
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

                        mBackupDirectory = new Directory(directory);
                        mChooseDirectoryBtn.setText(mBackupDirectory.getName());

                        updateListView();
                    }
                });
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final File file = new File(mBackupDirectory + "/" + mListView.getItemAtPosition(position));
                Bundle bundle = new Bundle();
                bundle.putString(ConfirmationDialog.TITLE, getString(R.string.restoreBackup));
                bundle.putString(ConfirmationDialog.CONTENT, getString(R.string.restore_backup_confirmation));

                ConfirmationDialog confirmationDialog = new ConfirmationDialog();
                confirmationDialog.setOnConfirmationListener(new ConfirmationDialog.OnConfirmationResult() {
                    @Override
                    public void onConfirmationResult(boolean restore) {
                        if (restore)
                            restoreDatabaseState(file);
                    }
                });
                confirmationDialog.setArguments(bundle);
                confirmationDialog.show(getFragmentManager(), "backup_confirm_restore");
            }
        });

        mCreateBackupFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(BasicTextInputDialog.TITLE, getResources().getString(R.string.choose_new_backup_name));
                bundle.putString(BasicTextInputDialog.HINT, getDefaultBackupName());

                BasicTextInputDialog basicDialog = new BasicTextInputDialog();
                basicDialog.setOnTextInputListener(new BasicTextInputDialog.OnTextInput() {

                    @Override
                    public void onTextInput(String textInput) {

                        createBackup(textInput);
                    }
                });
                basicDialog.setArguments(bundle);
                basicDialog.show(getFragmentManager(), "backup_name");
            }
        });
    }

    private Directory getBackupDirectory() {
        AppInternalPreferences preferences = new AppInternalPreferences(this);
        return preferences.getBackupDirectory();
    }

    /**
     * Methode um die ListView neu zu laden, wenn sich die Anzahl der Backups geändert hat.
     */
    private void updateListView() {

        List<String> mOldBackups = getBackupsInDirectory(mBackupDirectory);

        ArrayAdapter<String> mListViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mOldBackups);

        mListView.setAdapter(mListViewAdapter);

        mListViewAdapter.notifyDataSetChanged();
    }

    /**
     * Methode um ein Backup zu erstellen
     */
    private void createBackup(@Nullable String backupName) {

        Intent backupServiceIntent = new Intent(this, BackupCreatorService.class);
        backupServiceIntent.putExtra(BackupCreatorService.USER_TRIGGERED, true);
        backupServiceIntent.putExtra(BackupCreatorService.BACKUP_DIR_NAME, (Parcelable) mBackupDirectory);
        if (backupName != null)
            backupServiceIntent.putExtra(BackupCreatorService.BACKUP_NAME, backupName);

        startService(backupServiceIntent);

        updateListView();
    }

    /**
     * Methode um das vom User ausgewählte Backup wiederherzustellen
     *
     * @param backupDatabase Backup das wiederhergestellt werden soll
     */
    private void restoreDatabaseState(File backupDatabase) {
        //todo funktioniere ich?
        //Bitte überprüfe ob der User eine Datenbankdatei auswählen kann und diese dann laden
        FileDuplicator fileDuplicator = new FileDuplicator(backupDatabase, new Directory(getDatabasePath(ExpensesDbHelper.DB_NAME).toString()));
        fileDuplicator.copy(ExpensesDbHelper.DB_NAME + ".db");
    }

    /**
     * Methode um die Namen aller App eigenen Backups (.sdf) Datein aus einem Verzeichniss zu erhalten
     *
     * @param directory Verzeichniss, in dem gesucht werden soll.
     * @return Dateinamen der Backups
     */
    private List<String> getBackupsInDirectory(File directory) {
        List<String> backups = new ArrayList<>();
        for (File file : directory.listFiles()) {
            if (file.getName().contains(mBackupExtension))
                backups.add(file.getName());
        }

        return backups;
    }

    /**
     * Methode um den default Namen eines Backups zu bekommen.
     *
     * @return Default Backupname
     */
    private String getDefaultBackupName() {
        return new SimpleDateFormat("YYYYMMdd", Locale.US).format(Calendar.getInstance().getTime()).concat("_Backup");
    }
}
