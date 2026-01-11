package com.kito.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kito.data.remote.model.TeacherModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FacultyCardContent(faculty: TeacherModel) {
    val uiColors = UIColors()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

        Text(
            text = "${faculty.name}",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = uiColors.textPrimary,
            style = MaterialTheme.typography.titleMediumEmphasized
        )

        Text(
            text = "Faculty Room: ${faculty.office_room}",
            fontFamily = FontFamily.Monospace,
            color = uiColors.textSecondary,
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "Email: ${faculty.email}",
            fontFamily = FontFamily.Monospace,
            color = uiColors.textSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
