package com.hexagraph.jagrati_android.ui.screens.studentAttendance

import com.hexagraph.jagrati_android.model.AttendanceModel
import com.hexagraph.jagrati_android.model.ProcessedImage
import com.hexagraph.jagrati_android.model.StudentDetails

data class StudentAttendanceUIState(
    val currentListOfStudents: List<AttendanceModel> = emptyList(),
    val allProcessedImage: List<ProcessedImage> = emptyList(),
    val allStudentDetails: List<StudentDetails> = emptyList(),
    val isScanModeActive: Boolean = false,
    val dateMillis: Long = System.currentTimeMillis(),
)
