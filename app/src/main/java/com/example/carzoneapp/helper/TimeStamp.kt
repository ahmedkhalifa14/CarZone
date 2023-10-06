package com.example.carzoneapp.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun extractFormattedDate(timestamp: Long): String {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    return dateFormat.format(date)
}