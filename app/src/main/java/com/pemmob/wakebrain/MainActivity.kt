package com.pemmob.wakebrain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pemmob.wakebrain.data.Alarm
import com.pemmob.wakebrain.data.AppDatabase
import com.pemmob.wakebrain.data.repository.AlarmRepository
import com.pemmob.wakebrain.data.repository.PuzzleRepository
import com.pemmob.wakebrain.ui.screens.ActiveAlarmScreen
import com.pemmob.wakebrain.ui.screens.AddEditAlarmScreen
import com.pemmob.wakebrain.ui.screens.HomeScreen
import com.pemmob.wakebrain.ui.screens.SuccessScreen
import com.pemmob.wakebrain.ui.theme.WakeBrainTheme
import com.pemmob.wakebrain.ui.viewmodel.AlarmViewModel
import com.pemmob.wakebrain.ui.viewmodel.AlarmViewModelFactory
import com.pemmob.wakebrain.ui.viewmodel.PuzzleUiState
import com.pemmob.wakebrain.ui.viewmodel.PuzzleViewModel
import com.pemmob.wakebrain.ui.viewmodel.PuzzleViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val alarmRepository = AlarmRepository(database.alarmDao())
        val puzzleRepository = PuzzleRepository(database.questionDao())

        setContent {
            WakeBrainTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WakeBrainApp(
                        alarmRepository = alarmRepository,
                        puzzleRepository = puzzleRepository
                    )
                }
            }
        }
    }
}

@Composable
fun WakeBrainApp(
    alarmRepository: AlarmRepository,
    puzzleRepository: PuzzleRepository
) {
    val alarmViewModel: AlarmViewModel = viewModel(
        factory = AlarmViewModelFactory(alarmRepository)
    )
    val puzzleViewModel: PuzzleViewModel = viewModel(
        factory = PuzzleViewModelFactory(puzzleRepository)
    )

    val alarms by alarmViewModel.alarms.collectAsState()
    val puzzleState by puzzleViewModel.uiState.collectAsState()

    // State Navigation (Materi 5: State Management & UDF)
    var currentScreen by rememberSaveable { mutableStateOf("HOME") }
    var selectedAlarmId by rememberSaveable { mutableStateOf<Int?>(null) }
    var activeAlarm by remember { mutableStateOf<Alarm?>(null) }

    // Saat puzzleState menjadi Solved, otomatis arahkan ke layar SUCCESS
    LaunchedEffect(puzzleState) {
        if (puzzleState is PuzzleUiState.Solved && currentScreen == "ACTIVE_ALARM") {
            currentScreen = "SUCCESS"
        }
    }

    // Tangani tombol Back fisik perangkat
    BackHandler(enabled = currentScreen != "HOME") {
        if (currentScreen != "ACTIVE_ALARM") {
            currentScreen = "HOME"
        }
    }

    when (currentScreen) {
        "HOME" -> {
            HomeScreen(
                alarms = alarms,
                onAddAlarmClick = {
                    selectedAlarmId = null
                    currentScreen = "ADD_ALARM"
                },
                onEditAlarmClick = { alarmId ->
                    selectedAlarmId = alarmId
                    currentScreen = "EDIT_ALARM"
                },
                onToggleActive = { alarm, isActive ->
                    alarmViewModel.toggleAlarmActive(alarm, isActive)
                },
                onTriggerAlarmSimulate = { alarm ->
                    activeAlarm = alarm
                    puzzleViewModel.loadPuzzle(alarm.puzzleType, alarm.difficulty)
                    currentScreen = "ACTIVE_ALARM"
                }
            )
        }

        "ADD_ALARM" -> {
            AddEditAlarmScreen(
                alarm = null,
                onSave = { hour, minute, puzzleType, difficulty, label, days ->
                    alarmViewModel.addAlarm(
                        hour = hour,
                        minute = minute,
                        puzzleType = puzzleType,
                        difficulty = difficulty,
                        label = label,
                        days = days
                    )
                    currentScreen = "HOME"
                },
                onDelete = {},
                onBack = { currentScreen = "HOME" }
            )
        }

        "EDIT_ALARM" -> {
            val alarmToEdit = alarms.firstOrNull { it.id == selectedAlarmId }
            AddEditAlarmScreen(
                alarm = alarmToEdit,
                onSave = { hour, minute, puzzleType, difficulty, label, days ->
                    if (alarmToEdit != null) {
                        alarmViewModel.updateAlarm(
                            alarmToEdit.copy(
                                hour = hour,
                                minute = minute,
                                puzzleType = puzzleType,
                                difficulty = difficulty,
                                label = label,
                                days = days
                            )
                        )
                    }
                    currentScreen = "HOME"
                },
                onDelete = { alarm ->
                    alarmViewModel.deleteAlarm(alarm)
                    currentScreen = "HOME"
                },
                onBack = { currentScreen = "HOME" }
            )
        }

        "ACTIVE_ALARM" -> {
            ActiveAlarmScreen(
                uiState = puzzleState,
                alarm = activeAlarm,
                onSubmitAnswer = { answer ->
                    puzzleViewModel.submitAnswer(answer)
                }
            )
        }

        "SUCCESS" -> {
            SuccessScreen(
                onFinish = {
                    currentScreen = "HOME"
                }
            )
        }
    }
}