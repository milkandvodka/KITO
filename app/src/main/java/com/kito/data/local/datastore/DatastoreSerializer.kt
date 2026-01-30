package com.kito.data.local.datastore

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object DatastoreSerializer: Serializer<ProtoDataStoreDTO> {
    override suspend fun readFrom(input: InputStream): ProtoDataStoreDTO {
        return try {
            // Read bytes from input stream, convert to string, then decode from JSON
            Json.decodeFromString(
                deserializer = ProtoDataStoreDTO.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(
        t: ProtoDataStoreDTO,
        output: OutputStream
    ) {
        output.write(
            Json.encodeToString(
                serializer = ProtoDataStoreDTO.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }

    override val defaultValue: ProtoDataStoreDTO
        get() = ProtoDataStoreDTO()
}