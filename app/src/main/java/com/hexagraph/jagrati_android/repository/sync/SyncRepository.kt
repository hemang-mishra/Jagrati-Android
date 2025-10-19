package com.hexagraph.jagrati_android.repository.sync

import android.content.Context
import android.graphics.Paint
import android.util.Log
import com.hexagraph.jagrati_android.model.dao.StudentDao
import com.hexagraph.jagrati_android.model.dao.VolunteerDao
import com.hexagraph.jagrati_android.model.dao.VillageDao
import com.hexagraph.jagrati_android.model.dao.GroupsDao
import com.hexagraph.jagrati_android.model.toEntity
import com.hexagraph.jagrati_android.model.user.UserDetailsWithRolesAndPermissions
import com.hexagraph.jagrati_android.model.user.toEntity
import com.hexagraph.jagrati_android.repository.omniscan.OmniScanRepository
import com.hexagraph.jagrati_android.util.AppPreferences
import com.hexagraph.jagrati_android.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncRepository(
    private val context: Context,
    private val studentDao: StudentDao,
    private val volunteerDao: VolunteerDao,
    private val villageDao: VillageDao,
    private val groupsDao: GroupsDao,
    private val omniScanRepository: OmniScanRepository,
    private val appPreferences: AppPreferences
) {
    suspend fun syncToLocalDb(data: UserDetailsWithRolesAndPermissions) = withContext(Dispatchers.IO) {
        // Sync students
        data.students.forEach { studentDto ->
            studentDao.upsertStudentDetails(studentDto.toEntity())
            if(studentDto.profilePic != null){
                val bitamp = Utils.getBitmapFromURL(context, studentDto.profilePic.url)
                if(bitamp == null) {
                    Log.d("SyncRepository", "Bitmap is null for student ID: ${studentDto.pid} name: ${studentDto.firstName}")
                    return@forEach
                }
                val processedImage = omniScanRepository.processImageFromBitmap(bitamp, Paint())
                processedImage.onSuccess {
                    omniScanRepository.saveFaceLocally(it)
                }
            }
        }
        // Sync volunteers
        data.volunteers.forEach { volunteerDto ->
            volunteerDao.upsertVolunteer(volunteerDto.toEntity())
            if(volunteerDto.profilePic != null){
                val bitamp = Utils.getBitmapFromURL(context, volunteerDto.profilePic.url)
                if(bitamp == null) {
                    Log.d("SyncRepository", "Bitmap is null for volunteer ID: ${volunteerDto.pid} name: ${volunteerDto.firstName}")
                    return@forEach
                }
                val processedImage = omniScanRepository.processImageFromBitmap(bitamp, Paint())
                processedImage.onSuccess {
                    omniScanRepository.saveFaceLocally(it)
                }
            }

            //Handling self profile
            val user = appPreferences.userDetails.get()
            if(user?.pid == volunteerDto.pid && volunteerDto.profilePic != null){
                appPreferences.userDetails.set(user.copy(
                    photoUrl = volunteerDto.profilePic.url
                ))
            }
        }
        // Sync villages
        data.villages.forEach { villageDto ->
            villageDao.upsertVillage(villageDto.toEntity())
        }
        // Sync groups
        data.groups.forEach { groupDto ->
            groupsDao.upsertGroup(groupDto.toEntity())
        }
    }
}

