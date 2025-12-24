package com.kito.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.glance.LocalContext
import androidx.glance.text.Text
import androidx.glance.text.TextAlign

@Composable
fun AboutELabsDialog(
    onDismiss: () -> Unit,
    context: Context
) {
    val uiColors = UIColors()
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xfffffff),
                            Color(0xFFB45104)
                        ),
                        tileMode = TileMode.Mirror
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(com.kito.R.drawable.e_labs_logo),
                contentDescription = "E-Labs Logo",
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "About E-Labs",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "E-Labs is a student-driven innovation space focused on practical learning, real-world projects, and industry-ready development.",
                style = MaterialTheme.typography.bodyMedium,

            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Instagram: @elabs_official",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    openLink(
                        context= context,
                        url = "http://instagram.com/elabs.kiit/"
                    )
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Website: www.elabs.edu",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    openLink(
                        context = context,
                        "https://elabskiit.in/")
                }
            )
//
//            Spacer(modifier = Modifier.height(6.dp))
//
//            Text(
//                text = "Made with Love ❤\uFE0F ~ Android Team" ,
//                style = MaterialTheme.typography.bodyMedium
//            )

        }
    }
}
