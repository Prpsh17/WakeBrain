package com.pemmob.wakebrain.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pemmob.wakebrain.data.Alarm
import com.pemmob.wakebrain.data.repository.AlarmRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlarmViewModel(private val repository: AlarmRepository) : ViewModel() {

    // Aliran daftar alarm real-time untuk LazyColumn
    val alarms: StateFlow<List<Alarm>> = repository.allAlarms.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleAlarmActive(alarm: Alarm, isActive: Boolean) {
        viewModelScope.launch {
            repository.update(alarm.copy(isActive = isActive))
        }
    }

    fun addAlarm(hour: Int, minute: Int, puzzleType: String) {
        viewModelScope.launch {
            repository.insert(Alarm(hour = hour, minute = minute, isActive = true, puzzleType = puzzleType))
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.delete(alarm)
        }
    }
}

class AlarmViewModelFactory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            return AlarmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
