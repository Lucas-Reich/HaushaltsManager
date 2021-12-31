package com.example.lucas.haushaltsmanager.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.lucas.haushaltsmanager.Dialogs.ProgressBarDialog
import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.IImporter
import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImportStrategies.ImportBookingStrategy
import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.Importer
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.CSVFileReader
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.CSVFile
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.IFileReader
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.AccountParser
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.CategoryParser
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.DateParser
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.AbsDoubleParser
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.BookingParser
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.PriceParser.PriceParser
import com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService.Saver
import com.example.lucas.haushaltsmanager.R
import com.example.lucas.haushaltsmanager.Utils.BundleUtils
import com.example.lucas.haushaltsmanager.Views.ButtonContainer
import com.example.lucas.haushaltsmanager.Views.RequiredFieldsView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.FileNotFoundException

class ExpenseImporterActivity : AbstractAppCompatActivity() {
    companion object {
        const val INTENT_FILE_PATH = "file_path"
        private const val TAG = "ExpenseImporterActivity"
    }

    private lateinit var requiredFieldsView: RequiredFieldsView
    private lateinit var startImportFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_importer)

        requiredFieldsView = RequiredFieldsView(this)
        startImportFab = findViewById(R.id.expense_importer_start_import_fab)

        initializeToolbar()
    }

    override fun onStart() {
        super.onStart()

        guardAgainstMissingFile()

        val importFile = openFile(getFilePathFromExtras())
        if (isFinishing || null == importFile) {
            return
        }

        val importer = buildNewImporter(tryCreateFileReader(importFile))

        requiredFieldsView.configure(
            ButtonContainer(findViewById(R.id.expense_importer_button_container)),
            findViewById(R.id.expense_importer_header),
            importFile.headers,
            importer.requiredFields
        )
        requiredFieldsView.setListener {
            importer.setMapping(it)

            showImportFAB(importer)
        }
    }

    private fun buildNewImporter(fileReader: IFileReader?): Importer {
        return Importer(
            fileReader!!, ImportBookingStrategy(
                BookingParser(PriceParser(AbsDoubleParser()), DateParser()),
                AccountParser(),
                CategoryParser(),
                Saver.create(this)
            )
        )
    }

    private fun guardAgainstMissingFile() {
        val filePath = getFilePathFromExtras()

        if (filePath.isNotEmpty()) {
            return
        }

        errorCloseActivity()
    }

    private fun tryCreateFileReader(file: CSVFile): IFileReader? {
        return try {
            CSVFileReader(file)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "Could not find file ${file.name}", e)
            errorCloseActivity()

            null
        }
    }

    private fun openFile(path: String): CSVFile? {
        try {
            return CSVFile.open(path)
        } catch (e: FileNotFoundException) {
            Log.e("Importer", "Could not open file", e)
        } catch (e: InvalidFileException) {
            Log.e("Importer", "Could not open file", e)
        }

        errorCloseActivity()
        return null
    }

    private fun showImportFAB(importer: IImporter) {
        startImportFab.visibility = View.VISIBLE

        startImportFab.setOnClickListener {
            ProgressBarDialog(this, importer).show()

            Thread(importer).start()
        }
    }

    private fun getFilePathFromExtras(): String {
        val bundle = BundleUtils(intent.extras!!)

        return bundle.getString(INTENT_FILE_PATH, "")
    }

    private fun errorCloseActivity() {
        Toast.makeText(this, R.string.generic_file_open_error, Toast.LENGTH_LONG).show()

        finish()
    }
}