package com.pemmob.wakebrain.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.wakebrain.data.Alarm
import com.pemmob.wakebrain.ui.theme.DarkError
import com.pemmob.wakebrain.ui.theme.DarkErrorContainer
import com.pemmob.wakebrain.ui.theme.DarkOnErrorContainer
import com.pemmob.wakebrain.ui.theme.DarkPrimary
import com.pemmob.wakebrain.ui.theme.DarkPrimaryContainer
import com.pemmob.wakebrain.ui.theme.DarkSurface
import com.pemmob.wakebrain.ui.theme.DarkSurfaceContainer
import com.pemmob.wakebrain.ui.theme.DarkSurfaceContainerHigh
import com.pemmob.wakebrain.ui.theme.DarkSurfaceContainerHighest
import com.pemmob.wakebrain.ui.theme.DarkSurfaceContainerLow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAlarmScreen(
    alarm: Alarm?,
    onSave: (hour: Int, minute: Int, puzzleType: String, difficulty: String, label: String, days: String) -> Unit,
    onDelete: (Alarm) -> Unit,
    onBack: () -> Unit
) {
    val isEditMode = (alarm != null)

    var hour by remember { mutableIntStateOf(alarm?.hour ?: 7) }
    var minute by remember { mutableIntStateOf(alarm?.minute ?: 0) }
    var isAm by remember { mutableStateOf(hour < 12) }
    var label by remember { mutableStateOf(alarm?.label ?: "Kuliah Pagi") }
    var puzzleType by remember { mutableStateOf(alarm?.puzzleType ?: "Matematika") }
    var difficulty by remember { mutableStateOf(alarm?.difficulty ?: "EASY") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val allDays = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    val selectedDays = remember {
        mutableStateListOf<String>().apply {
            if (alarm != null) {
                allDays.forEach { d ->
                    if (alarm.days.contains(d)) add(d)
                }
            } else {
                addAll(listOf("Sen", "Sel", "Rab", "Kam", "Jum"))
            }
        }
    }

    if (showDeleteDialog && alarm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = DarkSurfaceContainerHigh,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkErrorContainer.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚠️", fontSize = 18.sp)
                    }
                    Text(
                        text = "Hapus Alarm Ini?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Text(
                    text = "Alarm \"$label\" pukul ${String.format("%02d:%02d", hour, minute)} akan dihapus secara permanen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(alarm)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkErrorContainer,
                        contentColor = DarkOnErrorContainer
                    )
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Alarm" else "Tambah Alarm",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus Alarm",
                                tint = DarkError
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkSurface)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ambient Sleep Inertia Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainerLow)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "🌙", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "Bangun Lebih Fokus",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Disiplin Pagi dengan stimulasi kognitif interaktif.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 1. Time Picker Display Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Waktu Dering",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )

                        // AM/PM Toggle Pill
                        Surface(
                            shape = CircleShape,
                            color = DarkSurfaceContainerHighest
                        ) {
                            Row(modifier = Modifier.padding(2.dp)) {
                                Surface(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable {
                                            if (!isAm) {
                                                isAm = true
                                                if (hour >= 12) hour -= 12
                                            }
                                        },
                                    color = if (isAm) DarkPrimaryContainer else Color.Transparent,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "AM",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAm) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable {
                                            if (isAm) {
                                                isAm = false
                                                if (hour < 12) hour += 12
                                            }
                                        },
                                    color = if (!isAm) DarkPrimaryContainer else Color.Transparent,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "PM",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isAm) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Big Hour : Minute Display
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceContainerHigh,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = String.format("%02d", hour),
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        Text(
                            text = ":",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceContainerHigh,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = String.format("%02d", minute),
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stepper Adjustment Buttons (-15m, +15m, +1h, -1h)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    minute = (minute - 15 + 60) % 60
                                },
                            shape = CircleShape,
                            color = DarkSurfaceContainerHighest
                        ) {
                            Text(
                                text = "-15m",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    minute = (minute + 15) % 60
                                },
                            shape = CircleShape,
                            color = DarkSurfaceContainerHighest
                        ) {
                            Text(
                                text = "+15m",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    hour = (hour + 1) % 24
                                    isAm = (hour < 12)
                                },
                            shape = CircleShape,
                            color = DarkSurfaceContainerHighest
                        ) {
                            Text(
                                text = "+1 Jam",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // 2. Alarm Label Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Nama Alarm",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Contoh: Kuliah Pagi, Olahraga") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                }
            }

            // 3. Repeat Days Matrix
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ulangi Hari",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (selectedDays.size == 7) "Setiap Hari" else if (selectedDays.isEmpty()) "Sekali Saja" else selectedDays.joinToString(" • "),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        allDays.forEach { day ->
                            val isSelected = selectedDays.contains(day)
                            Surface(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        if (isSelected) selectedDays.remove(day) else selectedDays.add(day)
                                    },
                                shape = CircleShape,
                                color = if (isSelected) DarkPrimaryContainer else DarkSurfaceContainerHighest
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = day,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Cognitive Protocol Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🧠", fontSize = 20.sp)
                        Text(
                            text = "Tantangan Bangun (Anti-Snooze)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Pilih tipe tantangan utama saat alarm berdering. Aplikasi secara otomatis menyajikan 2 tantangan kognitif berurutan.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Puzzle Type Chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val types = listOf("Matematika", "Trivia")
                        types.forEach { type ->
                            val isSelected = (puzzleType.equals(type, ignoreCase = true))
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { puzzleType = type },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) DarkPrimaryContainer else DarkSurfaceContainerHighest
                            ) {
                                Text(
                                    text = if (type == "Matematika") "🔢 Matematika" else "🧩 Trivia Pengetahuan",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    // Difficulty Chips
                    Text(
                        text = "Tingkat Kesulitan:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val levels = listOf("EASY", "MEDIUM", "HARD")
                        levels.forEach { lvl ->
                            val isSelected = (difficulty.equals(lvl, ignoreCase = true))
                            Surface(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { difficulty = lvl },
                                shape = CircleShape,
                                color = if (isSelected) DarkPrimaryContainer else DarkSurfaceContainerHighest
                            ) {
                                Text(
                                    text = lvl,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Button(
                onClick = {
                    val daysStr = if (selectedDays.isEmpty()) "Sekali Saja" else selectedDays.joinToString(" • ")
                    onSave(hour, minute, puzzleType, difficulty, label, daysStr)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = if (isEditMode) "Simpan Perubahan" else "Simpan Alarm",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isEditMode) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DarkError
                    )
                ) {
                    Text(
                        text = "Hapus Alarm",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
