package com.pemmob.wakebrain.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pemmob.wakebrain.alarm.AlarmScheduler
import com.pemmob.wakebrain.data.model.Alarm
import com.pemmob.wakebrain.data.repository.AlarmRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmViewModel(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler? = null,
) : ViewModel() {

    // Aliran daftar alarm real-time untuk LazyColumn
    val alarms: StateFlow<List<Alarm>> = repository.allAlarms.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    fun toggleAlarmActive(alarm: Alarm, isActive: Boolean) {
        val updatedAlarm = alarm.copy(isActive = isActive)
        viewModelScope.launch {
            repository.update(updatedAlarm)
            if (isActive) {
                alarmScheduler?.schedule(updatedAlarm)
            } else {
                alarmScheduler?.cancel(updatedAlarm)
            }
        }
    }

    fun addAlarm(
        hour: Int,
        minute: Int,
        puzzleType: String = "Matematika",
        difficulty: String = "EASY",
        label: String = "Alarm Pagi",
        days: String = "Sen • Sel • Rab • Kam • Jum",
        ringtone: String = "Nada 1",
    ) {
        viewModelScope.launch {
            val newAlarm = Alarm(
                hour = hour,
                minute = minute,
                isActive = true,
                puzzleType = puzzleType,
                difficulty = difficulty,
                label = label,
                days = days,
                ringtone = ringtone,
            )
            val generatedId = repository.insert(newAlarm)
            val scheduledAlarm = newAlarm.copy(id = generatedId.toInt())
            alarmScheduler?.schedule(scheduledAlarm)
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.update(alarm)
            if (alarm.isActive) {
                alarmScheduler?.schedule(alarm)
            } else {
                alarmScheduler?.cancel(alarm)
            }
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.delete(alarm)
            alarmScheduler?.cancel(alarm)
        }
    }
}

class AlarmViewModelFactory(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler? = null,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            return AlarmViewModel(repository, alarmScheduler) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
