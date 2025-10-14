package com.hexagraph.jagrati_android.repository.auth

import com.hexagraph.jagrati_android.model.attendance.AttendanceRecordListResponse
import com.hexagraph.jagrati_android.model.attendance.AttendanceReportResponse
import com.hexagraph.jagrati_android.model.attendance.BulkAttendanceRequest
import com.hexagraph.jagrati_android.model.attendance.BulkAttendanceResultResponse
import com.hexagraph.jagrati_android.service.auth.KtorAttendanceService
import com.hexagraph.jagrati_android.util.Resource
import com.hexagraph.jagrati_android.util.Utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class KtorAttendanceRepository(
    private val attendanceService: KtorAttendanceService
) : AttendanceRepository {
    override suspend fun markStudentAttendanceBulk(request: BulkAttendanceRequest): Flow<Resource<BulkAttendanceResultResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { attendanceService.markStudentAttendanceBulk(request) }
        emit(response)
    }

    override suspend fun markVolunteerAttendanceBulk(request: BulkAttendanceRequest): Flow<Resource<BulkAttendanceResultResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { attendanceService.markVolunteerAttendanceBulk(request) }
        emit(response)
    }

    override suspend fun getAttendanceReport(date: String): Flow<Resource<AttendanceReportResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { attendanceService.getAttendanceReport(date) }
        emit(response)
    }

    override suspend fun deleteStudentAttendance(id: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { attendanceService.deleteStudentAttendance(id) }
        emit(response)
    }

    override suspend fun deleteVolunteerAttendance(id: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { attendanceService.deleteVolunteerAttendance(id) }
        emit(response)
    }

    override suspend fun getStudentAttendance(pid: String): Flow<Resource<AttendanceRecordListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { attendanceService.getStudentAttendance(pid) }
        emit(response)
    }

    override suspend fun getVolunteerAttendance(pid: String): Flow<Resource<AttendanceRecordListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { attendanceService.getVolunteerAttendance(pid) }
        emit(response)
    }
}

