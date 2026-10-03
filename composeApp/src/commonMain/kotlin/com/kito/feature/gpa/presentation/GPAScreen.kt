package com.kito.feature.gpa.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.kito.core.designsystem.SharedExpandContainer
import com.kito.core.presentation.navigation3.Routes
import org.koin.compose.koinInject

@Composable
fun GPAScreen(
    viewModel: GPAViewmodel = koinInject(),
    onBack: () -> Unit
) {
    val selectedSemester by viewModel.semester.collectAsState()
    val selectedBranch by viewModel.branch.collectAsState()
    val roll by viewModel.roll.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    SharedExpandContainer(
        routeKey = Routes.GPACalc,
        backgroundColor = Color(0xFF121116),
    ) {
        GPAContent(
            selectedSemester = selectedSemester,
            selectedBranch = selectedBranch,
            roll = roll,
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onSemesterSelected = { semester -> viewModel.onEvent(GPAEvent.UpdateSemester(semester)) },
            onBranchSelected = { branch -> viewModel.onEvent(GPAEvent.UpdateBranch(branch)) },
            onBack = onBack
        )
    }
}
