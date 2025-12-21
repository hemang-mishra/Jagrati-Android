package com.hexagraph.jagrati_android.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object TimeUtils {
    fun getTimeMillisRangeForDate(timeInMillis: Long): Pair<Long, Long>{
        return getTimeMillisRangeForDate(
            year = getYearOfDayFromMillis(timeInMillis),
            month = getMonthOfDayFromMillis(timeInMillis),
            day = getDayOfMonthFromMillis(timeInMillis)
        )
    }

    fun getTimeMillisRangeForDate(year: Int, month:Int, day: Int): Pair<Long,Long>{
        val calendar = Calendar.getInstance()

        calendar.set(year, month - 1, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        calendar.set(year, month - 1, day, 23, 59, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis
        return Pair(startTime, endTime)
    }

    fun getYearOfDayFromMillis(timeInMillis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return calendar.get(Calendar.YEAR)
    }

    fun getMonthOfDayFromMillis(timeInMillis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return calendar.get(Calendar.MONTH) + 1 // Months are 0-based in Calendar
    }

    fun getDayOfMonthFromMillis(timeInMillis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun convertMillisToDate(currentTimeMillis: Long): String {
        val date = Date(currentTimeMillis)
        val dateFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
        return dateFormat.format(date)
    }
}