package com.example.lucas.haushaltsmanager.Activities.ImportExportTab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TabImport extends Fragment {

    private List<File> mSelectableFilesList;

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

        return rootView;
    }

    /**
     * Methode um alle Datein in einem Verzeichniss zu bekommen, die importiert werden können.
     *
     * @param file Pfad, welcher überprüft werden soll
     * @return Liste mit den unterstützten Datein
     */
    private List<File> getImportableFilesInDirectory(File file) {
        //todo erstelle funktionalität
        return new ArrayList<>();
    }

    private ExpenseObject stringToExpense(String expenseString){

        return ExpenseObject.createDummyExpense(getContext());
    }

    /**
     * Methode die von der ParentActivity aufgerufen wird, wenn der User ein neues Directory ausgewählt hat.
     *
     * @param file Pfad zum neuen Directory
     */
    public void onDirectorySelected(File file) {
        //todo passe die ListView an
    }
}
