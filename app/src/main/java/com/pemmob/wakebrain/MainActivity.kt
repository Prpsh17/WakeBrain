package com.pemmob.wakebrain

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pemmob.wakebrain.alarm.AlarmRingingService
import com.pemmob.wakebrain.alarm.AlarmScheduler
import com.pemmob.wakebrain.alarm.alarmOrNull
import com.pemmob.wakebrain.data.local.AppDatabase
import com.pemmob.wakebrain.data.local.SettingsManager
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
import com.pemmob.wakebrain.ui.donation.DonationScreen
import com.pemmob.wakebrain.ui.home.HomeScreen
import com.pemmob.wakebrain.ui.theme.WakeBrainTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var alarmScheduler: AlarmScheduler
    private val alarmTrigger = MutableStateFlow<AlarmTrigger?>(null)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val database = AppDatabase.getDatabase(applicationContext)
        val alarmRepository = AlarmRepository(database.alarmDao())
        val puzzleRepository = PuzzleRepository(database.questionDao())
        alarmScheduler = AlarmScheduler(applicationContext)

        acceptAlarmIntent(intent)

        lifecycleScope.launch {
            alarmRepository.allAlarms.collectLatest { alarms ->
                alarms.filter { it.isActive }.forEach(alarmScheduler::schedule)
            }
        }

        setContent {
            var isDarkMode by rememberSaveable { mutableStateOf(true) }
            val currentAlarmTrigger by alarmTrigger.collectAsState()
            WakeBrainTheme(darkTheme = isDarkMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WakeBrainApp(
                        alarmRepository = alarmRepository,
                        puzzleRepository = puzzleRepository,
                        alarmScheduler = alarmScheduler,
                        triggeredAlarm = currentAlarmTrigger?.alarm,
                        triggerEventId = currentAlarmTrigger?.eventId ?: -1L,
                        isDarkMode = isDarkMode,
                        onThemeChange = { isDarkMode = it },
                    )
                }
            }
        }

        requestNotificationPermissionIfNeeded()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        acceptAlarmIntent(intent)
    }

    private fun acceptAlarmIntent(intent: Intent?) {
        val alarm = intent?.alarmOrNull() ?: return
        val scheduledAt = intent.getLongExtra(
            AlarmScheduler.EXTRA_TRIGGER_AT,
            System.currentTimeMillis(),
        )
        alarmTrigger.value = AlarmTrigger(alarm, scheduledAt)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

private data class AlarmTrigger(
    val alarm: Alarm,
    val eventId: Long,
)

@Composable
fun WakeBrainApp(
    alarmRepository: AlarmRepository,
    puzzleRepository: PuzzleRepository,
    alarmScheduler: AlarmScheduler,
    triggeredAlarm: Alarm?,
    triggerEventId: Long,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }

    val alarmViewModel: AlarmViewModel = viewModel(factory = AlarmViewModelFactory(alarmRepository, alarmScheduler))
    val puzzleViewModel: PuzzleViewModel = viewModel(factory = PuzzleViewModelFactory(puzzleRepository))

    val alarms by alarmViewModel.alarms.collectAsState()
    val puzzleState by puzzleViewModel.uiState.collectAsState()

    var currentScreen by rememberSaveable {
        mutableStateOf(
            if (triggeredAlarm != null) {
                if (settingsManager.isChallengeDisabled) "SUCCESS" else "ACTIVE_ALARM"
            } else {
                "SPLASH"
            },
        )
    }
    var selectedAlarmId by rememberSaveable { mutableStateOf<Int?>(null) }
    var activeAlarm by remember { mutableStateOf<Alarm?>(triggeredAlarm) }

    LaunchedEffect(triggerEventId) {
        if (triggeredAlarm != null) {
            activeAlarm = triggeredAlarm
            if (settingsManager.isChallengeDisabled) {
                AlarmRingingService.stop(context)
                currentScreen = "SUCCESS"
            } else {
                puzzleViewModel.loadPuzzle(triggeredAlarm.puzzleType, triggeredAlarm.difficulty)
                currentScreen = "ACTIVE_ALARM"
            }
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

    BackHandler(enabled = (currentScreen != "HOME" && currentScreen != "DONATION")) {
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
                    if (settingsManager.isChallengeDisabled) {
                        AlarmRingingService.stop(context)
                        currentScreen = "SUCCESS"
                    } else {
                        puzzleViewModel.loadPuzzle(alarm.puzzleType, alarm.difficulty)
                        currentScreen = "ACTIVE_ALARM"
                    }
                },
                onDonationClick = { currentScreen = "DONATION" },
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
                onBack = { currentScreen = "HOME" },
            )
        }
        "EDIT_ALARM" -> {
            val alarmToEdit = alarms.firstOrNull { it.id == selectedAlarmId }
            AddEditAlarmScreen(
                alarm = alarmToEdit,
                onSave = { hour, minute, puzzleType, difficulty, label, days ->
                    if (alarmToEdit != null) {
                        alarmViewModel.updateAlarm(
                            alarmToEdit.copy(hour = hour, minute = minute, puzzleType = puzzleType, difficulty = difficulty, label = label, days = days),
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
