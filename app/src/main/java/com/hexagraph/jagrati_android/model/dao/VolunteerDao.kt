package com.hexagraph.jagrati_android.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.hexagraph.jagrati_android.model.Volunteer
import kotlinx.coroutines.flow.Flow

@Dao
interface VolunteerDao {
    @Upsert
    suspend fun upsertVolunteer(volunteer: Volunteer)

    @Delete
    suspend fun deleteVolunteer(volunteer: Volunteer)

    @Query("DELETE FROM volunteer WHERE pid = :pid")
    suspend fun deleteByPid(pid: String)

    @Query("SELECT * FROM volunteer WHERE is_active=1")
    fun getAllActiveVolunteers(): Flow<List<Volunteer>>

    @Query("SELECT * FROM volunteer WHERE pid = :pid")
    suspend fun getVolunteer(pid: String): Volunteer?

    @Query("SELECT EXISTS(SELECT * FROM volunteer WHERE pid = :pid)")
    suspend fun volunteerExists(pid: String): Boolean

    @Query("SELECT * FROM volunteer WHERE is_active=1 AND first_name LIKE '%' || :query || '%' OR last_name LIKE '%' || :query || '%' OR roll_number LIKE '%' || :query || '%'")
    suspend fun getVolunteersByQuery(query: String): List<Volunteer>

    @Query("SELECT pid FROM volunteer WHERE is_active=0")
    suspend fun getAllDeletedVolunteerPids(): List<String>
}

