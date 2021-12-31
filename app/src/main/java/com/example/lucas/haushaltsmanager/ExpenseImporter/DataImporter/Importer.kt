package com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter

import com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter.ImportStrategies.IImportStrategy
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidInputException
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.IFileReader
import com.example.lucas.haushaltsmanager.ExpenseImporter.ISub
import com.example.lucas.haushaltsmanager.ExpenseImporter.MappingList
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField

class Importer(
    val fileReader: IFileReader,
    val importStrategy: IImportStrategy
) : IImporter {
    private val subs: MutableList<ISub> = ArrayList()
    private var mappingList: MappingList? = null

    override fun run() {
        while (fileReader.moveToNext()) {
            try {
                val line = fileReader.currentLine
                importStrategy.handle(line, mappingList)

                notifySubs(true)
            } catch (e: NoMappingFoundException) {
                notifySubs(false)
            } catch (e: InvalidInputException) {
                notifySubs(false)
            }
        }

        releaseResources()
    }

    override fun abort() {
        fileReader.close()
        importStrategy.abort()

        releaseResources()
    }

    override fun addSub(sub: ISub) {
        subs.add(sub)
    }

    override fun removeSub(sub: ISub) {
        subs.remove(sub)
    }

    override fun totalSteps(): Int {
        return fileReader.lineCount
    }

    override fun getRequiredFields(): MutableList<IRequiredField> {
        return importStrategy.requiredFields
    }

    fun setMapping(mapping: MappingList) {
        this.mappingList = mapping
    }

    private fun releaseResources() {
        fileReader.close()

        importStrategy.finish()
    }

    private fun notifySubs(successful: Boolean) {
        for (sub in subs) {
            if (successful) {
                sub.notifySuccess()
                break
            }

            sub.notifyFailure()
        }
    }
}