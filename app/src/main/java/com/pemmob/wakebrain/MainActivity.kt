package com.pemmob.wakebrain

import android.content.Intent
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
import com.pemmob.wakebrain.alarm.AlarmScheduler
import com.pemmob.wakebrain.alarm.alarmOrNull
import com.pemmob.wakebrain.data.local.AppDatabase
import com.pemmob.wakebrain.data.model.Alarm
import com.pemmob.wakebrain.data.repository.AlarmRepository
import com.pemmob.wakebrain.data.repository.PuzzleRepository
import com.pemmob.wakebrain.ui.activealarm.ActiveAlarmScreen
import com.pemmob.wakebrain.ui.activealarm.PuzzleUiState
import com.pemmob.wakebrain.ui.activealarm.PuzzleViewModel
import com.pemmob.wakebrain.ui.activealarm.PuzzleViewModelFactory
import com.pemmob.wakebrain.ui.activealarm.SuccessScreen
import com.pemmob.wakebrain.ui.alarm.AddEditAlarmScreen
import com.pemmob.wakebrain.ui.alarm.AlarmViewModel
import com.pemmob.wakebrain.ui.alarm.AlarmViewModelFactory
import com.pemmob.wakebrain.ui.home.HomeScreen
import com.pemmob.wakebrain.ui.theme.WakeBrainTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var alarmScheduler: AlarmScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val alarmRepository = AlarmRepository(database.alarmDao())
        val puzzleRepository = PuzzleRepository(database.questionDao())
        alarmScheduler = AlarmScheduler(applicationContext)

        val initialAlarm = intent?.alarmOrNull()

        setContent {
            var isDarkMode by rememberSaveable { mutableStateOf(true) }
            WakeBrainTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WakeBrainApp(
                        alarmRepository = alarmRepository,
                        puzzleRepository = puzzleRepository,
                        alarmScheduler = alarmScheduler,
                        triggeredAlarm = initialAlarm,
                        isDarkMode = isDarkMode,
                        onThemeChange = { isDarkMode = it },
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@Composable
fun WakeBrainApp(
    alarmRepository: AlarmRepository,
    puzzleRepository: PuzzleRepository,
    alarmScheduler: AlarmScheduler,
    triggeredAlarm: Alarm?,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,
) {
    val alarmViewModel: AlarmViewModel = viewModel(factory = AlarmViewModelFactory(alarmRepository, alarmScheduler))
    val puzzleViewModel: PuzzleViewModel = viewModel(factory = PuzzleViewModelFactory(puzzleRepository))

    val alarms by alarmViewModel.alarms.collectAsState()
    val puzzleState by puzzleViewModel.uiState.collectAsState()

    var currentScreen by rememberSaveable {
        mutableStateOf(
            if (triggeredAlarm != null) "ACTIVE_ALARM" else "SPLASH",
        )
    }
    var selectedAlarmId by rememberSaveable { mutableStateOf<Int?>(null) }
    var activeAlarm by remember { mutableStateOf<Alarm?>(triggeredAlarm) }

    LaunchedEffect(triggeredAlarm) {
        if (triggeredAlarm != null) {
            activeAlarm = triggeredAlarm
            puzzleViewModel.loadPuzzle(triggeredAlarm.puzzleType, triggeredAlarm.difficulty)
            currentScreen = "ACTIVE_ALARM"
        }
    }

    LaunchedEffect(puzzleState) {
        if (puzzleState is PuzzleUiState.Solved && currentScreen == "ACTIVE_ALARM") {
            activeAlarm?.let { alarm ->
                if (alarm.days.contains("Sekali", ignoreCase = true) || alarm.days.isEmpty()) {
                    alarmViewModel.toggleAlarmActive(alarm, false)
                }
            }
            currentScreen = "SUCCESS"
        }
    }

    BackHandler(enabled = (currentScreen != "HOME" && currentScreen != "ACTIVE_ALARM")) {
        currentScreen = "HOME"
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
            )
        }
        "ADD_ALARM" -> {
            AddEditAlarmScreen(
                alarm = null,
                onSave = { hour, minute, puzzleType, difficulty, label, days, ringtone ->
                    alarmViewModel.addAlarm(hour, minute, puzzleType, difficulty, label, days, ringtone)
                    currentScreen = "HOME"
                },
                onDelete = {},
                onBack = { currentScreen = "HOME" },
            )
        }
        "EDIT_ALARM" -> {
            val alarmToEdit = alarms.firstOrNull { it.id == selectedAlarmId }
            AddEditAlarmScreen(
                alarm = alarmToEdit,
                onSave = { hour, minute, puzzleType, difficulty, label, days, ringtone ->
                    if (alarmToEdit != null) {
                        alarmViewModel.updateAlarm(
                            alarmToEdit.copy(hour = hour, minute = minute, puzzleType = puzzleType, difficulty = difficulty, label = label, days = days, ringtone = ringtone),
                        )
                    }
                    currentScreen = "HOME"
                },
                onDelete = { alarm ->
                    alarmViewModel.deleteAlarm(alarm)
                    currentScreen = "HOME"
                },
                onBack = { currentScreen = "HOME" },
            )
        }
        "ACTIVE_ALARM" -> {
            ActiveAlarmScreen(
                uiState = puzzleState,
                alarm = activeAlarm,
                onSubmitAnswer = { answer -> puzzleViewModel.submitAnswer(answer) },
            )
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
                easing = { android.view.animation.OvershootInterpolator(2f).getInterpolation(it) },
            ),
        )
        delay(1000L)
        onTimeout()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Text(
            text = "WakeBrain",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.scale(scale.value),
        )
    }
}
