package com.hexagraph.jagrati_android.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.hexagraph.jagrati_android.model.Groups
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupsDao {
    @Upsert
    suspend fun upsertGroup(group: Groups)

    @Delete
    suspend fun deleteGroup(group: Groups)

    @Query("DELETE FROM `groups` WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM `groups` WHERE is_active=1")
    fun getAllActiveGroups(): Flow<List<Groups>>

    @Query("SELECT * FROM `groups` WHERE id = :id")
    suspend fun getGroup(id: Long): Groups?

    @Query("SELECT EXISTS(SELECT * FROM `groups` WHERE id = :id)")
    suspend fun groupExists(id: Long): Boolean

    @Query("SELECT * FROM `groups` WHERE is_active=1 AND name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    suspend fun getGroupsByQuery(query: String): List<Groups>
}
