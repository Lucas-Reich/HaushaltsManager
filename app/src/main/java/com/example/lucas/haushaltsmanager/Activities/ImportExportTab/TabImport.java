package com.example.lucas.haushaltsmanager.Activities.ImportExportTab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lucas.haushaltsmanager.Dialogs.ConfirmationAlertDialog;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.FileAdapter;
import com.example.lucas.haushaltsmanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TabImport extends Fragment implements ConfirmationAlertDialog.OnConfirmationResult {

    private List<File> mSelectableFilesList;
    private ListView mListView;
    private File mSelectedFile;

    private enum SupportedFileExtensions {
        CSV,
        CUSTOM_FILE_EXTENSION;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectableFilesList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_import, container, false);

        mListView = (ListView) rootView.findViewById(R.id.import_importable_files_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectedFile = mSelectableFilesList.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.confirmation_dialog_title));
                bundle.putString("message", getString(R.string.import_bookings_confirmation));

                ConfirmationAlertDialog confirmationDialog = new ConfirmationAlertDialog();
                confirmationDialog.setArguments(bundle);
                confirmationDialog.show(getActivity().getFragmentManager(), "tab_import_confirm_import");
            }
        });
        return rootView;
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
                    mSelectableFilesList.add(file);
                    break;
                }
            }

        }
        return new ArrayList<>();
    }

    private ExpenseObject stringToExpense(String expenseString) {

        return ExpenseObject.createDummyExpense(getContext());
    }

    /**
     * Methode um die ListView nach einer Änderung anzuzeigen.
     */
    private void updateListView() {

        FileAdapter fileAdapter = new FileAdapter(mSelectableFilesList, getContext());

        mListView.setAdapter(fileAdapter);

        fileAdapter.notifyDataSetChanged();
    }

    /**
     * Methode die von der ParentActivity aufgerufen wird, wenn der User ein neues Directory ausgewählt hat.
     *
     * @param file Pfad zum neuen Directory
     */
    public void onDirectorySelected(File file) {
        if (file.isDirectory()) {

            mSelectableFilesList.clear();
            mSelectableFilesList.addAll(getImportableFilesInDirectory(file));

            updateListView();
        }
    }

    @Override
    public void onConfirmationResult(boolean result, String tag) {
        if (result && tag.equals("tab_import_confirm_import")) {
            switch (getFileExtension(mSelectedFile)) {
                //basierend auf der fileExtension soll die datei ausgelesen werden
            }
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
