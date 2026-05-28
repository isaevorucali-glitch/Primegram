package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.*
import com.example.data.repository.MessengerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Onboarding : Screen()
    object Chats : Screen()
    data class ActiveChat(val chatId: Long) : Screen()
    object Cloud : Screen()
    object GroupCall : Screen()
    object MiniApps : Screen()
    object Profile : Screen()
}

class MessengerViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = MessengerRepository(database.primeDao())

    // UI States
    val profile = repository.profileFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val chats = repository.chatsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val files = repository.filesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val botMiniApps = repository.botsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val gifts = repository.giftsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current navigation state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Chats)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Active Chat details
    private val _activeChat = MutableStateFlow<Chat?>(null)
    val activeChat: StateFlow<Chat?> = _activeChat.asStateFlow()

    private val _activeMessages = MutableStateFlow<List<Message>>(emptyList())
    val activeMessages: StateFlow<List<Message>> = _activeMessages.asStateFlow()

    // Onboarding step (0..3) if not completed onboarding
    private val _onboardingStep = MutableStateFlow(0)
    val onboardingStep: StateFlow<Int> = _onboardingStep.asStateFlow()

    // Dynamic encryption simulator status for chats
    private val _isDecryptingLocalView = MutableStateFlow(false)
    val isDecryptingLocalView: StateFlow<Boolean> = _isDecryptingLocalView.asStateFlow()

    // Active Video Call State
    private val _callState = MutableStateFlow("idle") // idle, dialing, connected
    val callState: StateFlow<String> = _callState.asStateFlow()

    private val _isLowTrafficCompressionOn = MutableStateFlow(true)
    val isLowTrafficCompressionOn: StateFlow<Boolean> = _isLowTrafficCompressionOn.asStateFlow()

    private val _audioMuted = MutableStateFlow(false)
    val audioMuted: StateFlow<Boolean> = _audioMuted.asStateFlow()

    private val _videoMuted = MutableStateFlow(false)
    val videoMuted: StateFlow<Boolean> = _videoMuted.asStateFlow()

    private val _callDuration = MutableStateFlow(0)
    val callDuration: StateFlow<Int> = _callDuration.asStateFlow()

    // Gamification Toast Notification
    private val _xpToastMsg = MutableStateFlow<String?>(null)
    val xpToastMsg: StateFlow<String?> = _xpToastMsg.asStateFlow()

    // TicTacToe MiniApp Sandbox State
    private val _ticTacToeBoard = MutableStateFlow(List(9) { "" })
    val ticTacToeBoard: StateFlow<List<String>> = _ticTacToeBoard.asStateFlow()

    private val _ticTacToeWinner = MutableStateFlow("") // "", "Me", "Bot", "Draw"
    val ticTacToeWinner: StateFlow<String> = _ticTacToeWinner.asStateFlow()

    // API integration / webhook custom form responses
    private val _webhookExecutionLog = MutableStateFlow<List<String>>(emptyList())
    val webhookExecutionLog: StateFlow<List<String>> = _webhookExecutionLog.asStateFlow()

    // Auto-running countdown logic for burnable messages
    private var countdownJob: Job? = null
    private var callDurationJob: Job? = null

    init {
        viewModelScope.launch {
            repository.preseedDatabaseIfEmpty()
            
            // Observe onboarding status to redirect to onboarding if needed
            profile.collect { prof ->
                if (prof != null) {
                    if (!prof.completedOnboarding && _currentScreen.value is Screen.Chats) {
                        _currentScreen.value = Screen.Onboarding
                    }
                }
            }
        }
        
        startSelfDestructMonitor()
    }

    // ONBOARDING ACTIONS
    fun nextOnboardingStep() {
        if (_onboardingStep.value < 3) {
            _onboardingStep.value += 1
        } else {
            completeOnboarding()
        }
    }

    fun skipOnboarding() {
        completeOnboarding()
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            val prof = repository.getProfile()
            repository.saveProfile(prof.copy(completedOnboarding = true))
            _currentScreen.value = Screen.Chats
            triggerXpToast("Setup complete! +25 XP")
            repository.awardXp(25)
        }
    }

    // NAVIGATION
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        if (screen is Screen.ActiveChat) {
            loadChatMessages(screen.chatId)
        } else {
            _activeChat.value = null
            _activeMessages.value = emptyList()
        }
    }

    // GHOST MODE AND PREMIUM TOGGLE
    fun toggleGhostMode() {
        viewModelScope.launch {
            repository.toggleGhostMode()
            val state = repository.getProfile().isGhostModeActive
            triggerXpToast(if (state) "Ghost mode ACTIVE" else "Ghost mode DEACTIVE")
        }
    }

    fun purchasePremium() {
        viewModelScope.launch {
            repository.buyPremium()
            triggerXpToast("💎 Premium Activated! +500 Stars")
        }
    }

    fun addStarsSecretly(amount: Int) {
        viewModelScope.launch {
            val p = repository.getProfile()
            repository.saveProfile(p.copy(stars = p.stars + amount))
            triggerXpToast("Earned +$amount Prime Stars!")
        }
    }

    // THEME CUSTOMIZER
    fun selectTheme(index: Int) {
        viewModelScope.launch {
            val current = repository.getProfile()
            repository.saveProfile(current.copy(selectedThemeIndex = index))
        }
    }

    // SYSTEM NOTIFICATION SOUND SELECTOR
    fun setNotificationSound(name: String) {
        viewModelScope.launch {
            val current = repository.getProfile()
            repository.saveProfile(current.copy(chosenNotificationSound = name))
            triggerXpToast("Sound changed: $name")
        }
    }

    // CHAT & MESSAGES OPERATIONS
    private var messageObserverJob: Job? = null

    private fun loadChatMessages(chatId: Long) {
        messageObserverJob?.cancel()
        _isDecryptingLocalView.value = true
        
        viewModelScope.launch {
            val targetChat = chats.value.find { it.id == chatId }
            _activeChat.value = targetChat
            repository.selectChat(chatId)
            
            // Decryption simulation
            delay(400)
            _isDecryptingLocalView.value = false
        }

        messageObserverJob = viewModelScope.launch {
            repository.observeMessagesForChat(chatId).collect { msgs ->
                // Start self-destruct timers for any messages that has self-destruct setting AND was unread in active screen
                val updatedList = msgs.map { m ->
                    if (m.selfDestructSecs > 0 && m.selfDestructTimeLeft == -1 && !m.isMyMessage) {
                        viewModelScope.launch {
                            repository.updateMessage(m.copy(selfDestructTimeLeft = m.selfDestructSecs))
                        }
                        m.copy(selfDestructTimeLeft = m.selfDestructSecs)
                    } else {
                        m
                    }
                }
                _activeMessages.value = updatedList.filter { it.selfDestructTimeLeft != 0 }
            }
        }
    }

    fun createNewSecretChat(companionName: String, selfDestructDefault: Int, emoji: String) {
        viewModelScope.launch {
            val newChat = Chat(
                title = companionName,
                isGhost = true,
                isEncrypted = true,
                selfDestructDefault = selfDestructDefault,
                avatarEmoji = emoji,
                lastMessage = "Secret conversation established.",
                lastMessageTime = System.currentTimeMillis()
            )
            val id = repository.saveChat(newChat)
            repository.sendMessage(id, "Secure channel opened anonymously.", false, selfDestructDefault)
            navigateTo(Screen.ActiveChat(id))
            repository.awardXp(10)
            triggerXpToast("Secure Chat Created! +10 XP")
        }
    }

    fun deleteChatCascade(chatId: Long) {
        viewModelScope.launch {
            repository.deleteChat(chatId)
            navigateTo(Screen.Chats)
        }
    }

    fun sendInstantMessage(text: String) {
        val chat = _activeChat.value ?: return
        viewModelScope.launch {
            // My message stores instantly
            val selfDestructLimit = chat.selfDestructDefault
            repository.sendMessage(chat.id, text, isMyMessage = true, selfDestructSecs = selfDestructLimit)
            
            // Gain XP
            repository.awardXp(5)
            triggerXpToast("+5 XP for secure data post")

            // Simulate self-destruct countdown trigger for my own message if it is self-destruct
            // (typically triggers immediately when sent/delivered)
            
            // If it's a bot helper, trigger smart response
            if (chat.title == "Gemini AI Helper" || chat.title.contains("Bot")) {
                simulateBotResponse(chat.id, text)
            } else {
                // Regular mock response after delay
                delay(1200)
                val responseText = when {
                    text.contains("hello", true) || text.contains("привет", true) -> "Anonymity established. What information do you seek?"
                    text.contains("meet", true) || text.contains("встреча", true) -> "Understood. Clock is running, meet me at Sector 7."
                    text.contains("password", true) -> "Encryption key rotated. Keep cloud documents password protected!"
                    else -> "Message securely received. Encrypted payloads match key signatures."
                }
                repository.sendMessage(chat.id, responseText, isMyMessage = false, selfDestructSecs = selfDestructLimit)
                repository.awardXp(5)
            }
        }
    }

    private suspend fun simulateBotResponse(chatId: Long, text: String) {
        delay(1000)
        val botText = when {
            text.contains("status", true) -> "SYS NODE ACTIVE: End-to-End Encryption active, Proxy routing active (3 hops), zero leak profile verified."
            text.contains("help", true) -> "Prime Bot Help:\nCommands:\n- status: Test system status\n- secret: Create automated password-hashed text file\n- clear: Clear log history"
            text.contains("secret", true) -> {
                val fileId = repository.saveFile("bot_automated_secure.txt", "12 KB", "text/plain", true, "1337", "Classified Automated Memo - Authorized Nodes Only.")
                "Secured database file generated automatically! Check Cloud tab. Pwd hash set: 1337."
            }
            else -> "Processing API Automation event: Log event parsed. Webhook simulation triggered. Query returned successfully."
        }
        repository.sendMessage(chatId, botText, isMyMessage = false, selfDestructSecs = 0)
        repository.awardXp(10)
        triggerXpToast("Bot Automated Workflow triggered! +10 XP")
    }

    // SELF DESTRUCT COUNTER REGULATOR
    private fun startSelfDestructMonitor() {
        countdownJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val chat = _activeChat.value
                if (chat != null) {
                    val activeList = _activeMessages.value
                    activeList.forEach { m ->
                        if (m.selfDestructTimeLeft > 0) {
                            val nextSec = m.selfDestructTimeLeft - 1
                            if (nextSec == 0) {
                                // Message burns! Let's delete it
                                repository.deleteMessage(m.id)
                            } else {
                                repository.updateMessage(m.copy(selfDestructTimeLeft = nextSec))
                            }
                        }
                    }
                }
            }
        }
    }

    // CLOUD MEMORY PASSWORDS AND FILES
    fun addSecureFile(name: String, size: String, mime: String, isSecret: Boolean, pwdBase: String, contents: String) {
        viewModelScope.launch {
            repository.saveFile(name, size, mime, isSecret, pwdBase, contents)
            repository.awardXp(15)
            triggerXpToast("Cloud File Uploaded! +15 XP")
        }
    }

    fun removeFile(fileId: Long) {
        viewModelScope.launch {
            repository.deleteFile(fileId)
            triggerXpToast("File securely shredded from node")
        }
    }

    // VIDEO CONFERENCE CALL
    fun initiateCall() {
        _callState.value = "dialing"
        _callDuration.value = 0
        viewModelScope.launch {
            delay(2000) // Simulating dial connection
            _callState.value = "connected"
            startCallTimer()
            triggerXpToast("E2E Secure Video Linked! +30 XP")
            repository.awardXp(30)
        }
    }

    fun toggleCallLowTraffic() {
        _isLowTrafficCompressionOn.value = !_isLowTrafficCompressionOn.value
        triggerXpToast(if (_isLowTrafficCompressionOn.value) "Low traffic compression ENGAGED" else "Full Resolution enabled")
    }

    fun toggleAudioMute() { _audioMuted.value = !_audioMuted.value }
    fun toggleVideoMute() { _videoMuted.value = !_videoMuted.value }

    fun hangupCall() {
        _callState.value = "idle"
        callDurationJob?.cancel()
    }

    private fun startCallTimer() {
        callDurationJob?.cancel()
        callDurationJob = viewModelScope.launch {
            while (_callState.value == "connected") {
                delay(1000)
                _callDuration.value += 1
            }
        }
    }

    // GIFTS FLOW
    fun makeGiftExchange(giftName: String, receiver: String, starPrice: Int, txtNote: String) {
        viewModelScope.launch {
            val p = repository.getProfile()
            if (p.stars >= starPrice) {
                repository.sendGift(giftName, "Me", receiver, starPrice, txtNote)
                triggerXpToast("Gifted $giftName to $receiver! +${starPrice * 3} XP")
            } else {
                triggerXpToast("Insufficient stars! Play TicTacToe or upgrade to Premium.")
            }
        }
    }

    // CUSTOM BOT & WEBHOOK API CREATOR
    fun buildCustomBot(name: String, category: String, desc: String, emoji: String, url: String) {
        viewModelScope.launch {
            repository.addCustomBot(name, category, desc, emoji, url)
            triggerXpToast("Registered Bot Node via API! +15 XP")
            repository.awardXp(15)
        }
    }

    fun simulateCustomWebhookTrigger(botId: String, webhookUrl: String, jsonPayload: String) {
        viewModelScope.launch {
            repository.incrementBotUsage(botId)
            _webhookExecutionLog.value = _webhookExecutionLog.value + "Sending Event to $webhookUrl..."
            delay(800)
            _webhookExecutionLog.value = _webhookExecutionLog.value + "API status 202: Payload accepted securely by proxy."
            _webhookExecutionLog.value = _webhookExecutionLog.value + "Response: {\"status\":\"success\", \"node_synced\": true}"
            repository.awardXp(10)
            triggerXpToast("API Automated Post Succesful! +10 XP")
        }
    }

    // TICTACTOE MINIAPP PLAYGROUND (GAMIFICATION STIMULATOR)
    fun playTicTacToeMove(index: Int) {
        if (_ticTacToeWinner.value.isNotEmpty() || _ticTacToeBoard.value[index].isNotEmpty()) return

        viewModelScope.launch {
            val currentBoard = _ticTacToeBoard.value.toMutableList()
            // My Move
            currentBoard[index] = "X"
            _ticTacToeBoard.value = currentBoard
            repository.awardXp(2)

            if (checkTicTacToeWinner(currentBoard, "X")) {
                _ticTacToeWinner.value = "Me"
                repository.refundStars(15) // earn 15 stars!
                triggerXpToast("Victory! Earned 15 Prime Stars! 😎")
                return@launch
            }

            if (!currentBoard.contains("")) {
                _ticTacToeWinner.value = "Draw"
                repository.refundStars(3)
                triggerXpToast("Draw! Earned 3 Prime Stars 🤝")
                return@launch
            }

            // Simulating Bot Turn after small delay
            delay(500)
            val botBoard = _ticTacToeBoard.value.toMutableList()
            val blankIndices = botBoard.mapIndexed { idx, s -> if (s.isEmpty()) idx else -1 }.filter { it != -1 }
            if (blankIndices.isNotEmpty()) {
                val botSelection = blankIndices.random()
                botBoard[botSelection] = "O"
                _ticTacToeBoard.value = botBoard

                if (checkTicTacToeWinner(botBoard, "O")) {
                    _ticTacToeWinner.value = "Bot"
                    triggerXpToast("AI Node wins! Retry for better luck!")
                } else if (!botBoard.contains("")) {
                    _ticTacToeWinner.value = "Draw"
                    repository.refundStars(3)
                }
            }
        }
    }

    fun resetTicTacToe() {
        _ticTacToeBoard.value = List(9) { "" }
        _ticTacToeWinner.value = ""
    }

    private fun checkTicTacToeWinner(board: List<String>, symbol: String): Boolean {
        val wins = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // cols
            listOf(0, 4, 8), listOf(2, 4, 6)                  // diags
        )
        for (combo in wins) {
            if (board[combo[0]] == symbol && board[combo[1]] == symbol && board[combo[2]] == symbol) {
                return true
            }
        }
        return false
    }

    // VIEW TOAST FEEDBACKS
    private fun triggerXpToast(msg: String) {
        viewModelScope.launch {
            _xpToastMsg.value = msg
            delay(2500)
            if (_xpToastMsg.value == msg) {
                _xpToastMsg.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
        callDurationJob?.cancel()
    }
}
