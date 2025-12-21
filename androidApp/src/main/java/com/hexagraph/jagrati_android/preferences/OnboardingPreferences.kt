package com.hexagraph.jagrati_android.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Manages preferences related to onboarding
 */
class OnboardingPreferences(context: Context) {
    
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE
    )
    
    /**
     * Check if onboarding has been completed
     */
    fun isOnboardingCompleted(): Boolean {
        return preferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
    
    /**
     * Mark onboarding as completed
     */
    fun setOnboardingCompleted() {
        preferences.edit {
            putBoolean(KEY_ONBOARDING_COMPLETED, true)
        }
    }
    
    /**
     * Reset onboarding status (for testing purposes)
     */
    fun resetOnboardingStatus() {
        preferences.edit {
            putBoolean(KEY_ONBOARDING_COMPLETED, false)
        }
    }
    
    companion object {
        private const val PREFERENCES_NAME = "jagrati_onboarding_preferences"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        
        // Singleton instance
        @Volatile
        private var INSTANCE: OnboardingPreferences? = null
        
        fun getInstance(context: Context): OnboardingPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: OnboardingPreferences(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}