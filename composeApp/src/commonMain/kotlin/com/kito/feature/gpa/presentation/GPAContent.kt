package com.kito.feature.gpa.presentation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.core.designsystem.RopeTabRow
import com.kito.core.designsystem.UIColors
import com.kito.core.platform.sendEmail
import com.kito.feature.gpa.presentation.components.CGPAScreen
import com.kito.feature.gpa.presentation.components.GPAHeader
import com.kito.feature.gpa.presentation.components.SGPAScreen
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class,
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class
)
@Composable
fun GPAContent(
    selectedSemester: Int,
    selectedBranch: String,
    roll: String,
    onSemesterSelected: (Int) -> Unit,
    onBranchSelected: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    enableAnimations: Boolean = true
) {
    val uiColors = UIColors()
    val hazeState = rememberHazeState()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            2
        }
    )
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121116))
            .semantics { testTag = "gpa_content" }
    ) {
        Box(
            modifier = Modifier.hazeSource(hazeState)
        ) {
            HorizontalPager(
                state = pagerState
            ){
                when(it) {
                    0 -> SGPAScreen(
                        selectedSemester = selectedSemester,
                        selectedBranch = selectedBranch
                    )

                    1 -> CGPAScreen()
                }
            }
        }

        Box(
            modifier = Modifier
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 15.dp
                    noiseFactor = 0.05f
                    inputScale = HazeInputScale.Auto
                    alpha = 0.98f
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                        start = 16.dp,
                        end = 16.dp
                    )
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            onBack()
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.08f),
                            contentColor = uiColors.progressAccent
                        ),
                        modifier = Modifier.size(32.dp)
                    ){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Report",
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
                        modifier = Modifier
                            .weight(1f)
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
                Box() {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(
                                RoundedCornerShape(24.dp)
                            )
                            .background(Color(0xFF121116))
                    )
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
                Spacer(modifier = Modifier.height(16.dp))
                val tabProgress = pagerState.currentPage + pagerState.currentPageOffsetFraction
                RopeTabRow(
                    tabPosition = tabProgress,
                    onTabSelected = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                page = it,
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
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
        onSemesterSelected = {},
        onBranchSelected = {},
        onBack = {}
    )
}
