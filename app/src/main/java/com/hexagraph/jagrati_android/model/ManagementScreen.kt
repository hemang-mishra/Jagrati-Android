package com.hexagraph.jagrati_android.model

import com.hexagraph.jagrati_android.model.permission.AllPermissions
import com.hexagraph.jagrati_android.ui.navigation.Screens

enum class ManagementScreen(
    val screenName: String,
    val section: String,
    val screen: Screens,
    val permissionsRequired: List<AllPermissions>
) {
    ROLES_LIST_AND_EDIT(
        screenName = "Manage User Roles",
        section = "Roles and Permissions",
        screen = Screens.NavManageRolesRoute,
        permissionsRequired = listOf(
            AllPermissions.ROLE_VIEW,
            AllPermissions.ROLE_CREATE,
            AllPermissions.ROLE_UPDATE,
            AllPermissions.ROLE_DEACTIVATE
        )
    ),
    MANAGE_PERMISSIONS(
        screenName = "Manage Permissions",
        section = "Roles and Permissions",
        screen = Screens.NavManagePermissionsRoute,
        permissionsRequired = listOf(
            AllPermissions.PERMISSION_VIEW,
            AllPermissions.PERMISSION_ASSIGN_ROLE,
            AllPermissions.PERMISSION_REMOVE_ROLE,
        )
    ),
    USER_ROLE_MANAGEMENT(
        screenName = "User Role Management",
        section = "Roles and Permissions",
        screen = Screens.NavUserRoleManagementRoute,
        permissionsRequired = listOf(
            AllPermissions.USER_VIEW,
            AllPermissions.USER_ROLE_ASSIGN,
            AllPermissions.USER_ROLE_REMOVE
        )
    ),
    MANAGE_VOLUNTEER_REQUESTS(
        screenName = "Manage Volunteer Requests",
        section = "Roles and Permissions",
        screen = Screens.NavManageVolunteerRequestsRoute,
        permissionsRequired = listOf(
            AllPermissions.VOLUNTEER_REQUEST_VIEW,
            AllPermissions.VOLUNTEER_REQUEST_APPROVE,
            AllPermissions.VOLUNTEER_REQUEST_REJECT
        )
    ),
    MY_VOLUNTEER_REQUESTS(
        screenName = "My Volunteer Requests",
        section = "Roles and Permissions",
        screen = Screens.NavMyVolunteerRequestsRoute,
        permissionsRequired = listOf()
    ),
    VILLAGE_MANAGEMENT(
        screenName = "Village Management",
        screen = Screens.NavVillageManagementRoute,
        section = "Village and Groups",
        permissionsRequired = listOf(
            AllPermissions.VILLAGE_MANAGE,
        )
    ),
    GROUP_MANAGEMENT(
        screenName = "Group Management",
        screen = Screens.NavGroupManagementRoute,
        section = "Village and Groups",
        permissionsRequired = listOf(
            AllPermissions.GROUP_MANAGE,
        )
    ),
    ;
}