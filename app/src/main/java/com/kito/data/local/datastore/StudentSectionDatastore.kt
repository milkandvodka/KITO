package com.kito.data.local.datastore

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class ListStudentSectionDataStore(
    val list: List<StudentSectionDatastore> = emptyList(),
    val nextClassStartMillis: Long? = null,
    val lastUpdated: Long = 0L
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