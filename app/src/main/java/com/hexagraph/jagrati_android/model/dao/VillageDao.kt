package com.hexagraph.jagrati_android.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.hexagraph.jagrati_android.model.Village
import kotlinx.coroutines.flow.Flow

@Dao
interface VillageDao {
    @Upsert
    suspend fun upsertVillage(village: Village)

    @Delete
    suspend fun deleteVillage(village: Village)

    @Query("DELETE FROM villages WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM villages WHERE is_active=1")
    fun getAllActiveVillages(): Flow<List<Village>>

    @Query("SELECT * FROM villages WHERE id = :id")
    suspend fun getVillage(id: Long): Village?

    @Query("SELECT EXISTS(SELECT * FROM villages WHERE id = :id)")
    suspend fun villageExists(id: Long): Boolean

    @Query("SELECT * FROM villages WHERE is_active=1 AND name LIKE '%' || :query || '%'")
    suspend fun getVillagesByQuery(query: String): List<Village>
}

