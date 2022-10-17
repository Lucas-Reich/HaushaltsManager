package com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity

import com.yatatsu.fieldschema.TS
import ro.dobrescuandrei.roomdynamicdao.QueryBuilder
import ro.dobrescuandrei.roomdynamicdao.QueryWhereConditions

class BookingQueryBuilder(filter: BookingFilter) : QueryBuilder<BookingFilter>(filter) {
    override fun tableName(): String = TS.Booking

    override fun where(conditions: QueryWhereConditions): String {
        if (filter.accountId != null) {
            conditions.add("${TS.Booking_accountId} IN (x'${filter.accountId}')")
        }

        if (filter.fromDate != null) {
            conditions.add("${TS.Booking_date} >= ${filter.fromDate}")
        }

        if (filter.toDate != null) {
            conditions.add("${TS.Booking_date} < ${filter.toDate}")
        }

        return conditions.mergeWithAnd();
    }
}