package com.kito.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile

object ProtoDataStoreProvider {
    @Volatile
    private var INSTANCE: DataStore<ProtoDataStoreDTO>? = null

    fun get(context: Context): DataStore<ProtoDataStoreDTO> {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: DataStoreFactory.create(
                serializer = DatastoreSerializer,
                produceFile = {
                    context.applicationContext
                        .dataStoreFile("student_section.pb")
                }
            ).also { INSTANCE = it }
        }
    }
}