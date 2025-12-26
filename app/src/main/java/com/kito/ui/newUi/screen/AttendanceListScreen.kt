package com.kito.ui.newUi.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.kito.data.local.db.attendance.AttendanceEntity
import com.kito.data.local.db.attendance.toAttendanceEntity
import com.kito.sap.SubjectAttendance
import com.kito.ui.components.AttendanceCard
import com.kito.ui.components.UIColors
import com.kito.ui.navigation.Destinations
import com.kito.ui.newUi.viewmodel.AttendanceListScreenViewModel
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlin.math.ceil
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class, ExperimentalHazeApi::class
)
@Composable
fun AttendanceListScreen(
    viewModel: AttendanceListScreenViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val cardHaze = rememberHazeState()
    val uiColors = UIColors()
    val hazeState = rememberHazeState()
    val attendance by viewModel.attendance.collectAsState()
    val sapLoggedIn by viewModel.sapLoggedIn.collectAsState()
    val currentAttendance = remember { mutableStateOf<AttendanceEntity?>(null) }
    Box(
        modifier = Modifier.hazeSource(cardHaze)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 46.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .hazeSource(hazeState)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            if(sapLoggedIn) {
                itemsIndexed(attendance) { index, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(
                            topStart = if (index == 0) 24.dp else 4.dp,
                            topEnd = if (index == 0) 24.dp else 4.dp,
                            bottomStart = if (index == attendance.size - 1) 24.dp else 4.dp,
                            bottomEnd = if (index == attendance.size - 1) 24.dp else 4.dp
                        ),
                        onClick = {
                            currentAttendance.value = item
                        }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                 brush = Brush.linearGradient(
                                         colors = listOf(
                                             uiColors.cardBackground,
                                             Color(0xFF2F222F),
                                             Color(0xFF2F222F),
                                             uiColors.cardBackgroundHigh
                                         )
                                 )
                            )
                        ) {
                            AttendanceCard(item)
                        }
                    }
                }
                item {
                    Spacer(
                        modifier = Modifier.height(
                            106.dp + WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()
                        )
                    )
                }
            }else{
                itemsIndexed(sampleAttendance.map {
                    it.toAttendanceEntity(
                        year = "2025",
                        term = "1"
                    )
                }) { index, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        colors = CardDefaults.cardColors(containerColor = uiColors.cardBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(
                            topStart = if (index == 0) 24.dp else 4.dp,
                            topEnd = if (index == 0) 24.dp else 4.dp,
                            bottomStart = if (index == attendance.size - 1) 24.dp else 4.dp,
                            bottomEnd = if (index == attendance.size - 1) 24.dp else 4.dp
                        )
                    ) {
                        AttendanceCard(item)
                    }
                }
                item {
                    Spacer(
                        modifier = Modifier.height(
                            106.dp + WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()
                        )
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 15.dp
                    noiseFactor = 0.05f
                    inputScale = HazeInputScale.Auto
                    alpha = 0.98f
                }
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Spacer(
                modifier = Modifier.height(
                    16.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                )
            )
            Text(
                text = "Attendance",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = uiColors.textPrimary,
                style = MaterialTheme.typography.titleLargeEmphasized
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (!sapLoggedIn) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = WindowInsets.statusBars.asPaddingValues()
                            .calculateTopPadding() + 46.dp
                    )
            ) {

                // 1️⃣ Input blocker + blur
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                            blurRadius = 15.dp
                            noiseFactor = 0.05f
                            inputScale = HazeInputScale.Auto
                            alpha = 0.98f
                        }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    awaitPointerEvent().changes.forEach {
                                        it.consume()
                                    }
                                }
                            }
                        }
                )
                Button(
                    onClick = {
                        navController.navigate(Destinations.Profile) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.align(Alignment.Center),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = uiColors.progressAccent,
                        contentColor = uiColors.textPrimary
                    )
                ) {
                    Text(
                        text = "Connect to sap",
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.labelMediumEmphasized
                    )
                }
            }
        }
    }
    currentAttendance.value?.let { attendance ->
        AttendanceDialog(
            hazeState = cardHaze,
            attendance = attendance,
            onDismiss = { currentAttendance.value = null }
        )
    }
}


