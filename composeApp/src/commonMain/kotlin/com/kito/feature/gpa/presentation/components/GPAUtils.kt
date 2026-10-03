package com.kito.feature.gpa.presentation.components

val gradePoints = mapOf(
    "O" to 10,
    "E" to 9,
    "A" to 8,
    "B" to 7,
    "C" to 6,
    "D" to 5,
    "F" to 2
)

fun calculateSGPA(subjects: List<Pair<Int, Int>>): Double {
    return calculateSGPAOrNull(subjects) ?: 0.0
}

fun calculateSGPAOrNull(subjects: List<Pair<Int, Int>>): Double? {
    val totalCredits = subjects.sumOf { it.first }
    if (subjects.isEmpty() || totalCredits <= 0) return null
    val totalPoints = subjects.sumOf { it.first * it.second }
    return totalPoints.toDouble() / totalCredits
}

fun calculateCGPA(
    oldCgpa: Double,
    completedSem: Int,
    currentSgpa: Double
): Double {
    return if (completedSem == 0) currentSgpa
    else ((oldCgpa * completedSem) + currentSgpa) / (completedSem + 1)
}
