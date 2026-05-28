package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.Chat
import com.example.data.db.Message
import com.example.data.db.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    profile: UserProfile?,
    chatsList: List<Chat>,
    activeChat: Chat?,
    activeMessages: List<Message>,
    isDecrypting: Boolean,
    plugins: List<com.example.data.db.CustomPlugin> = emptyList(),
    onSavedToCloud: (String, String, String) -> Unit = { _, _, _ -> },
    onNavigateToChat: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onCreateChat: (String, Int, String) -> Unit,
    onDeleteChat: (Long) -> Unit,
    onToggleGhostMode: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showCreateChatDialog by remember { mutableStateOf(false) }

    val filteredChats = chatsList.filter {
        it.title.contains(searchQuery, ignoreCase = true) || 
        it.lastMessage.contains(searchQuery, ignoreCase = true)
    }

    if (activeChat != null) {
        // RENDER ACTIVE CONVERSATION SCREEN
        ActiveChatView(
            chat = activeChat,
            messages = activeMessages,
            isDecrypting = isDecrypting,
            customBubbleRadius = profile?.customBubbleRadius ?: 16,
            plugins = plugins,
            onSavedToCloud = onSavedToCloud,
            onBack = onNavigateBack,
            onSendMessage = onSendMessage,
            onDeleteChat = { onDeleteChat(activeChat.id) }
        )
    } else {
        // RENDER CHAT LIST SCREEN
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Primegramm",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF3B82F6).copy(alpha = 0.12f))
                                        .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "PREMIUM",
                                        color = Color(0xFF60A5FA),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                            Text(
                                if (profile?.isGhostModeActive == true) "GHOST MODE ACTIVE" else "SECURED NODE LOCAL PEER",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (profile?.isGhostModeActive == true) Color(0xFFC084FC) else Color(0xFF64748B),
                                letterSpacing = 1.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color(0xFF0F1115),
                        titleContentColor = Color.White
                    ),
                    actions = {
                        // Ghost Mode Activation Button
                        IconButton(
                            onClick = onToggleGhostMode,
                            modifier = Modifier.testTag("ghost_mode_toggle")
                        ) {
                            Icon(
                                imageVector = if (profile?.isGhostModeActive == true) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Ghost Mode Toggle",
                                tint = if (profile?.isGhostModeActive == true) Color(0xFFC084FC) else Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateChatDialog = true },
                    containerColor = Color(0xFF3B82F6),
                    contentColor = Color.White,
                    modifier = Modifier.testTag("create_chat_fab")
                ) {
                    Icon(imageVector = Icons.Default.AddComment, contentDescription = "New Secret Chat")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F1115))
                    .padding(innerPadding)
            ) {
                // STORIES ROW (Extracted from UI layout theme specs)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Story 1: Add Story (dashed border)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { /* add story action */ }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .border(1.5.dp, Color(0xFF475569), shape = CircleShape)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Story", tint = Color(0xFF64748B), modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Add Story", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                    }

                    // Story 2: AI Assistant
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .border(2.dp, Color(0xFF3B82F6), shape = CircleShape)
                                .padding(3.dp)
                                .background(Color(0xFF1E293B), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🤖", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Assistant", fontSize = 10.sp, color = Color(0xFFE2E8F0), fontWeight = FontWeight.Medium)
                    }

                    // Story 3: John D. (Opacity 60%)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.alpha(0.6f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .border(1.5.dp, Color(0xFF475569), shape = CircleShape)
                                .padding(3.dp)
                                .background(Color(0xFF4F46E5), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("JD", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("John D.", fontSize = 10.sp, color = Color(0xFFE2E8F0), fontWeight = FontWeight.Medium)
                    }
                }

                // Curved Messages Container (HTML: bg-[#15171D] rounded-t-[32px] border-t border-white/5)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(Color(0xFF15171D))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                ) {
                    // Header of chat list
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MESSAGES",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.5.sp
                        )

                        // Encrypted pill indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF22C55E).copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.2f), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF22C55E), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ENCRYPTED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF22C55E),
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Search Field
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Decrypt conversations / search...", color = Color(0xFF64748B)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF3B82F6)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .testTag("chat_search_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0F1115),
                            unfocusedContainerColor = Color(0xFF0F1115),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (filteredChats.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                if (searchQuery.isNotBlank()) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.5f)),
                                        border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f)),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.WifiTethering, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                "SECURE PEER ROUTING SEARCH",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF60A5FA),
                                                letterSpacing = 1.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                "No local peer matching '$searchQuery' was found. Tap below to establish a direct secure link via address routing index.",
                                                fontSize = 12.sp,
                                                color = Color.White.copy(alpha = 0.7f),
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Button(
                                                onClick = {
                                                    onCreateChat(searchQuery, 0, "👤")
                                                    searchQuery = ""
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.testTag("establish_secure_link")
                                            ) {
                                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Open Secure Chat with '@$searchQuery'", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.NoEncryption,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = Color.White.copy(alpha = 0.08f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "No active encryption nodes",
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(filteredChats) { chat ->
                                ChatItemRow(chat = chat, onClick = { onNavigateToChat(chat.id) })
                            }
                        }
                    }

                    // Premium Widget / Stars Progression Level Card (Gamification Level 14 in HTML specs)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF312E81).copy(alpha = 0.25f),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "GAMIFICATION LEVEL 14",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA5B4FC),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    "240 / 300 Stars",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.8f) // 80% progression status
                                        .background(Color(0xFF6366F1), CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Send 5 gifts to reach Master rank and unlock custom emojis.",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateChatDialog) {
        CreateSecretChatDialog(
            onDismiss = { showCreateChatDialog = false },
            onConfirm = { name, burnTime, emoji ->
                onCreateChat(name, burnTime, emoji)
                showCreateChatDialog = false
            }
        )
    }
}

