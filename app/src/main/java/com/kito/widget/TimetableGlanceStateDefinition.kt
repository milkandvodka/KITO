package com.kito.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import com.kito.data.local.datastore.ProtoDataStoreDTO
import com.kito.data.local.datastore.ProtoDataStoreProvider
import java.io.File

object TimetableGlanceStateDefinition :
    GlanceStateDefinition<ProtoDataStoreDTO> {

    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<ProtoDataStoreDTO> {
        return ProtoDataStoreProvider.get(context)
    }

    override fun getLocation(
        context: Context,
        fileKey: String
    ): File {
        return context.applicationContext
            .dataStoreFile("student_section_$fileKey.pb")
    }
}
