package com.hexagraph.jagrati_android.repository.student

import android.content.Context
import com.hexagraph.jagrati_android.model.FaceInfo
import com.hexagraph.jagrati_android.model.StudentDetails
import com.hexagraph.jagrati_android.model.dao.AttendanceDao
import com.hexagraph.jagrati_android.model.dao.EmbeddingsDAO
import com.hexagraph.jagrati_android.model.dao.FaceInfoDao
import com.hexagraph.jagrati_android.model.dao.StudentDetailsDao
import com.hexagraph.jagrati_android.util.FileUtility
import javax.inject.Inject


class StudentProfileManagementImpl @Inject constructor(
    private val studentDetailsDao: StudentDetailsDao,
    private val faceInfoDao: FaceInfoDao,
    private val embeddingsDAO: EmbeddingsDAO,
    private val attendanceDao: AttendanceDao
): StudentProfileManagement {


    override suspend fun deleteStudentProfile(pid: String, context: Context): Boolean {
        studentDetailsDao.deleteByPid(pid)
        faceInfoDao.delete(pid)
        embeddingsDAO.deleteEmbeddingsByPid(pid)
//        attendanceDao
        //Delete files here as well.
        FileUtility.deleteFile(context, FaceInfo.getFrameFileName(pid))
        FileUtility.deleteFile(context, FaceInfo.getImageFileName(pid))
        FileUtility.deleteFile(context, FaceInfo.getFaceFileName(pid))
        return true
    }

    override suspend fun updateStudentProfile(studentDetails: StudentDetails) {
        studentDetailsDao.updateStudentDetails(studentDetails)
    }
}