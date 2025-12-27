package com.hexagraph.jagrati_android.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import platform.Foundation.*

fun createDataStore(): DataStore<Preferences> =
    createDataStore {
        val dir = NSFileManager.defaultManager
            .URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )!!
        dir.path + "/$dataStoreFileName"
    }
