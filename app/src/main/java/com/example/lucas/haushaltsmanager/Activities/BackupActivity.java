package com.example.lucas.haushaltsmanager.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;
import com.example.lucas.haushaltsmanager.PreferencesHelper.AppInternalPreferences;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.Entities.Directory;
import com.example.lucas.haushaltsmanager.Utils.FileUtils;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_backup);

        initializeToolbar();

        mBackupDirectory = getBackupDirectory();

        mChooseDirectoryBtn = findViewById(R.id.create_backup_directory_btn);
        mListView = findViewById(R.id.create_backup_list_view);
        mListView.setEmptyView(findViewById(R.id.empty_list_view));

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
                if (!hasFilePermission())
                    requestFilePermission();

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
                    public void onConfirmationResult(boolean restoreDatabase) {
                        if (restoreDatabase) {
                            //todo Was ist wenn die ausgewählte Datenbank und die aktuelle Datenbank unterschiedliche Versionen haben
                            //Die alte Datenbank hat beispielsweise einige neue Tabellen oder Felder noch nicht, welche ich nach und nach der Datenbank hinzugefügt habe
                            boolean isRestored = FileUtils.copy(
                                    file,
                                    getDatabaseDir(),
                                    ExpensesDbHelper.DB_NAME
                            );

                            showToast(isRestored ? R.string.backup_restoring_successful : R.string.backup_restoring_failed);
                        }
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
                    public void onTextInput(String backupName) {
                        Intent backupServiceIntent = new Intent(BackupActivity.this, BackupCreatorService.class);
                        backupServiceIntent.putExtra(BackupCreatorService.INTENT_USER_TRIGGERED, true);
                        backupServiceIntent.putExtra(BackupCreatorService.INTENT_BACKUP_DIR, (Parcelable) mBackupDirectory);
                        backupServiceIntent.putExtra(BackupCreatorService.INTENT_PENDING_INTENT, createPendingResult(100, new Intent(), 0));
                        if (backupName != null)
                            backupServiceIntent.putExtra(BackupCreatorService.INTENT_BACKUP_NAME, backupName);

                        startService(backupServiceIntent);
                    }
                });
                basicDialog.setArguments(bundle);
                basicDialog.show(getFragmentManager(), "backup_name");
            }
        });
    }

    private boolean hasFilePermission() {
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        final int hasPermission = ContextCompat.checkSelfPermission(this, permission);

        return hasPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestFilePermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    /**
     * Methode um das aktuelle Backupverzeichniss zu bekommen.
     *
     * @return Pfad des Backupverzeichnisses
     */
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            showToast(resultCode == 200 ? R.string.created_backup : R.string.could_not_create_backup);

            updateListView();
        }
    }

    private void showToast(@StringRes int message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    private Directory getDatabaseDir() {
        File databaseFile = getDatabasePath(ExpensesDbHelper.DB_NAME);
        String path = databaseFile.toString();
        path = path.substring(0, path.lastIndexOf(File.separator));

        return new Directory(path);
    }

    /**
     * Methode um die Namen aller App eigenen Backups (.sdf) Datein aus einem Verzeichniss zu erhalten
     *
     * @param directory Verzeichniss, in dem gesucht werden soll.
     * @return Dateinamen der Backups
     */
    private List<String> getBackupsInDirectory(Directory directory) {
        List<File> backups = FileUtils.listFiles(directory, true, BackupCreatorService.BACKUP_EXTENSION_REGEX);
        List<String> backupNames = new ArrayList<>();
        for (File file : backups) {
            backupNames.add(file.getName());
        }

        return backupNames;
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
