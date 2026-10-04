package com.example.ui.apps

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.theme.ForgeCyanPrimary
import com.example.ui.theme.ForgeEmeraldTertiary
import com.example.ui.theme.ForgeVioletSecondary
import com.example.viewmodel.OSViewModel
import com.example.viewmodel.WallpaperMode

enum class SettingsSection {
    HOME,
    WIFI,
    BLUETOOTH,
    DISPLAY,
    SOUND,
    NOTIFICATIONS,
    BATTERY,
    STORAGE,
    APPS,
    SECURITY,
    ABOUT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsApp(
    viewModel: OSViewModel,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var currentSection by remember { mutableStateOf(SettingsSection.HOME) }
    val deviceInfo by viewModel.deviceInfo.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val wallpaper by viewModel.wallpaper.collectAsState()
    val volumeRatio by viewModel.volumeRatio.collectAsState()
    val brightnessRatio by viewModel.brightnessRatio.collectAsState()
    val installedApps by viewModel.installedThirdPartyApps.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (currentSection == SettingsSection.HOME) "Settings"
                        else currentSection.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (currentSection != SettingsSection.HOME) {
                        IconButton(onClick = { currentSection = SettingsSection.HOME }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (currentSection) {
                SettingsSection.HOME -> SettingsHomeList(
                    deviceInfo = deviceInfo,
                    onSelectSection = { currentSection = it }
                )
                SettingsSection.WIFI -> WifiSettingsView(context, deviceInfo)
                SettingsSection.BLUETOOTH -> BluetoothSettingsView(context)
                SettingsSection.DISPLAY -> DisplaySettingsView(
                    context = context,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    wallpaper = wallpaper,
                    onSelectWallpaper = { viewModel.setWallpaper(it) },
                    brightness = brightnessRatio,
                    onBrightnessChange = { viewModel.setBrightness(it) }
                )
                SettingsSection.SOUND -> SoundSettingsView(
                    context = context,
                    volume = volumeRatio,
                    onVolumeChange = { viewModel.setVolume(it) }
                )
                SettingsSection.NOTIFICATIONS -> NotificationsSettingsView(
                    context = context,
                    onTestNotification = {
                        viewModel.postRealSystemNotification(
                            "UIForge OS Test",
                            "Real system notification triggered successfully!"
                        )
                    }
                )
                SettingsSection.BATTERY -> BatterySettingsView(context, deviceInfo)
                SettingsSection.STORAGE -> StorageSettingsView(context, deviceInfo)
                SettingsSection.APPS -> AppsSettingsView(context, installedApps)
                SettingsSection.SECURITY -> SecuritySettingsView(context, onLockDevice = { viewModel.lockDevice() })
                SettingsSection.ABOUT -> AboutPhoneView(deviceInfo)
            }
        }
    }
}

@Composable
private fun SettingsHomeList(
    deviceInfo: DeviceInfo,
    onSelectSection: (SettingsSection) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Quick Device Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(ForgeCyanPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Smartphone,
                            contentDescription = null,
                            tint = ForgeCyanPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "${deviceInfo.manufacturer} ${deviceInfo.model}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "UIForge OS • Android ${deviceInfo.androidVersion}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        val items = listOf(
            SettingsEntry(SettingsSection.WIFI, Icons.Default.Wifi, "Wi-Fi", deviceInfo.wifiSsid),
            SettingsEntry(SettingsSection.BLUETOOTH, Icons.Default.Bluetooth, "Bluetooth", "Paired devices & connections"),
            SettingsEntry(SettingsSection.DISPLAY, Icons.Default.BrightnessMedium, "Display", "Wallpaper, Dark theme, Brightness"),
            SettingsEntry(SettingsSection.SOUND, Icons.AutoMirrored.Filled.VolumeUp, "Sound", "Volume, Vibration, Do Not Disturb"),
            SettingsEntry(SettingsSection.NOTIFICATIONS, Icons.Default.Notifications, "Notifications", "App permissions & status"),
            SettingsEntry(SettingsSection.BATTERY, Icons.Default.BatteryChargingFull, "Battery", "${deviceInfo.batteryPercent}% • ${if (deviceInfo.isCharging) "Charging" else "On battery"}"),
            SettingsEntry(SettingsSection.STORAGE, Icons.Default.Storage, "Storage", "${deviceInfo.storagePercentUsed}% used • ${deviceInfo.freeStorageGb} GB free"),
            SettingsEntry(SettingsSection.APPS, Icons.Default.Apps, "Apps", "Manage installed applications"),
            SettingsEntry(SettingsSection.SECURITY, Icons.Default.Security, "Security & Home App", "Lock screen, default launcher"),
            SettingsEntry(SettingsSection.ABOUT, Icons.Default.Info, "About Phone", "Model, Hardware & Android specs")
        )

        items(items) { entry ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectSection(entry.section) }
                    .testTag("settings_${entry.section.name.lowercase()}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = entry.icon,
                        contentDescription = entry.title,
                        tint = ForgeCyanPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = entry.subtitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private data class SettingsEntry(
    val section: SettingsSection,
    val icon: ImageVector,
    val title: String,
    val subtitle: String
)

@Composable
private fun WifiSettingsView(context: Context, deviceInfo: DeviceInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Current Network", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(deviceInfo.wifiSsid, fontSize = 15.sp)
                    AssistChip(
                        onClick = {},
                        label = { Text("Connected") },
                        colors = AssistChipDefaults.assistChipColors(
                            labelColor = ForgeEmeraldTertiary
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Transport: ${deviceInfo.networkType}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = "Modern Android security restricts third-party apps from toggling Wi-Fi directly without user intent. Use the button below to launch the system Wi-Fi management panel.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ForgeCyanPrimary)
        ) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Android System Wi-Fi Settings")
        }
    }
}

@Composable
private fun BluetoothSettingsView(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Bluetooth Radio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Manage connected Bluetooth audio devices, keyboards, and accessories via Android system controller.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ForgeCyanPrimary)
        ) {
            Icon(Icons.Default.Bluetooth, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Android Bluetooth Settings")
        }
    }
}

@Composable
private fun DisplaySettingsView(
    context: Context,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    wallpaper: WallpaperMode,
    onSelectWallpaper: (WallpaperMode) -> Unit,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dark Theme Switch
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Dark Mode", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text("Switch between light and dark UI themes", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onToggleDarkMode() }
                )
            }
        }

        // Brightness Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Display Brightness", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = brightness,
                    onValueChange = onBrightnessChange,
                    colors = SliderDefaults.colors(
                        thumbColor = ForgeCyanPrimary,
                        activeTrackColor = ForgeCyanPrimary
                    )
                )
            }
        }

        // Wallpaper Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("OS Wallpaper", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WallpaperMode.entries.forEach { mode ->
                        val isSelected = mode == wallpaper
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onSelectWallpaper(mode) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp, 90.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (mode) {
                                            WallpaperMode.ABSTRACT_SILK -> ForgeCyanPrimary
                                            WallpaperMode.CYBER_EMERALD -> ForgeEmeraldTertiary
                                            WallpaperMode.MIDNIGHT_COSMOS -> Color(0xFF0F172A)
                                            WallpaperMode.SUNSET_NEON -> Color(0xFFF43F5E)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(mode.name.take(6), fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text("System Display Settings", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun SoundSettingsView(
    context: Context,
    volume: Float,
    onVolumeChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Media Volume", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    colors = SliderDefaults.colors(
                        thumbColor = ForgeCyanPrimary,
                        activeTrackColor = ForgeCyanPrimary
                    )
                )
            }
        }

        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_SOUND_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ForgeCyanPrimary)
        ) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open System Sound Settings")
        }
    }
}

