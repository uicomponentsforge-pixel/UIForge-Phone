package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.model.AppId
import com.example.model.AppItem
import com.example.ui.apps.*
import com.example.ui.components.*
import com.example.ui.home.HomeScreen
import com.example.ui.theme.*
import com.example.viewmodel.OSViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: OSViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val builtInApps = listOf(
            AppItem(id = "phone", title = "Phone", icon = Icons.Default.Call, accentColor = ForgeEmeraldTertiary, builtInId = AppId.PHONE),
            AppItem(id = "messages", title = "Messages", icon = Icons.Default.Chat, accentColor = ForgeCyanPrimary, builtInId = AppId.MESSAGES),
            AppItem(id = "contacts", title = "Contacts", icon = Icons.Default.Contacts, accentColor = ForgeVioletSecondary, builtInId = AppId.CONTACTS),
            AppItem(id = "camera", title = "Camera", icon = Icons.Default.CameraAlt, accentColor = ForgeRoseError, builtInId = AppId.CAMERA),
            AppItem(id = "gallery", title = "Gallery", icon = Icons.Default.PhotoLibrary, accentColor = Color(0xFFF59E0B), builtInId = AppId.GALLERY),
            AppItem(id = "calculator", title = "Calculator", icon = Icons.Default.Calculate, accentColor = Color(0xFF10B981), builtInId = AppId.CALCULATOR),
            AppItem(id = "calendar", title = "Calendar", icon = Icons.Default.CalendarMonth, accentColor = Color(0xFF3B82F6), builtInId = AppId.CALENDAR),
            AppItem(id = "clock", title = "Clock", icon = Icons.Default.AccessTime, accentColor = Color(0xFFEC4899), builtInId = AppId.CLOCK),
            AppItem(id = "music", title = "Music", icon = Icons.Default.MusicNote, accentColor = Color(0xFF8B5CF6), builtInId = AppId.MUSIC),
            AppItem(id = "weather", title = "Weather", icon = Icons.Default.WbSunny, accentColor = ForgeCyanLight, builtInId = AppId.WEATHER),
            AppItem(id = "files", title = "Files", icon = Icons.Default.Folder, accentColor = Color(0xFFEAB308), builtInId = AppId.FILES),
            AppItem(id = "browser", title = "Browser", icon = Icons.Default.Public, accentColor = Color(0xFF14B8A6), builtInId = AppId.BROWSER),
            AppItem(id = "settings", title = "Settings", icon = Icons.Default.Settings, accentColor = Color(0xFF94A3B8), builtInId = AppId.SETTINGS)
        )

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentApp by viewModel.currentApp.collectAsState()
            val isLocked by viewModel.isLocked.collectAsState()
            val isRecentsOpen by viewModel.isRecentsOpen.collectAsState()
            val isAppDrawerOpen by viewModel.isAppDrawerOpen.collectAsState()
            val isNotificationShadeOpen by viewModel.isNotificationShadeOpen.collectAsState()
            val currentTime by viewModel.currentTime.collectAsState()
            val currentDate by viewModel.currentDate.collectAsState()
            val deviceInfo by viewModel.deviceInfo.collectAsState()
            val weather by viewModel.weather.collectAsState()
            val isWeatherLoading by viewModel.isWeatherLoading.collectAsState()
            val wallpaperMode by viewModel.wallpaper.collectAsState()
            val recentsList by viewModel.recentsList.collectAsState()
            val thirdPartyApps by viewModel.installedThirdPartyApps.collectAsState()
            val notifications by viewModel.notifications.collectAsState()

            // System Back Handling
            BackHandler(enabled = isNotificationShadeOpen || isRecentsOpen || isAppDrawerOpen || currentApp != null) {
                viewModel.pressBack()
            }

            UIForgeTheme(darkTheme = isDarkMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    if (isLocked) {
                        // Secure Smartphone Lock Screen
                        LockScreenView(
                            currentTime = currentTime,
                            currentDate = currentDate,
                            deviceInfo = deviceInfo,
                            notifications = notifications,
                            onUnlock = { viewModel.unlockDevice() },
                            onQuickApp = { appId ->
                                viewModel.unlockDevice()
                                viewModel.openApp(appId)
                            }
                        )
                    } else {
                        // Unlocked OS Shell
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Top System Status Bar
                            SystemStatusBar(
                                currentTime = currentTime,
                                deviceInfo = deviceInfo,
                                onOpenShade = { viewModel.toggleNotificationShade() },
                                contentColor = if (currentApp == null) Color.White else MaterialTheme.colorScheme.onSurface
                            )

                            // Main View Area: Home or Active App
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                if (currentApp == null) {
                                    HomeScreen(
                                        currentTime = currentTime,
                                        currentDate = currentDate,
                                        deviceInfo = deviceInfo,
                                        weather = weather,
                                        wallpaperMode = wallpaperMode,
                                        builtInApps = builtInApps,
                                        onOpenApp = { viewModel.openApp(it) },
                                        onOpenAppDrawer = { viewModel.toggleAppDrawer() }
                                    )
                                } else {
                                    // Render Active App
                                    when (currentApp) {
                                        AppId.PHONE -> PhoneApp(onClose = { viewModel.closeApp() })
                                        AppId.MESSAGES -> MessagesApp(onClose = { viewModel.closeApp() })
                                        AppId.CONTACTS -> ContactsApp(onClose = { viewModel.closeApp() })
                                        AppId.CAMERA -> CameraApp(
                                            onOpenGallery = { viewModel.openApp(AppId.GALLERY) },
                                            onClose = { viewModel.closeApp() }
                                        )
                                        AppId.GALLERY -> GalleryApp(onClose = { viewModel.closeApp() })
                                        AppId.CALCULATOR -> CalculatorApp(onClose = { viewModel.closeApp() })
                                        AppId.CALENDAR -> CalendarApp(onClose = { viewModel.closeApp() })
                                        AppId.CLOCK -> ClockApp(onClose = { viewModel.closeApp() })
                                        AppId.MUSIC -> MusicApp(audioPlayer = viewModel.audioPlayer, onClose = { viewModel.closeApp() })
                                        AppId.WEATHER -> WeatherApp(
                                            weatherData = weather,
                                            isLoading = isWeatherLoading,
                                            onRefresh = { viewModel.refreshWeather(it) },
                                            popularCities = viewModel.weatherService.popularCities.map { it.name },
                                            onClose = { viewModel.closeApp() }
                                        )
                                        AppId.SETTINGS -> SettingsApp(viewModel = viewModel, onClose = { viewModel.closeApp() })
                                        AppId.FILES -> FilesApp(onClose = { viewModel.closeApp() })
                                        AppId.BROWSER -> BrowserApp(onClose = { viewModel.closeApp() })
                                        null -> {}
                                    }
                                }
                            }

                            // Bottom System Navigation Bar
                            SystemNavigationBar(
                                onBack = { viewModel.pressBack() },
                                onHome = { viewModel.pressHome() },
                                onRecents = { viewModel.toggleRecents() },
                                iconColor = if (currentApp == null) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Overlay: App Drawer
                        AnimatedVisibility(
                            visible = isAppDrawerOpen,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                        ) {
                            AppDrawerSheet(
                                builtInApps = builtInApps,
                                thirdPartyApps = thirdPartyApps,
                                onOpenBuiltIn = { viewModel.openApp(it) },
                                onLaunchThirdParty = { viewModel.launchThirdPartyApp(it) },
                                onClose = { viewModel.toggleAppDrawer() }
                            )
                        }

                        // Overlay: App Switcher Carousel (Recents)
                        AnimatedVisibility(
                            visible = isRecentsOpen,
                            enter = fadeIn() + scaleIn(initialScale = 0.9f),
                            exit = fadeOut() + scaleOut(targetScale = 0.9f)
                        ) {
                            AppSwitcherCarousel(
                                recentApps = recentsList,
                                onSelectApp = { viewModel.openApp(it) },
                                onDismissApp = { viewModel.dismissRecentApp(it) },
                                onClearAll = { viewModel.clearAllRecents() },
                                onClose = { viewModel.toggleRecents() }
                            )
                        }

                        // Overlay: Notification Center / Quick Settings Shade
                        AnimatedVisibility(
                            visible = isNotificationShadeOpen,
                            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                        ) {
                            QuickSettingsShade(
                                viewModel = viewModel,
                                onClose = { viewModel.toggleNotificationShade() }
                            )
                        }
                    }
                }
            }
        }
    }
}
