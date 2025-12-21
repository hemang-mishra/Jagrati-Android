package com.hexagraph.jagrati_android.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.hexagraph.jagrati_android.R

enum class PermissionsRequired(
    val permission: String,
    val title: String,
    val permanentlyDeclinedRationale: String, 
    val rationaleText: String,
    val image: Int,
    val minSdk: Int, // Minimum Android version required
    val maxSdk: Int? = null, // Optional upper range
    val requiresSettingsNavigation: Boolean // Whether direct settings navigation is needed
) {
    CAMERA(
        permission = Manifest.permission.CAMERA,
        title = "Camera Permission",
        permanentlyDeclinedRationale = "Camera permission is required for attendance scanning. Please enable it in app settings.",
        rationaleText = "We need camera access to scan student faces for attendance.",
        image = R.drawable.ic_launcher_foreground, // Replace with actual camera icon
        minSdk = Build.VERSION_CODES.LOLLIPOP,
        requiresSettingsNavigation = true
    ),
    
    NOTIFICATIONS(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            "android.permission.POST_NOTIFICATIONS"
        },
        title = "Notifications Permission",
        permanentlyDeclinedRationale = "Notification permission is required to receive important updates. Please enable it in app settings.",
        rationaleText = "We need to send you notifications about attendance updates and important events.",
        image = R.drawable.ic_launcher_foreground, // Replace with actual notification icon
        minSdk = Build.VERSION_CODES.TIRAMISU,
        requiresSettingsNavigation = true
    );
    
    companion object {
        /**
         * Checks if the permission is granted
         */
        fun isPermissionGranted(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        
        /**
         * Checks if the current device SDK version is within the range for this permission
         */
        fun isPermissionAvailable(permission: PermissionsRequired): Boolean {
            val currentSdk = Build.VERSION.SDK_INT
            return currentSdk >= permission.minSdk && 
                   (permission.maxSdk == null || currentSdk <= permission.maxSdk)
        }
        
        /**
         * Opens app settings for the user to manually grant permissions
         */
        fun openAppSettings(context: Context) {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
            context.startActivity(intent)
        }
    }
}

/**
 * Composable function to request a permission
 */
@Composable
fun rememberPermissionLauncher(
    onPermissionResult: (Boolean) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = onPermissionResult
)

/**
 * Checks if all required permissions are granted
 */
fun areAllPermissionsGranted(context: Context): Boolean {
    return PermissionsRequired.values().all { permission ->
        !PermissionsRequired.isPermissionAvailable(permission) || 
        PermissionsRequired.isPermissionGranted(context, permission.permission)
    }
}