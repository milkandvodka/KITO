package com.kito.feature.schedule

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import com.kito.core.designsystem.UIColors
import com.kito.feature.schedule.domain.model.AvailableSectionsData
import com.kito.feature.schedule.domain.model.ElectiveSlotOption
import com.kito.feature.schedule.domain.model.ScheduleLookupState
import com.kito.feature.schedule.presentation.ScheduleContent
import com.kito.feature.schedule.presentation.ScheduleUiState
import com.kito.feature.schedule.presentation.WeekDay
import com.kito.feature.schedule.presentation.components.ManualScheduleSetupCard
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ManualScheduleSetupUiTest {

    @Test
    fun manualScheduleSetupCard_rendersAllElements() = runComposeUiTest {
        val testData = AvailableSectionsData(
            availableBatches = listOf("batch_3"),
            branchesByBatch = mapOf("batch_3" to listOf("CSE")),
            coreSectionsByBatchAndBranch = mapOf("batch_3" to mapOf("CSE" to listOf("CSE-12"))),
            electiveSlotsByBatch = mapOf(
                "batch_3" to listOf(
                    ElectiveSlotOption("elective_1", "Elective 1", listOf("EL-01"))
                )
            )
        )

        setContent {
            ManualScheduleSetupCard(
                availableData = testData,
                selectedBatch = "batch_3",
                selectedBranch = "CSE",
                selectedCoreSection = "CSE-12",
                selectedElective1 = "EL-01",
                selectedElective2 = "",
                onSelectBatch = {},
                onSelectBranch = {},
                onSelectCoreSection = {},
                onSelectElective1 = {},
                onSelectElective2 = {},
                onSubmit = {},
                isSubmitting = false,
                uiColors = UIColors()
            )
        }

        onNodeWithTag("manual_schedule_setup_card").assertIsDisplayed()
        onNodeWithTag("manual_schedule_submit_button").assertIsDisplayed()
    }

    @Test
    fun scheduleContent_whenRollNotFound_rendersContent() = runComposeUiTest {
        val scheduleMap = WeekDay.entries.associateWith {
            emptyList<com.kito.feature.schedule.domain.model.ScheduleItem>()
        }
        val uiState = ScheduleUiState(
            lookupState = ScheduleLookupState.RollNotFound(null),
            isManualSchedule = false
        )

        setContent {
            ScheduleContent(
                schedule = scheduleMap,
                onBack = {},
                uiState = uiState,
                enableAnimations = false
            )
        }

        onNodeWithTag("schedule_content").assertIsDisplayed()
    }
}
