package com.kito.core.network.supabase.request

import kotlinx.serialization.Serializable

@Serializable
data class MissingRollReportRequest(
    val roll_no: String,
    val section: String,
    val batch: String,
    val elective_1: String? = null,
    val elective_2: String? = null
)
