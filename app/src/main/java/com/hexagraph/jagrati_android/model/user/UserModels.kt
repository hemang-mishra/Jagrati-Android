package com.hexagraph.jagrati_android.model.user

import com.hexagraph.jagrati_android.model.User
import com.hexagraph.jagrati_android.model.permission.PermissionResponse
import com.hexagraph.jagrati_android.model.permission.RoleSummaryResponse
import com.hexagraph.jagrati_android.model.role.RoleResponse
import kotlinx.serialization.Serializable

/**
 * Response model for a user with their assigned roles
 */
@Serializable
data class UserWithRolesResponse(
    val pid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val roles: List<RoleResponse>
)

/**
 * Response model for a list of users with their roles
 */
@Serializable
data class UserWithRolesListResponse(
    val users: List<UserWithRolesResponse>
)

/**
 * Request model for assigning a role to a user
 */
@Serializable
data class AssignRoleToUserRequest(
    val userPid: String,
    val roleId: Long
)

/**
 * Request model for removing a role from a user
 */
@Serializable
data class RemoveRoleFromUserRequest(
    val userPid: String,
    val roleId: Long
)

/**
 * Response model for role assignment/removal operations
 */
@Serializable
data class UserRoleAssignmentResponse(
    val userPid: String,
    val roleId: Long,
    val message: String
)

/**
 * Summary information about a user
 */
@Serializable
data class UserSummaryDTO(
    val pid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val profileImageUrl: String?
){
    fun toUser(): User =
        User(
            uid = pid,
            firstName = firstName,
            lastName = lastName,
            email = email,
            isEmailVerified = true,
            photoUrl = profileImageUrl
        )
}
/**
 * Response model containing list of permissions
 */
@Serializable
data class PermissionListResponse(
    val permissions: List<PermissionResponse>
)

/**
 * Response model containing user details along with their roles and permissions
 */
@Serializable
data class UserDetailsWithRolesAndPermissions(
    val userDetails: UserSummaryDTO,
    val roles: List<RoleSummaryResponse>,
    val permissions: PermissionListResponse,
    val villages: List<VillageDTO> = emptyList(),
    val groups: List<GroupDTO> = emptyList(),
    val students: List<StudentDTO> = emptyList(),
    val volunteers: List<VolunteerDTO> = emptyList(),
    val volunteerProfile: VolunteerDTO? = null,
    val faceInfo: List<FaceDataDTO> = emptyList(),
    val isVolunteer: Boolean = false
)
