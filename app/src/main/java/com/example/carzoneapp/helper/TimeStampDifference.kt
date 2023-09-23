package com.example.carzoneapp.helper

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.Period
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit


@RequiresApi(Build.VERSION_CODES.O)
fun calculateTimeStampDifference(timestamp: Long, currentTimestamp: Long): List<String> {
    val instant1 = Instant.ofEpochMilli(timestamp)
    val instant2 = Instant.ofEpochMilli(currentTimestamp)
    val date1 = instant1.atZone(ZoneOffset.UTC).toLocalDate()
    val date2 = instant2.atZone(ZoneOffset.UTC).toLocalDate()
    val time1 = instant1.atZone(ZoneOffset.UTC).toLocalTime()
    val time2 = instant2.atZone(ZoneOffset.UTC).toLocalTime()
    val period = Period.between(date1, date2)
    val years = period.years
    val months = period.months
    val days = period.days
    val duration = ChronoUnit.SECONDS.between(time1, time2)
    val hours = duration / 3600
    val minutes = (duration % 3600) / 60
    val seconds = duration % 60
    val result = mutableListOf<String>()
    if (years > 0) {
        result.add("$years y")
    }
    if (months > 0) {
        result.add("$months mon")
    }
    if (days > 0) {
        result.add("$days d")
    }
    if (hours > 0) {
        result.add("$hours h")
    }
    if (minutes > 0) {
        result.add("$minutes min")
    }
    if (seconds > 0) {
        result.add("$seconds sec")
    }
    if (result.isEmpty()) {
        result.add("now")
    }
    return result
}
