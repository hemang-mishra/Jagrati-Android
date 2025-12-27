package com.hexagraph.jagrati_android.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hexagraph.jagrati_android.BuildKonfig

/**
 * Helper class for Firebase Crashlytics operations
 * Provides convenient methods for logging and reporting crashes
 */
actual object LogHelper {

    private val crashlytics: FirebaseCrashlytics by lazy {
        FirebaseCrashlytics.getInstance()
    }

    private val isDebug: Boolean = BuildKonfig.DEBUG

    /**
     * Log a non-fatal exception to Crashlytics
     */
    actual fun logException(exception: Throwable, tag:String) {
        try {
            crashlytics.recordException(exception)
            if (isDebug) {
                Log.e(tag, "Exception logged", exception)
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error logging exception", e)
        }
    }

    /**
     * Log a message to Crashlytics
     */
    actual fun log(tag: String, message: String) {
        try {
            crashlytics.log(message)
            if (isDebug) {
                Log.d(tag, message)
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error logging message", e)
        }
    }

    /**
     * Set user identifier for crash reports
     */
    actual fun setUserId(userId: String) {
        try {
            crashlytics.setUserId(userId)
            if (isDebug) {
                Log.d("Crashlytics", "User ID set: $userId")
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error setting user ID", e)
        }
    }

    /**
     * Set custom key-value pair for crash reports
     */
    actual fun setCustomKey(key: String, value: String) {
        try {
            crashlytics.setCustomKey(key, value)
            if (isDebug) {
                Log.d("Crashlytics", "Custom key set: $key = $value")
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error setting custom key", e)
        }
    }

    /**
     * Set custom key-value pair for crash reports (Int value)
     */
    actual fun setCustomKey(key: String, value: Int) {
        try {
            crashlytics.setCustomKey(key, value)
            if (isDebug) {
                Log.d("Crashlytics", "Custom key set: $key = $value")
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error setting custom key", e)
        }
    }

    /**
     * Set custom key-value pair for crash reports (Boolean value)
     */
    actual fun setCustomKey(key: String, value: Boolean) {
        try {
            crashlytics.setCustomKey(key, value)
            if (isDebug) {
                Log.d("Crashlytics", "Custom key set: $key = $value")
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error setting custom key", e)
        }
    }

    /**
     * Clear user identifier when user logs out
     */
    actual fun clearUserId() {
        try {
            crashlytics.setUserId("")
            if (isDebug) {
                Log.d("Crashlytics", "User ID cleared")
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error clearing user ID", e)
        }
    }

    /**
     * Force a test crash (for testing purposes only)
     * Should only be used in debug builds
     */
    actual fun testCrash() {
        if (isDebug) {
            throw RuntimeException("This is a test crash from Crashlytics")
        } else {
            Log.w("CrashlyticsHelper", "Test crash is only available in debug builds")
        }
    }

    /**
     * Send a non-fatal error with additional context
     */
    actual fun logError(tag: String, message: String, throwable: Throwable?) {
        try {
            val errorMessage = "[$tag] $message"
            crashlytics.log(errorMessage)

            if (throwable != null) {
                crashlytics.recordException(throwable)
            }

            if (isDebug) {
                if (throwable != null) {
                    Log.e(tag, message, throwable)
                } else {
                    Log.e(tag, message)
                }
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error logging error", e)
        }
    }
}