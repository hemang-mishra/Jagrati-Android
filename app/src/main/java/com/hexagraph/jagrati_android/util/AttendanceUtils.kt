package com.hexagraph.jagrati_android.util

import com.hexagraph.jagrati_android.model.attendance.AttendanceRecordResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object AttendanceUtils {

    /**
     * Get the last date when the student was present
     * @param records List of attendance records
     * @return Last present date as string or null if never present
     */
    fun getLastPresentDate(records: List<AttendanceRecordResponse>): String? {
        return records
            .maxByOrNull { it.date }
            ?.date
    }

    /**
     * Count the number of times present in the last week
     * @param records List of attendance records
     * @return Count of days present in the last week
     */
    fun getPresentCountLastWeek(records: List<AttendanceRecordResponse>): Int {
        val today = LocalDate.now()
        val lastWeek = today.minusWeeks(1)

        return records.count { record ->
            val recordDate = LocalDate.parse(record.date, DateTimeFormatter.ISO_LOCAL_DATE)
            recordDate.isAfter(lastWeek) && recordDate.isBefore(today.plusDays(1))
        }
    }

    /**
     * Count the number of times present in the last month
     * @param records List of attendance records
     * @return Count of days present in the last month
     */
    fun getPresentCountLastMonth(records: List<AttendanceRecordResponse>): Int {
        val today = LocalDate.now()
        val lastMonth = today.minusMonths(1)

        return records.count { record ->
            val recordDate = LocalDate.parse(record.date, DateTimeFormatter.ISO_LOCAL_DATE)
            recordDate.isAfter(lastMonth) && recordDate.isBefore(today.plusDays(1))
        }
    }

    /**
     * Format date from ISO format to readable format
     * @param isoDate Date in ISO format (yyyy-MM-dd)
     * @return Formatted date string (dd MMM yyyy)
     */
    fun formatDate(isoDate: String): String {
        return try {
            val date = LocalDate.parse(isoDate, DateTimeFormatter.ISO_LOCAL_DATE)
            date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        } catch (e: Exception) {
            isoDate
        }
    }

    /**
     * Format date-time from ISO format to readable format
     * @param isoDateTime DateTime in ISO format
     * @return Formatted date-time string
     */
    fun formatDateTime(isoDateTime: String): String {
        return try {
            val dateTime = java.time.LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
        } catch (e: Exception) {
            isoDateTime
        }
    }
}

