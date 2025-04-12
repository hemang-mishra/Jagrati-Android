package com.hexagraph.jagrati_android.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.hexagraph.jagrati_android.model.StudentDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDetailsDao {
    @Upsert
    suspend fun updateStudentDetails(studentDetails: StudentDetails)

    @Delete
    suspend fun deleteStudentDetails(studentDetails: StudentDetails)

    @Query("SELECT * FROM student_details")
    fun getAllStudentDetails(): Flow<List<StudentDetails>>

    @Query("SELECT * FROM student_details WHERE pid = :pid")
    suspend fun getStudentDetails(pid: String): StudentDetails

    //Function to check if student with particular pid exists
    @Query("SELECT EXISTS(SELECT * FROM student_details WHERE pid = :pid)")
    suspend fun studentExists(pid: String): Boolean

    @Query("SELECT * FROM student_details WHERE pid = :pid")
    suspend fun getStudentDetailsByPid(pid: String): StudentDetails?

    @Query("SELECT * FROM student_details WHERE first_name LIKE '%' || :query || '%' OR last_name LIKE '%' || :query || '%'")
    suspend fun getStudentDetailsByQuery(query: String): List<StudentDetails>
}