package com.hexagraph.jagrati_android.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

fun createDataStore(producePath : ()-> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath {
        producePath().toPath()
    }

internal const val dataStoreFileName = "jagrati_prefs.preferences_pb"