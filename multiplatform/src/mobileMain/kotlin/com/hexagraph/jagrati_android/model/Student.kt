package com.hexagraph.jagrati_android.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
data class Student(
    @PrimaryKey
    @ColumnInfo(name = "pid")
    val pid: String,

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "year_of_birth")
    val yearOfBirth: Int? = null,

    @ColumnInfo(name = "gender")
    val gender: String,

    @ColumnInfo(name = "profile_pic_details")
    val profilePic: ImageKitResponse? = null,

    @ColumnInfo(name = "school_class")
    val schoolClass: String? = null,

    @ColumnInfo(name = "village_id")
    val villageId: Long,

    @ColumnInfo(name = "group_id")
    val groupId: Long,

    @ColumnInfo(name = "primary_contact_no")
    val primaryContactNo: String? = null,

    @ColumnInfo(name = "secondary_contact_no")
    val secondaryContactNo: String? = null,

    @ColumnInfo(name = "fathers_name")
    val fathersName: String? = null,

    @ColumnInfo(name = "mothers_name")
    val mothersName: String? = null,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "registered_by_pid")
    val registeredByPid: String? = null
){
    val pattern get(): String = "${firstName}_${pid}.png"
    val faceFileName get(): String = "Face_${pattern}"
    val imageFileName get(): String = "Image_${pattern}"
    val frameFileName get(): String = "Frame_${pattern}"
}