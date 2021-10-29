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
    val balance: Price
) : Parcelable {

    constructor(accountName: String, price: Price) : this(UUID.randomUUID(), accountName, price)

    fun isSet(): Boolean {
        return name != ""
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Account) {
            return false
        }

        return name == other.name
                && id == other.id
                && balance == other.balance
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + balance.hashCode()
        return result
    }
}