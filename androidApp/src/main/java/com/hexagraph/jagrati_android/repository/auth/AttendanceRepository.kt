package com.hexagraph.jagrati_android.repository.auth

import com.hexagraph.jagrati_android.model.attendance.AttendanceRecordListResponse
import com.hexagraph.jagrati_android.model.attendance.AttendanceReportResponse
import com.hexagraph.jagrati_android.model.attendance.BulkAttendanceRequest
import com.hexagraph.jagrati_android.model.attendance.BulkAttendanceResultResponse
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    suspend fun markStudentAttendanceBulk(request: BulkAttendanceRequest): Flow<Resource<BulkAttendanceResultResponse>>

    suspend fun markVolunteerAttendanceBulk(request: BulkAttendanceRequest): Flow<Resource<BulkAttendanceResultResponse>>

    suspend fun getAttendanceReport(date: String): Flow<Resource<AttendanceReportResponse>>

    suspend fun deleteStudentAttendance(id: Long): Flow<Resource<Unit>>

    suspend fun deleteVolunteerAttendance(id: Long): Flow<Resource<Unit>>

    suspend fun getStudentAttendance(pid: String): Flow<Resource<AttendanceRecordListResponse>>

    suspend fun getVolunteerAttendance(pid: String): Flow<Resource<AttendanceRecordListResponse>>
}

