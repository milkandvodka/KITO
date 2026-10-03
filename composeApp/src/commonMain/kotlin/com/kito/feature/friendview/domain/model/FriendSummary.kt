package com.kito.feature.friendview.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FriendSummary(
    val roll: String,
    val name: String = "",
    val section: String = "",
    val batch: String = "",
    val elective1: String = "",
    val elective2: String = "",
    val isLoading: Boolean = false,
    val notFound: Boolean = false,
    val lastSyncedAt: Long = 0L
) {
    val displayName: String
        get() {
            val cleanName = name.trim()
            if (cleanName.isNotBlank() && !cleanName.startsWith("SEC:")) {
                return cleanName
            }
            if (roll.startsWith("SEC:")) {
                return section.ifBlank { roll.removePrefix("SEC:").substringBefore(":") }
            }
            return roll
        }

    val monogram: String
        get() {
            val cleanName = name.trim()
            if (cleanName.isNotBlank() && !cleanName.startsWith("SEC:")) {
                val parts = cleanName.split(" ").filter { it.isNotBlank() }
                return if (parts.size >= 2) {
                    "${parts[0].first()}${parts[1].first()}".uppercase()
                } else {
                    cleanName.take(2).uppercase()
                }
            }
            val sec = section.ifBlank { if (roll.startsWith("SEC:")) roll.removePrefix("SEC:").substringBefore(":") else "" }
            if (sec.isNotBlank()) {
                return sec.take(2).uppercase()
            }
            return if (roll.length >= 2 && !roll.startsWith("SEC:")) roll.takeLast(2) else "FR"
        }

    val subtitleText: String
        get() {
            if (notFound) return "Details unavailable"
            if (section.isBlank()) return if (isLoading) "Loading details..." else "No section found"
            val electives = listOf(elective1, elective2).filter { it.isNotBlank() }
            return if (electives.isNotEmpty()) {
                "$section • ${electives.joinToString(", ")}"
            } else {
                section
            }
        }
}
