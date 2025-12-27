package com.hexagraph.jagrati_android.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createDataStore(context: Context) : DataStore<Preferences> {
    return createDataStore {
        context.filesDir.resolve(dataStoreFileName).absolutePath
    }
}