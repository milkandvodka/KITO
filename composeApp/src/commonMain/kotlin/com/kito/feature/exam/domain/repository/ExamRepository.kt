package com.kito.feature.exam.domain.repository

import com.kito.feature.exam.domain.model.ExamSchedule

interface ExamRepository {
    /** Cached per roll; [forceRefresh] bypasses the cache. Falls back to stale data offline. */
    suspend fun getExamSchedule(roll: String, forceRefresh: Boolean = false): List<ExamSchedule>
}
