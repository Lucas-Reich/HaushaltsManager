package com.example.lucas.haushaltsmanager.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lucas.haushaltsmanager.App.app
import com.example.lucas.haushaltsmanager.R
import com.example.lucas.haushaltsmanager.entities.booking.ExpenseType
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import java.util.*

@Parcelize
@Entity(tableName = "categories")
class Category(
    @PrimaryKey val id: @WriteWith<UUIDParceler> UUID,
    var name: String,
    var color: Color,
    @ColumnInfo(name = "default_expense_type") var defaultExpenseType: ExpenseType
) : Parcelable {

    constructor(name: String, color: Color, expenseType: ExpenseType) : this(
        UUID.randomUUID(),
        name,
        color,
        expenseType
    )

    fun isSet(): Boolean {
        return name != app.getStringResource(R.string.no_name)
    }

    override fun toString() = name

    override fun equals(other: Any?): Boolean {
        if (other !is Category) {
            return false
        }

        return id == other.id
                && name == other.name
                && color == other.color
                && defaultExpenseType == other.defaultExpenseType
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + defaultExpenseType.hashCode()
        return result
    }
}