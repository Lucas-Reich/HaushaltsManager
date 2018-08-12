package com.example.lucas.haushaltsmanager.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.codekidlabs.storagechooser.StorageChooser;
import com.example.lucas.haushaltsmanager.Dialogs.BasicTextInputDialog;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Services.BackupCreatorService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateBackupActivity extends AppCompatActivity {
    private static final String TAG = CreateBackupActivity.class.getSimpleName();

    private FloatingActionButton mCreateBackupFab;
    private Button mChooseDirectoryBtn;
    private File mBackupDirectory = BackupCreatorService.getBackupDirectory();
    private ListView mListView;
    private ImageButton mBackArrow;

    //.SavedDataFile
    final String mBackupExtension = ".sdf";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_backup);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mBackArrow = (ImageButton) findViewById(R.id.back_arrow);

        mChooseDirectoryBtn = (Button) findViewById(R.id.create_backup_directory_btn);
        mListView = (ListView) findViewById(R.id.create_backup_list_view);
        TextView emptyText = (TextView) findViewById(R.id.empty_list_view);
        mListView.setEmptyView(emptyText);
        //todo die backups sollen so sortiert sein, dass der aktuellste eintrag an erster stelle steht

        mCreateBackupFab = (FloatingActionButton) findViewById(R.id.create_backup_create_backup_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateListView();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mBackArrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        mChooseDirectoryBtn.setHint(mBackupDirectory.getName());
        mChooseDirectoryBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StorageChooser storageChooser = new StorageChooser.Builder()
                        .withActivity(CreateBackupActivity.this)
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

                        mBackupDirectory = new File(directory);
                        mChooseDirectoryBtn.setText(mBackupDirectory.getName());

                        updateListView();
                    }
                });
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                showConfirmationDialog(new File(mBackupDirectory + "/" + mListView.getItemAtPosition(position)));
            }
        });

        mCreateBackupFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("title", getResources().getString(R.string.choose_new_backup_name));
                bundle.putString("hint", getDefaultBackupName());

                BasicTextInputDialog basicDialog = new BasicTextInputDialog();
                basicDialog.setOnTextInputListener(new BasicTextInputDialog.BasicDialogCommunicator() {

                    @Override
                    public void onTextInput(String textInput) {

                        createBackup(textInput);
                    }
                });
                basicDialog.setArguments(bundle);
                basicDialog.show(getFragmentManager(), "create_backup_name");
            }
        });
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
        backupServiceIntent.putExtra("user_triggered", true);
        backupServiceIntent.putExtra("backup_directory", mBackupDirectory.toString());
        if (backupName != null)
            backupServiceIntent.putExtra("backup_name", backupName);

        startService(backupServiceIntent);

        updateListView();
    }

    /**
     * Methode um das vom User ausgewählte Backup wiederherzustellen
     *
     * @param fileName Backup das wiederhergestellt werden soll
     */
    private void restoreDatabaseState(File fileName) {

        try {

            BackupCreatorService.copyFile(fileName, getDatabasePath("expenses.db"));
        } catch (IOException e) {

            Log.e(TAG, "restoreDatabaseState: Fehler beim kopieren der Backupdatei", e);
        }
    }

    /**
     * Methode um einen ConfirmationDialog zu zeigen der den User fragt, ob er sich sicher ist, dass er das Backup wiederherstellen möchte.
     *
     * @param file Das Backup
     */
    private void showConfirmationDialog(final File file) {

        //todo durch confirmationDialog ersetzen
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.restoreBackup);

        builder.setMessage(getResources().getString(R.string.restore_backup_confirmation) + ": " + file.getName());

        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                restoreDatabaseState(file);
            }
        });

        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
            }
        });

        builder.create();

        builder.show();
    }

    /**
     * Methode um die Namen aller App eigenen Backups (.sdf) Datein aus einem Verzeichniss zu erhalten
     *
     * @param directory Verzeichniss, in dem gesucht werden soll.
     * @return Dateinamen der Backups
     */
    private List<String> getBackupsInDirectory(File directory) {

        List<String> backups = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files != null && files.length != 0) {

            for (int i = files.length - 1; i >= 0; i--) {
                if (files[i].getName().contains(mBackupExtension)) {
                    backups.add(files[i].getName());
                }
            }
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
