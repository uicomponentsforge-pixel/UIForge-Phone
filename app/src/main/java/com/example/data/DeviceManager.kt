package com.example.data

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.example.model.AppItem
import com.example.model.DeviceInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeviceManager(private val context: Context) {

    private val _deviceInfo = MutableStateFlow(fetchDeviceInfo())
    val deviceInfo: StateFlow<DeviceInfo> = _deviceInfo.asStateFlow()

    private val cameraManager by lazy {
        context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    }

    private val audioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }

    private var torchCameraId: String? = null
    var isFlashlightOn: Boolean = false
        private set

    init {
        try {
            cameraManager?.cameraIdList?.firstOrNull { id ->
                val chars = cameraManager?.getCameraCharacteristics(id)
                chars?.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true &&
                        chars.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            }?.let { torchCameraId = it }
        } catch (e: Exception) {
            Log.e("DeviceManager", "Torch init error: ${e.message}")
        }
    }

    fun refreshDeviceInfo() {
        _deviceInfo.value = fetchDeviceInfo()
    }

    fun fetchDeviceInfo(): DeviceInfo {
        // Real Battery
        val batteryIntent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 80
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val batteryPct = if (level >= 0 && scale > 0) (level * 100) / scale else 80
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        val healthInt = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val health = when (healthInt) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheated"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Degraded"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            else -> "Normal"
        }
        val tempTenths = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 250) ?: 250
        val tempCelsius = tempTenths / 10.0f

        // Real Network
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = connMgr?.activeNetwork
        val capabilities = connMgr?.getNetworkCapabilities(activeNetwork)
        val networkType = when {
            capabilities == null -> "Disconnected"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi Connected"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular (5G/LTE)"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "Connected"
        }

        var ssid = "Wi-Fi"
        if (capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
            try {
                val wifiMgr = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                val wifiInfo: WifiInfo? = wifiMgr?.connectionInfo
                val rawSsid = wifiInfo?.ssid
                if (!rawSsid.isNullOrBlank() && rawSsid != "<unknown ssid>") {
                    ssid = rawSsid.replace("\"", "")
                } else {
                    ssid = "Wi-Fi Network"
                }
            } catch (_: Exception) {
                ssid = "Wi-Fi Network"
            }
        }

        // Real Storage via StatFs
        val stat = StatFs(Environment.getDataDirectory().path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong
        val totalBytes = totalBlocks * blockSize
        val freeBytes = availableBlocks * blockSize
        val usedBytes = totalBytes - freeBytes

        val totalGb = String.format("%.1f", totalBytes / (1024.0 * 1024.0 * 1024.0)).toDoubleOrNull() ?: 128.0
        val freeGb = String.format("%.1f", freeBytes / (1024.0 * 1024.0 * 1024.0)).toDoubleOrNull() ?: 64.0
        val usedGb = String.format("%.1f", usedBytes / (1024.0 * 1024.0 * 1024.0)).toDoubleOrNull() ?: 64.0
        val percentUsed = if (totalBytes > 0) ((usedBytes * 100) / totalBytes).toInt() else 50

        // Real RAM
        val actMgr = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actMgr?.getMemoryInfo(memInfo)
        val totalRamGb = String.format("%.1f GB", memInfo.totalMem / (1024.0 * 1024.0 * 1024.0))

        return DeviceInfo(
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            batteryPercent = batteryPct,
            isCharging = isCharging,
            batteryHealth = health,
            batteryTemp = tempCelsius,
            networkType = networkType,
            wifiSsid = ssid,
            totalStorageGb = totalGb,
            freeStorageGb = freeGb,
            usedStorageGb = usedGb,
            storagePercentUsed = percentUsed,
            ramInfo = totalRamGb
        )
    }

    fun toggleFlashlight(): Boolean {
        return try {
            val id = torchCameraId ?: return false
            val newState = !isFlashlightOn
            cameraManager?.setTorchMode(id, newState)
            isFlashlightOn = newState
            true
        } catch (e: Exception) {
            Log.e("DeviceManager", "Failed to toggle torch: ${e.message}")
            false
        }
    }

    fun getVolume(streamType: Int = AudioManager.STREAM_MUSIC): Float {
        val current = audioManager?.getStreamVolume(streamType) ?: 0
        val max = audioManager?.getStreamMaxVolume(streamType) ?: 1
        return current.toFloat() / max.toFloat().coerceAtLeast(1f)
    }

    fun setVolume(streamType: Int = AudioManager.STREAM_MUSIC, ratio: Float) {
        val max = audioManager?.getStreamMaxVolume(streamType) ?: 15
        val target = (ratio.coerceIn(0f, 1f) * max).toInt()
        try {
            audioManager?.setStreamVolume(streamType, target, 0)
        } catch (e: Exception) {
            Log.e("DeviceManager", "Volume adjust error: ${e.message}")
        }
    }

    fun getInstalledThirdPartyApps(): List<AppItem> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }

        return resolved.mapNotNull { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            if (pkg == context.packageName) return@mapNotNull null // Exclude self
            val label = resolveInfo.loadLabel(pm).toString()
            val icon = resolveInfo.loadIcon(pm)
            AppItem(
                id = pkg,
                title = label,
                isBuiltIn = false,
                packageName = pkg,
                appIconDrawable = icon
            )
        }.sortedBy { it.title.lowercase() }
    }

    fun launchAppByPackage(packageName: String): Boolean {
        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("DeviceManager", "Launch error: ${e.message}")
            false
        }
    }
}
