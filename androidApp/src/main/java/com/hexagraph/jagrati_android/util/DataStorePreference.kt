package com.hexagraph.jagrati_android.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Interface for accessing preferences stored in DataStore in a type-safe manner.
 */
interface DataStorePreference<T> {
    /**
     * Synchronously get the current value of the preference.
     */
    suspend fun get(): T = getFlow().first()

    /**
     * Get a Flow of the preference value, which emits whenever the value changes.
     */
    fun getFlow(): Flow<T>

    /**
     * Set a new value for the preference.
     */
    suspend fun set(value: T)

    /**
     * Update the value of the preference in atomic read-modify-write manner.
     */
    suspend fun getAndUpdate(update: (T) -> T): Unit =
        throw NotImplementedError("getAndUpdate has not been implemented for this preference.")
}
