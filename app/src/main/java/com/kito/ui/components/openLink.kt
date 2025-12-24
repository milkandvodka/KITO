package com.kito.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

fun openLink(context: Context, url: String) {
    // Ensure the URL starts with http:// or https://
    val finalUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
        "http://$url"
    } else {
        url
    }

    val browserIntent = Intent(Intent.ACTION_VIEW, finalUrl.toUri())

    try {
        context.startActivity(browserIntent)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        Toast.makeText(context, "No browser found to open the link.", Toast.LENGTH_SHORT).show()
    }
}