@Composable
private fun NotificationsSettingsView(
    context: Context,
    onTestNotification: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Live Notification Dispatcher", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "UIForge OS registers real Android notification channels (NotificationManagerCompat) with sound and badge alerts.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onTestNotification,
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeCyanPrimary)
                ) {
                    Text("Trigger Test Notification")
                }
            }
        }

        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text("App Notification Channel Settings", color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun BatterySettingsView(context: Context, deviceInfo: DeviceInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${deviceInfo.batteryPercent}%",
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForgeEmeraldTertiary
                )
                Text(
                    text = if (deviceInfo.isCharging) "Charging via AC / USB" else "Battery Discharging",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Battery Health", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(deviceInfo.batteryHealth, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Battery Temp", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${deviceInfo.batteryTemp} °C", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {
                    context.startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ForgeCyanPrimary)
        ) {
            Icon(Icons.Default.BatteryChargingFull, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open System Battery Usage")
        }
    }
}

@Composable
private fun StorageSettingsView(context: Context, deviceInfo: DeviceInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Internal Storage", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (deviceInfo.storagePercentUsed / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = ForgeCyanPrimary,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${deviceInfo.usedStorageGb} GB Used", fontWeight = FontWeight.SemiBold)
                    Text("${deviceInfo.freeStorageGb} GB Free", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Total Capacity: ${deviceInfo.totalStorageGb} GB (Calculated from StatFs)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(intent)
                } catch (_: Exception) {}
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ForgeCyanPrimary)
        ) {
            Icon(Icons.Default.Storage, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open System Storage Settings")
        }
    }
}

@Composable
private fun AppsSettingsView(context: Context, apps: List<com.example.model.AppItem>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "${apps.size} Installed Applications",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(apps) { app ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(app.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(
                                app.packageName ?: "",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.parse("package:${app.packageName}")
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Info", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SecuritySettingsView(context: Context, onLockDevice: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Default Home App (Launcher)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Set UIForge Phone as your smartphone's primary default home launcher.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            })
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeCyanPrimary)
                ) {
                    Text("Select Default Home App")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Lock Screen Experience", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Standard third-party apps do not possess system privileges to override Android OS power-button lock. UIForge Phone provides a secure in-app lock screen simulation with clock, notifications, and PIN.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onLockDevice,
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeVioletSecondary)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lock Screen Now")
                }
            }
        }
    }
}

@Composable
private fun AboutPhoneView(deviceInfo: DeviceInfo) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val specs = listOf(
            "Device Model" to deviceInfo.model,
            "Manufacturer" to deviceInfo.manufacturer,
            "Android Version" to "Android ${deviceInfo.androidVersion}",
            "API Level" to "API ${deviceInfo.apiLevel}",
            "RAM" to deviceInfo.ramInfo,
            "Internal Storage" to "${deviceInfo.totalStorageGb} GB",
            "Hardware Board" to Build.BOARD,
            "Hardware Chipset" to Build.HARDWARE,
            "Device Brand" to Build.BRAND,
            "Build Fingerprint" to Build.FINGERPRINT.take(45) + "..."
        )

        items(specs) { (label, value) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}
