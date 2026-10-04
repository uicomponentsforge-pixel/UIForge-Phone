package com.example.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppId
import com.example.model.AppItem
import com.example.model.DeviceInfo
import com.example.model.WeatherData
import com.example.ui.theme.ForgeCyanPrimary
import com.example.ui.theme.ForgeEmeraldTertiary
import com.example.ui.theme.ForgeVioletSecondary
import com.example.viewmodel.WallpaperMode

@Composable
fun HomeScreen(
    currentTime: String,
    currentDate: String,
    deviceInfo: DeviceInfo,
    weather: WeatherData,
    wallpaperMode: WallpaperMode,
    builtInApps: List<AppItem>,
    onOpenApp: (AppId) -> Unit,
    onOpenAppDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_container")
    ) {
        // Wallpaper Layer
        when (wallpaperMode) {
            WallpaperMode.ABSTRACT_SILK -> {
                Image(
                    painter = painterResource(id = R.drawable.uiforge_wallpaper),
                    contentDescription = "OS Wallpaper",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark glass scrim for contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33000000))
                )
            }
            WallpaperMode.CYBER_EMERALD -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF022C22), Color(0xFF064E3B), Color(0xFF0F172A))
                            )
                        )
                )
            }
            WallpaperMode.MIDNIGHT_COSMOS -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF1E1B4B), Color(0xFF0F172A), Color(0xFF020617))
                            )
                        )
                )
            }
            WallpaperMode.SUNSET_NEON -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF4C0519), Color(0xFF831843), Color(0xFF0F172A))
                            )
                        )
                )
            }
        }

        // Main Home Content (Pages + Bottom Dock)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp), // Space below status bar
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Horizontal Pager for Home Pages
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> PrimaryHomePage(
                        currentTime = currentTime,
                        currentDate = currentDate,
                        weather = weather,
                        apps = builtInApps.filter {
                            it.builtInId in listOf(
                                AppId.PHONE, AppId.MESSAGES, AppId.BROWSER,
                                AppId.CAMERA, AppId.GALLERY, AppId.SETTINGS,
                                AppId.MUSIC, AppId.FILES
                            )
                        },
                        onOpenApp = onOpenApp,
                        onOpenWeather = { onOpenApp(AppId.WEATHER) },
                        onOpenClock = { onOpenApp(AppId.CLOCK) }
                    )
                    1 -> UtilitiesPage(
                        deviceInfo = deviceInfo,
                        apps = builtInApps.filter {
                            it.builtInId in listOf(
                                AppId.CALCULATOR, AppId.CALENDAR, AppId.CLOCK,
                                AppId.CONTACTS, AppId.WEATHER, AppId.SETTINGS
                            )
                        },
                        onOpenApp = onOpenApp
                    )
                    2 -> DiagnosticsPage(
                        deviceInfo = deviceInfo,
                        onOpenSettings = { onOpenApp(AppId.SETTINGS) },
                        onOpenAppDrawer = onOpenAppDrawer
                    )
                }
            }

            // Page Indicator Dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { idx ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (pagerState.currentPage == idx) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == idx) Color.White
                                else Color.White.copy(alpha = 0.4f)
                            )
                    )
                }
            }

            // Bottom Dock
            BottomDock(
                onOpenApp = onOpenApp,
                onOpenAppDrawer = onOpenAppDrawer
            )
        }
    }
}

