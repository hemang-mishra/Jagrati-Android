package com.hexagraph.jagrati_android.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month


object DateTimeFormatter {
    //Format date in "DD MMM YYYY"
    fun formatDate(dateTime: LocalDateTime): String {
        return buildString {
            append(dateTime.day.toString().padStart(2, '0'))
            append(" ")
            append(getMonthAbbreviation(dateTime.month))
            append(" ")
            append(dateTime.year)
        }
    }

    fun formatDateTime(dateTime: LocalDateTime): String {
        return buildString {
            append(dateTime.day.toString().padStart(2, '0'))
            append(" ")
            append(getMonthAbbreviation(dateTime.month))
            append(" ")
            append(dateTime.year)
            append(", ")
            append(dateTime.hour.toString().padStart(2, '0'))
            append(":")
            append(dateTime.minute.toString().padStart(2, '0'))
        }
    }

    private fun getMonthAbbreviation(month: Month): String {
        return when (month) {
            Month.JANUARY -> "Jan"
            Month.FEBRUARY -> "Feb"
            Month.MARCH -> "Mar"
            Month.APRIL -> "Apr"
            Month.MAY -> "May"
            Month.JUNE -> "Jun"
            Month.JULY -> "Jul"
            Month.AUGUST -> "Aug"
            Month.SEPTEMBER -> "Sep"
            Month.OCTOBER -> "Oct"
            Month.NOVEMBER -> "Nov"
            Month.DECEMBER -> "Dec"
            else -> month.name.take(3)
        }
    }
}