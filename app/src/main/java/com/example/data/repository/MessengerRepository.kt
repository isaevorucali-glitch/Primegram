package com.example.data.repository

import com.example.data.db.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MessengerRepository(private val dao: PrimeDao) {

    val profileFlow: Flow<UserProfile?> = dao.observeProfile()
    val chatsFlow: Flow<List<Chat>> = dao.observeChats()
    val filesFlow: Flow<List<CloudFile>> = dao.observeCloudFiles()
    val botsFlow: Flow<List<BotMiniApp>> = dao.observeBotMiniApps()
    val giftsFlow: Flow<List<GiftExchange>> = dao.observeGifts()

    fun observeMessagesForChat(chatId: Long): Flow<List<Message>> = dao.observeMessages(chatId)

    suspend fun getProfile(): UserProfile {
        return dao.getProfile() ?: UserProfile().also { dao.saveProfile(it) }
    }

    suspend fun saveProfile(profile: UserProfile) {
        dao.saveProfile(profile)
    }

    suspend fun saveChat(chat: Chat): Long {
        return dao.saveChat(chat)
    }

    suspend fun selectChat(chatId: Long) {
        dao.markChatAsRead(chatId)
    }

    suspend fun deleteChat(chatId: Long) {
        dao.deleteChat(chatId)
        dao.clearChatMessages(chatId)
    }

    suspend fun sendMessage(chatId: Long, text: String, isMyMessage: Boolean, selfDestructSecs: Int): Long {
        val encrypted = true // Simulate encrypting text before saving
        val message = Message(
            chatId = chatId,
            text = text,
            senderName = if (isMyMessage) "Me" else "Companion",
            isMyMessage = isMyMessage,
            selfDestructSecs = selfDestructSecs,
            isEncrypted = encrypted
        )
        val msgId = dao.saveMessage(message)
        dao.updateLastMessage(chatId, text, System.currentTimeMillis())
        return msgId
    }

    suspend fun updateMessage(message: Message) {
        dao.updateMessage(message)
    }

    suspend fun deleteMessage(id: Long) {
        dao.deleteMessage(id)
    }

    suspend fun saveFile(name: String, size: String, mimeType: String, isSecret: Boolean, passwordHash: String, content: String): Long {
        val file = CloudFile(
            name = name,
            size = size,
            mimeType = mimeType,
            isSecret = isSecret,
            passwordHash = passwordHash,
            dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
            decryptedData = content
        )
        return dao.saveCloudFile(file)
    }

    suspend fun deleteFile(fileId: Long) {
        dao.deleteCloudFile(fileId)
    }

    suspend fun incrementBotUsage(botId: String) {
        dao.incrementBotUsage(botId)
    }

    suspend fun sendGift(giftType: String, sender: String, receiver: String, starCost: Int, text: String) {
        val gift = GiftExchange(
            giftType = giftType,
            sender = sender,
            receiver = receiver,
            starCost = starCost,
            message = text
        )
        dao.saveGift(gift)
        
        // Deduct stars and award XP
        val currentProfile = getProfile()
        val changeStars = currentProfile.stars - starCost
        val changeXp = currentProfile.xp + (starCost * 3) // 3 XP per star spent
        val newLevel = 1 + (changeXp / 100) // 100 XP per level
        
        dao.saveProfile(currentProfile.copy(
            stars = if (changeStars < 0) 0 else changeStars,
            xp = changeXp,
            level = newLevel
        ))
    }

    suspend fun awardXp(amount: Int) {
        val currentProfile = getProfile()
        val changeXp = currentProfile.xp + amount
        val newLevel = 1 + (changeXp / 100)
        dao.saveProfile(currentProfile.copy(
            xp = changeXp,
            level = newLevel
        ))
    }

    suspend fun refundStars(amount: Int) {
        val currentProfile = getProfile()
        dao.saveProfile(currentProfile.copy(
            stars = currentProfile.stars + amount
        ))
    }

    suspend fun toggleGhostMode() {
        val currentProfile = getProfile()
        dao.saveProfile(currentProfile.copy(
            isGhostModeActive = !currentProfile.isGhostModeActive
        ))
    }

    suspend fun buyPremium() {
        val currentProfile = getProfile()
        dao.saveProfile(currentProfile.copy(
            isPremium = true,
            stars = currentProfile.stars + 500 // Premium users get 500 bonus stars!
        ))
    }

    suspend fun addCustomBot(name: String, category: String, desc: String, emoji: String, url: String) {
        val bot = BotMiniApp(
            id = "custom_" + System.currentTimeMillis(),
            name = name,
            category = category,
            iconEmoji = emoji,
            description = desc,
            integrationUrl = url
        )
        dao.saveBotMiniApp(bot)
    }

    // Seed initial values to make the app incredibly interactive
    suspend fun preseedDatabaseIfEmpty() {
        val existingChats = chatsFlow.firstOrNull() ?: emptyList()
        if (existingChats.isEmpty()) {
            // Seed profile
            val profile = dao.getProfile()
            if (profile == null) {
                dao.saveProfile(UserProfile(
                    username = "GhostOperative",
                    stars = 350,
                    xp = 25,
                    level = 1,
                    isPremium = false,
                    selectedThemeIndex = 0,
                    isGhostModeActive = false,
                    chosenNotificationSound = "Neon Chime",
                    completedOnboarding = false
                ))
            }

            // Seed initial chats
            val secretAgentId = dao.saveChat(Chat(
                title = "Alice (E2E Encrypted)",
                isGhost = true,
                isEncrypted = true,
                selfDestructDefault = 5,
                avatarEmoji = "🕵️‍♀️",
                lastMessage = "Let's meet at the regular safehouse.",
                lastMessageTime = System.currentTimeMillis() - 3600_000
            ))

            val botSystemId = dao.saveChat(Chat(
                title = "Gemini AI Helper",
                isGhost = false,
                isEncrypted = true,
                selfDestructDefault = 0,
                avatarEmoji = "🤖",
                lastMessage = "Hi! I am custom AI Node. Ask me to automate anything!",
                lastMessageTime = System.currentTimeMillis() - 7200_000
            ))

            val developerId = dao.saveChat(Chat(
                title = "Primegramm Support",
                isGhost = false,
                isEncrypted = true,
                selfDestructDefault = 0,
                avatarEmoji = "👑",
                lastMessage = "Thank you for installing Primegramm Beta. Enjoy E2E secure anonymous networking!",
                lastMessageTime = System.currentTimeMillis() - 120_000
            ))

            // Seed messages for each
            dao.saveMessage(Message(
                chatId = secretAgentId,
                text = "Secure channel initialised.",
                senderName = "Alice",
                timestamp = System.currentTimeMillis() - 7200_000,
                isMyMessage = false,
                isRead = true
            ))
            dao.saveMessage(Message(
                chatId = secretAgentId,
                text = "Perfect. Is ghost mode enabled?",
                senderName = "Me",
                timestamp = System.currentTimeMillis() - 7000_000,
                isMyMessage = true,
                isRead = true
            ))
            dao.saveMessage(Message(
                chatId = secretAgentId,
                text = "Yes, typing indicator disabled. Also self-destruct is set to 5s. Message burns after 5s of reading.",
                senderName = "Alice",
                timestamp = System.currentTimeMillis() - 6800_000,
                isMyMessage = false,
                isRead = true
            ))
            dao.saveMessage(Message(
                chatId = secretAgentId,
                text = "Let's meet at the regular safehouse.",
                senderName = "Alice",
                timestamp = System.currentTimeMillis() - 3600_000,
                isMyMessage = false,
                isRead = false // Keep unread to show indicator
            ))

            dao.saveMessage(Message(
                chatId = botSystemId,
                text = "Hi! I am custom AI Node. Ask me to automate anything! You can integrate custom Webhook API keys too.",
                senderName = "Gemini AI Helper",
                timestamp = System.currentTimeMillis() - 7200_000,
                isMyMessage = false,
                isRead = true
            ))

            dao.saveMessage(Message(
                chatId = developerId,
                text = "Thank you for installing Primegramm Beta. Enjoy E2E secure anonymous networking!",
                senderName = "Primegramm Support",
                timestamp = System.currentTimeMillis() - 120_000,
                isMyMessage = false,
                isRead = true
            ))

            // Seed Cloud files
            dao.saveCloudFile(CloudFile(
                name = "classified_manifesto.pdf",
                size = "1.8 MB",
                mimeType = "application/pdf",
                isSecret = true,
                passwordHash = "1234", // simple plain hash for prototype convenience
                dateStr = "28 May 2026",
                decryptedData = "Primegramm Protocol: E2E Secret Messaging, Zero-knowledge logs, Client-side persistence node."
            ))
            dao.saveCloudFile(CloudFile(
                name = "avatar_premium_icon.png",
                size = "412 KB",
                mimeType = "image/png",
                isSecret = false,
                passwordHash = "",
                dateStr = "27 May 2026",
                decryptedData = "[Binary Image Representation]"
            ))

            // Seed Bot Mini Apps
            dao.saveBotMiniApp(BotMiniApp(
                id = "gemini_ai",
                name = "Gemini Automator",
                category = "Bot",
                iconEmoji = "🤖",
                description = "AI bot supporting dynamic JSON query analysis, chat responses, and auto work-orders."
            ))
            dao.saveBotMiniApp(BotMiniApp(
                id = "tictactoe",
                name = "Crypto TicTacToe",
                category = "MiniApp",
                iconEmoji = "🎮",
                description = "Play gamified tic-tac-toe to earn premium prime stars and climb the global safe ranks!"
            ))
            dao.saveBotMiniApp(BotMiniApp(
                id = "webhook_tester",
                name = "APIs Webhook Automation",
                category = "MiniApp",
                iconEmoji = "🌐",
                description = "Map post payloads to external webhook URLs (e.g. n8n, Zapier) directly from your chats."
            ))
        }
    }
}
