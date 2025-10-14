package com.hexagraph.jagrati_android.service.auth

import com.hexagraph.jagrati_android.model.attendance.AttendanceRecordListResponse
import com.hexagraph.jagrati_android.model.attendance.AttendanceReportResponse
import com.hexagraph.jagrati_android.model.attendance.BulkAttendanceRequest
import com.hexagraph.jagrati_android.model.attendance.BulkAttendanceResultResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KtorAttendanceService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun markStudentAttendanceBulk(request: BulkAttendanceRequest): BulkAttendanceResultResponse {
        return client.post("$baseUrl/api/attendance/students/mark-bulk") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun markVolunteerAttendanceBulk(request: BulkAttendanceRequest): BulkAttendanceResultResponse {
        return client.post("$baseUrl/api/attendance/volunteers/mark-bulk") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getAttendanceReport(date: String): AttendanceReportResponse {
        return client.get("$baseUrl/api/attendance/report") {
            parameter("date", date)
        }.body()
    }

    suspend fun deleteStudentAttendance(id: Long) {
        client.delete("$baseUrl/api/attendance/students/$id")
    }

    suspend fun deleteVolunteerAttendance(id: Long) {
        client.delete("$baseUrl/api/attendance/volunteers/$id")
    }

    suspend fun getStudentAttendance(pid: String): AttendanceRecordListResponse {
        return client.get("$baseUrl/api/attendance/students/$pid").body()
    }

    suspend fun getVolunteerAttendance(pid: String): AttendanceRecordListResponse {
        return client.get("$baseUrl/api/attendance/volunteers/$pid").body()
    }
}

