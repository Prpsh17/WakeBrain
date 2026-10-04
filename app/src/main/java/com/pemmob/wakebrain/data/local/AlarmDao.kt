package com.pemmob.wakebrain.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pemmob.wakebrain.data.model.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    // Mengambil semua alarm untuk di-render oleh LazyColumn di Beranda
    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Query("SELECT * FROM alarms WHERE isActive = 1")
    suspend fun getActiveAlarms(): List<Alarm>

    @Query("UPDATE alarms SET isActive = 0 WHERE id = :alarmId")
    suspend fun deactivateAlarm(alarmId: Int): Int

    @Insert
    suspend fun insertAlarm(alarm: Alarm): Long

    @Update
    suspend fun updateAlarm(alarm: Alarm): Int

    @Delete
    suspend fun deleteAlarm(alarm: Alarm): Int
}
