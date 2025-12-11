package com.kito.ui.newUi.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// sample model
data class AttendanceItem(
    val title: String,
    val present: Int,
    val total: Int,
    val faculty: String
) {
    val percentage: Float
        get() = if (total == 0) 0f else (present.toFloat() / total.toFloat()) * 100f
}

// color constants (kept in theme with your login screen palette)
private val BackgroundTop = Color(0xFF1A1423) // dark purple-black
private val BackgroundBottom = Color(0xFF0F0C15)
private val CardBackground = Color(0xFF4D4650) // muted purple/gray card
private val CardSurface = Color(0xFF504953)
private val TextPrimary = Color(0xFFF3EFF3)
private val TextSecondary = Color(0xFFCECAD0)
private val ProgressAccent = Color(0xFFE1B57D) // purple progress
private val AccentOrangeStart = Color(0xFFEA850A)
private val AccentOrangeEnd = Color(0xFFFF6A00)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceListScreen(
    items: List<AttendanceItem> = sampleAttendance,
    onRefresh: () -> Unit = {}
) {
    // nice diagonal dark background that keeps the orange button visible from login
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(BackgroundTop, BackgroundBottom)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Your Attendance",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    },
                    actions = {
                        IconButton(onClick = onRefresh) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    AttendanceCard(item)
                }

                // bottom spacing to avoid nav bar overlap
                item {
                    Spacer(modifier = Modifier.height(36.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AttendanceCard(item: AttendanceItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(
                text = item.title,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Attendance row
            Text(
                text = "Attendance: ${item.present}/${item.total}",
                fontFamily = FontFamily.Monospace,
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar visual (thin purple line like screenshot)
            val progress = remember(item) {
                (item.percentage / 100f).coerceIn(0f, 1f)
            }

//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(6.dp)
//                    .clip(RoundedCornerShape(6.dp))
//                    .background(Color(0xFF3A3539))
//            ) {
//                // filled portion
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth(progress)
//                        .height(6.dp)
//                        .background(ProgressAccent)
//                )
//            }
            
            LinearWavyProgressIndicator(
                progress = {
                    progress
                },
                color = AccentOrangeStart,
                trackColor = ProgressAccent,
                modifier = Modifier.fillMaxWidth(),
                amplitude = {
                    0.8f
                },
                waveSpeed = 20.dp,
                wavelength = 50.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Percentage and faculty
            Text(
                text = "Percentage: ${"%.2f".format(item.percentage)}%",
                fontFamily = FontFamily.Monospace,
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Faculty: ${item.faculty}",
                fontFamily = FontFamily.Monospace,
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}

// ---------- Sample preview data (use in preview or call the composable) ----------
val sampleAttendance = listOf(
    AttendanceItem("Data Mining and Data Warehousing", 4, 41, "Amiya Ranjan Panda"),
    AttendanceItem("Engineering Economics", 4, 39, "Arvind Kumar Yadav"),
    AttendanceItem("Design and Analysis of Algorithms", 1, 41, "Partha Sarathi Paul"),
    AttendanceItem("Software Engineering", 24, 52, "Ipsita Paul"),
    AttendanceItem("Computer Networks", 10, 40, "Nitin Varyani"),
    AttendanceItem("Algorithms Laboratory", 0, 14, "Partha Sarathi Paul")
)
