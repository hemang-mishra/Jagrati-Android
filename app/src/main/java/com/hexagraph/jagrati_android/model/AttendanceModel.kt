package com.hexagraph.jagrati_android.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class AttendanceModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pid: String = "",
    val personType: PersonType = PersonType.STUDENT,
    val attendanceDate: Long = System.currentTimeMillis(),
    val updateTimeMillis: Long = System.currentTimeMillis()
){

}