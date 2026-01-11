package com.kito.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Destinations {
    @Serializable
    object Home: Destinations()
    @Serializable
    object Attendance: Destinations()
    @Serializable
    object Faculty: Destinations()
    @Serializable
    object Profile: Destinations()
    @Serializable
    data class FacultyDetail(val facultyId: Long) : Destinations()
}