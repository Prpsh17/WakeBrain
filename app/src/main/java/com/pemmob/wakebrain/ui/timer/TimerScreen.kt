package com.pemmob.wakebrain.ui.timer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TimerScreen() {
    var hours by remember { mutableIntStateOf(0) }
    var minutes by remember { mutableIntStateOf(5) }
    var seconds by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var totalSeconds by remember { mutableIntStateOf(0) }
    var currentSeconds by remember { mutableIntStateOf(0) }

    val infiniteTransition = rememberInfiniteTransition(label = "hourglass")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "rotation"
    )

    LaunchedEffect(isRunning, totalSeconds) {
        if (isRunning && totalSeconds > 0) {
            while (totalSeconds > 0) {
                delay(1000L)
                totalSeconds--
                currentSeconds++
            }
            isRunning = false
        }
    }

    val startTimer = {
        val total = hours * 3600 + minutes * 60 + seconds
        if (total > 0) { totalSeconds = total; currentSeconds = 0; isRunning = true }
    }
    val resetTimer = { isRunning = false; totalSeconds = 0; currentSeconds = 0 }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Timer", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 16.dp))

        Box(modifier = Modifier.size(150.dp).padding(16.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = Icons.Default.HourglassBottom, contentDescription = "Hourglass", modifier = Modifier.size(120.dp).rotate(if (isRunning) rotation else 0f), tint = MaterialTheme.colorScheme.primary)
        }

        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer), shape = RoundedCornerShape(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                TimeSelector(value = hours, onValueChange = { if (!isRunning) hours = it }, label = "Jam", enabled = !isRunning)
                Text(text = ":", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                TimeSelector(value = minutes, onValueChange = { if (!isRunning) minutes = it }, label = "Menit", enabled = !isRunning)
                Text(text = ":", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                TimeSelector(value = seconds, onValueChange = { if (!isRunning) seconds = it }, label = "Detik", enabled = !isRunning)
            }
        }

        if (isRunning && totalSeconds > 0) {
            val progress = currentSeconds.toFloat() / (hours * 3600 + minutes * 60 + seconds)
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = MaterialTheme.colorScheme.primary, trackColor = MaterialTheme.colorScheme.surfaceContainerHighest)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Sisa: ${formatTime(totalSeconds)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (isRunning) {
                Button(onClick = { isRunning = false }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Icon(imageVector = Icons.Default.Pause, contentDescription = "Jeda")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Jeda", fontWeight = FontWeight.Bold)
                }
                Button(onClick = resetTimer, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(onClick = startTimer, enabled = hours > 0 || minutes > 0 || seconds > 0, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.weight(1f).height(56.dp)) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Mulai")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mulai Timer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Preset Cepat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickPresetChip(modifier = Modifier.weight(1f), label = "5 Menit", onClick = { if (!isRunning) { hours = 0; minutes = 5; seconds = 0 } }, enabled = !isRunning)
            QuickPresetChip(modifier = Modifier.weight(1f), label = "15 Menit", onClick = { if (!isRunning) { hours = 0; minutes = 15; seconds = 0 } }, enabled = !isRunning)
            QuickPresetChip(modifier = Modifier.weight(1f), label = "30 Menit", onClick = { if (!isRunning) { hours = 0; minutes = 30; seconds = 0 } }, enabled = !isRunning)
        }
    }
}

@Composable
fun TimeSelector(value: Int, onValueChange: (Int) -> Unit, label: String, enabled: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp).clickable(enabled) {
        if (enabled) {
            val newValue = (value + 1) % if (label == "Jam") 24 else 60
            onValueChange(newValue)
        }
    }) {
        Surface(modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceContainerHighest, tonalElevation = 2.dp) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = String.format("%02d", value), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun QuickPresetChip(modifier: Modifier = Modifier, label: String, onClick: () -> Unit, enabled: Boolean) {
    Surface(modifier = modifier.height(40.dp).clickable(enabled, onClick = onClick), shape = RoundedCornerShape(20.dp), color = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

fun formatTime(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) String.format("%02d:%02d:%02d", h, m, s) else String.format("%02d:%02d", m, s)
}
