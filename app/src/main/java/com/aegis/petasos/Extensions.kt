package com.aegis.petasos

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import java.text.DateFormat
import java.util.*

fun Long.formatted(): String {
    return DateFormat.getDateTimeInstance(
        DateFormat.SHORT,
        DateFormat.SHORT
    ).format(Date(this))
}

fun String.getLocale(): Locale {
    return when {
        this == "zh_CH" -> {
            Locale.SIMPLIFIED_CHINESE
        }
        this == "zh_TW" -> {
            Locale.TRADITIONAL_CHINESE
        }
        else -> {
            Locale.getDefault()
        }
    }
}

fun ImageView.setImg(@DrawableRes id: Int) {
    setImageDrawable(ContextCompat.getDrawable(this.context, id))
}