package com.kito.feature.schedule.domain.model

data class AvailableSectionsData(
    val availableBatches: List<String> = listOf("batch_1", "batch_2", "batch_3", "batch_4"),
    val branchesByBatch: Map<String, List<String>> = emptyMap(),
    val coreSectionsByBatchAndBranch: Map<String, Map<String, List<String>>> = emptyMap(),
    val electiveSlotsByBatch: Map<String, List<ElectiveSlotOption>> = emptyMap()
)
