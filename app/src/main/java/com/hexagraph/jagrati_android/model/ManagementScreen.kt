package com.hexagraph.jagrati_android.model

import com.hexagraph.jagrati_android.model.permission.AllPermissions

enum class ManagementScreen(
    val screenName: String,
    val permissionsRequired: List<AllPermissions>
) {
    ROLES_LIST_AND_EDIT(
        screenName = "Manage User Roles",
        permissionsRequired = listOf(
            AllPermissions.ROLE_VIEW,
            AllPermissions.ROLE_CREATE,
            AllPermissions.ROLE_UPDATE,
            AllPermissions.ROLE_DEACTIVATE
        )
    )
}