package com.example.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DeviceManager
import com.example.data.RealAudioPlayer
import com.example.data.RealContactsManager
import com.example.data.WeatherService
import com.example.model.AppId
import com.example.model.AppItem
import com.example.model.DeviceInfo
import com.example.model.NotificationItem
import com.example.model.WeatherData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class WallpaperMode {
    ABSTRACT_SILK,
    CYBER_EMERALD,
    MIDNIGHT_COSMOS,
    SUNSET_NEON
}

class OSViewModel(application: Application) : AndroidViewModel(application) {

    val deviceManager = DeviceManager(application)
    val weatherService = WeatherService()
    val contactsManager = RealContactsManager(application)
    val audioPlayer = RealAudioPlayer(application)

    // Navigation & Window states
    private val _currentApp = MutableStateFlow<AppId?>(null)
    val currentApp: StateFlow<AppId?> = _currentApp.asStateFlow()

    private val _recentsList = MutableStateFlow<List<AppId>>(emptyList())
    val recentsList: StateFlow<List<AppId>> = _recentsList.asStateFlow()

    private val _isRecentsOpen = MutableStateFlow(false)
    val isRecentsOpen: StateFlow<Boolean> = _isRecentsOpen.asStateFlow()

    private val _isAppDrawerOpen = MutableStateFlow(false)
    val isAppDrawerOpen: StateFlow<Boolean> = _isAppDrawerOpen.asStateFlow()

    private val _isNotificationShadeOpen = MutableStateFlow(false)
    val isNotificationShadeOpen: StateFlow<Boolean> = _isNotificationShadeOpen.asStateFlow()

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    // Real-time Clock
    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    private val _currentDate = MutableStateFlow("")
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    // Device telemetry
    val deviceInfo: StateFlow<DeviceInfo> = deviceManager.deviceInfo

    // Weather
    private val _weather = MutableStateFlow(WeatherData())
    val weather: StateFlow<WeatherData> = _weather.asStateFlow()

    private val _isWeatherLoading = MutableStateFlow(false)
    val isWeatherLoading: StateFlow<Boolean> = _isWeatherLoading.asStateFlow()

    // Quick Settings State
    private val _isWifiEnabled = MutableStateFlow(true)
    val isWifiEnabled: StateFlow<Boolean> = _isWifiEnabled.asStateFlow()

    private val _isBluetoothEnabled = MutableStateFlow(true)
    val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled.asStateFlow()

    private val _isFlashlightOn = MutableStateFlow(false)
    val isFlashlightOn: StateFlow<Boolean> = _isFlashlightOn.asStateFlow()

    private val _isAutoRotateEnabled = MutableStateFlow(true)
    val isAutoRotateEnabled: StateFlow<Boolean> = _isAutoRotateEnabled.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _wallpaper = MutableStateFlow(WallpaperMode.ABSTRACT_SILK)
    val wallpaper: StateFlow<WallpaperMode> = _wallpaper.asStateFlow()

    private val _volumeRatio = MutableStateFlow(deviceManager.getVolume())
    val volumeRatio: StateFlow<Float> = _volumeRatio.asStateFlow()

