package com.example.ui.apps

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForgeCyanPrimary
import com.example.ui.theme.ForgeEmeraldTertiary
import com.example.ui.theme.ForgeRoseError
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ClockApp(
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("World Clock", "Stopwatch", "Timer")
    val tabIcons = listOf(Icons.Default.Public, Icons.Default.Timer, Icons.Default.HourglassEmpty)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tabIcons[index], contentDescription = title) },
                        label = { Text(title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ForgeCyanPrimary,
                            indicatorColor = ForgeCyanPrimary.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> WorldClockTab()
                1 -> StopwatchTab()
                2 -> TimerTab()
            }
        }
    }
}

@Composable
private fun WorldClockTab() {
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (isActive) {
            currentTime = Date()
            delay(1000)
        }
    }

    val localTimeFormat = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())

    val worldCities = listOf(
        "New York" to "America/New_York",
        "London" to "Europe/London",
        "Tokyo" to "Asia/Tokyo",
        "Paris" to "Europe/Paris",
        "Sydney" to "Australia/Sydney"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Analog Clock Face
        Box(
            modifier = Modifier
                .size(190.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AnalogClockCanvas(currentTime)
        }

        Text(
            text = localTimeFormat.format(currentTime),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = dateFormat.format(currentTime),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(worldCities) { _, (city, tzId) ->
                val tz = TimeZone.getTimeZone(tzId)
                val cityFormat = SimpleDateFormat("h:mm a", Locale.getDefault()).apply {
                    timeZone = tz
                }
                val cityDate = SimpleDateFormat("EEE", Locale.getDefault()).apply {
                    timeZone = tz
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(city, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                            Text(cityDate.format(currentTime), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            cityFormat.format(currentTime),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = ForgeCyanPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalogClockCanvas(date: Date) {
    val cal = java.util.Calendar.getInstance().apply { time = date }
    val hours = cal.get(java.util.Calendar.HOUR)
    val minutes = cal.get(java.util.Calendar.MINUTE)
    val seconds = cal.get(java.util.Calendar.SECOND)

    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val primaryColor = ForgeCyanPrimary
    val onSurface = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        // Dial rim
        drawCircle(color = surfaceColor, radius = radius)
        drawCircle(color = primaryColor.copy(alpha = 0.3f), radius = radius, style = Stroke(width = 4f))

        // Hour hand
        val hourAngle = Math.toRadians((hours * 30 + minutes * 0.5 - 90).toDouble())
        val hourLen = radius * 0.5f
        drawLine(
            color = onSurface,
            start = center,
            end = Offset(
                center.x + (hourLen * cos(hourAngle)).toFloat(),
                center.y + (hourLen * sin(hourAngle)).toFloat()
            ),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )

        // Minute hand
        val minAngle = Math.toRadians((minutes * 6 - 90).toDouble())
        val minLen = radius * 0.75f
        drawLine(
            color = onSurface,
            start = center,
            end = Offset(
                center.x + (minLen * cos(minAngle)).toFloat(),
                center.y + (minLen * sin(minAngle)).toFloat()
            ),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )

        // Second hand
        val secAngle = Math.toRadians((seconds * 6 - 90).toDouble())
        val secLen = radius * 0.85f
        drawLine(
            color = primaryColor,
            start = center,
            end = Offset(
                center.x + (secLen * cos(secAngle)).toFloat(),
                center.y + (secLen * sin(secAngle)).toFloat()
            ),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )

        // Center pin
        drawCircle(color = primaryColor, radius = 6f)
    }
}

@Composable
private fun StopwatchTab() {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedMillis by remember { mutableLongStateOf(0L) }
    var laps by remember { mutableStateOf(listOf<Long>()) }

    LaunchedEffect(isRunning) {
        val startTime = System.currentTimeMillis() - elapsedMillis
        while (isRunning && isActive) {
            elapsedMillis = System.currentTimeMillis() - startTime
            delay(16) // ~60fps refresh
        }
    }

    val minutes = (elapsedMillis / 60000)
    val seconds = (elapsedMillis % 60000) / 1000
    val millis = (elapsedMillis % 1000) / 10

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = String.format("%02d:%02d.%02d", minutes, seconds, millis),
            fontSize = 54.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    if (isRunning) {
                        laps = listOf(elapsedMillis) + laps
                    } else {
                        elapsedMillis = 0L
                        laps = emptyList()
                    }
                },
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(if (isRunning) "Lap" else "Reset", color = MaterialTheme.colorScheme.onSurface)
            }

            Button(
                onClick = { isRunning = !isRunning },
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .testTag("stopwatch_toggle_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) ForgeRoseError else ForgeEmeraldTertiary
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(if (isRunning) "Stop" else "Start", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(laps) { index, lapTime ->
                val lapMin = (lapTime / 60000)
                val lapSec = (lapTime % 60000) / 1000
                val lapMil = (lapTime % 1000) / 10

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Lap ${laps.size - index}", fontWeight = FontWeight.Medium)
                    Text(
                        String.format("%02d:%02d.%02d", lapMin, lapSec, lapMil),
                        fontFamily = FontFamily.Monospace,
                        color = ForgeCyanPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun TimerTab() {
    val context = LocalContext.current
    var totalSeconds by remember { mutableIntStateOf(60) }
    var remainingSeconds by remember { mutableIntStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && remainingSeconds > 0 && isActive) {
            delay(1000)
            remainingSeconds--
        }
        if (remainingSeconds == 0 && isTimerRunning) {
            isTimerRunning = false
            triggerVibrationAlert(context)
        }
    }

    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 0f
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 12.dp.toPx()
                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.2f),
                        style = Stroke(width = stroke)
                    )
                    drawArc(
                        color = ForgeCyanPrimary,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Quick Preset Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(30 to "30s", 60 to "1m", 300 to "5m", 600 to "10m").forEach { (secs, label) ->
                OutlinedButton(
                    onClick = {
                        isTimerRunning = false
                        totalSeconds = secs
                        remainingSeconds = secs
                    }
                ) {
                    Text(label)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timer Start/Stop
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    isTimerRunning = false
                    remainingSeconds = totalSeconds
                },
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
            }

            Button(
                onClick = { isTimerRunning = !isTimerRunning },
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .testTag("timer_start_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTimerRunning) ForgeRoseError else ForgeEmeraldTertiary
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(if (isTimerRunning) "Pause" else "Start", color = Color.White)
            }
        }
    }
}

private fun triggerVibrationAlert(context: Context) {
    try {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(500)
        }
    } catch (_: Exception) {}
}
