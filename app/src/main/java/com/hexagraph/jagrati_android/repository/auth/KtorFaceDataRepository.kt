package com.hexagraph.jagrati_android.repository.auth

import com.hexagraph.jagrati_android.model.facedata.AddFaceDataRequest
import com.hexagraph.jagrati_android.model.facedata.FaceDataResponse
import com.hexagraph.jagrati_android.model.facedata.StudentWithFaceDataListResponse
import com.hexagraph.jagrati_android.model.facedata.UpdateFaceDataRequest
import com.hexagraph.jagrati_android.model.facedata.VolunteerWithFaceDataListResponse
import com.hexagraph.jagrati_android.service.auth.KtorFaceDataService
import com.hexagraph.jagrati_android.util.Resource
import com.hexagraph.jagrati_android.util.Utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class KtorFaceDataRepository(
    private val faceDataService: KtorFaceDataService
) : FaceDataRepository {
    override suspend fun addFaceData(request: AddFaceDataRequest): Flow<Resource<FaceDataResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { faceDataService.addFaceData(request) }
        emit(response)
    }

    override suspend fun updateFaceData(pid: String, request: UpdateFaceDataRequest): Flow<Resource<FaceDataResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { faceDataService.updateFaceData(pid, request) }
        emit(response)
    }

    override suspend fun deleteFaceData(pid: String): Flow<Resource<Unit>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { faceDataService.deleteFaceData(pid) }
        emit(response)
    }

    override suspend fun getFaceDataByPid(pid: String): Flow<Resource<FaceDataResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { faceDataService.getFaceDataByPid(pid) }
        emit(response)
    }

    override suspend fun getAllStudentsWithFaceData(): Flow<Resource<StudentWithFaceDataListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { faceDataService.getAllStudentsWithFaceData() }
        emit(response)
    }

    override suspend fun getAllVolunteersWithFaceData(): Flow<Resource<VolunteerWithFaceDataListResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { faceDataService.getAllVolunteersWithFaceData() }
        emit(response)
    }

    override suspend fun addFaceDataForMe(request: UpdateFaceDataRequest): Flow<Resource<FaceDataResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { faceDataService.addFaceDataForMe(request) }
        emit(response)
    }

    override suspend fun getMyFaceData(): Flow<Resource<FaceDataResponse>> = flow {
        emit(Resource.loading())
        val response = safeApiCall { faceDataService.getMyFaceData() }
        emit(response)
    }
}

