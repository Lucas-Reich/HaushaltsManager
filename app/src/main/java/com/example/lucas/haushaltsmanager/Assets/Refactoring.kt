package com.example.lucas.haushaltsmanager.Assets

import com.example.lucas.changelogdialog.ChangelogItem

class Refactoring(additionalInformation: String?) : ChangelogItem {
    private val description: String = additionalInformation ?: ""

    override fun getDescription(): String {
        return description
    }

    override fun getType(): String {
        return "Refactoring"
    }
}