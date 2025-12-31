package com.example.expensetrackerclean.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formateDate(timestamp: Long): String {

    val sdf = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}