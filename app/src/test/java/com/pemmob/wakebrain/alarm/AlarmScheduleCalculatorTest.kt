package com.pemmob.wakebrain.alarm

import com.pemmob.wakebrain.data.model.Alarm
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class AlarmScheduleCalculatorTest {
    private val zone = ZoneId.of("Asia/Jakarta")

    @Test
    fun oneTimeAlarmUsesTodayWhenTimeIsStillAhead() {
        val now = dateTime(2026, 10, 3, 6, 30)
        val alarm = alarm(hour = 7, minute = 0, days = "Sekali Saja")

        val result = AlarmScheduleCalculator.nextTriggerMillis(alarm, now, zone)

        assertEquals(dateTime(2026, 10, 3, 7, 0), result)
    }

    @Test
    fun oneTimeAlarmMovesToTomorrowWhenTimeHasPassed() {
        val now = dateTime(2026, 10, 3, 7, 1)
        val alarm = alarm(hour = 7, minute = 0, days = "Sekali Saja")

        val result = AlarmScheduleCalculator.nextTriggerMillis(alarm, now, zone)

        assertEquals(dateTime(2026, 10, 4, 7, 0), result)
    }

    @Test
    fun weekdayAlarmSkipsWeekend() {
        // 3 Oktober 2026 jatuh pada hari Sabtu.
        val now = dateTime(2026, 10, 3, 8, 0)
        val alarm = alarm(hour = 7, minute = 0, days = "Sen • Sel • Rab • Kam • Jum")

        val result = AlarmScheduleCalculator.nextTriggerMillis(alarm, now, zone)

        assertEquals(dateTime(2026, 10, 5, 7, 0), result)
    }

    @Test
    fun everyDayAlarmUsesNextDay() {
        val now = dateTime(2026, 10, 3, 8, 0)
        val alarm = alarm(hour = 7, minute = 0, days = "Setiap Hari (Sen - Min)")

        val result = AlarmScheduleCalculator.nextTriggerMillis(alarm, now, zone)

        assertEquals(dateTime(2026, 10, 4, 7, 0), result)
    }

    @Test
    fun remainingTimeShowsHoursAndMinutes() {
        val now = dateTime(2026, 10, 3, 8, 0)
        val trigger = dateTime(2026, 10, 3, 19, 54)

        val result = AlarmScheduleCalculator.formatTimeUntil(trigger, now)

        assertEquals("11 jam 54 menit lagi", result)
    }

    @Test
    fun remainingTimeRoundsPartialMinuteUp() {
        val result = AlarmScheduleCalculator.formatTimeUntil(
            triggerAtMillis = 89_000L,
            nowMillis = 0L,
        )

        assertEquals("2 menit lagi", result)
    }

    private fun alarm(hour: Int, minute: Int, days: String) = Alarm(
        id = 1,
        hour = hour,
        minute = minute,
        days = days,
    )

    private fun dateTime(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
    ): Long = ZonedDateTime.of(year, month, day, hour, minute, 0, 0, zone)
        .toInstant()
        .toEpochMilli()
}
