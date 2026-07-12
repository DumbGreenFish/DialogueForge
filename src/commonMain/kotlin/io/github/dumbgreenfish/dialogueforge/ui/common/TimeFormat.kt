package io.github.dumbgreenfish.dialogueforge.ui.common

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private fun epochToLocal(ms: Long) =
    Instant.fromEpochMilliseconds(ms).toLocalDateTime(TimeZone.currentSystemDefault())

fun formatMessageTime(ms: Long): String {
    val dt = epochToLocal(ms)
    return "${dt.hour}:${dt.minute.toString().padStart(2, '0')}"
}

fun formatDateLabel(ms: Long): String {
    val dt = epochToLocal(ms)
    val month = dt.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    return "${dt.dayOfMonth} $month ${dt.year}"
}

fun formatDate(ms: Long): String = epochToLocal(ms).date.toString()
