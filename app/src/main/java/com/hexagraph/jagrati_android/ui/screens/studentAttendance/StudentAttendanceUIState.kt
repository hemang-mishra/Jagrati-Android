package com.hexagraph.jagrati_android.ui.screens.studentAttendance

import com.hexagraph.jagrati_android.model.StudentDetails

data class StudentAttendanceUIState(
    val currentListOfStudents: List<StudentDetails> = emptyList(),
)
