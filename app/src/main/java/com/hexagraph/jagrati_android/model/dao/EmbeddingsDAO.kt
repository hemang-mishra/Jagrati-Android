package com.hexagraph.jagrati_android.model.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hexagraph.jagrati_android.model.FaceEmbeddingsEntity

@Dao
interface EmbeddingsDAO {
    @Upsert
    suspend fun upsertEmbeddings(embeddings: FaceEmbeddingsEntity)

    @Query("SELECT * FROM FaceEmbeddingsEntity WHERE pid = :pid")
    suspend fun getEmbeddingsByPid(pid: String): FaceEmbeddingsEntity?
}