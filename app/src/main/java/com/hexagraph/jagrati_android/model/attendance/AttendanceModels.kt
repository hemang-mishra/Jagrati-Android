package com.hexagraph.jagrati_android.model.attendance

import com.hexagraph.jagrati_android.model.Gender
import kotlinx.serialization.Serializable

@Serializable
data class BulkAttendanceRequest(
    val date: String,
    val pids: List<String>
)

@Serializable
data class BulkAttendanceResultResponse(
    val date: String,
    val totalRequested: Int,
    val inserted: Int,
    val skippedExisting: Int,
    val missingPids: List<String>
)

@Serializable
data class AttendanceRecordResponse(
    val id: Long,
    val date: String,
    val remarks: String?
)

@Serializable
data class AttendanceRecordListResponse(
    val attendees: List<AttendanceRecordResponse>
)

@Serializable
data class StudentVillageGenderCount(
    val villageId: Long,
    val villageName: String,
    val gender: Gender,
    val count: Long
)

@Serializable
data class VolunteerBatchCount(
    val batch: String?,
    val count: Long
)

@Serializable
data class PresentStudent(
    val pid: String,
    val aid: String,
    val firstName: String,
    val lastName: String,
    val gender: Gender,
    val villageId: Long,
    val villageName: String,
    val groupId: Long,
    val groupName: String
)

@Serializable
data class PresentVolunteer(
    val pid: String,
    val aid: String,
    val firstName: String,
    val lastName: String,
    val batch: String?,
    val rollNo: String
)

@Serializable
data class AttendanceReportResponse(
    val date: String,
    val studentsByVillageGender: List<StudentVillageGenderCount>,
    val volunteersByBatch: List<VolunteerBatchCount>,
    val presentStudents: List<PresentStudent>,
    val presentVolunteers: List<PresentVolunteer>
)
