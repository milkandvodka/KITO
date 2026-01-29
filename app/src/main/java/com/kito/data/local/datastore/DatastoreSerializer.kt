package com.kito.data.local.datastore

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object DatastoreSerializer: Serializer<ListStudentSectionDataStore> {
    override suspend fun readFrom(input: InputStream): ListStudentSectionDataStore {
        return try {
            // Read bytes from input stream, convert to string, then decode from JSON
            Json.decodeFromString(
                deserializer = ListStudentSectionDataStore.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(
        t: ListStudentSectionDataStore,
        output: OutputStream
    ) {
        output.write(
            Json.encodeToString(
                serializer = ListStudentSectionDataStore.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }

    override val defaultValue: ListStudentSectionDataStore
        get() = ListStudentSectionDataStore()
}