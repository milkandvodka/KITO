package com.kito.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FacultyCardContent(
    facultyName: String,
    facultyOffice: String?,
    facultyEmail: String?
) {
    val uiColors = UIColors()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

        Text(
            text = facultyName,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = uiColors.textPrimary,
            style = MaterialTheme.typography.titleMediumEmphasized,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "Faculty Room: ${facultyOffice?:""}",
            fontFamily = FontFamily.Monospace,
            color = uiColors.textSecondary,
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "Email: ${facultyEmail?:""}",
            fontFamily = FontFamily.Monospace,
            color = uiColors.textSecondary,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun FacultyCardShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Name
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(20.dp)
                .shimmer()
        )

        // Faculty room
        Box(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .height(14.dp)
                .shimmer()
        )

        // Email
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(14.dp)
                .shimmer()
        )
    }
}