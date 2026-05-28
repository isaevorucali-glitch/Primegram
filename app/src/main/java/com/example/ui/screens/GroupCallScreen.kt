package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCallScreen(
    callState: String, // "idle", "dialing", "connected"
    callDuration: Int,
    isLowTrafficOn: Boolean,
    audioMuted: Boolean,
    videoMuted: Boolean,
    onStartCall: () -> Unit,
    onToggleLowTraffic: () -> Unit,
    onToggleAudio: () -> Unit,
    onToggleVideo: () -> Unit,
    onHangup: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("E2E VidComms Node", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF3B82F6).copy(alpha = 0.12f))
                                    .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "MESH",
                                    color = Color(0xFF60A5FA),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                        Text(
                            if (callState == "connected") "Secure Mesh Session Active" else "Offline • Channel Standby",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF64748B),
                            letterSpacing = 1.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F1115),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0F1115))
        ) {
            when (callState) {
                "idle" -> {
                    // STANDBY VIEW: DIAL PORTAL
                    VideoCallStandbyView(onStartCall = onStartCall)
                }
                "dialing" -> {
                    // DIALING VIEW
                    VideoCallDialingView(onHangup = onHangup)
                }
                "connected" -> {
                    // CONSTRUCT ACTIVE CALL VIEW WITH 4 BEAUTIFUL GRIDS
                    VideoCallActiveView(
                        duration = callDuration,
                        lowTraffic = isLowTrafficOn,
                        audioMuted = audioMuted,
                        videoMuted = videoMuted,
                        onToggleLowTraffic = onToggleLowTraffic,
                        onToggleAudio = onToggleAudio,
                        onToggleVideo = onToggleVideo,
                        onHangup = onHangup
                    )
                }
            }
        }
    }
}

@Composable
fun VideoCallStandbyView(onStartCall: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant pulsing satellite logo
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                val infiniteAnim = rememberInfiniteTransition()
                val scale by infiniteAnim.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.35f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f * scale))
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f * scale))
                )
                IconButton(
                    onClick = onStartCall,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Start call",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "COMMUNICATION NODE UNLINKED",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Initiate instant E2E multipeer encrypted calling. Highly optimized peer compression delivers sharp 1080p feeds scaling down to 0.2 KB/s audio-only frames on unstable dynamic mesh routing.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onStartCall,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("initiate_call_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Call, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Link Multi-Conference Room", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun VideoCallDialingView(onHangup: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val pulsingTranslation = rememberInfiniteTransition()
            val scalePulse by pulsingTranslation.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(130.dp)) {
                Box(
                    modifier = Modifier
                        .size(100.dp * scalePulse)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                )
                Icon(
                    imageVector = Icons.Default.OnlinePrediction,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "COMMUNICATION TUNNEL CONNECTING...",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Allocating encryption slots • Mapping peer proxy routes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            FloatingActionButton(
                onClick = onHangup,
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                modifier = Modifier.testTag("abort_call_fab")
            ) {
                Icon(imageVector = Icons.Default.CallEnd, contentDescription = "Hangup Connecting Call")
            }
        }
    }
}

@Composable
fun VideoCallActiveView(
    duration: Int,
    lowTraffic: Boolean,
    audioMuted: Boolean,
    videoMuted: Boolean,
    onToggleLowTraffic: () -> Unit,
    onToggleAudio: () -> Unit,
    onToggleVideo: () -> Unit,
    onHangup: () -> Unit
) {
    val durationStr = String.format("%02d:%02d", duration / 60, duration % 60)

    Column(modifier = Modifier.fillMaxSize()) {
        // TOP METRICS RIBBON (Bandwidth low-traffic indicators)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (lowTraffic) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Call Duration: $durationStr",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (lowTraffic) Icons.Default.Compress else Icons.Default.HighQuality,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lowTraffic) "0.2 KB/s (Compression ACTIVE) [78% Saved]" else "8.4 MB/s (Full Definition)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // GRID OF 4 SECURED CALL FEEDS
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                // Feeds 1: ME
                PeerLiveFeedBox(
                    label = "Me (Secured Peer)",
                    avatarCharacter = "👤",
                    feedActive = !videoMuted,
                    isMuted = audioMuted,
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                // Feed 2: Companion 1
                PeerLiveFeedBox(
                    label = "Alice (Agent Phoenix)",
                    avatarCharacter = "🕵️‍♀️",
                    feedActive = true,
                    isMuted = false,
                    accentColor = Color(0xFFCE93D8),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                // Feed 3: Companion 2
                PeerLiveFeedBox(
                    label = "Bob (Cyber Owl)",
                    avatarCharacter = "🦉",
                    feedActive = true,
                    isMuted = true,
                    accentColor = Color(0xFFA5D6A7),
                    modifier = Modifier.weight(1f)
                )
                // Feed 4: Companion 3
                PeerLiveFeedBox(
                    label = "Helix (Operator K)",
                    avatarCharacter = "🦾",
                    feedActive = false,
                    isMuted = false,
                    accentColor = Color(0xFFFFCC80),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // FOOTER CALL CONTROL ACTIONS UTILITY BAR
        Surface(
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Low traffic compressed switch toggle
                IconButton(
                    onClick = onToggleLowTraffic,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (lowTraffic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("low_traffic_call_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.NetworkWifi3Bar,
                        contentDescription = "Optimize dynamic calls bandwidth",
                        tint = if (lowTraffic) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Audio Mute toggle
                IconButton(
                    onClick = onToggleAudio,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (audioMuted) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("audio_mute_call_toggle")
                ) {
                    Icon(
                        imageVector = if (audioMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute mic",
                        tint = if (audioMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Video Feed Mute toggle
                IconButton(
                    onClick = onToggleVideo,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (videoMuted) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("video_mute_call_toggle")
                ) {
                    Icon(
                        imageVector = if (videoMuted) Icons.Default.VideocamOff else Icons.Default.Videocam,
                        contentDescription = "Mute stream",
                        tint = if (videoMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Call disconnect Hangup button
                IconButton(
                    onClick = onHangup,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                        .testTag("hangup_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "Terminate linked call",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
}

@Composable
fun PeerLiveFeedBox(
    label: String,
    avatarCharacter: String,
    feedActive: Boolean,
    isMuted: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (feedActive) {
                // Simulate Active cryptographic video frame
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.sweepGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.15f),
                                    accentColor.copy(alpha = 0.35f),
                                    accentColor.copy(alpha = 0.15f)
                                )
                            )
                        )
                ) {
                    // Running mock encryption matrix scan lines
                    val transition = rememberInfiniteTransition()
                    val offsetY by transition.animateFloat(
                        initialValue = -1f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2200, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.02f)
                            .align(Alignment.Center)
                            .offset(y = (offsetY * 80).dp)
                            .background(accentColor.copy(alpha = 0.45f))
                    )

                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(avatarCharacter, fontSize = 54.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "E2E Feed Secured",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }
            } else {
                // Static blank avatar camera off
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F0F0F)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(avatarCharacter, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "CAMERA DEACTIVATED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }
            }

            // Small mic label
            if (isMuted) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MicOff,
                        contentDescription = "Muted",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Identifier moniker card on bottom left
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomStart)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
