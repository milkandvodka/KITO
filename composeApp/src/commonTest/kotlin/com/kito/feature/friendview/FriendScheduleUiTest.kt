package com.kito.feature.friendview

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import com.kito.feature.friendview.domain.model.FriendSummary
import com.kito.feature.friendview.presentation.schedule.FriendScheduleContent
import com.kito.feature.schedule.presentation.WeekDay
import com.kito.testing.friendScheduleItem
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class FriendScheduleUiTest {

    @Test
    fun friendSchedule_rendersSchedule_whenDataAvailable() = runComposeUiTest {
        val scheduleMap = WeekDay.entries.associateWith { day ->
            listOf(
                friendScheduleItem(subject = "Data Structures", day = day.name)
            )
        }
        setContent {
            FriendScheduleContent(
                roll = "2205001",
                summary = FriendSummary(roll = "2205001", section = "CSE-48"),
                schedule = scheduleMap,
                isLoading = false,
                onBack = {},
                enableAnimations = false
            )
        }

        onNodeWithTag("friendschedule_content").assertIsDisplayed()
        onNodeWithText("2205001").assertIsDisplayed()
        onNodeWithText("CSE-48").assertIsDisplayed()
    }
}
