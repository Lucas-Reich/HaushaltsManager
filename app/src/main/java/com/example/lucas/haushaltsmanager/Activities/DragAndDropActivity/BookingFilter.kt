package com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity

import ro.andreidobrescu.basefilter.BaseFilter

class BookingFilter : BaseFilter() {
    var accountId: String? = null
    var fromDate: Long? = null
    var toDate: Long? = null
}