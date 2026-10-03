package com.kito.core.presentation.navigation3

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes: NavKey {

    @Serializable
    object Tabs: Routes, NavKey

    @Serializable
    object Schedule: Routes, NavKey

    @Serializable
    data class FacultyDetail(val facultyId: Long) : Routes, NavKey
    @Serializable
    object ExamSchedule : Routes, NavKey

    @Serializable
    object Onboarding: Routes, NavKey

    @Serializable
    object UserSetup: Routes, NavKey

    @Serializable
    data class Promotions(val url: String): Routes, NavKey

    @Serializable
    object FriendView: Routes, NavKey

    @Serializable
    data class FriendSchedule(val roll: String) : Routes, NavKey

    @Serializable
    object HolidayList: Routes, NavKey

    @Serializable
    object GPACalc: Routes, NavKey

    @Serializable
    object Calendar: Routes, NavKey

    @Serializable
    data class RestaurantMenu(
        val restaurantId: String,
        val restaurantName: String,
        val restaurantImage: String?,   // nullable — not all restaurants have images
        val restaurantRating: Float,
        val browseOnly: Boolean
    ) : Routes, NavKey

    @Serializable
    object Faculty : Routes, NavKey
}

@Serializable
sealed interface TabRoutes: NavKey {

    @Serializable
    object Home : TabRoutes,NavKey

    @Serializable
    object Attendance : TabRoutes,NavKey

    @Serializable
    object Profile : TabRoutes,NavKey
}