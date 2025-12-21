package com.hexagraph.jagrati_android.util

import com.hexagraph.jagrati_android.model.attendance.AttendanceRecordResponse
import java.time.LocalDate
import java.time.YearMonth
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

    /**
     * Get all dates when the person was present in a specific month
     */
    fun getPresentDatesInMonth(records: List<AttendanceRecordResponse>, year: Int, month: Int): Set<Int> {
        val yearMonth = YearMonth.of(year, month)
        return records
            .mapNotNull { record ->
                try {
                    val recordDate = LocalDate.parse(record.date, DateTimeFormatter.ISO_LOCAL_DATE)
                    if (recordDate.year == year && recordDate.monthValue == month) {
                        recordDate.dayOfMonth
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
            .toSet()
    }

    /**
     * Get attendance count for a specific month
     */
    fun getPresentCountInMonth(records: List<AttendanceRecordResponse>, year: Int, month: Int): Int {
        return getPresentDatesInMonth(records, year, month).size
    }

    /**
     * Get total attendance count
     */
    fun getTotalPresentCount(records: List<AttendanceRecordResponse>): Int {
        return records.size
    }

    /**
     * Get attendance stats for all months in a year
     */
    fun getMonthlyStats(records: List<AttendanceRecordResponse>, year: Int): Map<Int, Int> {
        return (1..12).associateWith { month ->
            getPresentCountInMonth(records, year, month)
        }
    }

    /**
     * Get all years that have attendance records
     */
    fun getYearsWithRecords(records: List<AttendanceRecordResponse>): List<Int> {
        return records
            .mapNotNull { record ->
                try {
                    LocalDate.parse(record.date, DateTimeFormatter.ISO_LOCAL_DATE).year
                } catch (e: Exception) {
                    null
                }
            }
            .distinct()
            .sortedDescending()
    }

    /**
     * Get month name from number
     */
    fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> ""
        }
    }

    /**
     * Get short month name
     */
    fun getShortMonthName(month: Int): String {
        return getMonthName(month).take(3)
    }

    /**
     * Check if a specific date is present in records
     */
    fun isDatePresent(records: List<AttendanceRecordResponse>, year: Int, month: Int, day: Int): Boolean {
        val targetDate = LocalDate.of(year, month, day).format(DateTimeFormatter.ISO_LOCAL_DATE)
        return records.any { it.date == targetDate }
    }

    /**
     * Get attendance record for a specific date
     */
    fun getRecordForDate(records: List<AttendanceRecordResponse>, year: Int, month: Int, day: Int): AttendanceRecordResponse? {
        val targetDate = LocalDate.of(year, month, day).format(DateTimeFormatter.ISO_LOCAL_DATE)
        return records.find { it.date == targetDate }
    }
}
