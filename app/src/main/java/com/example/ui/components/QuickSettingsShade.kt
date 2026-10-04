package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeviceInfo
import com.example.model.NotificationItem
import com.example.ui.theme.ForgeCyanPrimary
import com.example.ui.theme.ForgeEmeraldTertiary
import com.example.viewmodel.OSViewModel

@Composable
fun QuickSettingsShade(
    viewModel: OSViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val isWifiOn by viewModel.isWifiEnabled.collectAsState()
    val isBluetoothOn by viewModel.isBluetoothEnabled.collectAsState()
    val isFlashlightOn by viewModel.isFlashlightOn.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isAutoRotate by viewModel.isAutoRotateEnabled.collectAsState()
    val volume by viewModel.volumeRatio.collectAsState()
    val brightness by viewModel.brightnessRatio.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notification_shade_panel"),
        color = Color(0xF2090D16) // Modern deep blur backdrop
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Row (Clock + Date + Close handle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = currentTime,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = currentDate,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "System Settings", tint = Color.White)
                    }

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Close Shade", tint = Color.White)
                    }
                }
            }

            // Quick Settings Grid (6-8 Tiles)
            val tiles = listOf(
                TileData("Wi-Fi", if (isWifiOn) deviceInfo.wifiSsid else "Off", Icons.Default.Wifi, isWifiOn) {
                    viewModel.toggleWifi(context)
                },
                TileData("Bluetooth", if (isBluetoothOn) "On" else "Off", Icons.Default.Bluetooth, isBluetoothOn) {
                    viewModel.toggleBluetooth(context)
                },
                TileData("Flashlight", if (isFlashlightOn) "On" else "Off", Icons.Default.FlashlightOn, isFlashlightOn) {
                    viewModel.toggleFlashlight()
                },
                TileData("Theme", if (isDarkMode) "Dark" else "Light", Icons.Default.DarkMode, isDarkMode) {
                    viewModel.toggleDarkMode()
                },
                TileData("Auto Rotate", if (isAutoRotate) "On" else "Locked", Icons.Default.ScreenRotation, isAutoRotate) {
                    viewModel.toggleAutoRotate()
                },
                TileData("Airplane", "Open Settings", Icons.Default.AirplanemodeActive, false) {
                    val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try { context.startActivity(intent) } catch (_: Exception) {}
                },
                TileData("Mobile Data", "Carrier", Icons.Default.SignalCellularAlt, true) {
                    val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try { context.startActivity(intent) } catch (_: Exception) {}
                },
                TileData("Lock Screen", "Secure", Icons.Default.Lock, false) {
                    viewModel.lockDevice()
                }
            )

            // Render tiles in 2 columns
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                tiles.chunked(2).forEach { rowTiles ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowTiles.forEach { tile ->
                            QuickTile(
                                tile = tile,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Brightness Slider Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1E293B).copy(alpha = 0.8f))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.BrightnessMedium, contentDescription = "Brightness", tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Slider(
                    value = brightness,
                    onValueChange = { viewModel.setBrightness(it) },
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = ForgeCyanPrimary,
                        activeTrackColor = ForgeCyanPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Volume Slider Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1E293B).copy(alpha = 0.8f))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Volume", tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Slider(
                    value = volume,
                    onValueChange = { viewModel.setVolume(it) },
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = ForgeCyanPrimary,
                        activeTrackColor = ForgeCyanPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Notifications Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifications (${notifications.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                if (notifications.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearAllNotifications() }) {
                        Text("Clear all", color = ForgeCyanPrimary, fontSize = 13.sp)
                    }
                }
            }

            // Notifications List
            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No new notifications",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications) { notif ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1E293B).copy(alpha = 0.75f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ForgeCyanPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = ForgeCyanPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = notif.appName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = ForgeCyanPrimary
                                        )
                                        Text(
                                            text = notif.time,
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = notif.message,
                                        fontSize = 13.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.dismissNotification(notif.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class TileData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val isActive: Boolean,
    val onClick: () -> Unit
)

@Composable
private fun QuickTile(
    tile: TileData,
    modifier: Modifier = Modifier
) {
    val bg = if (tile.isActive) ForgeCyanPrimary else Color(0xFF1E293B).copy(alpha = 0.8f)
    val textCol = if (tile.isActive) Color.Black else Color.White

    Surface(
        onClick = tile.onClick,
        modifier = modifier
            .height(58.dp)
            .testTag("tile_${tile.title.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        color = bg
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = tile.icon,
                contentDescription = tile.title,
                tint = textCol,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = tile.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = textCol,
                    maxLines = 1
                )
                Text(
                    text = tile.subtitle,
                    fontSize = 10.sp,
                    color = textCol.copy(alpha = 0.75f),
                    maxLines = 1
                )
            }
        }
    }
}
