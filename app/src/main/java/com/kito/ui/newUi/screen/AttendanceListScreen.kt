package com.kito.ui.newUi.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kito.ui.components.AttendanceCard
import com.kito.ui.components.AttendanceItem
import com.kito.ui.components.UIColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceListScreen(
    items: List<AttendanceItem> = sampleAttendance,
    onRefresh: () -> Unit = {}
) {
    val uiColors = UIColors()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        itemsIndexed(items) { index,item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF261E26)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(
                    topStart = if (index == 0) 24.dp else 4.dp,
                    topEnd = if (index == 0) 24.dp else 4.dp,
                    bottomStart = if (index == items.size - 1) 24.dp else 4.dp,
                    bottomEnd = if (index == items.size - 1) 24.dp else 4.dp
                )
            ) {
                AttendanceCard(item)
            }
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
