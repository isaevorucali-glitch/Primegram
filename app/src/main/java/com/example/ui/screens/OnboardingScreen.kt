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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    currentStep: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    var languageModeRussian by remember { mutableStateOf(true) }

    val onboardingData = if (languageModeRussian) {
        listOf(
            OnboardingPageData(
                title = "Нулевое разглашение",
                description = "Добро пожаловать в Primegramm. Полностью анонимная регистрация — без телефона, e-mail и логов. Абсолютная приватность и сетевой призрак-режим.",
                icon = Icons.Default.Security,
                colorAccent = Color(0xFF3B82F6)
            ),
            OnboardingPageData(
                title = "Самоуничтожение сообщений",
                description = "Параллельные туннели со сквозным шифрованием поддерживают авто-сгорающие сообщения. Текст превратится в пепел спустя секунды после прочтения.",
                icon = Icons.Default.LocalFireDepartment,
                colorAccent = Color(0xFFFF7043)
            ),
            OnboardingPageData(
                title = "Шифрованное Облако",
                description = "Защищенное файловое хранилище с клиенским хешированием локального пароля. Храните документы и медиафайлы отдельно от диска вашего устройства.",
                icon = Icons.Default.CloudQueue,
                colorAccent = Color(0xFF10B981)
            ),
            OnboardingPageData(
                title = "API Автоматизация и Боты",
                description = "Интегрируйте ботов, настраивайте внешние вебхуки с другими сервисами, обменивайтесь звездными коинами и устанавливайте любую из 20+ визуальных тем.",
                icon = Icons.Default.Webhook,
                colorAccent = Color(0xFF8B5CF6)
            )
        )
    } else {
        listOf(
            OnboardingPageData(
                title = "Zero-Knowledge Anonymity",
                description = "Welcome to Primegramm. No email, phone number, or identity required. Zero-logs routing ensures absolute user abstractness.",
                icon = Icons.Default.Security,
                colorAccent = Color(0xFF3B82F6)
            ),
            OnboardingPageData(
                title = "Self-Destruct Messages",
                description = "E2E encrypted secure tunnels support self-burning message bubbles. Decrypted texts literally turn to ash seconds after standard reading.",
                icon = Icons.Default.LocalFireDepartment,
                colorAccent = Color(0xFFFF7043)
            ),
            OnboardingPageData(
                title = "Shielded Cloud Storage",
                description = "A private document vault with client-side password hashing. Store passwords, contracts, or media off your device physical drive securely.",
                icon = Icons.Default.CloudQueue,
                colorAccent = Color(0xFF10B981)
            ),
            OnboardingPageData(
                title = "API Automation & Gifts",
                description = "Trigger remote API webhooks via secure chat bots, send digital star badges, play gamified apps to earn stars, and personalize 20+ design themes.",
                icon = Icons.Default.Webhook,
                colorAccent = Color(0xFF8B5CF6)
            )
        )
    }

    val activeData = onboardingData[currentStep]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
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
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Russian toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { languageModeRussian = !languageModeRussian }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (languageModeRussian) "ENG 🇬🇧" else "РУС 🇷🇺",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.testTag("skip_onboarding_button")
                ) {
                    Text(
                        text = if (languageModeRussian) "Пропустить" else "Skip",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // Central Animated Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Pulsating icon aura
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(90.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(activeData.colorAccent.copy(alpha = 0.15f))
                        )
                        Icon(
                            imageVector = activeData.icon,
                            contentDescription = null,
                            tint = activeData.colorAccent,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = activeData.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = activeData.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // INTERACTIVE MODULES SPECIFIC TO STEP
                    when (currentStep) {
                        0 -> {
                            // Anon Key Generator Sandbox
                            var generatedKey by remember { mutableStateOf("") }
                            var isGenerating by remember { mutableStateOf(false) }
                            val scope = rememberCoroutineScope()

                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                if (generatedKey.isNotEmpty()) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Text(
                                            text = generatedKey,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = Color(0xFF00FF00),
                                            modifier = Modifier.padding(10.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        if (!isGenerating) {
                                            isGenerating = true
                                            generatedKey = if (languageModeRussian) "Инициализация энтропии..." else "Initializing PGP entropy..."
                                            scope.launch {
                                                delay(400)
                                                generatedKey = if (languageModeRussian) "Защита сокета..." else "Securing socket node..."
                                                delay(400)
                                                val hex = (1..6).joinToString("") { ('a'..'f').random().toString() + (0..9).random().toString() }
                                                generatedKey = "PGP-ANON-$hex-RSA-4096"
                                                isGenerating = false
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = activeData.colorAccent),
                                    modifier = Modifier.testTag("onboard_gen_anon_key")
                                ) {
                                    Text(
                                        text = if (isGenerating) {
                                            if (languageModeRussian) "Сжатие ключа..." else "Computing Entropy..."
                                        } else {
                                            if (languageModeRussian) "Сгенерировать анонимный PGP токен" else "Compute Anon Session Token"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        1 -> {
                            // Burning messages Sandbox
                            var customText by remember { mutableStateOf("") }
                            var burnTimer by remember { mutableStateOf(-1) }
                            val scope = rememberCoroutineScope()

                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                if (burnTimer >= 0) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (burnTimer > 0) activeData.colorAccent.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Text(
                                            text = if (burnTimer > 0) {
                                                "🔥 [ $customText ] (self-destructs in ${burnTimer}s)"
                                            } else {
                                                if (languageModeRussian) "💨 [ СООБЩЕНИЕ ПОЛНОСТЬЮ СГОРЕЛО ] (0 логов в базе)" else "💨 [ MESSAGE REDUCED TO ASHES ] (Zero logs remain)"
                                            },
                                            fontSize = 11.sp,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            color = if (burnTimer > 0) Color.White else Color.Red,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }

                                if (burnTimer <= 0) {
                                    OutlinedTextField(
                                        value = customText,
                                        onValueChange = { customText = it },
                                        placeholder = { Text(if (languageModeRussian) "Введите секретный текст..." else "Enter secret text...", fontSize = 11.sp) },
                                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("onboard_burn_input"),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            if (customText.isNotBlank()) {
                                                burnTimer = 5
                                                scope.launch {
                                                    while (burnTimer > 0) {
                                                        delay(1000)
                                                        burnTimer -= 1
                                                    }
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = activeData.colorAccent),
                                        modifier = Modifier.testTag("onboard_launch_burn")
                                    ) {
                                        Text(
                                            text = if (languageModeRussian) "Отправить как сгорающий пакет" else "Send As Self-Burning Packet",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                        2 -> {
                            // Hash Verification Password Sandbox
                            var typedPwd by remember { mutableStateOf("") }
                            val shaMock = remember(typedPwd, languageModeRussian) {
                                if (typedPwd.isBlank()) {
                                    if (languageModeRussian) "Введите ключ для локального шифрования..." else "Type key for zero-knowledge hash..."
                                } else {
                                    val hashed = typedPwd.hashCode().toString(16).padEnd(20, 'a')
                                    "SHA-256 (Local): $hashed"
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = typedPwd,
                                    onValueChange = { typedPwd = it },
                                    label = { Text(if (languageModeRussian) "Ввод пароля (Хэш локально)" else "Local Password Salt Hash", fontSize = 11.sp) },
                                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("onboard_hash_input"),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = shaMock,
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = Color(0xFF10B981),
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                        3 -> {
                            // Live Accent theme coloring selector
                            var onboardAccent by remember { mutableStateOf(Color(0xFF8B5CF6)) }
                            val colors = listOf(Color(0xFF8B5CF6), Color(0xFF10B981), Color(0xFFFF7043), Color(0xFF3B82F6), Color(0xFFEF4444))

                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = if (languageModeRussian) "Выберите визуальный акцент прямо сейчас:" else "Choose visual accent dynamically:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    colors.forEach { col ->
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(col)
                                                .clickable { onboardAccent = col }
                                                .border(2.dp, if (onboardAccent == col) Color.White else Color.Transparent, CircleShape)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = onboardAccent.copy(alpha = 0.15f)),
                                    border = BorderStroke(1.dp, onboardAccent.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (languageModeRussian) "Тема применяется к чатам, облаку и консоли разработчиков в реальном времени." else "Dynamic accents bind theme pallets and automation sockets in real-time.",
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        color = Color.White,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Actions & Dynamic Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Indicators Row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    for (i in 0 until 4) {
                        val active = i == currentStep
                        val width by animateDpAsState(
                            targetValue = if (active) 24.dp else 8.dp,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(
                                    if (active) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                                )
                        )
                    }
                }

                // Next Button
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("next_onboarding_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentStep == 3) {
                                if (languageModeRussian) "Начать общение анонимно" else "Enter Secure Space"
                            } else {
                                if (languageModeRussian) "Далее" else "Continue Info"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val colorAccent: Color
)
