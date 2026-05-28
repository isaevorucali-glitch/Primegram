package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onRegister: (String, String) -> Boolean
) {
    var rawUsername by remember { mutableStateOf("") }
    var chosenAvatar by remember { mutableStateOf("🕵️‍♂️") }
    var languageRussian by remember { mutableStateOf(false) }
    var showScanOverlay by remember { mutableStateOf(false) }
    var scanStateText by remember { mutableStateOf("") }
    var currentProgress by remember { mutableStateOf(0f) }

    val scope = rememberCoroutineScope()

    val avatars = listOf("🕵️‍♂️", "👾", "👤", "🚀", "🎭", "🛡️", "💫", "🌋", "🎯", "🤖")

    // Dynamic nickname check state
    val searchState = remember(rawUsername) {
        if (rawUsername.trim().length < 3) {
            if (languageRussian) "Минимум 3 символа" else "Min 3 characters"
        } else {
            val lower = rawUsername.trim().lowercase()
            val taken = listOf("alice", "gemini", "support", "ghostoperative", "admin", "primegramm")
            if (taken.any { lower.contains(it) }) {
                if (languageRussian) "❌ Место связи занято другим узлом!" else "❌ Peer conflict: Handle already leased!"
            } else {
                if (languageRussian) "✅ Узел свободен для подключения" else "✅ Secure entry path clear (no conflicts)"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF020617)
                    )
                )
            )
            .padding(24.dp)
            .navigationBarsPadding()
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 500.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Toggle Language
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF22D3EE).copy(alpha = 0.1f))
                        .border(1.dp, Color(0xFF22D3EE).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { languageRussian = !languageRussian }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (languageRussian) "ENG 🇬🇧" else "РУС 🇷🇺",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF22D3EE)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Brand Vector Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (languageRussian) "РЕГИСТРАЦИЯ УЗЛА" else "PEER REGISTER GATE",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 2.sp,
                    color = Color.White
                )
                Text(
                    text = if (languageRussian) {
                        "Выберите уникальное имя в анонимной системе Primegramm. Наш протокол полностью автономен."
                    } else {
                        "Secure your unique handle signature in the decentralized routing system. 100% data client-owned."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.4f)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (languageRussian) "ЛИЧНАЯ ИДЕНТИФИКАЦИЯ (АВАТАР):" else "SET CUSTOM PEER AVATAR:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Selected avatar preview
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3B82F6).copy(alpha = 0.15f))
                            .border(2.dp, Color(0xFF3B82F6), CircleShape)
                    ) {
                        Text(chosenAvatar, fontSize = 44.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // List of gorgeous preset avatars
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        avatars.take(5).forEach { av ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (chosenAvatar == av) Color(0xFF3B82F6).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.04f))
                                    .border(1.dp, if (chosenAvatar == av) Color(0xFF3B82F6) else Color.Transparent, CircleShape)
                                    .clickable { chosenAvatar = av }
                            ) {
                                Text(av, fontSize = 20.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        avatars.drop(5).forEach { av ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (chosenAvatar == av) Color(0xFF3B82F6).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.04f))
                                    .border(1.dp, if (chosenAvatar == av) Color(0xFF3B82F6) else Color.Transparent, CircleShape)
                                    .clickable { chosenAvatar = av }
                            ) {
                                Text(av, fontSize = 20.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nickname Input and Validation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.4f)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (languageRussian) "КОНФИДЕНЦИАЛЬНЫЙ СИНОНИМ (ID):" else "SECURE ADDRESS ROUTING (ID):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = rawUsername,
                        onValueChange = { rawUsername = it },
                        placeholder = { Text(if (languageRussian) "Например: Cypher_Ghost" else "Example: Cyber_Operative") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_username_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.3f),
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Checking status indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = null,
                            tint = if (searchState.contains("❌")) Color.Red else Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = searchState,
                            fontSize = 11.sp,
                            color = if (searchState.contains("❌")) Color.Red else Color(0xFF22C55E),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fingerprint scanner trigger button
            Button(
                onClick = {
                    if (rawUsername.isNotBlank() && !searchState.contains("❌")) {
                        // Launch full biometric sequence simulation before writing profile!
                        showScanOverlay = true
                        currentProgress = 0f
                        scanStateText = if (languageRussian) "Инициализация подписи..." else "Initializing cryptographic handshake..."
                        scope.launch {
                            delay(600)
                            scanStateText = if (languageRussian) "Генерация локального PGP пары..." else "Generating localized master key pairs..."
                            currentProgress = 0.35f
                            delay(700)
                            scanStateText = if (languageRussian) "Сканирование биометрии безопасности..." else "Scanning zero-knowledge fingerprint seal..."
                            currentProgress = 0.7f
                            delay(800)
                            scanStateText = if (languageRussian) "Узел интегрирован в Primegramm!" else "Secure Node Sync Successful!"
                            currentProgress = 1.0f
                            delay(500)
                            showScanOverlay = false
                            onRegister(rawUsername, chosenAvatar)
                        }
                    }
                },
                enabled = rawUsername.isNotBlank() && !searchState.contains("❌") && rawUsername.trim().length >= 3,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White,
                    disabledContainerColor = Color.White.copy(alpha = 0.05f),
                    disabledContentColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_registration_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (languageRussian) "Завершить Регистрацию" else "Instantiate Secure Link",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // Full Hologram Scan Overlay Dialog
    if (showScanOverlay) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                // Pulsing spinning lock icon
                val infiniteTransition = rememberInfiniteTransition()
                val angle by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(2.dp, Brush.sweepGradient(listOf(Color(0xFF3B82F6), Color(0xFF10B981))), CircleShape)
                            .rotate(angle)
                    )
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (languageRussian) "КРИПТОГРАФИЧЕСКАЯ ИНТЕГРАЦИЯ УЗЛА" else "Z-K CRYPTO PEER CORRELATION",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = currentProgress,
                    color = Color(0xFF3B82F6),
                    trackColor = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier
                        .width(220.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = scanStateText,
                    fontSize = 12.sp,
                    color = Color(0xFF22C55E),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
