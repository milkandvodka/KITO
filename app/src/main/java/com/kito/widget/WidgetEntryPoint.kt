package com.kito.widget

import com.kito.data.local.db.AppDB
import com.kito.data.local.db.studentsection.StudentSectionRepository
import com.kito.data.local.preferences.PrefsRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun studentSectionRepository(): StudentSectionRepository
    fun prefsRepository(): PrefsRepository
}