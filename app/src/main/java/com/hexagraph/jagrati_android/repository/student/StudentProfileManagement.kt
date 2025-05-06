package com.hexagraph.jagrati_android.repository.student

import android.content.Context
import com.hexagraph.jagrati_android.model.StudentDetails

interface StudentProfileManagement {
    suspend fun deleteStudentProfile(pid: String, context: Context): Boolean

    suspend fun updateStudentProfile(studentDetails: StudentDetails)
}