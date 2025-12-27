package com.hexagraph.jagrati_android.model.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hexagraph.jagrati_android.model.FaceEmbeddingsCacheEntity

@Dao
interface EmbeddingsDAO {
    @Upsert
    suspend fun upsertEmbeddings(embeddings: FaceEmbeddingsCacheEntity)

    @Query("SELECT * FROM FaceEmbeddingsCacheEntity WHERE pid = :pid")
    suspend fun getEmbeddingsByPid(pid: String): FaceEmbeddingsCacheEntity?

    @Query("DELETE FROM FaceEmbeddingsCacheEntity WHERE pid = :pid")
    suspend fun deleteEmbeddingsByPid(pid: String)
}