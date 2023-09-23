package com.example.carzoneapp.helper

import java.util.Date

fun calculateDateDifference(startDate: Date, endDate: Date) {
    var different = endDate.time - startDate.time
    println("startDate : $startDate")
    println("endDate : $endDate")
    println("different : $different")
    val secondsInMilli: Long = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24
    val elapsedDays = different / daysInMilli
    different %= daysInMilli
    val elapsedHours = different / hoursInMilli
    different %= hoursInMilli
    val elapsedMinutes = different / minutesInMilli
    different %= minutesInMilli
    val elapsedSeconds = different / secondsInMilli
    println(
        "$elapsedDays days, $elapsedHours hours, $elapsedMinutes minutes, $elapsedSeconds seconds"
    )

}
//
//        fun main() {
//            val simpleDateFormat = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//
//            try {
//                val date1 = simpleDateFormat.parse("10/10/2013 11:30:10")
//                val date2 = simpleDateFormat.parse("13/10/2013 20:35:55")
//
//                val dateTimeUtils = DateTimeUtils()
//                dateTimeUtils.printDifference(date1, date2)
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

