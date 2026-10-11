package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameId: String,
    val gameName: String,
    val score: Int,
    val xpEarned: Int,
    val coinsEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val streak: Int = 0,
    val isCompletedToday: Boolean = false,
    val lastCompletedDate: Long = 0,
    val completedDaysMask: Int = 0 // Bitmask for Mon=1, Tue=2, Wed=4, Thu=8, Fri=16, Sat=32, Sun=64
)

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val priority: String = "Medium", // High, Medium, Low
    val category: String = "All", // Urgent, Work, Personal, Health
    val dueDate: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val colorTag: String = "#A7F3D0", // Mint green default
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "crm_contacts")
data class CrmContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relationship: String,
    val notes: String,
    val lastContactDate: Long = System.currentTimeMillis(),
    val nextFollowUpDate: Long = 0L
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateStr: String,
    val mood: String = "😊 Peaceful",
    val content: String,
    val gratitude: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val frequency: String = "Weekly", // Weekly, Daily, Monthly
    val targetCategory: String = "games Games", // games Games, focus Focus, habits Habits
    val targetCount: Int = 5,
    val currentProgress: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_items")
data class SavedItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String, // "Idea", "Plan", "Meeting Summary"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
