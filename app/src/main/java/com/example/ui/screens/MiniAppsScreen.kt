package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.BotMiniApp
import com.example.data.db.CustomPlugin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniAppsScreen(
    botsList: List<BotMiniApp>,
    ticTacToeBoard: List<String>,
    ticTacToeWinner: String,
    webhookLogs: List<String>,
    plugins: List<CustomPlugin> = emptyList(),
    onTogglePlugin: (Long, Boolean) -> Unit = { _, _ -> },
    onInstallPlugin: (String, String, String) -> Unit = { _, _, _ -> },
    onUninstallPlugin: (Long) -> Unit = { _ -> },
    onPlayTicTacToeMove: (Int) -> Unit,
    onResetTicTacToe: () -> Unit,
    onExecuteWebhook: (String, String, String) -> Unit,
    onRegisterCustomBot: (String, String, String, String, String) -> Unit
) {
    var activeMiniAppId by remember { mutableStateOf<String?>(null) }
    var showCreateBotDialog by remember { mutableStateOf(false) }

    if (activeMiniAppId != null) {
        // ENTERS SPECIFIC ACTIVE SANDBOX MINI-APP MODE
        when (activeMiniAppId) {
            "tictactoe" -> {
                TicTacToeSandboxApp(
                    board = ticTacToeBoard,
                    winner = ticTacToeWinner,
                    onMove = onPlayTicTacToeMove,
                    onReset = onResetTicTacToe,
                    onBack = { activeMiniAppId = null }
                )
            }
            "webhook_tester" -> {
                WebhookConnectorApp(
                    logs = webhookLogs,
                    onTrigger = { botId, url, payload -> onExecuteWebhook(botId, url, payload) },
                    onBack = { activeMiniAppId = null }
                )
            }
            else -> {
                // Renders detail dialog for selected generic bots
                val selectedBot = botsList.find { it.id == activeMiniAppId }
                if (selectedBot != null) {
                    GenericBotSandboxView(
                        bot = selectedBot,
                        onBack = { activeMiniAppId = null },
                        onExecuteApiTrigger = { payload ->
                            onExecuteWebhook(selectedBot.id, selectedBot.integrationUrl.ifEmpty { "https://api.primegramm.net/webhook" }, payload)
                        },
                        logs = webhookLogs
                    )
                } else {
                    activeMiniAppId = null
                }
            }
        }
    } else {
        // RENDERS PRIMARY DIRECTORY ECOSYSTEM
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "Ecosystem Nodes",
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
                                        "API-ON",
                                        color = Color(0xFF60A5FA),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                            Text(
                                "Mini-Apps & Custom API Automated Bots",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF64748B),
                                letterSpacing = 1.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    actions = {
                        IconButton(onClick = { showCreateBotDialog = true }) {
                            Icon(Icons.Default.PostAdd, contentDescription = "Add Custom Bot API Hook", tint = Color(0xFF3B82F6))
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                // Curved Messages Container (HTML: bg-[#15171D] rounded-t-[32px] border-t border-white/5)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF3B82F6).copy(alpha = 0.08f)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Casino,
                                        contentDescription = null,
                                        tint = Color(0xFF3B82F6),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text("Ecosystem Gamification", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                                        Text("Play Crypto TicTacToe to earn Prime Stars, and trigger remote API integrations via bots.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "ACTIVE ENDPOINTS DIRECTORY",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.sp
                            )
                        }

                        items(botsList) { app ->
                            EcosystemEcosystemItem(
                                app = app,
                                onClick = { activeMiniAppId = app.id }
                            )
                        }

                        item {
                            DeveloperApiHandbookCard()
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "DECENTRALIZED PLUGINS & CIS CORRELATION",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF22D3EE),
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.sp
                            )
                        }

                        if (plugins.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No secure plugins installed on this peer terminal.", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                }
                            }
                        } else {
                            items(plugins) { plugin ->
                                PluginItemRow(
                                    plugin = plugin,
                                    onToggle = { onTogglePlugin(plugin.id, it) },
                                    onUninstall = { onUninstallPlugin(plugin.id) }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            // Button to install new custom secure plugin
                            Button(
                                onClick = {
                                    onInstallPlugin(
                                        "Secure Anti-Whale Auto Tracker", 
                                        "Spam flags transactions above local limits automatically on peer nodes.",
                                        "CustomAPI"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().testTag("install_new_plugin_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22D3EE).copy(alpha = 0.11f), contentColor = Color(0xFF22D3EE)),
                                border = BorderStroke(1.dp, Color(0xFF22D3EE).copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Extension, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Compile & Install Custom Dev Plugin (+15 XP)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateBotDialog) {
        CreateCustomBotDialog(
            onDismiss = { showCreateBotDialog = false },
            onConfirm = { name, category, emoji, desc, callbackUrl ->
                onRegisterCustomBot(name, category, desc, emoji, callbackUrl)
                showCreateBotDialog = false
            }
        )
    }
}

@Composable
fun EcosystemEcosystemItem(
    app: BotMiniApp,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("bot_item_row_${app.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (app.category == "MiniApp") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(app.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.name,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (app.category == "MiniApp") MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = app.category.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (app.category == "MiniApp") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = app.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.61f)
                )
            }

            Icon(Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
        }
    }
}

// 1. TICTACTOE MINI APP VIEW
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicTacToeSandboxApp(
    board: List<String>,
    winner: String,
    onMove: (Int) -> Unit,
    onReset: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Return")
                    }
                },
                title = { Text("Tic-Tac-Toe Prime Miner", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onReset) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "Reset Board")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "MINING BOOSTER ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Beat the AI Node to mine +15 Stars instantly! (Draw awards +3 Stars)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // GRID BOARD 3x3
            Card(
                modifier = Modifier
                    .size(280.dp)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(9) { idx ->
                            val s = board[idx]
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { onMove(idx) }
                                    .border(
                                        1.dp,
                                        if (s == "X") MaterialTheme.colorScheme.primary 
                                        else if (s == "O") MaterialTheme.colorScheme.tertiary 
                                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .testTag("ttt_cell_$idx"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = s,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (s == "X") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }
            }

            // RESULTS ANNOUNCEMENT BLOCK
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (winner.isNotEmpty()) {
                    Text(
                        text = when (winner) {
                            "Me" -> "VICTORY! YOU MINED 15 STARS!"
                            "Bot" -> "AI DEFEATED PEER. TRY AGAIN."
                            else -> "DRAW NODE: SYNCHRONIZED 3 STARS."
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = if (winner == "Me") MaterialTheme.colorScheme.primary else if (winner == "Draw") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onReset,
                        modifier = Modifier.testTag("ttt_play_again_button")
                    ) {
                        Text("Reset Encryption Core Grid")
                    }
                } else {
                    Text(
                        text = "Your Turn: Grid Token represents [ X ].",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// 2. APIS WEBHOOK TESTER VIEW
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebhookConnectorApp(
    logs: List<String>,
    onTrigger: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var specUrl by remember { mutableStateOf("https://n8n.primegramm.net/webhook/trigger") }
    var paramPayload by remember { mutableStateOf("{\n  \"peer_id\": \"92xfv\",\n  \"event\": \"secure_handshake\",\n  \"auth\": \"zero-know\"\n}") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                title = { Text("Webhook API Connector", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Map outgoing events to external execution endpoints (e.g. n8n, Make, Custom REST Servers) to automate workflows dynamically on message triggers.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            OutlinedTextField(
                value = specUrl,
                onValueChange = { specUrl = it },
                label = { Text("Remote Webhook POST URL") },
                modifier = Modifier.fillMaxWidth().testTag("webhook_url_input"),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = paramPayload,
                onValueChange = { paramPayload = it },
                label = { Text("JSON Parameter Content Structure") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .testTag("webhook_payload_input"),
                placeholder = { Text("Enter payload body...") },
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = { onTrigger("webhook_tester", specUrl, paramPayload) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("execute_webhook_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.SendToMobile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Transmit Webhook Event Protocol", fontWeight = FontWeight.Bold)
            }

            Text(
                text = "EXECUTION DEPLOYMENT TERMINAL LOG:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0C0C)),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Box(modifier = Modifier.padding(12.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                    Column {
                        if (logs.isEmpty()) {
                            Text(
                                "Terminal idle. Standard handshake ready.",
                                color = Color(0xFF00FF00),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        } else {
                            logs.forEach { log ->
                                Text(
                                    log,
                                    color = Color(0xFF00FF00),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3. GENERIC BOTS SANDBOX CONSOLE (Trigger API Webhook Event Form)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericBotSandboxView(
    bot: BotMiniApp,
    onBack: () -> Unit,
    onExecuteApiTrigger: (String) -> Unit,
    logs: List<String>
) {
    var rawParams by remember { mutableStateOf("{\n  \"action\": \"trigger_workflow\",\n  \"bot_api_key\": \"sec_839fd02a11b8\"\n}") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                title = { Text("Bot API: ${bot.name}", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(bot.iconEmoji, fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(bot.name, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("API Interface Class: Zero-Trust Node Sync", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                }
            }

            Text(
                text = bot.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            HorizontalDivider()

            Text("TRIGGER AUTOMATED API POST ACTION", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = rawParams,
                onValueChange = { rawParams = it },
                label = { Text("Workflow JSON params") },
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                modifier = Modifier.fillMaxWidth().height(100.dp).testTag("generic_bot_json_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = { onExecuteApiTrigger(rawParams) },
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("generic_bot_deploy_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Transmit Automated API Call", fontWeight = FontWeight.Bold)
            }

            Text("SANDBOX LOGS", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C0C0C))
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    Column {
                        if (logs.isEmpty()) {
                            Text("Ready.", color = Color(0xFF00FF00), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        } else {
                            logs.takeLast(4).forEach { item ->
                                Text(item, color = Color(0xFF00FF00), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 4. ADD NEW SECURE BOT OR MINI-APP DIALOG (REGISTER API ENDPOINT)
@Composable
fun CreateCustomBotDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var bName by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf("Bot") }
    var emojiAvatar by remember { mutableStateOf("🤖") }
    var customDescription by remember { mutableStateOf("") }
    var targetUrl by remember { mutableStateOf("") }

    val emojis = listOf("🤖", "🪐", "💎", "💻", "🔥", "⚙️", "📈", "🧩")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Custom Bot Endpoint") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = bName,
                    onValueChange = { bName = it },
                    label = { Text("Bot/App Name") },
                    modifier = Modifier.fillMaxWidth().testTag("add_bot_name"),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedCat == "Bot") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedCat = "Bot" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("BOT NODE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (selectedCat == "Bot") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedCat == "MiniApp") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedCat = "MiniApp" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("MINI-APP CLASS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (selectedCat == "MiniApp") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground)
                    }
                }

                OutlinedTextField(
                    value = customDescription,
                    onValueChange = { customDescription = it },
                    label = { Text("Functional Description") },
                    placeholder = { Text("What automation should this bot represent?") },
                    modifier = Modifier.fillMaxWidth().testTag("add_bot_desc"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = targetUrl,
                    onValueChange = { targetUrl = it },
                    label = { Text("API Sync Webhook URL (JSON endpoint)") },
                    placeholder = { Text("https://your-domain.com/webhook") },
                    modifier = Modifier.fillMaxWidth().testTag("add_bot_url"),
                    shape = RoundedCornerShape(12.dp)
                )

                Column {
                    Text("Designate Glyph Emblem:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        emojis.forEach { em ->
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (emojiAvatar == em) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { emojiAvatar = em }
                                    .border(
                                        1.dp,
                                        if (emojiAvatar == em) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(
                                            alpha = 0.15f
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(em, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (bName.trim().isNotEmpty() && customDescription.trim().isNotEmpty()) {
                        onConfirm(bName.trim(), selectedCat, emojiAvatar, customDescription.trim(), targetUrl.trim())
                    }
                },
                modifier = Modifier.testTag("dialog_add_bot_confirm_btn")
            ) {
                Text("Verify & Deploy hook")
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
fun DeveloperApiHandbookCard() {
    var readInRussian by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .testTag("dev_api_handbook_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B).copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (readInRussian) "СПРАВОЧНИК РАЗРАБОТЧИКА API" else "DEVELOPER API SPECIFICATION",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = Color(0xFF60A5FA),
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF3B82F6).copy(alpha = 0.15f))
                        .clickable { readInRussian = !readInRussian }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (readInRussian) "ENG 🇬🇧" else "РУС 🇷🇺",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF60A5FA)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (readInRussian) {
                    "PrimegrammBot API позволяет автоматизировать отправку сообщений, обрабатывать события в реальном времени посредством вебхуков и отвечать пользователям в защищенном канале."
                } else {
                    "The Primegramm Bot API lets you safely automate interactions, process realtime E2E events via webhook integrations, and return custom automated packets."
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // METHOD 1: RECEIVE EVENT
            Text(
                text = if (readInRussian) "1. ФОРМАТ ЗАПРОСА ВЕБХУКА (POST)" else "1. INCOMING WEBHOOK PAYLOAD (POST)",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = """// Outgoing from Primegramm
{
  "event": "messages.received",
  "peer_id": "com.prime.9a0b",
  "text_raw": "status --check",
  "is_encrypted": true
}""",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color(0xFF22C55E),
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // METHOD 2: RESPONSE RESPONSE
            Text(
                text = if (readInRussian) "2. ОТВЕТ ВАШЕГО СЕРВЕРА (JSON)" else "2. WORKFLOW AUTOMATION REPLY (JSON)",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = """// Return to Primegramm
{
  "action": "reply",
  "reply_text": "✅ Remote action verified.",
  "send_coins": 5
}""",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // PIPELINE INTEGRATION GUIDE
            Text(
                text = if (readInRussian) "ПОРЯДОК ПОДКЛЮЧЕНИЯ НОВОГО БОТА:" else "BOT ECOSYSTEM SETUP GUIDE:",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (readInRussian) {
                    "• Нажмите '+' сверху экрана.\n" +
                    "• Задайте имя бота, описание и POST-адрес API.\n" +
                    "• Любые новые сообщения в чатах отправят триггер на этот URL.\n" +
                    "• Бот автоматически выполнит ответ и добавит запись в лог терминала."
                } else {
                    "• Tap the '+' icon in the top right corner.\n" +
                    "• Register bot name, token emoji, and callback endpoint.\n" +
                    "• Outgoing conversation logs will map events directly code-side.\n" +
                    "• The response automates replies and updates the sandbox log."
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun PluginItemRow(
    plugin: com.example.data.db.CustomPlugin,
    onToggle: (Boolean) -> Unit,
    onUninstall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (plugin.isEnabled) Color(0xFF22D3EE).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f)),
        colors = CardDefaults.cardColors(
            containerColor = if (plugin.isEnabled) Color(0xFF0F2D37).copy(alpha = 0.5f) else Color(0xFF1E293B).copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (plugin.isEnabled) Color(0xFF22D3EE).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (plugin.isEnabled) Icons.Default.SettingsInputAntenna else Icons.Default.Block,
                        contentDescription = null,
                        tint = if (plugin.isEnabled) Color(0xFF22D3EE) else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plugin.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = "TYPE: ${plugin.type.uppercase()}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF22D3EE)
                    )
                }

                Switch(
                    checked = plugin.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF22D3EE),
                        checkedTrackColor = Color(0xFF22D3EE).copy(alpha = 0.5f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = plugin.description,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.61f)
            )

            if (plugin.type == "CustomAPI") {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onUninstall,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red),
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Decompile Plugin Block", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
