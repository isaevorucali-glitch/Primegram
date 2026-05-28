package com.example.data.db

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ==========================================
// DB ENTITIES
// ==========================================

@Entity(tableName = "profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val username: String = "SecretPrime",
    val stars: Int = 230,
    val xp: Int = 0,
    val level: Int = 1,
    val isPremium: Boolean = false,
    val selectedThemeIndex: Int = 0,
    val isGhostModeActive: Boolean = false,
    val chosenNotificationSound: String = "Classic Synth",
    val completedOnboarding: Boolean = false
)

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val isGhost: Boolean = false,
    val isEncrypted: Boolean = true,
    val selfDestructDefault: Int = 0, // 0 = disabled, others in seconds like 5, 10, 30
    val unreadCount: Int = 0,
    val avatarEmoji: String = "👤",
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chatId: Long,
    val text: String,
    val senderName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isMyMessage: Boolean,
    val isRead: Boolean = false,
    val selfDestructSecs: Int = 0, // 0 = infinite, else countdown in seconds
    val selfDestructTimeLeft: Int = -1, // -1 means timer not started yet, 0 means burned
    val isEncrypted: Boolean = true
)

@Entity(tableName = "cloud_files")
data class CloudFile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val size: String,
    val mimeType: String,
    val isSecret: Boolean = false,
    val passwordHash: String = "",
    val dateStr: String = "",
    val decryptedData: String = "" // Simulates private contents
)

@Entity(tableName = "bot_mini_apps")
data class BotMiniApp(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // "Bot", "MiniApp"
    val iconEmoji: String,
    val description: String,
    val usageCount: Int = 0,
    val integrationUrl: String = ""
)

@Entity(tableName = "gifts")
data class GiftExchange(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val giftType: String, // "Star Badge", "Infinity Loop", "Diamond Crown", "Golden Cup"
    val sender: String,
    val receiver: String,
    val starCost: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String = ""
)

// ==========================================
// DB DAOS & OPERATIONS
// ==========================================

@Dao
interface PrimeDao {
    // Profile
    @Query("SELECT * FROM profiles WHERE id = 1 LIMIT 1")
    fun observeProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM profiles WHERE id = 1 LIMIT 1")
    suspend fun getProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: UserProfile)

    // Chats
    @Query("SELECT * FROM chats ORDER BY lastMessageTime DESC")
    fun observeChats(): Flow<List<Chat>>

    @Query("SELECT * FROM chats WHERE id = :id LIMIT 1")
    suspend fun getChatById(id: Long): Chat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChat(chat: Chat): Long

    @Query("UPDATE chats SET lastMessage = :text, lastMessageTime = :time WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: Long, text: String, time: Long)

    @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
    suspend fun markChatAsRead(chatId: Long)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChat(chatId: Long)

    // Messages
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun observeMessages(chatId: Long): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND selfDestructTimeLeft != 0 ORDER BY timestamp ASC")
    suspend fun getActiveMessages(chatId: Long): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMessage(message: Message): Long

    @Update
    suspend fun updateMessage(message: Message)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: Long)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun clearChatMessages(chatId: Long)

    // Cloud Files
    @Query("SELECT * FROM cloud_files ORDER BY id DESC")
    fun observeCloudFiles(): Flow<List<CloudFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCloudFile(file: CloudFile): Long

    @Query("DELETE FROM cloud_files WHERE id = :fileId")
    suspend fun deleteCloudFile(fileId: Long)

    // Bot/MiniApps
    @Query("SELECT * FROM bot_mini_apps ORDER BY category DESC")
    fun observeBotMiniApps(): Flow<List<BotMiniApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBotMiniApp(bot: BotMiniApp)

    @Query("UPDATE bot_mini_apps SET usageCount = usageCount + 1 WHERE id = :id")
    suspend fun incrementBotUsage(id: String)

    // Gifts
    @Query("SELECT * FROM gifts ORDER BY timestamp DESC")
    fun observeGifts(): Flow<List<GiftExchange>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGift(gift: GiftExchange): Long
}

// ==========================================
// DB CLASS
// ==========================================

@Database(
    entities = [
        UserProfile::class,
        Chat::class,
        Message::class,
        CloudFile::class,
        BotMiniApp::class,
        GiftExchange::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun primeDao(): PrimeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "primegramm_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
