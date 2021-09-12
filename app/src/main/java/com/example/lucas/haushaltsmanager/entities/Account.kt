package com.example.lucas.haushaltsmanager.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import java.util.*

@Entity(tableName = "accounts")
@Parcelize
class Account(
    @PrimaryKey val id: @WriteWith<UUIDParceler> UUID,
    var name: String,
    var price: Price
) : Parcelable {

    constructor(accountName: String, price: Price) : this(UUID.randomUUID(), accountName, price)

    fun isSet(): Boolean {
        return name != ""
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Account) {
            return false
        }

        return name == other.name && id == other.id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + price.hashCode()
        return result
    }

    override fun toString(): String {
        return name
    }
}