package com.example.lucas.haushaltsmanager.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.codekidlabs.storagechooser.StorageChooser;
import com.example.lucas.haushaltsmanager.Backup.BackupUtils;
import com.example.lucas.haushaltsmanager.Backup.Handler.Decorator.DatabaseBackupHandler;
import com.example.lucas.haushaltsmanager.Backup.Handler.FileBackupHandler;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationDialog;
import com.example.lucas.haushaltsmanager.entities.Directory;
import com.example.lucas.haushaltsmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;

public class BackupActivity extends AbstractAppCompatActivity {
    private FloatingActionButton mCreateBackupFab;
    private Button mChooseDirectoryBtn;
    private Directory mBackupDirectory;
    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_backup);

        initializeToolbar();

        mBackupDirectory = BackupUtils.getBackupDirectory(this);

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
                            DatabaseBackupHandler backupHandler = new DatabaseBackupHandler(BackupActivity.this, new FileBackupHandler());
                            boolean successful = backupHandler.restore(file);

                            showToast(successful ? R.string.backup_restoring_successful : R.string.backup_restoring_failed);
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
                bundle.putString(BasicTextInputDialog.TITLE, getString(R.string.choose_new_backup_name));
                bundle.putString(BasicTextInputDialog.HINT, BackupUtils.getDefaultBackupName());

                BasicTextInputDialog basicDialog = new BasicTextInputDialog();
                basicDialog.setOnTextInputListener(new BasicTextInputDialog.OnTextInput() {

                    @Override
                    public void onTextInput(String backupName) {

                        DatabaseBackupHandler backupHandler = new DatabaseBackupHandler(BackupActivity.this, new FileBackupHandler());
                        boolean successful = backupHandler.backup(mBackupDirectory, backupName);

                        if (successful)
                            showToast(R.string.created_backup);
                        else
                            showToast(R.string.could_not_create_backup);

                        updateListView();
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
     * Methode um die ListView neu zu laden, wenn sich die Anzahl der Backups geändert hat.
     */
    private void updateListView() {
        List<String> mOldBackups = BackupUtils.getBackupsInDir(mBackupDirectory);

        ArrayAdapter<String> mListViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mOldBackups);

        mListView.setAdapter(mListViewAdapter);

        mListViewAdapter.notifyDataSetChanged();
    }

    private void showToast(@StringRes int message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}
