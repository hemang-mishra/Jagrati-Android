package com.hexagraph.jagrati_android.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hexagraph.jagrati_android.BuildConfig

/**
 * Helper class for Firebase Crashlytics operations
 * Provides convenient methods for logging and reporting crashes
 */
object CrashlyticsHelper {

    private val crashlytics: FirebaseCrashlytics by lazy {
        FirebaseCrashlytics.getInstance()
    }

    /**
     * Log a non-fatal exception to Crashlytics
     */
    fun logException(exception: Throwable) {
        try {
            crashlytics.recordException(exception)
            if (BuildConfig.DEBUG) {
                Log.e("Crashlytics", "Exception logged", exception)
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error logging exception", e)
        }
    }

    /**
     * Log a message to Crashlytics
     */
    fun log(message: String) {
        try {
            crashlytics.log(message)
            if (BuildConfig.DEBUG) {
                Log.d("Crashlytics", message)
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error logging message", e)
        }
    }

    /**
     * Set user identifier for crash reports
     */
    fun setUserId(userId: String) {
        try {
            crashlytics.setUserId(userId)
            if (BuildConfig.DEBUG) {
                Log.d("Crashlytics", "User ID set: $userId")
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error setting user ID", e)
        }
    }

    /**
     * Set custom key-value pair for crash reports
     */
    fun setCustomKey(key: String, value: String) {
        try {
            crashlytics.setCustomKey(key, value)
            if (BuildConfig.DEBUG) {
                Log.d("Crashlytics", "Custom key set: $key = $value")
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error setting custom key", e)
        }
    }

    /**
     * Set custom key-value pair for crash reports (Int value)
     */
    fun setCustomKey(key: String, value: Int) {
        try {
            crashlytics.setCustomKey(key, value)
            if (BuildConfig.DEBUG) {
                Log.d("Crashlytics", "Custom key set: $key = $value")
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error setting custom key", e)
        }
    }

    /**
     * Set custom key-value pair for crash reports (Boolean value)
     */
    fun setCustomKey(key: String, value: Boolean) {
        try {
            crashlytics.setCustomKey(key, value)
            if (BuildConfig.DEBUG) {
                Log.d("Crashlytics", "Custom key set: $key = $value")
            }
        } catch (e: Exception) {
            Log.e("CrashlyticsHelper", "Error setting custom key", e)
        }
    }

    /**
     * Clear user identifier when user logs out
     */
    fun clearUserId() {
        try {
            crashlytics.setUserId("")
            if (BuildConfig.DEBUG) {
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
    fun testCrash() {
        if (BuildConfig.DEBUG) {
            throw RuntimeException("This is a test crash from Crashlytics")
        } else {
            Log.w("CrashlyticsHelper", "Test crash is only available in debug builds")
        }
    }

    /**
     * Send a non-fatal error with additional context
     */
    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        try {
            val errorMessage = "[$tag] $message"
            crashlytics.log(errorMessage)

            if (throwable != null) {
                crashlytics.recordException(throwable)
            }

            if (BuildConfig.DEBUG) {
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

