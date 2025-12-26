package com.kito.ui.components

fun formatTo12Hour(time: String): String {
    return try {
        val inputFormat =
            java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        val outputFormat =
            java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())

        val date = inputFormat.parse(time)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        time
    }
}