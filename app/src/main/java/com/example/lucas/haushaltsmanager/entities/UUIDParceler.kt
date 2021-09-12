package com.example.lucas.haushaltsmanager.entities

import android.os.Parcel
import kotlinx.parcelize.Parceler
import java.util.*

object UUIDParceler : Parceler<UUID> {
    override fun create(parcel: Parcel): UUID = UUID.fromString(parcel.readString())

    override fun UUID.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this.toString())
    }
}