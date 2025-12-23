package com.hexagraph.jagrati_android.util

import com.hexagraph.jagrati_android.model.Volunteer
import com.hexagraph.jagrati_android.model.volunteer.UpdateVolunteerRequest

fun UpdateVolunteerRequest.toVolunteer(pid: String, isActive: Boolean = true): Volunteer = Volunteer(
    pid = pid,
    rollNumber = rollNumber,
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    gender = gender?.name ?: "",
    alternateEmail = alternateEmail,
    batch = batch,
    programme = programme,
    streetAddress1 = streetAddress1,
    streetAddress2 = streetAddress2,
    pincode = pincode,
    city = city,
    state = state,
    dateOfBirth = dateOfBirth ?: "",
    contactNumber = contactNumber,
    college = college,
    branch = branch,
    yearOfStudy = yearOfStudy,
    profilePic = profilePic,
    isActive = isActive
)