package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MessengerViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

  private val viewModel: MessengerViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val profileState by viewModel.profile.collectAsStateWithLifecycle()
      val chatsList by viewModel.chats.collectAsStateWithLifecycle()
      val activeChatState by viewModel.activeChat.collectAsStateWithLifecycle()
      val activeMessagesList by viewModel.activeMessages.collectAsStateWithLifecycle()
      val isDecryptingState by viewModel.isDecryptingLocalView.collectAsStateWithLifecycle()
      val onboardingStepState by viewModel.onboardingStep.collectAsStateWithLifecycle()
      val currentScreenState by viewModel.currentScreen.collectAsStateWithLifecycle()

      // Call state parameters
      val callStateVal by viewModel.callState.collectAsStateWithLifecycle()
      val callDurationVal by viewModel.callDuration.collectAsStateWithLifecycle()
      val isLowTrafficCompVal by viewModel.isLowTrafficCompressionOn.collectAsStateWithLifecycle()
      val audioMutedVal by viewModel.audioMuted.collectAsStateWithLifecycle()
      val videoMutedVal by viewModel.videoMuted.collectAsStateWithLifecycle()

      // Cloud files parameters
      val filesList by viewModel.files.collectAsStateWithLifecycle()

      // Bots & mini apps
      val botsList by viewModel.botMiniApps.collectAsStateWithLifecycle()
      val tttBoard by viewModel.ticTacToeBoard.collectAsStateWithLifecycle()
      val tttWinner by viewModel.ticTacToeWinner.collectAsStateWithLifecycle()
      val webhookLogsVal by viewModel.webhookExecutionLog.collectAsStateWithLifecycle()

      // Gifts
      val giftsList by viewModel.gifts.collectAsStateWithLifecycle()

      // Gamification notification overlay toast
      val xpToastMsgValue by viewModel.xpToastMsg.collectAsStateWithLifecycle()

      // Load active theme index
      val themeIndex = profileState?.selectedThemeIndex ?: 0

      MyApplicationTheme(paletteIndex = themeIndex) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            
            // GAMIFICATION SLIDE-DOWN NOTIFICATION OVERLAY
            AnimatedVisibility(
              visible = xpToastMsgValue != null,
              enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
              exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
              modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp)
                .zIndex(999f)
            ) {
              val msg = xpToastMsgValue ?: ""
              Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.primary,
                  contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier
                  .padding(horizontal = 24.dp)
                  .testTag("gamification_overlay_toast")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = if (msg.contains("XP") || msg.contains("stars")) Icons.Default.Casino else Icons.Default.MilitaryTech,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = msg,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                  )
                }
              }
            }

            // PRIMARY NAVIGATION SCREEN GRAPH SWAP
            when (val screen = currentScreenState) {
              is Screen.Onboarding -> {
                OnboardingScreen(
                  currentStep = onboardingStepState,
                  onNext = { viewModel.nextOnboardingStep() },
                  onSkip = { viewModel.skipOnboarding() }
                )
              }
              else -> {
                // Renders general chat/vault/video tabbed application
                Scaffold(
                  modifier = Modifier.fillMaxSize(),
                  bottomBar = {
                    // Only show bottom navigation when NOT in active conversation chat view
                    if (activeChatState == null) {
                      NavigationBar(
                        containerColor = Color(0xFF1A1C22),
                        tonalElevation = 0.dp,
                        modifier = Modifier
                          .testTag("bottom_nav_bar")
                          .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                          )
                      ) {
                        val navColors = NavigationBarItemDefaults.colors(
                          selectedIconColor = Color(0xFF3B82F6),
                          selectedTextColor = Color(0xFF3B82F6),
                          unselectedIconColor = Color(0xFF64748B),
                          unselectedTextColor = Color(0xFF64748B),
                          indicatorColor = Color(0xFF3B82F6).copy(alpha = 0.12f)
                        )

                        NavigationBarItem(
                          selected = screen is Screen.Chats,
                          onClick = { viewModel.navigateTo(Screen.Chats) },
                          icon = { Icon(Icons.Default.ChatBubble, contentDescription = "Chats") },
                          label = { Text("SecComms", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                          colors = navColors,
                          modifier = Modifier.testTag("nav_chats")
                        )

                        NavigationBarItem(
                          selected = screen is Screen.Cloud,
                          onClick = { viewModel.navigateTo(Screen.Cloud) },
                          icon = { Icon(Icons.Default.Cloud, contentDescription = "Vault") },
                          label = { Text("CloudVault", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                          colors = navColors,
                          modifier = Modifier.testTag("nav_cloud")
                        )

                        NavigationBarItem(
                          selected = screen is Screen.GroupCall,
                          onClick = { viewModel.navigateTo(Screen.GroupCall) },
                          icon = { Icon(Icons.Default.Call, contentDescription = "Comms Grid") },
                          label = { Text("VidMesh", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                          colors = navColors,
                          modifier = Modifier.testTag("nav_calls")
                        )

                        NavigationBarItem(
                          selected = screen is Screen.MiniApps,
                          onClick = { viewModel.navigateTo(Screen.MiniApps) },
                          icon = { Icon(Icons.Default.Widgets, contentDescription = "Bots") },
                          label = { Text("Sandbox", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                          colors = navColors,
                          modifier = Modifier.testTag("nav_sandbox")
                        )

                        NavigationBarItem(
                          selected = screen is Screen.Profile,
                          onClick = { viewModel.navigateTo(Screen.Profile) },
                          icon = { Icon(Icons.Default.AccountBox, contentDescription = "Settings") },
                          label = { Text("Cockpit", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                          colors = navColors,
                          modifier = Modifier.testTag("nav_profile")
                        )
                      }
                    }
                  }
                ) { innerPadding ->
                  Box(
                    modifier = Modifier
                      .fillMaxSize()
                      .padding(innerPadding)
                  ) {
                    when (screen) {
                      is Screen.Chats -> {
                        ChatsScreen(
                          profile = profileState,
                          chatsList = chatsList,
                          activeChat = activeChatState,
                          activeMessages = activeMessagesList,
                          isDecrypting = isDecryptingState,
                          onNavigateToChat = { id -> viewModel.navigateTo(Screen.ActiveChat(id)) },
                          onNavigateBack = { viewModel.navigateTo(Screen.Chats) },
                          onSendMessage = { txt -> viewModel.sendInstantMessage(txt) },
                          onCreateChat = { companion, delayBurn, emoji -> 
                            viewModel.createNewSecretChat(companion, delayBurn, emoji) 
                          },
                          onDeleteChat = { chatId -> viewModel.deleteChatCascade(chatId) },
                          onToggleGhostMode = { viewModel.toggleGhostMode() }
                        )
                      }
                      is Screen.ActiveChat -> {
                        // ChatsScreen automatically handles embedding the active chat view when activeChatState != null
                        ChatsScreen(
                          profile = profileState,
                          chatsList = chatsList,
                          activeChat = activeChatState,
                          activeMessages = activeMessagesList,
                          isDecrypting = isDecryptingState,
                          onNavigateToChat = {},
                          onNavigateBack = { viewModel.navigateTo(Screen.Chats) },
                          onSendMessage = { txt -> viewModel.sendInstantMessage(txt) },
                          onCreateChat = { _, _, _ -> },
                          onDeleteChat = { chatId -> viewModel.deleteChatCascade(chatId) },
                          onToggleGhostMode = {}
                        )
                      }
                      is Screen.Cloud -> {
                        CloudScreen(
                          files = filesList,
                          onAddFile = { name, size, mime, isSecret, pwd, content ->
                            viewModel.addSecureFile(name, size, mime, isSecret, pwd, content)
                          },
                          onRemoveFile = { id -> viewModel.removeFile(id) }
                        )
                      }
                      is Screen.GroupCall -> {
                        GroupCallScreen(
                          callState = callStateVal,
                          callDuration = callDurationVal,
                          isLowTrafficOn = isLowTrafficCompVal,
                          audioMuted = audioMutedVal,
                          videoMuted = videoMutedVal,
                          onStartCall = { viewModel.initiateCall() },
                          onToggleLowTraffic = { viewModel.toggleCallLowTraffic() },
                          onToggleAudio = { viewModel.toggleAudioMute() },
                          onToggleVideo = { viewModel.toggleVideoMute() },
                          onHangup = { viewModel.hangupCall() }
                        )
                      }
                      is Screen.MiniApps -> {
                        MiniAppsScreen(
                          botsList = botsList,
                          ticTacToeBoard = tttBoard,
                          ticTacToeWinner = tttWinner,
                          webhookLogs = webhookLogsVal,
                          onPlayTicTacToeMove = { idx -> viewModel.playTicTacToeMove(idx) },
                          onResetTicTacToe = { viewModel.resetTicTacToe() },
                          onExecuteWebhook = { botId, url, payload -> 
                            viewModel.simulateCustomWebhookTrigger(botId, url, payload) 
                          },
                          onRegisterCustomBot = { name, cat, emoji, desc, callbackUrl ->
                            viewModel.buildCustomBot(name, cat, desc, emoji, callbackUrl)
                          }
                        )
                      }
                      is Screen.Profile -> {
                        ProfileScreen(
                          profile = profileState,
                          giftsList = giftsList,
                          onSelectTheme = { theme -> viewModel.selectTheme(theme) },
                          onPurchasePremium = { viewModel.purchasePremium() },
                          onSendGift = { gift, companion, starCost, greeting -> 
                            viewModel.makeGiftExchange(gift, companion, starCost, greeting)
                          },
                          onChangeNotificationTone = { name -> viewModel.setNotificationSound(name) }
                        )
                      }
                      else -> {}
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
}
