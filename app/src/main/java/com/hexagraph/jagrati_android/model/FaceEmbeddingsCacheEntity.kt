package com.hexagraph.jagrati_android.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity()
data class FaceEmbeddingsCacheEntity(
    @PrimaryKey
    val pid: String = "",
    val embedding: FloatArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FaceEmbeddingsCacheEntity

        if (pid != other.pid) return false
        if (!embedding.contentEquals(other.embedding)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pid.hashCode()
        result = 31 * result + embedding.contentHashCode()
        return result
    }
}
