package com.kito.data.local.datastore

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.PersistentList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProtoDatastoreRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = ProtoDataStoreProvider.get(context)

    val studentSectionsFlow = dataStore.data

    suspend fun setSections(
        list: PersistentList<StudentSectionDatastore>
    ) {
        dataStore.updateData {
            it.copy(list = list.toList())
        }
    }

    suspend fun setRollNo(rollNo: String) {
        dataStore.updateData {
            it.copy(rollNo = rollNo)
        }
    }

    suspend fun clearAll() {
        dataStore.updateData {
            ProtoDataStoreDTO()
        }
    }
}