package com.hexagraph.jagrati_android.repository.sync

import com.hexagraph.jagrati_android.model.dao.StudentDao
import com.hexagraph.jagrati_android.model.dao.VolunteerDao
import com.hexagraph.jagrati_android.model.dao.VillageDao
import com.hexagraph.jagrati_android.model.dao.GroupsDao
import com.hexagraph.jagrati_android.model.toEntity
import com.hexagraph.jagrati_android.model.user.UserDetailsWithRolesAndPermissions
import com.hexagraph.jagrati_android.model.user.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncRepository(
    private val studentDao: StudentDao,
    private val volunteerDao: VolunteerDao,
    private val villageDao: VillageDao,
    private val groupsDao: GroupsDao
) {
    suspend fun syncToLocalDb(data: UserDetailsWithRolesAndPermissions) = withContext(Dispatchers.IO) {
        // Sync students
        data.students.forEach { studentDto ->
            studentDao.upsertStudentDetails(studentDto.toEntity())
        }
        // Sync volunteers
        data.volunteers.forEach { volunteerDto ->
            volunteerDao.upsertVolunteer(volunteerDto.toEntity())
        }
        // Sync villages
        data.villages.forEach { villageDto ->
            villageDao.upsertVillage(villageDto.toEntity())
        }
        // Sync groups
        data.groups.forEach { groupDto ->
            groupsDao.upsertGroup(groupDto.toEntity())
        }
        //TODO Sync face data: (Files would be involved here as well)
    }
}

