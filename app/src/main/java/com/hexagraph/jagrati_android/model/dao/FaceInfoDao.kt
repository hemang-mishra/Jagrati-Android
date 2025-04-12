package com.hexagraph.jagrati_android.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hexagraph.jagrati_android.model.FaceInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface FaceInfoDao {
    @Query("DELETE FROM FaceInfo")
    suspend fun clear()

    @Query("DELETE FROM FaceInfo WHERE pid = :id")
    suspend fun delete(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: FaceInfo)

    @Query("SELECT * FROM FaceInfo WHERE pid = :id")
    fun face(id: String): Flow<FaceInfo>

    @Query("SELECT * FROM FaceInfo ")
    fun faces(): Flow<List<FaceInfo>>

    @Query("SELECT * FROM FaceInfo ")
    suspend fun faceList(): List<FaceInfo>

    @Query("SELECT * FROM FaceInfo WHERE pid = :id")
    suspend fun getFaceById(id: String): FaceInfo

}