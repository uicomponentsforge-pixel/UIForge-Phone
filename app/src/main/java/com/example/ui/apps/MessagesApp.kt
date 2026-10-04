package com.example.ui.apps

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForgeCyanPrimary

data class MessageThread(
    val id: String,
    val sender: String,
    val phone: String,
    val lastMessage: String,
    val time: String,
    val unread: Boolean = false
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromMe: Boolean,
    val time: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesApp(
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var activeThread by remember { mutableStateOf<MessageThread?>(null) }
    var isComposingNew by remember { mutableStateOf(false) }

    var recipientInput by remember { mutableStateOf("") }
    var messageInput by remember { mutableStateOf("") }

    var threads by remember {
        mutableStateOf(
            listOf(
                MessageThread("1", "Alex Vance", "+1 (555) 321-9876", "Got the UIForge design updates!", "10:42 AM", true),
                MessageThread("2", "Cloud Services", "22022", "Your security verification code is 849-102.", "Yesterday"),
                MessageThread("3", "Marcus Cole", "+1 (555) 884-1290", "Let's test the CameraX integration today.", "Oct 2")
            )
        )
    }

    var chatMessages by remember {
        mutableStateOf(
            listOf(
                ChatMessage("1", "Hey! How is the new UIForge OS build running?", false, "10:30 AM"),
                ChatMessage("2", "Running exceptionally fast. Real Android APIs connected!", true, "10:35 AM"),
                ChatMessage("3", "Got the UIForge design updates!", false, "10:42 AM")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            activeThread != null -> activeThread!!.sender
                            isComposingNew -> "New Message"
                            else -> "Messages"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    if (activeThread != null || isComposingNew) {
                        IconButton(onClick = {
                            activeThread = null
                            isComposingNew = false
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (activeThread == null && !isComposingNew) {
                        FloatingActionButton(
                            onClick = { isComposingNew = true },
                            containerColor = ForgeCyanPrimary,
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("compose_sms_btn"),
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "New Message")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
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
            when {
                // Thread View
                activeThread != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(chatMessages) { msg ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = if (msg.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
                                ) {
                                    Column(
                                        horizontalAlignment = if (msg.isFromMe) Alignment.End else Alignment.Start
                                    ) {
                                        Surface(
                                            color = if (msg.isFromMe) ForgeCyanPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (msg.isFromMe) 16.dp else 4.dp,
                                                bottomEnd = if (msg.isFromMe) 4.dp else 16.dp
                                            )
                                        ) {
                                            Text(
                                                text = msg.text,
                                                color = if (msg.isFromMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                                fontSize = 15.sp
                                            )
                                        }
                                        Text(
                                            text = msg.time,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 4.dp).padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Message Input Field
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = messageInput,
                                onValueChange = { messageInput = it },
                                placeholder = { Text("Text message...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_input_field"),
                                shape = RoundedCornerShape(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (messageInput.isNotBlank()) {
                                        val newMsg = ChatMessage(
                                            id = System.currentTimeMillis().toString(),
                                            text = messageInput,
                                            isFromMe = true,
                                            time = "Now"
                                        )
                                        chatMessages = chatMessages + newMsg
                                        sendRealSms(context, activeThread?.phone ?: "", messageInput)
                                        messageInput = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ForgeCyanPrimary)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                // New Compose View
                isComposingNew -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = recipientInput,
                            onValueChange = { recipientInput = it },
                            label = { Text("Recipient (phone number)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sms_recipient_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            label = { Text("Message") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .testTag("sms_message_body_input"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Button(
                            onClick = {
                                if (recipientInput.isNotBlank() && messageInput.isNotBlank()) {
                                    sendRealSms(context, recipientInput, messageInput)
                                    threads = listOf(
                                        MessageThread(
                                            id = System.currentTimeMillis().toString(),
                                            sender = recipientInput,
                                            phone = recipientInput,
                                            lastMessage = messageInput,
                                            time = "Just now"
                                        )
                                    ) + threads
                                    isComposingNew = false
                                    recipientInput = ""
                                    messageInput = ""
                                } else {
                                    Toast.makeText(context, "Please enter recipient and message", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("send_sms_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = ForgeCyanPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send SMS via System Service")
                        }
                    }
                }

                // Conversation Threads List
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(threads) { thread ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { activeThread = thread }
                                    .testTag("thread_item_${thread.id}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(ForgeCyanPrimary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = thread.sender.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = ForgeCyanPrimary,
                                            fontSize = 18.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = thread.sender,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = thread.time,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = thread.lastMessage,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
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
}

private fun sendRealSms(context: Context, phone: String, message: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${Uri.encode(phone)}")
            putExtra("sms_body", message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "SMS Composer error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
