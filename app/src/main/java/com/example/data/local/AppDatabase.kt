package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Game Scores
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameScore(score: GameScoreEntity): Long

    @Query("SELECT * FROM game_scores ORDER BY timestamp DESC")
    fun getAllGameScores(): Flow<List<GameScoreEntity>>

    @Query("SELECT COUNT(*) FROM game_scores")
    fun getGamesPlayedCount(): Flow<Int>

    @Query("SELECT MAX(score) FROM game_scores")
    fun getHighestScore(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM game_scores WHERE timestamp >= :sinceTimestamp")
    fun getGamesPlayedSince(sinceTimestamp: Long): Flow<Int>

    // Habits
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabit(id: Long)

    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    // Todos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity): Long

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodo(id: Long)

    @Query("SELECT * FROM todos ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>

    // Notes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long)

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    // CRM Contacts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrmContact(contact: CrmContactEntity): Long

    @Query("DELETE FROM crm_contacts WHERE id = :id")
    suspend fun deleteCrmContact(id: Long)

    @Query("SELECT * FROM crm_contacts ORDER BY lastContactDate DESC")
    fun getAllCrmContacts(): Flow<List<CrmContactEntity>>

    // Chat
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(msg: ChatMessageEntity): Long

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()
}

@Database(
    entities = [
        GameScoreEntity::class,
        HabitEntity::class,
        TodoEntity::class,
        NoteEntity::class,
        CrmContactEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "brine_booster.db"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
