package com.kito.feature.calendar.presentation.components

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/** A month as one Int (year * 12 + month - 1) so "next month" is just +1. */
fun LocalDate.monthIndex(): Int = year * 12 + month.number - 1

fun Int.indexYear(): Int = this / 12

fun Int.indexMonth(): Int = this % 12 + 1
