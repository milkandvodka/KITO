package com.kito.feature.gpa.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.RopeTabRow
import com.kito.core.designsystem.UIColors
import com.kito.core.platform.sendEmail
import com.kito.feature.gpa.presentation.components.CGPAScreen
import com.kito.feature.gpa.presentation.components.GPAHeader
import com.kito.feature.gpa.presentation.components.GpaResultHeader
import com.kito.feature.gpa.presentation.components.SGPAScreen
import com.kito.feature.gpa.presentation.components.formatGpa
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalHazeMaterialsApi::class,
    ExperimentalHazeApi::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
fun GPAContent(
    selectedSemester: Int,
    selectedBranch: String,
    roll: String,
    uiState: GPAUiState = GPAUiState(),
    onEvent: (GPAEvent) -> Unit = {},
    onSemesterSelected: (Int) -> Unit,
    onBranchSelected: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    enableAnimations: Boolean = true
) {
    val uiColors = UIColors()
    val hazeState = rememberHazeState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )
    val coroutineScope = rememberCoroutineScope()
    val deletedSubject = uiState.deletedSubject
    val topBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 64.dp
    val tabProgress = pagerState.currentPage + pagerState.currentPageOffsetFraction
    val isCgpaPage = pagerState.settledPage == 1
    val resultTitle = if (isCgpaPage) "CGPA" else "SGPA"
    val resultValue = if (isCgpaPage) formatGpa(uiState.cgpa) else formatGpa(uiState.sgpa)

    fun navigateToPage(targetPage: Int) {
        if (targetPage == pagerState.currentPage) return
        coroutineScope.launch {
            pagerState.animateScrollToPage(
                page = targetPage,
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    LaunchedEffect(deletedSubject?.token) {
        val deleted = deletedSubject ?: return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = "${deleted.subject.name} deleted",
            actionLabel = "Undo",
            duration = SnackbarDuration.Short
        )
        if (result == SnackbarResult.ActionPerformed) {
            onEvent(GPAEvent.UndoDelete(deleted.token))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121116))
            .semantics { testTag = "gpa_content" }
    ) {
        Box(modifier = Modifier.hazeSource(hazeState)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121116))
                    .padding(top = topBarPadding)
                    .padding(horizontal = 16.dp)
                    .imePadding(),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = 16.dp + WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding()
                )
            ) {
                item {
                    GPAHeader(
                        roll = roll,
                        isLoading = roll.isEmpty(),
                        selectedSemester = selectedSemester,
                        selectedBranch = selectedBranch,
                        onSemesterSelected = onSemesterSelected,
                        onBranchSelected = onBranchSelected,
                        enableAnimations = enableAnimations
                    )
                }

                item {
                    RopeTabRow(
                        tabPosition = tabProgress,
                        onTabSelected = { page -> navigateToPage(page) }
                    )
                }

                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF121116))
                            .padding(bottom = 2.5.dp)
                    ) {
                        GpaResultHeader(
                            title = resultTitle,
                            value = resultValue,
                            metadata = null,
                            topPadding = 0.dp,
                            horizontalPadding = 0.dp,
                        )
                    }
                }

                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth()
                        ) { page ->
                            when (page) {
                                0 -> SGPAScreen(
                                    uiState = uiState,
                                    onEvent = onEvent,
                                )

                                1 -> CGPAScreen(
                                    uiState = uiState,
                                    onEvent = onEvent,
                                )
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 15.dp
                    noiseFactor = 0.05f
                    inputScale = HazeInputScale.Auto
                    alpha = 0.98f
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                        start = 16.dp,
                        end = 16.dp
                    )
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onBack() },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = uiColors.progressAccent
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GPA Calc",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = uiColors.textPrimary,
                        style = MaterialTheme.typography.titleLargeEmphasized,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            sendEmail(
                                to = "kiito.admin@gmail.com",
                                subject = "KIITO GPA Calc Screen Report",
                                body = ""
                            )
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = Color(0xFFB32727)
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = "Report",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
private fun GPAContentPreview() {
    GPAContent(
        selectedSemester = 6,
        selectedBranch = "Computer Science",
        roll = "123456",
        uiState = GPAUiState(),
        onEvent = {},
        onSemesterSelected = {},
        onBranchSelected = {},
        onBack = {}
    )
}
