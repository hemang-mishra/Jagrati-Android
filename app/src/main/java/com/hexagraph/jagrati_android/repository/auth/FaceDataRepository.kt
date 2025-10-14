package com.hexagraph.jagrati_android.repository.auth

import com.hexagraph.jagrati_android.model.facedata.AddFaceDataRequest
import com.hexagraph.jagrati_android.model.facedata.FaceDataResponse
import com.hexagraph.jagrati_android.model.facedata.StudentWithFaceDataListResponse
import com.hexagraph.jagrati_android.model.facedata.UpdateFaceDataRequest
import com.hexagraph.jagrati_android.model.facedata.VolunteerWithFaceDataListResponse
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.flow.Flow

interface FaceDataRepository {
    suspend fun addFaceData(request: AddFaceDataRequest): Flow<Resource<FaceDataResponse>>

    suspend fun updateFaceData(pid: String, request: UpdateFaceDataRequest): Flow<Resource<FaceDataResponse>>

    suspend fun deleteFaceData(pid: String): Flow<Resource<Unit>>

    suspend fun getFaceDataByPid(pid: String): Flow<Resource<FaceDataResponse>>

    suspend fun getAllStudentsWithFaceData(): Flow<Resource<StudentWithFaceDataListResponse>>

    suspend fun getAllVolunteersWithFaceData(): Flow<Resource<VolunteerWithFaceDataListResponse>>

    suspend fun addFaceDataForMe(request: UpdateFaceDataRequest): Flow<Resource<FaceDataResponse>>

    suspend fun getMyFaceData(): Flow<Resource<FaceDataResponse>>
}

