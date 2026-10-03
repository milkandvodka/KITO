package com.kito.feature.schedule.presentation.components

fun formatBatchYear(batch: String): String {
    return when (batch.lowercase()) {
        "batch_1" -> "Year 1"
        "batch_2" -> "Year 2"
        "batch_3" -> "Year 3"
        "batch_4" -> "Year 4"
        else -> {
            val num = Regex("\\d+").find(batch)?.value
            if (num != null) {
                "Year $num"
            } else {
                batch
            }
        }
    }
}

fun extractBranchName(section: String): String {
    val trimmed = section.trim()
    return when {
        trimmed.contains("-") -> trimmed.substringBefore("-").trim()
        trimmed.contains(" ") -> trimmed.substringBefore(" ").trim()
        trimmed.any { it.isDigit() } -> {
            val letters = trimmed.takeWhile { !it.isDigit() }.trim()
            if (letters.isNotBlank()) letters else trimmed
        }
        else -> trimmed
    }
}

fun normalizeDay(day: String): String {
    val trimmed = day.trim().uppercase()
    return when {
        trimmed.startsWith("MON") -> "MON"
        trimmed.startsWith("TUE") -> "TUE"
        trimmed.startsWith("WED") -> "WED"
        trimmed.startsWith("THU") -> "THU"
        trimmed.startsWith("FRI") -> "FRI"
        trimmed.startsWith("SAT") -> "SAT"
        trimmed.startsWith("SUN") -> "SUN"
        else -> trimmed
    }
}
