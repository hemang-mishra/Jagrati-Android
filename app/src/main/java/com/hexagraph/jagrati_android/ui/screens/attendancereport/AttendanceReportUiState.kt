package com.hexagraph.jagrati_android.ui.screens.attendancereport

import com.hexagraph.jagrati_android.model.Gender
import com.hexagraph.jagrati_android.model.attendance.AttendanceReportResponse
import com.hexagraph.jagrati_android.model.attendance.PresentStudent
import com.hexagraph.jagrati_android.model.attendance.PresentVolunteer
import com.hexagraph.jagrati_android.model.attendance.StudentVillageGenderCount
import com.hexagraph.jagrati_android.model.attendance.VolunteerBatchCount

data class AttendanceReportUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedDateMillis: Long = System.currentTimeMillis(),
    val reportData: AttendanceReportResponse? = null,
    val selectedStudentVillage: Long? = null,
    val selectedStudentGender: Gender? = null,
    val selectedStudentGroup: Long? = null,
    val selectedVolunteerBatch: String? = null,
    val filteredStudents: List<PresentStudent> = emptyList(),
    val filteredVolunteers: List<PresentVolunteer> = emptyList(),
    val groupCounts: Map<Pair<Long, String>, Int> = emptyMap(),
    val isDeletingAttendance: Boolean = false,
    val canDeleteStudentAttendance: Boolean = false,
    val canDeleteVolunteerAttendance: Boolean = false,
    val studentProfilePics: Map<String, String?> = emptyMap(),
    val volunteerProfilePics: Map<String, String?> = emptyMap()
)
