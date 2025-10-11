package com.hexagraph.jagrati_android.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.hexagraph.jagrati_android.model.user.VolunteerDTO
import com.hexagraph.jagrati_android.model.Gender

@Entity(tableName = "volunteer")
data class Volunteer(
    @PrimaryKey
    @ColumnInfo(name = "pid")
    val pid: String,

    @ColumnInfo(name = "roll_number")
    val rollNumber: String? = null,

    @ColumnInfo(name = "first_name")
    val firstName: String,

    @ColumnInfo(name = "last_name")
    val lastName: String,

    @ColumnInfo(name = "gender")
    val gender: String,

    @ColumnInfo(name = "alternate_email")
    val alternateEmail: String? = null,

    @ColumnInfo(name = "batch")
    val batch: String? = null,

    @ColumnInfo(name = "programme")
    val programme: String? = null,

    @ColumnInfo(name = "street_address1")
    val streetAddress1: String? = null,

    @ColumnInfo(name = "street_address2")
    val streetAddress2: String? = null,

    @ColumnInfo(name = "pincode")
    val pincode: String? = null,

    @ColumnInfo(name = "city")
    val city: String? = null,

    @ColumnInfo(name = "state")
    val state: String? = null,

    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String,

    @ColumnInfo(name = "contact_number")
    val contactNumber: String? = null,

    @ColumnInfo(name = "college")
    val college: String? = null,

    @ColumnInfo(name = "branch")
    val branch: String? = null,

    @ColumnInfo(name = "year_of_study")
    val yearOfStudy: Int? = null,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)

fun Volunteer.toDTO(): VolunteerDTO = VolunteerDTO(
    pid = pid,
    rollNumber = rollNumber,
    firstName = firstName,
    lastName = lastName,
    gender = Gender.valueOf(gender),
    alternateEmail = alternateEmail,
    batch = batch,
    programme = programme,
    streetAddress1 = streetAddress1,
    streetAddress2 = streetAddress2,
    pincode = pincode,
    city = city,
    state = state,
    dateOfBirth = dateOfBirth,
    contactNumber = contactNumber,
    college = college,
    branch = branch,
    yearOfStudy = yearOfStudy,
    isActive = isActive
)

fun VolunteerDTO.toEntity(): Volunteer = Volunteer(
    pid = pid,
    rollNumber = rollNumber,
    firstName = firstName,
    lastName = lastName,
    gender = gender.name,
    alternateEmail = alternateEmail,
    batch = batch,
    programme = programme,
    streetAddress1 = streetAddress1,
    streetAddress2 = streetAddress2,
    pincode = pincode,
    city = city,
    state = state,
    dateOfBirth = dateOfBirth,
    contactNumber = contactNumber,
    college = college,
    branch = branch,
    yearOfStudy = yearOfStudy,
    isActive = isActive
)
