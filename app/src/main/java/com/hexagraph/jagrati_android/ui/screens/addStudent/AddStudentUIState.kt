package com.hexagraph.jagrati_android.ui.screens.addStudent

import com.hexagraph.jagrati_android.model.StudentDetails

data class AddStudentUIState(
    val studentData: StudentDetails = StudentDetails(),
    val isStudentNew: Boolean = true,
    val isFacialDataAdded: Boolean = false,
)
