package com.hexagraph.jagrati_android.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.hexagraph.jagrati_android.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Upsert
    suspend fun upsertStudentDetails(studentDetails: Student)

    @Delete
    suspend fun deleteStudentDetails(studentDetails: Student)

    @Query("DELETE FROM student WHERE pid = :pid")
    suspend fun deleteByPid(pid: String)

    @Query("SELECT * FROM student WHERE is_active=1")
    fun getAllActiveStudentDetails(): Flow<List<Student>>

    @Query("SELECT * FROM student WHERE pid = :pid")
    suspend fun getStudentDetails(pid: String): Student

    //Function to check if student with particular pid exists
    @Query("SELECT EXISTS(SELECT * FROM student WHERE pid = :pid)")
    suspend fun studentExists(pid: String): Boolean

    @Query("SELECT * FROM student WHERE pid = :pid")
    suspend fun getStudentDetailsByPid(pid: String): Student?

    @Query("SELECT * FROM student WHERE is_active=1 AND first_name LIKE '%' || :query || '%' OR last_name LIKE '%' || :query || '%'")
    suspend fun getStudentDetailsByQuery(query: String): List<Student>
}