package com.pemmob.wakebrain.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val isActive: Boolean = true,
    val puzzleType: String = "Matematika", // "Matematika" atau "Trivia"
    val difficulty: String = "EASY",       // "EASY", "MEDIUM", "HARD"
    val label: String = "Alarm Pagi",
    val days: String = "Sen • Sel • Rab • Kam • Jum"
)