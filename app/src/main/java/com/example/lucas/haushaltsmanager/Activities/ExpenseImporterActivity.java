package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Dialogs.ProgressBarDialog;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImportStrategies.ImportBookingStrategy;
import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.Importer;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.CSVFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.CSVFile;
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.IFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.AccountParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.CategoryParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.DateParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.PriceParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.BookingParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.Saver;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Views.ButtonContainer;
import com.example.lucas.haushaltsmanager.Views.HeaderView;
import com.example.lucas.haushaltsmanager.Views.RequiredFieldsView;

import java.io.FileNotFoundException;

public class ExpenseImporterActivity extends AbstractAppCompatActivity implements RequiredFieldsView.OnMappingCreated {
    public static final String INTENT_FILE_PATH = "file_path";

    private Importer importer;

    private RequiredFieldsView requiredFieldsView;
    private FloatingActionButton startImportFAB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_importer);

        startImportFAB = findViewById(R.id.expense_importer_start_import_fab);

        requiredFieldsView = new RequiredFieldsView(this);

        initializeToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        guardAgainstMissingFile();

        CSVFile importFile = openFile(getFilePathFromExtras());

        importer = buildNewImporter(tryCreateFileReader(importFile));

        requiredFieldsView.configure(
                new ButtonContainer((LinearLayout) findViewById(R.id.expense_importer_button_container)),
                (HeaderView) findViewById(R.id.expense_importer_header),
                importFile.getHeaders(),
                importer.getRequiredFields()
        );
        requiredFieldsView.setListener(this);
    }

    @Override
    public void onMappingCreated(MappingList mapping) {
        importer.setMapping(mapping);

        showImportFAB();
    }

    private Importer buildNewImporter(IFileReader fileReader) {
        Currency mainCurrency = getMainCurrency();

        return new Importer(fileReader, new ImportBookingStrategy(
                new BookingParser(new PriceParser(mainCurrency), new CategoryParser(), new DateParser(), mainCurrency),
                new AccountParser(mainCurrency),
                Saver.create(this)
        ));
    }

    private void guardAgainstMissingFile() {
        String filePath = getFilePathFromExtras();

        if (!filePath.isEmpty()) {
            return;
        }

        errorCloseActivity();
    }

    private IFileReader tryCreateFileReader(CSVFile file) {
        try {
            return new CSVFileReader(file);
        } catch (FileNotFoundException e) {
            errorCloseActivity();

            return null; // TODO: Kann ich das irgendwie anders lösen?
        }
    }

    private CSVFile openFile(String path) {
        try {
            return CSVFile.open(path);
        } catch (FileNotFoundException | InvalidFileException e) {
            return null;
        }
    }

    private Currency getMainCurrency() {
        return new UserSettingsPreferences(this).getMainCurrency();
    }

    private void showImportFAB() {
        startImportFAB.setVisibility(View.VISIBLE); // TODO: Add nice FAB popUp animation (inflating)

        startImportFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ProgressBarDialog(ExpenseImporterActivity.this, importer).show();

                new Thread(importer).start();
            }
        });
    }

    private String getFilePathFromExtras() {
        BundleUtils bundle = new BundleUtils(getIntent().getExtras());

        return bundle.getString(INTENT_FILE_PATH, "");
    }

    private void errorCloseActivity() {
        Toast.makeText(this, R.string.generic_file_open_error, Toast.LENGTH_SHORT).show();

        finish();
    }
}
