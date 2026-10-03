package com.kito.feature.schedule.domain.model

data class ElectiveSlotOption(
    val slotKey: String,
    val displayName: String,
    val availableSections: List<String>
)
