package com.example.lucas.haushaltsmanager.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.Dialogs.ProgressBarDialog;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.IImporter;
import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.Importer;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.CSVFile;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.FileReader.FileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.FileReader.IFileReader;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Files.IFile;
import com.example.lucas.haushaltsmanager.ExpenseImporter.HeaderHolder;
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.AccountMappings.ImportableAccountTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.ImportableBookingDate;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.ImportableBookingTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.ImportableCategoryTitle;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.PriceMappings.ImportablePriceType;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.PriceMappings.ImportablePriceValue;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.Parser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.Saver;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.Utils.BundleUtils;
import com.example.lucas.haushaltsmanager.Views.ButtonContainer;
import com.example.lucas.haushaltsmanager.Views.HeaderView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ExpenseImporterActivity extends AbstractAppCompatActivity {
    public static final String INTENT_FILE_PATH = "file_path";

    private HeaderView headerView;
    private int displayedHeaderCount = 0;
    private List<HeaderHolder> headers = new ArrayList<>();
    private MappingList mappingList = new MappingList();
    private FloatingActionButton startImportFAB;

    private ButtonContainer buttonContainer;

    private IImporter importer;
    private IFileReader fileReader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_importer);

        headerView = findViewById(R.id.expense_importer_header);
        startImportFAB = findViewById(R.id.expense_importer_start_import_fab);
        buttonContainer = new ButtonContainer((LinearLayout) findViewById(R.id.expense_importer_button_container));

        buttonContainer.setOnButtonClickListener(new ButtonContainer.OnButtonContainerClick() {
            @Override
            public void onClick(String buttonText) {
                mappingList.addMapping(getCurrentHeader().generateMapping(buttonText));

                showNextHeaderHolder();
            }
        });

        initializeToolbar();

        initializeRequiredHeader();
    }

    @Override
    protected void onStart() {
        super.onStart();

        guardAgainstMissingFile();

        CSVFile importFile = getCSVFile();

        fileReader = tryCreateFileReader(importFile);

        buttonContainer.createButtons(importFile.getHeaders());

        showNextHeaderHolder();
    }

    private void guardAgainstMissingFile() {
        String filePath = getFilePathFromExtras();

        if (!filePath.isEmpty()) {
            return;
        }

        Toast.makeText(this, R.string.generic_file_open_error, Toast.LENGTH_SHORT).show();

        finish();
    }

    private IFileReader tryCreateFileReader(IFile file) {
        try {
            return FileReader.read(file);
        } catch (InvalidFileException | FileNotFoundException e) {
            Toast.makeText(this, "Could not open FileReader", Toast.LENGTH_SHORT).show();

            finish();

            return null; // TODO: Kann ich das irgendwie anders lösen?
        }
    }

    private void buildImporter() {
        importer = Importer.createCSVImporter(
                this.fileReader,
                Saver.create(this, new Parser(getMainCurrency(), mappingList))
        );
    }

    private Currency getMainCurrency() {
        return new UserSettingsPreferences(this).getMainCurrency();
    }

    private void showImportFAB() {
        buttonContainer.disableButtons();

        startImportFAB.setVisibility(View.VISIBLE); // TODO: Add nice FAB popUp animation (inflating)

        startImportFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ProgressBarDialog(ExpenseImporterActivity.this, importer).show();

                new Thread(importer).start();
            }
        });
    }

    private void showNextHeaderHolder() {
        if (displayedHeaderCount >= headers.size()) {

            buildImporter();

            showImportFAB();

            return;
        }

        headerView.bindHeader(headers.get(displayedHeaderCount));

        displayedHeaderCount++;
    }

    private HeaderHolder getCurrentHeader() {
        return headerView.getCurrentlyVisibleHeader();
    }

    private CSVFile getCSVFile() {
        try {
            return CSVFile.open(getFilePathFromExtras());
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private String getFilePathFromExtras() {
        BundleUtils bundle = new BundleUtils(getIntent().getExtras());

        return bundle.getString(INTENT_FILE_PATH, "");
    }

    private void initializeRequiredHeader() {
        headers.add(new HeaderHolder(ImportableAccountTitle.class, R.string.mapping_account_title));
        headers.add(new HeaderHolder(ImportableCategoryTitle.class, R.string.mapping_category_title));
        headers.add(new HeaderHolder(ImportableBookingDate.class, R.string.mapping_booking_date));
        headers.add(new HeaderHolder(ImportableBookingTitle.class, R.string.mapping_booking_title));
        headers.add(new HeaderHolder(ImportablePriceValue.class, R.string.mapping_price_value));
        headers.add(new HeaderHolder(ImportablePriceType.class, R.string.mapping_price_type));
    }
}
