package com.pemmob.wakebrain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pemmob.wakebrain.data.AppDatabase
import com.pemmob.wakebrain.data.repository.AlarmRepository
import com.pemmob.wakebrain.data.repository.PuzzleRepository
import com.pemmob.wakebrain.ui.screens.ActiveAlarmScreen
import com.pemmob.wakebrain.ui.screens.AddEditAlarmScreen
import com.pemmob.wakebrain.ui.screens.DonationScreen
import com.pemmob.wakebrain.ui.screens.HomeScreen
import com.pemmob.wakebrain.ui.screens.SuccessScreen
import com.pemmob.wakebrain.ui.theme.WakeBrainTheme
import com.pemmob.wakebrain.ui.viewmodel.AlarmViewModel
import com.pemmob.wakebrain.ui.viewmodel.AlarmViewModelFactory
import com.pemmob.wakebrain.ui.viewmodel.PuzzleUiState
import com.pemmob.wakebrain.ui.viewmodel.PuzzleViewModel
import com.pemmob.wakebrain.ui.viewmodel.PuzzleViewModelFactory
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val alarmRepository = AlarmRepository(database.alarmDao())
        val puzzleRepository = PuzzleRepository(database.questionDao())

        setContent {
            var isDarkMode by rememberSaveable { mutableStateOf(true) }
            WakeBrainTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WakeBrainApp(
                        alarmRepository = alarmRepository,
                        puzzleRepository = puzzleRepository,
                        isDarkMode = isDarkMode,
                        onThemeChange = { isDarkMode = it }
                    )
                }
            }
        }
    }
}

@Composable
fun WakeBrainApp(
    alarmRepository: AlarmRepository,
    puzzleRepository: PuzzleRepository,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val alarmViewModel: AlarmViewModel = viewModel(factory = AlarmViewModelFactory(alarmRepository))
    val puzzleViewModel: PuzzleViewModel = viewModel(factory = PuzzleViewModelFactory(puzzleRepository))

    val alarms by alarmViewModel.alarms.collectAsState()
    val puzzleState by puzzleViewModel.uiState.collectAsState()

    var currentScreen by rememberSaveable { mutableStateOf("SPLASH") }
    var selectedAlarmId by rememberSaveable { mutableStateOf<Int?>(null) }
    var activeAlarm by remember { mutableStateOf<com.pemmob.wakebrain.data.Alarm?>(null) }

    LaunchedEffect(puzzleState) {
        if (puzzleState is PuzzleUiState.Solved && currentScreen == "ACTIVE_ALARM") {
            currentScreen = "SUCCESS"
        }
    }

    BackHandler(enabled = currentScreen != "HOME" && currentScreen != "DONATION") {
        if (currentScreen == "DONATION") {
            currentScreen = "HOME"
        } else if (currentScreen != "ACTIVE_ALARM") {
            currentScreen = "HOME"
        }
    }

    when (currentScreen) {
        "HOME" -> {
            HomeScreen(
                alarms = alarms,
                isDarkMode = isDarkMode,
                onThemeChange = onThemeChange,
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
                },
                onDonationClick = { currentScreen = "DONATION" }
            )
        }
        "DONATION" -> {
            DonationScreen(onBack = { currentScreen = "HOME" })
        }
        "ADD_ALARM" -> {
            AddEditAlarmScreen(
                alarm = null,
                onSave = { hour, minute, puzzleType, difficulty, label, days ->
                    alarmViewModel.addAlarm(hour, minute, puzzleType, difficulty, label, days)
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
                            alarmToEdit.copy(hour = hour, minute = minute, puzzleType = puzzleType, difficulty = difficulty, label = label, days = days)
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
            ActiveAlarmScreen(uiState = puzzleState, alarm = activeAlarm, onSubmitAnswer = { answer -> puzzleViewModel.submitAnswer(answer) })
        }
        "SUCCESS" -> {
            SuccessScreen(onFinish = { currentScreen = "HOME" })
        }
        "SPLASH" -> {
            SplashScreen(onTimeout = { currentScreen = "HOME" })
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { android.view.animation.OvershootInterpolator(2f).getInterpolation(it) }
            )
        )
        delay(1000L)
        onTimeout()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = "WakeBrain",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(scale.value)
        )
    }
}