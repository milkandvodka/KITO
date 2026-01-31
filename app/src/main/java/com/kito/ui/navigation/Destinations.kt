package com.kito.ui.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed interface RootDestination {

    @Serializable
    object Tabs : RootDestination

    @Serializable
    object Schedule : RootDestination

    @Serializable
    data class FacultyDetail(val facultyId: Long) : RootDestination
    @Serializable
    object ExamSchedule : RootDestination
}

@Serializable
sealed interface TabDestination {

    @Serializable
    object Home : TabDestination

    @Serializable
    object Attendance : TabDestination

    @Serializable
    object Faculty : TabDestination

    @Serializable
    object Profile : TabDestination
}