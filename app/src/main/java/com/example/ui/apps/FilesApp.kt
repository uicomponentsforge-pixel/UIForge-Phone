package com.example.ui.apps

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForgeCyanPrimary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesApp(
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val rootDir = remember { context.filesDir }
    var currentDir by remember { mutableStateOf(rootDir) }
    var fileList by remember { mutableStateOf<List<File>>(emptyList()) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf("") }
    var isNewFolder by remember { mutableStateOf(false) }

    val safPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            Toast.makeText(context, "Selected: $uri", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshFiles() {
        val files = currentDir.listFiles()?.toList()?.sortedWith(
            compareBy({ !it.isDirectory }, { it.name.lowercase() })
        ) ?: emptyList()
        fileList = files
    }

    LaunchedEffect(currentDir) {
        // Ensure sample directories exist if directory is empty
        if (currentDir == rootDir && currentDir.listFiles().isNullOrEmpty()) {
            File(currentDir, "Documents").mkdirs()
            File(currentDir, "Downloads").mkdirs()
            File(currentDir, "Pictures").mkdirs()
            val sampleNotes = File(currentDir, "Welcome_UIForge.txt")
            sampleNotes.writeText("Welcome to UIForge OS File Explorer.\nReal file system storage accessible!")
        }
        refreshFiles()
    }

    val dateFormat = SimpleDateFormat("MMM d, yyyy  h:mm a", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Files", fontWeight = FontWeight.Bold)
                        Text(
                            text = currentDir.name,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (currentDir != rootDir && currentDir.parentFile != null) {
                        IconButton(onClick = { currentDir = currentDir.parentFile!! }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Up Directory")
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isNewFolder = true
                            showCreateDialog = true
                        }
                    ) {
                        Icon(Icons.Default.CreateNewFolder, contentDescription = "New Folder", tint = ForgeCyanPrimary)
                    }
                    IconButton(
                        onClick = {
                            isNewFolder = false
                            showCreateDialog = true
                        }
                    ) {
                        Icon(Icons.Default.NoteAdd, contentDescription = "New File", tint = ForgeCyanPrimary)
                    }
                    IconButton(
                        onClick = { safPickerLauncher.launch(arrayOf("*/*")) }
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = "Storage Access Framework")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (fileList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "This folder is empty.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(fileList) { file ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (file.isDirectory) {
                                        currentDir = file
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "File: ${file.name} (${file.length()} bytes)",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                .testTag("file_item_${file.name}"),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.AutoMirrored.Filled.InsertDriveFile,
                                    contentDescription = null,
                                    tint = if (file.isDirectory) ForgeCyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = file.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (file.isDirectory) "${file.list()?.size ?: 0} items"
                                        else "${formatFileSize(file.length())} • ${dateFormat.format(Date(file.lastModified()))}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text(if (isNewFolder) "New Folder" else "New Text File") },
                text = {
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newFileName.isNotBlank()) {
                                if (isNewFolder) {
                                    File(currentDir, newFileName.trim()).mkdirs()
                                } else {
                                    val name = if (newFileName.contains(".")) newFileName.trim() else "${newFileName.trim()}.txt"
                                    File(currentDir, name).writeText("Created with UIForge Files")
                                }
                                refreshFiles()
                                showCreateDialog = false
                                newFileName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForgeCyanPrimary)
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val exp = (Math.log(bytes.toDouble()) / Math.log(1024.0)).toInt()
    val pre = "KMGTPE"[exp - 1]
    return String.format("%.1f %sB", bytes / Math.pow(1024.0, exp.toDouble()), pre)
}
