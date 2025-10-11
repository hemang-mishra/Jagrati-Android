package com.hexagraph.jagrati_android.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hexagraph.jagrati_android.model.user.GroupDTO
import com.hexagraph.jagrati_android.model.user.VillageDTO

@Entity(tableName = "villages")
data class Village(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)

fun Village.toDTO(): VillageDTO = VillageDTO(
    id = id,
    name = name,
    isActive = isActive
)

fun VillageDTO.toEntity(): Village = Village(
    id = id,
    name = name,
    isActive = isActive
)

fun Groups.toDTO(): GroupDTO = GroupDTO(
    id = id,
    name = name,
    description = description,
    isActive = isActive
)

fun GroupDTO.toEntity(): Groups = Groups(
    id = id,
    name = name,
    description = description,
    isActive = isActive
)
