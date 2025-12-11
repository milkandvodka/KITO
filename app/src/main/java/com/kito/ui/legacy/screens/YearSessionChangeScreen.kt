package com.kito.ui.legacy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun YearSessionChangeScreen(
    onYearSessionChange: (String, String) -> Unit,
    errorText: String = ""
) {
    var selectedYear by remember { mutableStateOf("") }
    var selectedTerm by remember { mutableStateOf("") }

    // Auto-detect current year
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear - 5..currentYear).map { it.toString() }.reversed()

    // Auto-select current year & Autumn on first load
    LaunchedEffect(Unit) {
        if (selectedYear.isEmpty()) selectedYear = currentYear.toString()
        if (selectedTerm.isEmpty()) selectedTerm = "010"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Change Year & Session",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Select new academic year and term",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        // Display error message if provided
        if (errorText.isNotEmpty()) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // === YEAR: Horizontal Scrollable Chips ===
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Academic Year",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(years) { year ->
                    val isSelected = selectedYear == year
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedYear = year },
                        label = { Text(year) },
                        modifier = Modifier.height(40.dp)
                    )
                }
            }
        }

        // === TERM: Two clean toggle chips ===
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Term",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = selectedTerm == "010",
                    onClick = { selectedTerm = "010" },
                    label = { Text("Autumn (010)") }
                )
                FilterChip(
                    selected = selectedTerm == "020",
                    onClick = { selectedTerm = "020" },
                    label = { Text("Spring (020)") }
                )
            }
        }

        Button(
            onClick = {
                onYearSessionChange(selectedYear, selectedTerm)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedYear.isNotEmpty() && selectedTerm.isNotEmpty()
        ) {
            Text("Update Year & Session")
        }
    }
}