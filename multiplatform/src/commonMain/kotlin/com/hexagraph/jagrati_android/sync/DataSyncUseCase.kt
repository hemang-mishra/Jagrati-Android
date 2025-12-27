package com.hexagraph.jagrati_android.sync

import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.user.UserDetailsWithRolesAndPermissions

interface DataSyncUseCase {
    fun syncDataInBackgroundAfterFCM()
    fun subscribeToSyncTopic()
    fun unsubscribeFromSyncTopic()
    suspend fun fetchUserDetails(
        onSuccessfulFetch: (UserDetailsWithRolesAndPermissions) -> Unit,
        onError: (ResponseError) -> Unit
    )
}