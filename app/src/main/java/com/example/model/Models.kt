package com.example.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppId {
    PHONE,
    MESSAGES,
    CONTACTS,
    CAMERA,
    GALLERY,
    CALCULATOR,
    CALENDAR,
    CLOCK,
    MUSIC,
    WEATHER,
    SETTINGS,
    FILES,
    BROWSER
}

data class AppItem(
    val id: String,
    val title: String,
    val icon: ImageVector? = null,
    val accentColor: Color = Color(0xFF06B6D4),
    val category: String = "Utilities",
    val isBuiltIn: Boolean = true,
    val builtInId: AppId? = null,
    val packageName: String? = null,
    val appIconDrawable: Drawable? = null
)

data class NotificationItem(
    val id: String,
    val appName: String,
    val title: String,
    val message: String,
    val time: String,
    val accentColor: Color = Color(0xFF06B6D4),
    val isDismissible: Boolean = true
)

data class HourlyForecast(
    val time: String,
    val temp: Double,
    val weatherCode: Int
)

data class DailyForecast(
    val day: String,
    val minTemp: Double,
    val maxTemp: Double,
    val weatherCode: Int
)

data class WeatherData(
    val cityName: String = "San Francisco",
    val temperature: Double = 21.0,
    val apparentTemp: Double = 20.5,
    val humidity: Int = 62,
    val windSpeed: Double = 14.5,
    val weatherCode: Int = 0,
    val weatherDesc: String = "Clear Sky",
    val isDay: Boolean = true,
    val hourly: List<HourlyForecast> = emptyList(),
    val daily: List<DailyForecast> = emptyList()
)

data class ContactItem(
    val id: String,
    val name: String,
    val phone: String,
    val initials: String = name.take(2).uppercase()
)

data class SongItem(
    val id: Long,
    val title: String,
    val artist: String,
    val durationMs: Long,
    val uriString: String? = null,
    val isBuiltInSynth: Boolean = false
)

data class DeviceInfo(
    val model: String = "Android Device",
    val manufacturer: String = "Google",
    val androidVersion: String = "15",
    val apiLevel: Int = 35,
    val batteryPercent: Int = 85,
    val isCharging: Boolean = false,
    val batteryHealth: String = "Good",
    val batteryTemp: Float = 28.5f,
    val networkType: String = "Wi-Fi Connected",
    val wifiSsid: String = "UIForge-Net-5G",
    val totalStorageGb: Double = 128.0,
    val freeStorageGb: Double = 64.2,
    val usedStorageGb: Double = 63.8,
    val storagePercentUsed: Int = 50,
    val ramInfo: String = "8 GB LPDDR5"
)
