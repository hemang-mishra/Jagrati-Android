package com.hexagraph.jagrati_android.ui.screens.studentProfile

import com.hexagraph.jagrati_android.model.AttendanceModel
import com.hexagraph.jagrati_android.model.StudentDetails

data class StudentProfileUIState(
    val studentInfo: StudentDetails = StudentDetails(),
    val studentAttendance: List<AttendanceModel> = emptyList()
)
