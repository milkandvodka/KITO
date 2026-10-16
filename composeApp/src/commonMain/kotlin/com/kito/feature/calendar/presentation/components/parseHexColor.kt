package com.kito.feature.calendar.presentation.components

import androidx.compose.ui.graphics.Color

/** "#RRGGBB" / "RRGGBB" / "#AARRGGBB" -> Color, null when invalid. */
fun parseHexColor(hex: String?): Color? {
    val clean = hex?.trim()?.removePrefix("#") ?: return null
    val argb = when (clean.length) {
        6 -> clean.toLongOrNull(16)?.or(0xFF000000)
        8 -> clean.toLongOrNull(16)
        else -> null
    } ?: return null
    return Color(argb.toInt())
}
