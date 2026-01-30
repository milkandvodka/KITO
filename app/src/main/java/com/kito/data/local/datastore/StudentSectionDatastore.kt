package com.kito.data.local.datastore

import kotlinx.serialization.Serializable

@Serializable
data class ProtoDataStoreDTO(
    val list: List<StudentSectionDatastore> = emptyList(),
    val redrawToken: Long = 0L,
    val rollNo: String = "",
)

@Serializable
data class StudentSectionDatastore (
    val sectionId: Int,
    val rollNo: String,
    val section: String,
    val batch: String,
    val day: String,
    val startTime: String,
    val endTime: String,
    val subject: String,
    val room: String?
)