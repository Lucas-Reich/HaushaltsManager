package com.example.lucas.haushaltsmanager.entities

import android.os.Parcelable
import com.example.lucas.haushaltsmanager.ExpenseImporter.FileReader.Files.File
import kotlinx.parcelize.Parcelize

@Parcelize
class Directory(private val dirPath: String) : Parcelable, File(dirPath) {
    init {
        if (!isDirectory) {
            throw IllegalArgumentException(String.format("%s not a directory", path));
        }
    }
}