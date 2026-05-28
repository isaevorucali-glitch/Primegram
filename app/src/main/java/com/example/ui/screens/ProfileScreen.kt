package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.GiftExchange
import com.example.data.db.UserProfile
import com.example.ui.theme.PrimeThemes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: UserProfile?,
    giftsList: List<GiftExchange>,
    onSelectTheme: (Int) -> Unit,
    onPurchasePremium: () -> Unit,
    onSendGift: (String, String, Int, String) -> Unit,
    onChangeNotificationTone: (String) -> Unit,
    onUpdateCustomStyling: (String, String, String, String, Int) -> Unit
) {
    var showGiftDialog by remember { mutableStateOf(false) }
    var activePremiumTab by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val currentLevel = profile?.level ?: 1
    val currentXp = profile?.xp ?: 0
    val xpInLevel = currentXp % 100
    val progressToNextLevel = xpInLevel / 100f

    val soundOptions = listOf("Neon Chime", "Classic Synth", "Laser Blip", "Pulse Laser", "Quantum Chime")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // TOP PROFILE BANNER (Gamified Badge, Avatar and Level)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
                .padding(vertical = 32.dp, horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Anonymous Avatar Badge
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(
                            2.dp,
                            if (profile?.isPremium == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.2f
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (profile?.isPremium == true) "👑" else "🕵️",
                        fontSize = 54.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile?.username ?: "AnonPrime",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (profile?.isPremium == true) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "PRIME PLATINUM",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                Text(
                    text = "Node Address: peer_${profile?.id ?: 1}_zero_knowledge@mesh",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // LEVEL & XP METRICS PROGRESS BAR
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Safehouse Level $currentLevel", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Text("$xpInLevel / 100 XP to next", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progressToNextLevel },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            "Earn XP by posting messages, syncing cloud vaults, playing miners, and gifting coins.",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // MIDDLE WORKSPACE COCKPIT (Themes, gifts, stars buy)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // STARS & PREMIUM UPGRADE PANELS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // STARS WALLET CARD
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PRIME STARS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("${profile?.stars ?: 0}", fontWeight = FontWeight.Black, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showGiftDialog = true },
                            modifier = Modifier.fillMaxWidth().height(36.dp).testTag("purchase_gift_button"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Send Gift", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // PREMIUM ENTRANCE CARD
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (profile?.isPremium == true) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) 
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💎", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PREMIUM PERK", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.tertiary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            if (profile?.isPremium == true) "Active" else "Inactive",
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onPurchasePremium,
                            enabled = profile?.isPremium != true,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().height(36.dp).testTag("upgrade_premium_btn"),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(if (profile?.isPremium == true) "Subscribed" else "+500 Stars", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // SOUND TRIGGERS PANEL
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Custom Alert Tones", fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        soundOptions.take(3).forEach { sound ->
                            val active = profile?.chosenNotificationSound == sound
                            Box(
                                modifier = Modifier
                                    .weight(11f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .clickable { onChangeNotificationTone(sound) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    sound.substringBefore(" "),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        soundOptions.drop(3).forEach { sound ->
                            val active = profile?.chosenNotificationSound == sound
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .clickable { onChangeNotificationTone(sound) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    sound,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }

            // THEME CUSTOMIZER CONFIG (20+ themes visualization)
            Text(
                "PREMIUM SYSTEM THEMES (20+ OPTIONS)",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Swap entire network color mapping configurations locally in real time:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Compact thematic selector listing
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.height(240.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(PrimeThemes.list) { index, pal ->
                            val activeThemeSelection = profile?.selectedThemeIndex == index
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectTheme(index) }
                                    .testTag("theme_card_$index"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (activeThemeSelection) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) 
                                    else pal.surface
                                ),
                                border = if (activeThemeSelection) BoxButtonBorder() else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Custom visual dots
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(pal.primary))
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(pal.secondary))
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(pal.background))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = pal.name.substringBefore(" ("),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = if (pal.isDark) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // DYNAMIC INDIVIDUAL UI CUSTOMIZER OR ПЕРСОНАЛИЗАЦИЯ ИНТЕРФЕЙСА
            var speakRussian by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (speakRussian) "ИНДИВИДУАЛЬНЫЕ НАСТРОЙКИ СТИЛЯ" else "INDIVIDUAL STYLE CUSTOMIZER",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { speakRussian = !speakRussian }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (speakRussian) "In English 🇬🇧" else "На русском 🇷🇺",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().testTag("custom_element_style_picker"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                var selectedPColor by remember { mutableStateOf(profile?.customPrimaryColor ?: "") }
                var selectedFont by remember { mutableStateOf(profile?.customFontFamily ?: "Default") }
                var selectedRadius by remember { mutableStateOf(profile?.customBubbleRadius ?: 16) }

                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = if (speakRussian) "Переопределите цвета, шрифты и кривизну элементов мессенджера независимо от пресета:" 
                        else "Override primary palette, text typography faces, and structure layout nodes independently:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )

                    // COLOR CHIPS ROW
                    Column {
                        Text(
                            text = if (speakRussian) "ЦВЕТОВОЕ РЕШЕНИЕ (HEX)" else "ACCENT PALETTE (HEX)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        val hexPresets = listOf(
                            "" to "Preset",
                            "#3B82F6" to "Sapphire",
                            "#FF71CE" to "Sakura",
                            "#A3E635" to "Toxic",
                            "#BD93F9" to "Dracula",
                            "#F97316" to "Amber",
                            "#EF4444" to "Lava"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            hexPresets.take(4).forEach { (hexVal, tag) ->
                                val active = selectedPColor == hexVal
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .clickable { selectedPColor = hexVal }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(tag, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            hexPresets.drop(4).forEach { (hexVal, tag) ->
                                val active = selectedPColor == hexVal
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .clickable { selectedPColor = hexVal }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(tag, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = selectedPColor,
                            onValueChange = { selectedPColor = it },
                            label = { Text(if (speakRussian) "Собственный HEX код (#RRGGBB)" else "Custom HEX Value (#RRGGBB)") },
                            modifier = Modifier.fillMaxWidth().testTag("custom_hex_input"),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        )
                    }

                    // FONTS ROW
                    Column {
                        Text(
                            text = if (speakRussian) "ШРИФТОВАЯ ГАРНИТУРА" else "TYPOGRAPHY FONTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val fonts = listOf("Default", "Serif", "Monospace", "Cursive")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            fonts.forEach { f ->
                                val active = selectedFont == f
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .clickable { selectedFont = f }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = f,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }

                    // BUBBLE RADIUS ROW
                    Column {
                        Text(
                            text = if (speakRussian) "КРИВИЗНА СООБЩЕНИЙ (КРАЯ)" else "BUBBLE CORNER ROUNDNESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val radii = listOf(4 to "Sharp", 12 to "Elegant", 16 to "Bubble", 26 to "Circle")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            radii.forEach { (radiusVal, label) ->
                                val active = selectedRadius == radiusVal
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .clickable { selectedRadius = radiusVal }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (speakRussian) {
                                            when(label) {
                                                "Sharp" -> "Острые"
                                                "Elegant" -> "Изящные"
                                                "Bubble" -> "Облачко"
                                                else -> "Круглые"
                                            }
                                        } else label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }

                    // REALTIME SAMPLE BUBBLE PREVIEW
                    Column {
                        Text(
                            text = if (speakRussian) "ИНТЕРАКТИВНЫЙ ПРЕДПРОСМОТР" else "LIVE COMPONENT PREVIEW",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.15f))
                                .padding(12.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(
                                    topStart = selectedRadius.dp,
                                    topEnd = selectedRadius.dp,
                                    bottomStart = selectedRadius.dp,
                                    bottomEnd = 4.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedPColor.isNotBlank()) com.example.ui.theme.parseHexColor(selectedPColor, MaterialTheme.colorScheme.primary) else MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.align(Alignment.CenterEnd).widthIn(max = 240.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = if (speakRussian) "Привет! Это предпросмотр сообщения с твоими стилями." else "Hello! This is an interactive message bubble showing your new custom design.",
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("12:00 PM • Verified Encrypted", fontSize = 8.sp, color = Color.White.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }

                    // DEPLOY BUTTON
                    Button(
                        onClick = {
                            onUpdateCustomStyling(selectedPColor, "", "", selectedFont, selectedRadius)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth().testTag("apply_custom_styles_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (speakRussian) "Применить конфигурацию стилей" else "Apply Dynamic Theme Config",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // HISTORIC GIFTS RECEIVED/SENT AT BOTTOM
            Text(
                "HISTORIC GIFT COIN TRANSFERS",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall
            )

            if (giftsList.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f))
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No gift transfers verified across ledger.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    giftsList.forEach { gf ->
                        GiftHistoryRow(gift = gf)
                    }
                }
            }
        }
    }

    // PURCHASE COIN GIFTS TELEGRAM-STYLE SECURED DIALOG
    if (showGiftDialog) {
        SentGiftPurchaseDialog(
            activeStars = profile?.stars ?: 0,
            onDismiss = { showGiftDialog = false },
            onConfirm = { name, receiver, starsCost, msg ->
                onSendGift(name, receiver, starsCost, msg)
                showGiftDialog = false
            }
        )
    }
}

@Composable
fun GiftHistoryRow(gift: GiftExchange) {
    val dateText = java.text.SimpleDateFormat("dd MMM, HH:mm", java.util.Locale.getDefault()).format(java.util.Date(gift.timestamp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    when (gift.giftType) {
                        "Golden Cup" -> "🏆"
                        "Diamond Crown" -> "👑"
                        "Infinity Loop" -> "♾️"
                        else -> "⭐"
                    },
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("${gift.giftType} Transferred to ${gift.receiver}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(gift.message.ifEmpty { "Cipher attachment" }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(dateText, fontSize = 9.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⭐", fontSize = 11.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text("${gift.starCost}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun SentGiftPurchaseDialog(
    activeStars: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int, String) -> Unit
) {
    var recNode by remember { mutableStateOf("") }
    var giftTitle by remember { mutableStateOf("Star Badge") }
    var giftCost by remember { mutableStateOf(50) }
    var phraseMessage by remember { mutableStateOf("") }

    val collectionGifts = listOf(
        Triple("Star Badge", "⭐", 50),
        Triple("Infinity Loop", "♾️", 150),
        Triple("Diamond Crown", "👑", 350),
        Triple("Golden Cup", "🏆", 500)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transfer Telegram Star Gift") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Select active gift catalog item to deduct from Stars balance & transfer E2E to companion.", style = MaterialTheme.typography.bodySmall)

                // Render catalog row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    collectionGifts.forEach { item ->
                        val active = giftTitle == item.first
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { 
                                    giftTitle = item.first
                                    giftCost = item.third
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(item.second, fontSize = 20.sp)
                                Text(item.first.substringBefore(" "), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground)
                                Text("${item.third} ⭐", fontSize = 9.sp, color = if (active) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = recNode,
                    onValueChange = { recNode = it },
                    label = { Text("Deliver to companion username") },
                    placeholder = { Text("e.g. Alice") },
                    modifier = Modifier.fillMaxWidth().testTag("gift_receiver_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = phraseMessage,
                    onValueChange = { phraseMessage = it },
                    label = { Text("Attached Greeting Cipher Memo") },
                    placeholder = { Text("e.g. Keep up the amazing surveillance!") },
                    modifier = Modifier.fillMaxWidth().testTag("gift_msg_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Your Active Star Funds:", style = MaterialTheme.typography.bodySmall)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$activeStars / Cost: $giftCost", fontWeight = FontWeight.Black, color = if (activeStars >= giftCost) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (recNode.trim().isNotEmpty() && activeStars >= giftCost) {
                        onConfirm(giftTitle, recNode.trim(), giftCost, phraseMessage.trim())
                    }
                },
                enabled = activeStars >= giftCost && recNode.trim().isNotEmpty(),
                modifier = Modifier.testTag("dialog_gift_confirm_btn")
            ) {
                Text("Transmit Star Gift Ledger")
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
fun BoxButtonBorder(): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
}
