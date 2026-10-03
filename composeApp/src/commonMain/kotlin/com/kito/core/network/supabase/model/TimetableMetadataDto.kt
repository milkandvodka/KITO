package com.kito.core.network.supabase.model

import kotlinx.serialization.Serializable

@Serializable
data class TimetableMetadataDto(
    val batch: String = "",
    val section: String = "",
    val source: String = "core"
)
