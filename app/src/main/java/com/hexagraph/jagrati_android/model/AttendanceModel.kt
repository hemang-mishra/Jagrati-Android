package com.hexagraph.jagrati_android.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hexagraph.jagrati_android.model.dao.FaceInfoDao
import com.hexagraph.jagrati_android.model.dao.StudentDetailsDao
import com.hexagraph.jagrati_android.util.TimeUtils

@Entity(tableName = "attendance")
data class AttendanceModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pid: String = "",
    val personType: PersonType = PersonType.STUDENT,
    val attendanceDate: Long = System.currentTimeMillis(),
    val updateTimeMillis: Long = System.currentTimeMillis()
){
    val attendanceDayOfMonth: Int
        get() = TimeUtils.getDayOfMonthFromMillis(attendanceDate)

    val attendanceMonth: Int
        get() = TimeUtils.getMonthOfDayFromMillis(attendanceDate)

    val attendanceYear: Int
        get() = TimeUtils.getYearOfDayFromMillis(attendanceDate)

    val timeMillisRange: Pair<Long,Long>
        get() = TimeUtils.getTimeMillisRangeForDate(attendanceYear, attendanceMonth, attendanceDayOfMonth)

    suspend fun toStudentDetails(dao: StudentDetailsDao): StudentDetails{
        return dao.getStudentDetails(pid)
    }

    suspend fun toFaceInfo(dao: FaceInfoDao): FaceInfo{
        return dao.getFaceById(pid)
    }
}