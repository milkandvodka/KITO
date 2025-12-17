package com.kito.ui.newUi.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kito.ui.components.AttendanceCard
import com.kito.ui.components.AttendanceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceListScreen(
    items: List<AttendanceItem> = sampleAttendance,
    onRefresh: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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

// ---------- Sample preview data (use in preview or call the composable) ----------
val sampleAttendance = listOf(
    AttendanceItem("Data Mining and Data Warehousing", 4, 41, "Amiya Ranjan Panda"),
    AttendanceItem("Engineering Economics", 4, 39, "Arvind Kumar Yadav"),
    AttendanceItem("Design and Analysis of Algorithms", 1, 41, "Partha Sarathi Paul"),
    AttendanceItem("Software Engineering", 24, 52, "Ipsita Paul"),
    AttendanceItem("Computer Networks", 10, 40, "Nitin Varyani"),
    AttendanceItem("Algorithms Laboratory", 0, 14, "Partha Sarathi Paul")
)
