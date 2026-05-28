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
    onChangeNotificationTone: (String) -> Unit
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
            .background(Color(0xFF0F1115))
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