    private val _brightnessRatio = MutableStateFlow(0.75f)
    val brightnessRatio: StateFlow<Float> = _brightnessRatio.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(
        listOf(
            NotificationItem(
                id = "1",
                appName = "UIForge OS",
                title = "System Ready",
                message = "Welcome to UIForge Phone! Live Android system APIs active.",
                time = "Now"
            ),
            NotificationItem(
                id = "2",
                appName = "Weather",
                title = "Real-time Forecast",
                message = "Live weather data streaming from Open-Meteo API.",
                time = "5m ago"
            ),
            NotificationItem(
                id = "3",
                appName = "Battery Guard",
                title = "Device Health",
                message = "Real battery monitoring active. Optimal charging status.",
                time = "12m ago"
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Apps List
    private val _installedThirdPartyApps = MutableStateFlow<List<AppItem>>(emptyList())
    val installedThirdPartyApps: StateFlow<List<AppItem>> = _installedThirdPartyApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        createNotificationChannel()
        startClockTicker()
        startDeviceMonitoring()
        loadInstalledApps()
        refreshWeather()
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            val timeFormat = SimpleDateFormat("h:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
            while (isActive) {
                val now = Date()
                _currentTime.value = timeFormat.format(now)
                _currentDate.value = dateFormat.format(now)
                delay(1000)
            }
        }
    }

    private fun startDeviceMonitoring() {
        viewModelScope.launch {
            while (isActive) {
                deviceManager.refreshDeviceInfo()
                delay(5000)
            }
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            val apps = deviceManager.getInstalledThirdPartyApps()
            _installedThirdPartyApps.value = apps
        }
    }

    fun refreshWeather(cityIndex: Int = 0) {
        viewModelScope.launch {
            _isWeatherLoading.value = true
            val city = weatherService.popularCities.getOrNull(cityIndex) ?: weatherService.popularCities[0]
            val data = weatherService.fetchWeather(city)
            _weather.value = data
            _isWeatherLoading.value = false
        }
    }

    fun openApp(appId: AppId) {
        _currentApp.value = appId
        _isAppDrawerOpen.value = false
        _isRecentsOpen.value = false
        _isNotificationShadeOpen.value = false

        // Update recents
        val list = _recentsList.value.toMutableList()
        list.remove(appId)
        list.add(0, appId)
        _recentsList.value = list
    }

    fun launchThirdPartyApp(packageName: String) {
        _isAppDrawerOpen.value = false
        _isRecentsOpen.value = false
        deviceManager.launchAppByPackage(packageName)
    }

    fun closeApp() {
        _currentApp.value = null
    }

    fun pressHome() {
        _currentApp.value = null
        _isRecentsOpen.value = false
        _isAppDrawerOpen.value = false
        _isNotificationShadeOpen.value = false
    }

    fun pressBack(): Boolean {
        return when {
            _isNotificationShadeOpen.value -> {
                _isNotificationShadeOpen.value = false
                true
            }
            _isRecentsOpen.value -> {
                _isRecentsOpen.value = false
                true
            }
            _isAppDrawerOpen.value -> {
                _isAppDrawerOpen.value = false
                true
            }
            _currentApp.value != null -> {
                _currentApp.value = null
                true
            }
            else -> false
        }
    }

    fun toggleRecents() {
        _isRecentsOpen.value = !_isRecentsOpen.value
        if (_isRecentsOpen.value) {
            _isAppDrawerOpen.value = false
            _isNotificationShadeOpen.value = false
        }
    }

    fun dismissRecentApp(appId: AppId) {
        val list = _recentsList.value.toMutableList()
        list.remove(appId)
        _recentsList.value = list
        if (_currentApp.value == appId) {
            _currentApp.value = null
        }
    }

    fun clearAllRecents() {
        _recentsList.value = emptyList()
        _currentApp.value = null
        _isRecentsOpen.value = false
    }

    fun toggleAppDrawer() {
        _isAppDrawerOpen.value = !_isAppDrawerOpen.value
        if (_isAppDrawerOpen.value) {
            _isRecentsOpen.value = false
            _isNotificationShadeOpen.value = false
        }
    }

    fun toggleNotificationShade() {
        _isNotificationShadeOpen.value = !_isNotificationShadeOpen.value
        if (_isNotificationShadeOpen.value) {
            _isRecentsOpen.value = false
            _isAppDrawerOpen.value = false
        }
    }

    fun lockDevice() {
        _isLocked.value = true
        _isNotificationShadeOpen.value = false
        _isRecentsOpen.value = false
    }

    fun unlockDevice() {
        _isLocked.value = false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleWifi(context: Context) {
        _isWifiEnabled.value = !_isWifiEnabled.value
        // Open real system Wi-Fi settings because Android Q+ restricts direct toggle by normal apps
        try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun toggleBluetooth(context: Context) {
        _isBluetoothEnabled.value = !_isBluetoothEnabled.value
        try {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun toggleFlashlight() {
        val success = deviceManager.toggleFlashlight()
        _isFlashlightOn.value = deviceManager.isFlashlightOn
    }

    fun toggleAutoRotate() {
        _isAutoRotateEnabled.value = !_isAutoRotateEnabled.value
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setWallpaper(mode: WallpaperMode) {
        _wallpaper.value = mode
    }

    fun setVolume(ratio: Float) {
        _volumeRatio.value = ratio
        deviceManager.setVolume(ratio = ratio)
    }

    fun setBrightness(ratio: Float) {
        _brightnessRatio.value = ratio
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "uiforge_channel",
                "UIForge System Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "System notifications for UIForge OS"
            }
            val manager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun postRealSystemNotification(title: String, message: String) {
        val context = getApplication<Application>()
        try {
            val builder = NotificationCompat.Builder(context, "uiforge_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            val manager = NotificationManagerCompat.from(context)
            manager.notify(System.currentTimeMillis().toInt(), builder.build())

            // Also add to UI list
            val item = NotificationItem(
                id = System.currentTimeMillis().toString(),
                appName = "UIForge System",
                title = title,
                message = message,
                time = "Just now"
            )
            _notifications.value = listOf(item) + _notifications.value
        } catch (e: SecurityException) {
            // Permission needed on Android 13+
            val item = NotificationItem(
                id = System.currentTimeMillis().toString(),
                appName = "UIForge",
                title = title,
                message = message,
                time = "Just now"
            )
            _notifications.value = listOf(item) + _notifications.value
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
