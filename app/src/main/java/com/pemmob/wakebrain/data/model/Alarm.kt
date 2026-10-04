package com.pemmob.wakebrain.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val isActive: Boolean = true,
    val puzzleType: String = "Matematika",
    val difficulty: String = "EASY",
    val label: String = "Alarm Pagi",
    val days: String = "Sen • Sel • Rab • Kam • Jum",
    val ringtone: String = "Nada 1",
)
