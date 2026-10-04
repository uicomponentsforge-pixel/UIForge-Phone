package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.model.AppId
import com.example.model.AppItem
import com.example.ui.theme.ForgeCyanPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerSheet(
    builtInApps: List<AppItem>,
    thirdPartyApps: List<AppItem>,
    onOpenBuiltIn: (AppId) -> Unit,
    onLaunchThirdParty: (String) -> Unit,
    onClose: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val allApps = remember(builtInApps, thirdPartyApps) {
        builtInApps + thirdPartyApps
    }

    val filteredApps = remember(allApps, searchQuery) {
        if (searchQuery.isBlank()) allApps
        else allApps.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_drawer_screen"),
        color = Color(0xF2090D16)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header with search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search all applications...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ForgeCyanPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("drawer_search_input"),
                    shape = RoundedCornerShape(24.dp)
                )

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close Drawer", tint = Color.White)
                }
            }

            Text(
                text = "${filteredApps.size} Applications Available",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )

            // App Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredApps) { app ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                if (app.isBuiltIn && app.builtInId != null) {
                                    onOpenBuiltIn(app.builtInId)
                                } else if (app.packageName != null) {
                                    onLaunchThirdParty(app.packageName)
                                }
                            }
                            .padding(vertical = 4.dp)
                            .testTag("app_item_${app.id}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (app.isBuiltIn) app.accentColor.copy(alpha = 0.2f)
                                    else Color(0xFF1E293B)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (app.appIconDrawable != null) {
                                val bmp = remember(app.appIconDrawable) {
                                    try {
                                        app.appIconDrawable.toBitmap(96, 96).asImageBitmap()
                                    } catch (_: Exception) { null }
                                }
                                if (bmp != null) {
                                    Image(
                                        bitmap = bmp,
                                        contentDescription = app.title,
                                        modifier = Modifier.size(36.dp)
                                    )
                                } else if (app.icon != null) {
                                    Icon(
                                        imageVector = app.icon,
                                        contentDescription = app.title,
                                        tint = app.accentColor,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            } else if (app.icon != null) {
                                Icon(
                                    imageVector = app.icon,
                                    contentDescription = app.title,
                                    tint = app.accentColor,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = app.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
