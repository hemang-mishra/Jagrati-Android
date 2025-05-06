package com.hexagraph.jagrati_android.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.hexagraph.jagrati_android.model.AttendanceModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Upsert
    fun insertAttendance(attendance: AttendanceModel)

    @Query("SELECT * FROM attendance WHERE attendanceDate BETWEEN :start AND :end")
    suspend fun getAttendanceInRange(start: Long, end: Long): List<AttendanceModel>

    @Delete
    fun deleteAttendance(attendance: AttendanceModel)

    @Query("DELETE FROM attendance WHERE pid=:pid")
    suspend fun deleteByPid(pid: String)
}