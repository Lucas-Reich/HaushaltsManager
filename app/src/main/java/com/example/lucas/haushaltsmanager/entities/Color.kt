package com.example.lucas.haushaltsmanager.entities

import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*
import java.util.regex.Pattern

@Parcelize
class Color(val color: String) : Parcelable, Color() {
    @IgnoredOnParcel
    val validColorPttrn = "^#[0-9a-fA-F]{8}\$|#[0-9a-fA-F]{6}\$|#[0-9a-fA-F]{4}\$|#[0-9a-fA-F]{3}\$"

    init {
        guardAgainstInvalidColorString(color)
    }

    constructor(@ColorInt color: Int) : this(
        "#${Integer.toHexString(color)}"
    )

    companion object {
        @JvmStatic
        fun black() = Color("#000000")

        @JvmStatic
        fun white() = Color("#FFFFFF")

        @JvmStatic
        fun random(): com.example.lucas.haushaltsmanager.entities.Color {
            val randomInt = Random().nextInt(0xffffff + 1)

            return Color(String.format("#%06x", randomInt))
        }
    }

    fun getColorInt() = parseColor(color)

    private fun guardAgainstInvalidColorString(colorString: String) {
        if (assertIsColorString(colorString)) {
            return
        }

        throw IllegalArgumentException("Could not create Color from: '%s'".format(colorString))
    }

    private fun assertIsColorString(color: String): Boolean {
        return Pattern.compile(validColorPttrn)
            .matcher(color)
            .matches()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is com.example.lucas.haushaltsmanager.entities.Color) {
            return false;
        }

        return color == other.color;
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + validColorPttrn.hashCode()
        return result
    }
}