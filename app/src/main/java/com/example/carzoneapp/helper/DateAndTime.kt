package com.example.carzoneapp.helper

import com.example.domain.entity.DateAndTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun extractDateAndTime(date: Date): DateAndTime {
    val calendar = Calendar.getInstance()
    calendar.time = date

    val year = calendar.get(Calendar.YEAR)
    val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(date) // Get month name
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val amPm = SimpleDateFormat("a", Locale.getDefault()).format(date) // Get AM/PM marker

    return DateAndTime(
        year.toString(),
        month,
        day.toString(),
        hour.toString(),
        minute.toString(),
        second.toString(),
        amPm
    )
}