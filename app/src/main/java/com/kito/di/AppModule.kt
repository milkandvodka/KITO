package com.kito.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.kito.data.local.db.AppDB
import com.kito.data.local.db.attendance.AttendanceDAO
import com.kito.data.local.db.section.SectionDAO
import com.kito.data.local.db.student.StudentDAO
import com.kito.data.local.db.studentsection.StudentSectionDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

private const val DATASTORE_NAME = "app_prefs"

// Replace extension with this delegate
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDB =
        Room.databaseBuilder(
            context,
            AppDB::class.java,
            "kito_db"
        ).build()

    @Provides
    fun provideUserDao(db: AppDB): AttendanceDAO =
        db.attendanceDao()
    @Provides
    fun provideStudentDao(db: AppDB): StudentDAO =
        db.studentDao()
    @Provides
    fun provideSectionDao(db: AppDB): SectionDAO =
        db.sectionDao()
    @Provides
    fun provideStudentSectionDao(db: AppDB): StudentSectionDAO =
        db.studentSectionDao()
}