@Composable
fun ChatItemRow(chat: Chat, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .testTag("chat_item_${chat.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Emoji avatar with glowing background & rounded-2xl styling
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (chat.isGhost) {
                        Color(0xFFCE93D8).copy(alpha = 0.2f)
                    } else {
                        Color(0xFF3B82F6).copy(alpha = 0.15f)
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (chat.isGhost) Color(0xFFC084FC).copy(alpha = 0.3f) else Color(0xFF3B82F6).copy(alpha = 0.3f),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(chat.avatarEmoji, fontSize = 24.sp)
            if (chat.isGhost) {
                // Ghost dot glow using purple-400
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Color(0xFFC084FC))
                        .border(1.5.dp, Color(0xFF15171D), CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Time formatting
                val timeStr = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(chat.lastMessageTime))
                Text(
                    text = timeStr,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (chat.isEncrypted) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Encrypted",
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFF3B82F6).copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = chat.lastMessage,
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    HorizontalDivider(modifier = Modifier.padding(start = 88.dp, end = 20.dp), color = Color.White.copy(alpha = 0.04f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveChatView(
    chat: Chat,
    messages: List<Message>,
    isDecrypting: Boolean,
    customBubbleRadius: Int,
    plugins: List<com.example.data.db.CustomPlugin> = emptyList(),
    onSavedToCloud: (String, String, String) -> Unit = { _, _, _ -> },
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onDeleteChat: () -> Unit
) {
    var typedText by remember { mutableStateOf("") }
    var showAttachmentPicker by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Automatically scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(chat.avatarEmoji, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(chat.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Https,
                                    contentDescription = null,
                                    modifier = Modifier.size(10.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    statusSubtext(chat),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onDeleteChat) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Purge Conversation Thread", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isDecrypting) {
                // DECRYPTION ANIMATION OVERLAY
                DecryptingCipherOverlay()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat Messages Scroll
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        
                        items(messages) { msg ->
                            MessageBubble(msg = msg, customBubbleRadius = customBubbleRadius, plugins = plugins, onSavedToCloud = onSavedToCloud)
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    // Input message row with stopwatch burn setting if default self-destruct is set
                    Surface(
                        tonalElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .navigationBarsPadding(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { showAttachmentPicker = true }, modifier = Modifier.testTag("attachment_picker_btn")) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "Attach file preset",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            TextField(
                                value = typedText,
                                onValueChange = { typedText = it },
                                placeholder = {
                                    Text(
                                        if (chat.selfDestructDefault > 0) "Secret message burns in ${chat.selfDestructDefault}s..." 
                                        else "Encrypted message..."
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_text_input"),
                                shape = RoundedCornerShape(24.dp),
                                trailingIcon = {
                                    if (chat.selfDestructDefault > 0) {
                                        Icon(
                                            imageVector = Icons.Default.LocalFireDepartment,
                                            contentDescription = "Burn Warning",
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            FilledIconButton(
                                onClick = {
                                    if (typedText.trim().isNotEmpty()) {
                                        onSendMessage(typedText)
                                        typedText = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("send_msg_button"),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Transmit cipher text",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun statusSubtext(chat: Chat): String {
    val b = java.lang.StringBuilder()
    b.append("E2E ACTIVE")
    if (chat.isGhost) b.append(" • Invisible")
    if (chat.selfDestructDefault > 0) b.append(" • Flame timer ${chat.selfDestructDefault}s")
    return b.toString()
}

@Composable
fun MessageBubble(
    msg: Message,
    customBubbleRadius: Int = 16,
    plugins: List<com.example.data.db.CustomPlugin> = emptyList(),
    onSavedToCloud: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val alignment = if (msg.isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (msg.isMyMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (msg.isMyMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(horizontalAlignment = if (msg.isMyMessage) Alignment.End else Alignment.Start) {
            Card(
                shape = RoundedCornerShape(
                    topStart = customBubbleRadius.dp,
                    topEnd = customBubbleRadius.dp,
                    bottomStart = if (msg.isMyMessage) customBubbleRadius.dp else 4.dp,
                    bottomEnd = if (msg.isMyMessage) 4.dp else customBubbleRadius.dp
                ),
                colors = CardDefaults.cardColors(containerColor = bubbleColor),
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .testTag("message_bubble_${msg.id}")
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    if (msg.text.startsWith("[FILE:") && msg.text.endsWith("]")) {
                        // Render gorgeous file attachment card!
                        val clean = msg.text.removePrefix("[FILE:").removeSuffix("]")
                        val parts = clean.split("|")
                        val fileName = parts.getOrNull(0)?.trim() ?: "file.bin"
                        val fileSize = parts.getOrNull(1)?.trim() ?: "unknown"
                        val fileType = parts.getOrNull(2)?.trim() ?: "application/octet-stream"
                        
                        FileAttachmentCard(
                            fileName = fileName,
                            fileSize = fileSize,
                            fileType = fileType,
                            isMyMessage = msg.isMyMessage,
                            onSaved = { onSavedToCloud(fileName, fileSize, fileType) }
                        )
                    } else {
                        Text(
                            text = msg.text,
                            fontSize = 15.sp,
                            color = textColor
                        )

                        // Active plug-in triggers
                        val translatorEnabled = plugins.find { it.type == "Translator" }?.isEnabled ?: false
                        val cipherHexEnabled = plugins.find { it.type == "CipherHex" }?.isEnabled ?: false
                        val spamFilterEnabled = plugins.find { it.type == "SpamFilter" }?.isEnabled ?: false

                        if (translatorEnabled) {
                            val translation = getMockTranslation(msg.text)
                            if (translation.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "🌐 Translated: $translation",
                                    fontSize = 12.sp,
                                    color = if (msg.isMyMessage) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (cipherHexEnabled) {
                            Spacer(modifier = Modifier.height(6.dp))
                            val mdStr = "🔐 HEX: 0x${Integer.toHexString(msg.text.hashCode())}F43D${if (msg.isMyMessage) "1A" else "9C"}"
                            Text(
                                text = mdStr,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (msg.isMyMessage) Color(0xFF22D3EE) else Color(0xFF0D9488)
                            )
                        }

                        if (spamFilterEnabled && (msg.text.length > 20 || msg.text.contains("http"))) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Red.copy(alpha = 0.12f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "⚠️ FILTER: Checked Secure",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Display burn seconds ticking clock if active self-destruct on
                        if (msg.selfDestructTimeLeft > 0) {
                            Icon(
                                imageVector = Icons.Default.HourglassEmpty,
                                contentDescription = "Burning message",
                                modifier = Modifier.size(11.dp),
                                tint = if (msg.isMyMessage) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${msg.selfDestructTimeLeft}s",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (msg.isMyMessage) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(msg.timestamp))
                        Text(
                            text = timeFormat,
                            fontSize = 10.sp,
                            color = if (msg.isMyMessage) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DecryptingCipherOverlay() {
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.VpnKey,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "DECRYPTING E2E CONVERSATION NODE...",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(8.dp))

            val fakeCipherString = remember(progress) {
                val chars = "A B C D E F 0 1 2 3 4 5 6 7 8 9 X Y Z @ # $"
                val length = 24
                (1..length).map { chars.random() }.joinToString("")
            }

            Text(
                text = "Key SHA-256: ...$fakeCipherString",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(200.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            )
        }
    }
}

@Composable
fun CreateSecretChatDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String) -> Unit
) {
    var companionName by remember { mutableStateOf("") }
    var selfDestructSecs by remember { mutableStateOf(0f) } // slider 0..60 (0 = off)
    var selectedEmoji by remember { mutableStateOf("🕵️‍♂️") }
    
    val emojiOptions = listOf("🕵️‍♂️", "🕵️‍♀️", "👻", "⚡", "🔮", "👽", "🦉", "🗝️", "🛡️", "🦾")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Synchronize Secure Tunnel") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = companionName,
                    onValueChange = { companionName = it },
                    label = { Text("Companion Moniker") },
                    placeholder = { Text("e.g. Agent Phoenix") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_companion_name_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Column {
                    Text(
                        text = "Message Self-Destruct Flame: " + if (selfDestructSecs.toInt() == 0) "DISABLED (Permanent)" else "${selfDestructSecs.toInt()} seconds",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (selfDestructSecs.toInt() > 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground
                    )
                    Slider(
                        value = selfDestructSecs,
                        onValueChange = { selfDestructSecs = it },
                        valueRange = 0f..60f,
                        steps = 11, // intervals of 5
                        modifier = Modifier.testTag("dialog_burn_slider")
                    )
                }

                Column {
                    Text("Designate Initial Emblem Avatar:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        emojiOptions.take(5).forEach { em ->
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedEmoji == em) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { selectedEmoji = em }
                                    .border(
                                        1.dp,
                                        if (selectedEmoji == em) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(
                                            alpha = 0.15f
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(em, fontSize = 20.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        emojiOptions.drop(5).forEach { em ->
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedEmoji == em) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { selectedEmoji = em }
                                    .border(
                                        1.dp,
                                        if (selectedEmoji == em) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(
                                            alpha = 0.15f
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(em, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (companionName.trim().isNotEmpty()) {
                        onConfirm(companionName.trim(), selfDestructSecs.toInt(), selectedEmoji)
                    }
                },
                modifier = Modifier.testTag("dialog_confirm_button")
            ) {
                Text("Open Secure Tunnel")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FileAttachmentCard(
    fileName: String,
    fileSize: String,
    fileType: String,
    isMyMessage: Boolean,
    onSaved: () -> Unit
) {
    var storedLocally by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMyMessage) Color.White.copy(alpha = 0.12f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, if (isMyMessage) Color.White.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isMyMessage) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when {
                            fileType.contains("pdf") -> Icons.Default.PictureAsPdf
                            fileType.contains("image") -> Icons.Default.Image
                            else -> Icons.Default.InsertDriveFile
                        },
                        contentDescription = null,
                        tint = if (isMyMessage) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fileName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isMyMessage) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$fileSize • ${fileType.uppercase()}",
                        fontSize = 10.sp,
                        color = if (isMyMessage) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Button(
                onClick = {
                    onSaved()
                    storedLocally = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (storedLocally) Color(0xFF22C55E) else MaterialTheme.colorScheme.primary,
                    contentColor = if (storedLocally) Color.White else MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = if (storedLocally) Icons.Default.CheckCircle else Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (storedLocally) "Saved to Secure Offline Vault" else "Save decrypted file payload",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun getMockTranslation(text: String): String {
    val t = text.trim().lowercase()
    return when {
         t.contains("meet") -> "Давай встретимся в обычном конспиративном доме."
         t.contains("channel") -> "Канал безопасности инициализирован."
         t.contains("mode") -> "Режим невидимки активирован?"
         t.contains("perfect") -> "Отлично. А невидимка включена?"
         t.contains("yes") -> "Да, индикация ввода отключена."
         t.contains("hi") || t.contains("hello") -> "Привет! Я умный ИИ Помощник Prime."
         t.contains("thank") -> "Спасибо за использование Primegramm Beta."
         t.contains("привет") -> "Hello! I am Gemini AI Node, companion helper."
         t.contains("как дела") -> "How are you? Local network operates at 100%."
         t.contains("спасибо") -> "Thank you for trusting Primegramm nodes."
         else -> ""
    }
}
