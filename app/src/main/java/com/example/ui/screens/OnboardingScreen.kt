package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun OnboardingScreen(
    currentStep: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val onboardingData = listOf(
        OnboardingPageData(
            title = "Zero-Knowledge Anonymity",
            description = "Welcome to Primegramm. No email, phone number, or identity required. Zero-logs routing ensures absolute user abstractness.",
            icon = Icons.Default.Security,
            colorAccent = Color(0xFF64B5F6)
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
            colorAccent = Color(0xFF26A69A)
        ),
        OnboardingPageData(
            title = "API Automation & Gifts",
            description = "Trigger remote API webhooks via secure chat bots, send digital star badges, play gamified apps to earn stars, and personalize 20+ design themes.",
            icon = Icons.Default.Webhook,
            colorAccent = Color(0xFFBB86FC)
        )
    )

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
                Text(
                    text = "PRIMEGRAMM",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.testTag("skip_onboarding_button")
                ) {
                    Text("Skip", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }

            // Central Animated Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 32.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Pulsating icon aura
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(120.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(activeData.colorAccent.copy(alpha = 0.15f))
                        )
                        Icon(
                            imageVector = activeData.icon,
                            contentDescription = null,
                            tint = activeData.colorAccent,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = activeData.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = activeData.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
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
                    modifier = Modifier.padding(bottom = 24.dp)
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
                        .height(56.dp)
                        .testTag("next_onboarding_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentStep == 3) "Enter Secure Space" else "Continue Info",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
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
