package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.NetworkCell
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeviceInfo
import com.example.ui.theme.ForgeEmeraldTertiary

@Composable
fun SystemStatusBar(
    currentTime: String,
    deviceInfo: DeviceInfo,
    onOpenShade: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .clickable { onOpenShade() }
            .padding(horizontal = 18.dp, vertical = 6.dp)
            .testTag("system_status_bar"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Time
        Text(
            text = currentTime,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Right: Telemetry (Network, Wi-Fi, Battery)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.NetworkCell,
                contentDescription = "Cellular Signal",
                tint = contentColor.copy(alpha = 0.85f),
                modifier = Modifier.size(16.dp)
            )

            Icon(
                imageVector = Icons.Default.Wifi,
                contentDescription = "Wi-Fi Status",
                tint = contentColor.copy(alpha = 0.85f),
                modifier = Modifier.size(16.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${deviceInfo.batteryPercent}%",
                    color = if (deviceInfo.batteryPercent < 20) Color(0xFFEF4444) else contentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = if (deviceInfo.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                    contentDescription = "Battery Status",
                    tint = if (deviceInfo.isCharging) ForgeEmeraldTertiary else contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
