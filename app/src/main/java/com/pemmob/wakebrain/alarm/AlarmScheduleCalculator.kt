package com.pemmob.wakebrain.alarm

import com.pemmob.wakebrain.data.model.Alarm
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId

/** Pure date/time calculation used by [AlarmScheduler] and unit tests. */
object AlarmScheduleCalculator {
    private val dayTokens = linkedMapOf(
        "Sen" to DayOfWeek.MONDAY,
        "Sel" to DayOfWeek.TUESDAY,
        "Rab" to DayOfWeek.WEDNESDAY,
        "Kam" to DayOfWeek.THURSDAY,
        "Jum" to DayOfWeek.FRIDAY,
        "Sab" to DayOfWeek.SATURDAY,
        "Min" to DayOfWeek.SUNDAY,
    )

    fun isOneTime(days: String): Boolean =
        days.isBlank() || days.contains("Sekali", ignoreCase = true)

    fun formatTimeUntil(triggerAtMillis: Long, nowMillis: Long): String {
        val remainingMillis = (triggerAtMillis - nowMillis).coerceAtLeast(0L)
        val totalMinutes = (remainingMillis + MILLIS_PER_MINUTE - 1) / MILLIS_PER_MINUTE
        if (totalMinutes < 1) return "kurang dari 1 menit lagi"

        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when {
            hours == 0L -> "$minutes menit lagi"
            minutes == 0L -> "$hours jam lagi"
            else -> "$hours jam $minutes menit lagi"
        }
    }

    fun nextTriggerMillis(
        alarm: Alarm,
        nowMillis: Long = System.currentTimeMillis(),
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): Long {
        val now = Instant.ofEpochMilli(nowMillis).atZone(zoneId)
        val selectedDays = selectedDays(alarm.days)

        for (offset in 0..7) {
            val date = now.toLocalDate().plusDays(offset.toLong())
            val candidate = date
                .atTime(alarm.hour, alarm.minute)
                .atZone(zoneId)

            val allowedDay = isOneTime(alarm.days) || date.dayOfWeek in selectedDays
            if (allowedDay && candidate.toInstant().toEpochMilli() > nowMillis) {
                return candidate.toInstant().toEpochMilli()
            }
        }

        // Defensive fallback for malformed legacy day values: schedule tomorrow.
        return now.toLocalDate()
            .plusDays(1)
            .atTime(alarm.hour, alarm.minute)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()
    }

    private fun selectedDays(days: String): Set<DayOfWeek> {
        if (days.contains("Setiap Hari", ignoreCase = true)) {
            return DayOfWeek.entries.toSet()
        }

        return dayTokens
            .filterKeys { token -> days.contains(token, ignoreCase = true) }
            .values
            .toSet()
    }

    private const val MILLIS_PER_MINUTE = 60_000L
}