@Composable
private fun PrimaryHomePage(
    currentTime: String,
    currentDate: String,
    weather: WeatherData,
    apps: List<AppItem>,
    onOpenApp: (AppId) -> Unit,
    onOpenWeather: () -> Unit,
    onOpenClock: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Clock & Weather Widget
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .clickable { onOpenClock() }
                .testTag("home_clock_widget"),
            colors = CardDefaults.cardColors(
                containerColor = Color(0x38000000)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = currentTime,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = currentDate,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                // Weather Mini Badge
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.clickable { onOpenWeather() }
                ) {
                    Text(
                        text = "${weather.temperature.toInt()}°C",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = ForgeCyanPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = weather.cityName,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Search Bar Pill
        Surface(
            onClick = { onOpenApp(AppId.BROWSER) },
            shape = RoundedCornerShape(24.dp),
            color = Color(0x4D000000),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("home_search_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Search UIForge or web...",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        }

        // App Grid (2 rows of 4)
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            apps.chunked(4).forEach { rowApps ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    rowApps.forEach { app ->
                        AppIconItem(
                            app = app,
                            onClick = { app.builtInId?.let { onOpenApp(it) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UtilitiesPage(
    deviceInfo: DeviceInfo,
    apps: List<AppItem>,
    onOpenApp: (AppId) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Telemetry Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .clickable { onOpenApp(AppId.SETTINGS) },
            colors = CardDefaults.cardColors(
                containerColor = Color(0x38000000)
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "System Telemetry",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Battery", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                        Text(
                            "${deviceInfo.batteryPercent}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeEmeraldTertiary
                        )
                    }
                    Column {
                        Text("Storage", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                        Text(
                            "${deviceInfo.freeStorageGb} GB Free",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeCyanPrimary
                        )
                    }
                    Column {
                        Text("Network", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                        Text(
                            deviceInfo.wifiSsid.take(12),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Secondary Apps Grid
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            apps.chunked(4).forEach { rowApps ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    rowApps.forEach { app ->
                        AppIconItem(
                            app = app,
                            onClick = { app.builtInId?.let { onOpenApp(it) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosticsPage(
    deviceInfo: DeviceInfo,
    onOpenSettings: () -> Unit,
    onOpenAppDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x38000000))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("UIForge OS Diagnostics", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Model: ${deviceInfo.manufacturer} ${deviceInfo.model}", color = Color.White, fontSize = 13.sp)
                Text("Android: ${deviceInfo.androidVersion} (API ${deviceInfo.apiLevel})", color = Color.White, fontSize = 13.sp)
                Text("RAM: ${deviceInfo.ramInfo}", color = Color.White, fontSize = 13.sp)
                Text("Battery Health: ${deviceInfo.batteryHealth} (${deviceInfo.batteryTemp}°C)", color = Color.White, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onOpenSettings,
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeCyanPrimary)
                ) {
                    Text("Open Detailed System Settings")
                }
            }
        }

        Button(
            onClick = onOpenAppDrawer,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0x4D000000)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Apps, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("View All Installed Apps", color = Color.White)
        }
    }
}

@Composable
private fun AppIconItem(
    app: AppItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp)
            .testTag("home_app_${app.id}")
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0x33000000)),
            contentAlignment = Alignment.Center
        ) {
            if (app.icon != null) {
                Icon(
                    imageVector = app.icon,
                    contentDescription = app.title,
                    tint = app.accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = app.title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(64.dp)
        )
    }
}

@Composable
private fun BottomDock(
    onOpenApp: (AppId) -> Unit,
    onOpenAppDrawer: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color(0x3D000000)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Phone
            DockButton(
                icon = Icons.Default.Call,
                label = "Phone",
                tint = ForgeEmeraldTertiary,
                onClick = { onOpenApp(AppId.PHONE) }
            )

            // Messages
            DockButton(
                icon = Icons.Default.Chat,
                label = "Messages",
                tint = ForgeCyanPrimary,
                onClick = { onOpenApp(AppId.MESSAGES) }
            )

            // App Drawer Launcher
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { onOpenAppDrawer() }
                    .testTag("dock_app_drawer"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = "App Drawer",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Browser
            DockButton(
                icon = Icons.Default.Public,
                label = "Browser",
                tint = ForgeVioletSecondary,
                onClick = { onOpenApp(AppId.BROWSER) }
            )

            // Camera
            DockButton(
                icon = Icons.Default.CameraAlt,
                label = "Camera",
                tint = Color(0xFFF43F5E),
                onClick = { onOpenApp(AppId.CAMERA) }
            )
        }
    }
}

@Composable
private fun DockButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .clickable { onClick() }
            .testTag("dock_${label.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
    }
}