private fun classesRequiredFor75(
    attended: Int,
    total: Int
): Int {
    return max(0, ceil(3 * total - 4 * attended.toDouble()).toInt())
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalHazeMaterialsApi::class,
    ExperimentalHazeApi::class
)
@Composable
private fun AttendanceDialog(
    hazeState: HazeState,
    attendance: AttendanceEntity,
    onDismiss: () -> Unit
){
    val uiColors = UIColors()
    var targetProgress by remember { mutableFloatStateOf(0f) }

    val progress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "attendance"
    )
    LaunchedEffect(Unit) {
        targetProgress = (attendance.percentage.toFloat() / 100f).coerceIn(0f, 1f)
    }
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 24.dp,
                    spotColor = uiColors.progressAccent
                )
                .clip(
                    shape = RoundedCornerShape(24.dp)
                )
                .hazeEffect(state = hazeState, style = HazeMaterials.ultraThin()) {
                    blurRadius = 30.dp
                    noiseFactor = 0.05f
                    inputScale = HazeInputScale.Auto
                    alpha = 0.98f
                    tints = listOf(HazeTint(Color(0xFF86431D).copy(alpha = 0.15f)))
                }
//                .background(
////                    color = uiColors.cardBackground,
//                    brush = Brush.linearGradient(
//                        colors = listOf(
//                            uiColors.cardBackground,
//                            Color(0xFF2F222F),
//                            Color(0xFF2F222F),
//                            uiColors.cardBackgroundHigh,
//                            Color(0xFF6E4B37),
//                        )
//                    ),
//                    shape = RoundedCornerShape(24.dp)
//                )

        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = attendance.subjectName,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = uiColors.textPrimary,
                style = MaterialTheme.typography.titleMediumEmphasized,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinearWavyProgressIndicator(
                progress = {
                    progress
                },
                color = uiColors.accentOrangeStart,
                trackColor = uiColors.progressAccent,
                modifier = Modifier
                    .fillMaxWidth(),
                amplitude = {
                    0.8f
                },
                waveSpeed = 20.dp,
                wavelength = 50.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Teacher - " + attendance.facultyName,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = uiColors.textPrimary,
                style = MaterialTheme.typography.bodyMediumEmphasized,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Total Classes - " + attendance.totalClasses.toString(),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = uiColors.textPrimary,
                style = MaterialTheme.typography.bodyMediumEmphasized,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Classes Attended - " + attendance.attendedClasses.toString(),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = uiColors.textPrimary,
                style = MaterialTheme.typography.bodyMediumEmphasized,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Attendance Percentage - " + attendance.percentage.toString() + "%",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = uiColors.textPrimary,
                style = MaterialTheme.typography.bodyMediumEmphasized,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (attendance.percentage < 75.0){
                val requiredClassesFor75 = remember(attendance) {
                    classesRequiredFor75(
                        attended = attendance.attendedClasses,
                        total = attendance.totalClasses
                    )
                }
                Text(
                    text = "Classes for 75% - $requiredClassesFor75",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = uiColors.textPrimary,
                    style = MaterialTheme.typography.bodyMediumEmphasized,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
private val sampleAttendance = listOf(
    SubjectAttendance(
        subjectCode = "00F4",
        subjectName = "Data Mining and Data Warehousing",
        attendedClasses = 4,
        totalClasses = 41,
        percentage = (4.0 / 41) * 100,
        facultyName = "Amiya Ranjan Panda"
    ),
    SubjectAttendance(
        subjectCode = "00F5",
        subjectName = "Engineering Economics",
        attendedClasses = 4,
        totalClasses = 39,
        percentage = (4.0 / 39) * 100,
        facultyName = "Arvind Kumar Yadav"
    ),
    SubjectAttendance(
        subjectCode = "00F6",
        subjectName = "Design and Analysis of Algorithms",
        attendedClasses = 1,
        totalClasses = 41,
        percentage = (1.0 / 41) * 100,
        facultyName = "Partha Sarathi Paul"
    ),
    SubjectAttendance(
        subjectCode = "00F7",
        subjectName = "Software Engineering",
        attendedClasses = 24,
        totalClasses = 52,
        percentage = (24.0 / 52) * 100,
        facultyName = "Ipsita Paul"
    ),
    SubjectAttendance(
        subjectCode = "00F8",
        subjectName = "Computer Networks",
        attendedClasses = 10,
        totalClasses = 40,
        percentage = (10.0 / 40) * 100,
        facultyName = "Nitin Varyani"
    )
)