package com.hexagraph.jagrati_android.repository.student

import com.hexagraph.jagrati_android.model.StudentDetails
import com.hexagraph.jagrati_android.model.dao.StudentDetailsDao
import com.hexagraph.jagrati_android.util.Utils
import javax.inject.Inject

class AddStudentRepositoryImpl @Inject constructor(
    private val studentDetailsDao: StudentDetailsDao
): AddStudentRepository {
    override suspend fun upsertStudent(studentDetails: StudentDetails) {

        studentDetailsDao.updateStudentDetails(studentDetails)
    }

    override suspend fun checkIfStudentWithPIDExists(pid: String): Boolean {
        return studentDetailsDao.studentExists(pid)
    }

    override suspend fun getStudentDetails(pid: String): StudentDetails? {
        return studentDetailsDao.getStudentDetailsByPid(pid)
    }
}