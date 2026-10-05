package com.pemmob.wakebrain.alarm

import android.content.Intent
import com.pemmob.wakebrain.data.model.Alarm

fun Intent.putAlarm(alarm: Alarm, triggerAtMillis: Long = System.currentTimeMillis()): Intent = apply {
    putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarm.id)
    putExtra(AlarmScheduler.EXTRA_HOUR, alarm.hour)
    putExtra(AlarmScheduler.EXTRA_MINUTE, alarm.minute)
    putExtra(AlarmScheduler.EXTRA_PUZZLE_TYPE, alarm.puzzleType)
    putExtra(AlarmScheduler.EXTRA_DIFFICULTY, alarm.difficulty)
    putExtra(AlarmScheduler.EXTRA_LABEL, alarm.label)
    putExtra(AlarmScheduler.EXTRA_DAYS, alarm.days)
    putExtra(AlarmScheduler.EXTRA_RINGTONE, alarm.ringtone)
    putExtra(AlarmScheduler.EXTRA_TRIGGER_AT, triggerAtMillis)
}

fun Intent.alarmOrNull(): Alarm? {
    val alarmId = getIntExtra(AlarmScheduler.EXTRA_ALARM_ID, -1)
    if (alarmId < 0) return null

    return Alarm(
        id = alarmId,
        hour = getIntExtra(AlarmScheduler.EXTRA_HOUR, 7),
        minute = getIntExtra(AlarmScheduler.EXTRA_MINUTE, 0),
        isActive = true,
        puzzleType = getStringExtra(AlarmScheduler.EXTRA_PUZZLE_TYPE) ?: "Matematika",
        difficulty = getStringExtra(AlarmScheduler.EXTRA_DIFFICULTY) ?: "EASY",
        label = getStringExtra(AlarmScheduler.EXTRA_LABEL) ?: "Alarm",
        days = getStringExtra(AlarmScheduler.EXTRA_DAYS) ?: "Sekali Saja",
        ringtone = getStringExtra(AlarmScheduler.EXTRA_RINGTONE) ?: "Nada 1",
    )
}
