package com.aegis.petasos

import java.text.DateFormat
import java.util.*

fun Long.formatted(): String {
    return DateFormat.getDateTimeInstance(
        DateFormat.SHORT,
        DateFormat.SHORT
    ).format(Date(this))
}