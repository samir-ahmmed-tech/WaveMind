package com.example.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.GameScoreEntity
import com.example.data.local.PreferencesManager
import com.example.data.local.UserPreferences
import com.example.domain.models.AchievementEngine
import com.example.domain.models.AchievementItem
import com.example.domain.models.AppCatalog
import com.example.domain.models.GameItem
import com.example.domain.models.UserTargetGoal
import com.example.ui.audio.SoundManager
import com.example.ui.components.NavTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val dao = db.appDao()
    private val prefsManager = PreferencesManager.getInstance(application)

    init {
        viewModelScope.launch {
            prefsManager.userPreferencesFlow.collect { prefs ->
                SoundManager.updateConfig(
                    master = prefs.masterSoundFx,
                    click = prefs.uiClickSounds,
                    victory = prefs.victoryFanfare,
                    buzzer = prefs.gameOverBuzzer,
                    volume = prefs.soundVolume,
                    pack = prefs.soundPack
                )
            }
        }
    }

    // DataStore Preferences
    val userPreferences: StateFlow<UserPreferences> = prefsManager.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    // Bottom Navigation
    private val _currentTab = MutableStateFlow(NavTab.HOME)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    fun selectTab(tab: NavTab) {
        _currentTab.value = tab
    }

    // Personal Target Goals (Custom training goals)
    private val _targetGoals = MutableStateFlow<List<UserTargetGoal>>(
        listOf(
            UserTargetGoal(
                id = 1L,
                title = "Complete 5 Memory Drills",
                category = "Memory",
                targetType = "Drills",
                targetCount = 5,
                currentCount = 2,
                frequency = "Daily",
                rewardCoins = 60,
                rewardXp = 120,
                iconEmoji = "🧠",
                difficulty = "Normal"
            ),
            UserTargetGoal(
                id = 2L,
                title = "Master 3 Logic Puzzles",
                category = "Logic",
                targetType = "Drills",
                targetCount = 3,
                currentCount = 1,
                frequency = "Daily",
                rewardCoins = 75,
                rewardXp = 150,
                iconEmoji = "⚡",
                difficulty = "Normal"
            ),
            UserTargetGoal(
                id = 3L,
                title = "Train Reflexes under 250ms",
                category = "Focus",
                targetType = "Speed",
                targetCount = 4,
                currentCount = 3,
                frequency = "Weekly",
                rewardCoins = 100,
                rewardXp = 200,
                iconEmoji = "🎯",
                difficulty = "Hard"
            )
        )
    )
    val targetGoals: StateFlow<List<UserTargetGoal>> = _targetGoals.asStateFlow()

    fun addTargetGoal(
        title: String,
        category: String = "All",
        targetCount: Int = 5,
        frequency: String = "Daily",
        targetType: String = "Drills",
        rewardCoins: Int = 50,
        rewardXp: Int = 100,
        iconEmoji: String = "🎯",
        difficulty: String = "Normal",
        note: String = ""
    ) {
        val newGoal = UserTargetGoal(
            id = System.currentTimeMillis(),
            title = title.ifBlank { "Custom Target Goal" },
            category = category,
            targetType = targetType,
            targetCount = targetCount.coerceAtLeast(1),
            currentCount = 0,
            frequency = frequency,
            rewardCoins = rewardCoins,
            rewardXp = rewardXp,
            iconEmoji = iconEmoji,
            difficulty = difficulty,
            note = note,
            isCompleted = false,
            isRewardClaimed = false
        )
        _targetGoals.value = listOf(newGoal) + _targetGoals.value
        SoundManager.playSuccess()
    }

    fun incrementGoalProgress(goalId: Long) {
        _targetGoals.value = _targetGoals.value.map { goal ->
            if (goal.id == goalId) {
                val nextCount = (goal.currentCount + 1).coerceAtMost(goal.targetCount)
                val completed = nextCount >= goal.targetCount
                if (completed && !goal.isCompleted) {
                    SoundManager.playTargetCompleted()
                } else {
                    SoundManager.playClick()
                }
                goal.copy(currentCount = nextCount, isCompleted = completed)
            } else goal
        }
    }

    fun claimTargetReward(goalId: Long) {
        val goal = _targetGoals.value.find { it.id == goalId } ?: return
        if (goal.isCompleted && !goal.isRewardClaimed) {
            awardRewards(coins = goal.rewardCoins, xp = goal.rewardXp)
            SoundManager.playLevelUp()
            SoundManager.playCoin()
            _targetGoals.value = _targetGoals.value.map {
                if (it.id == goalId) it.copy(isRewardClaimed = true) else it
            }
        }
    }

    fun deleteTargetGoal(goalId: Long) {
        SoundManager.playWhoosh()
        _targetGoals.value = _targetGoals.value.filter { it.id != goalId }
    }

    // Games Filter & Search
    private val _gameSearch = MutableStateFlow("")
    val gameSearch: StateFlow<String> = _gameSearch.asStateFlow()

    private val _selectedGameCategory = MutableStateFlow("All")
    val selectedGameCategory: StateFlow<String> = _selectedGameCategory.asStateFlow()

    private val _favoriteGameIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteGameIds: StateFlow<Set<String>> = _favoriteGameIds.asStateFlow()

    val filteredGames: StateFlow<List<GameItem>> = combine(
        _gameSearch,
        _selectedGameCategory,
        _favoriteGameIds
    ) { query, category, favorites ->
        AppCatalog.games.filter { game ->
            val matchesQuery = query.isBlank() ||
                    game.title.contains(query, ignoreCase = true) ||
                    game.description.contains(query, ignoreCase = true)

            val matchesCategory = when (category) {
                "All" -> true
                "Favorites" -> favorites.contains(game.id)
                else -> game.category.equals(category, ignoreCase = true)
            }

            matchesQuery && matchesCategory
        }.map { game ->
            game.copy(isFavorite = favorites.contains(game.id))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppCatalog.games)

    fun setGameSearch(query: String) { _gameSearch.value = query }
    fun setGameCategory(cat: String) { _selectedGameCategory.value = cat }

    fun toggleFavorite(gameId: String) {
        val current = _favoriteGameIds.value.toMutableSet()
        if (current.contains(gameId)) current.remove(gameId) else current.add(gameId)
        _favoriteGameIds.value = current
    }

    // Active Game
    private val _activeGame = MutableStateFlow<GameItem?>(null)
    val activeGame: StateFlow<GameItem?> = _activeGame.asStateFlow()

    fun openGame(game: GameItem) { _activeGame.value = game }
    fun closeGame() { _activeGame.value = null }

    // Room Database Stats
    val gameScores: StateFlow<List<GameScoreEntity>> = dao.getAllGameScores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalGamesPlayed: StateFlow<Int> = dao.getGamesPlayedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bestScore: StateFlow<Int?> = dao.getHighestScore()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Record game outcome
    fun recordGameFinished(gameId: String, gameName: String, score: Int, xpEarned: Int, coinsEarned: Int) {
        viewModelScope.launch {
            dao.insertGameScore(
                GameScoreEntity(
                    gameId = gameId,
                    gameName = gameName,
                    score = score,
                    xpEarned = xpEarned,
                    coinsEarned = coinsEarned
                )
            )
            prefsManager.addRewards(coinsEarned, xpEarned, score)

            // Auto-advance matching target goals!
            val game = AppCatalog.games.find { it.id == gameId }
            val cat = game?.category ?: "All"
            _targetGoals.value = _targetGoals.value.map { goal ->
                if (goal.category == "All" || goal.category.equals(cat, ignoreCase = true)) {
                    val next = (goal.currentCount + 1).coerceAtMost(goal.targetCount)
                    val done = next >= goal.targetCount
                    if (done && !goal.isCompleted) {
                        prefsManager.addRewards(coinsEarned = 25, xpEarned = 75)
                    }
                    goal.copy(currentCount = next, isCompleted = done)
                } else goal
            }
        }
    }

    fun awardRewards(coins: Int, xp: Int) {
        viewModelScope.launch {
            prefsManager.addRewards(coinsEarned = coins, xpEarned = xp)
        }
    }

    fun updateSetting(key: String, value: Any) {
        viewModelScope.launch {
            prefsManager.updatePreference(key, value)
        }
    }

    // ==========================================
    // BACKUP & RESTORE ENGINE (REQUESTED)
    // ==========================================
    fun exportBackupJson(): String {
        val prefs = userPreferences.value
        val root = JSONObject()
        root.put("app", "BrineBooster")
        root.put("version", 1)
        root.put("exportedAt", System.currentTimeMillis())

        val profileObj = JSONObject().apply {
            put("userName", prefs.userName)
            put("userAvatar", prefs.userAvatar)
            put("userRankTitle", prefs.userRankTitle)
            put("xp", prefs.xp)
            put("coins", prefs.coins)
            put("streak", prefs.streak)
            put("bestScore", prefs.bestScore)
        }
        root.put("profile", profileObj)

        val settingsObj = JSONObject().apply {
            put("themePreset", prefs.themePreset)
            put("primaryColor", prefs.primaryColor)
            put("cardGlowColor", prefs.cardGlowColor)
            put("glassOpacity", prefs.glassOpacity.toDouble())
            put("glassBlurIntensity", prefs.glassBlurIntensity.toDouble())
            put("cardCornerRadius", prefs.cardCornerRadius.toDouble())
            put("borderThickness", prefs.borderThickness.toDouble())
            put("glowIntensity", prefs.glowIntensity.toDouble())
            put("borderAnimStyle", prefs.borderAnimStyle)
            put("backgroundGrid", prefs.backgroundGrid)
            put("scanlineCrtEffect", prefs.scanlineCrtEffect)
            put("countdownDuration", prefs.countdownDuration)
            put("masterSoundFx", prefs.masterSoundFx)
            put("uiClickSounds", prefs.uiClickSounds)
            put("victoryFanfare", prefs.victoryFanfare)
            put("soundVolume", prefs.soundVolume.toDouble())
            put("soundPack", prefs.soundPack)
            put("hapticFeedback", prefs.hapticFeedback)
        }
        root.put("settings", settingsObj)

        val goalsArray = JSONArray()
        _targetGoals.value.forEach { goal ->
            val gObj = JSONObject().apply {
                put("id", goal.id)
                put("title", goal.title)
                put("category", goal.category)
                put("targetType", goal.targetType)
                put("targetCount", goal.targetCount)
                put("currentCount", goal.currentCount)
                put("frequency", goal.frequency)
                put("rewardCoins", goal.rewardCoins)
                put("rewardXp", goal.rewardXp)
                put("iconEmoji", goal.iconEmoji)
                put("difficulty", goal.difficulty)
                put("note", goal.note)
                put("isCompleted", goal.isCompleted)
                put("isRewardClaimed", goal.isRewardClaimed)
            }
            goalsArray.put(gObj)
        }
        root.put("targetGoals", goalsArray)

        return root.toString(2)
    }

    fun importBackupJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            if (root.has("profile")) {
                val p = root.getJSONObject("profile")
                if (p.has("userName")) updateSetting(PreferencesManager.USER_NAME.name, p.getString("userName"))
                if (p.has("userAvatar")) updateSetting(PreferencesManager.USER_AVATAR.name, p.getString("userAvatar"))
                if (p.has("userRankTitle")) updateSetting(PreferencesManager.USER_RANK_TITLE.name, p.getString("userRankTitle"))
                if (p.has("xp")) updateSetting(PreferencesManager.XP.name, p.getInt("xp"))
                if (p.has("coins")) updateSetting(PreferencesManager.COINS.name, p.getInt("coins"))
                if (p.has("streak")) updateSetting(PreferencesManager.STREAK.name, p.getInt("streak"))
                if (p.has("bestScore")) updateSetting(PreferencesManager.BEST_SCORE.name, p.getInt("bestScore"))
            }

            if (root.has("settings")) {
                val s = root.getJSONObject("settings")
                if (s.has("themePreset")) updateSetting(PreferencesManager.THEME_PRESET.name, s.getString("themePreset"))
                if (s.has("primaryColor")) updateSetting(PreferencesManager.PRIMARY_COLOR.name, s.getString("primaryColor"))
                if (s.has("cardGlowColor")) updateSetting(PreferencesManager.CARD_GLOW_COLOR.name, s.getString("cardGlowColor"))
                if (s.has("glassOpacity")) updateSetting(PreferencesManager.GLASS_OPACITY.name, s.getDouble("glassOpacity").toFloat())
                if (s.has("cardCornerRadius")) updateSetting(PreferencesManager.CARD_CORNER_RADIUS.name, s.getDouble("cardCornerRadius").toFloat())
                if (s.has("borderThickness")) updateSetting(PreferencesManager.BORDER_THICKNESS.name, s.getDouble("borderThickness").toFloat())
                if (s.has("glowIntensity")) updateSetting(PreferencesManager.GLOW_INTENSITY.name, s.getDouble("glowIntensity").toFloat())
                if (s.has("borderAnimStyle")) updateSetting(PreferencesManager.BORDER_STYLE.name, s.getString("borderAnimStyle"))
                if (s.has("backgroundGrid")) updateSetting(PreferencesManager.BG_GRID.name, s.getBoolean("backgroundGrid"))
                if (s.has("scanlineCrtEffect")) updateSetting(PreferencesManager.SCANLINE_CRT.name, s.getBoolean("scanlineCrtEffect"))
                if (s.has("countdownDuration")) updateSetting(PreferencesManager.COUNTDOWN_DURATION.name, s.getInt("countdownDuration"))
                if (s.has("soundVolume")) updateSetting(PreferencesManager.SOUND_VOLUME.name, s.getDouble("soundVolume").toFloat())
                if (s.has("soundPack")) updateSetting(PreferencesManager.SOUND_PACK.name, s.getString("soundPack"))
                if (s.has("masterSoundFx")) updateSetting(PreferencesManager.MASTER_SOUND_FX.name, s.getBoolean("masterSoundFx"))
                if (s.has("hapticFeedback")) updateSetting(PreferencesManager.HAPTIC_FEEDBACK.name, s.getBoolean("hapticFeedback"))
            }

            if (root.has("targetGoals")) {
                val array = root.getJSONArray("targetGoals")
                val restoredGoals = mutableListOf<UserTargetGoal>()
                for (i in 0 until array.length()) {
                    val g = array.getJSONObject(i)
                    restoredGoals.add(
                        UserTargetGoal(
                            id = if (g.has("id")) g.getLong("id") else System.currentTimeMillis() + i,
                            title = g.optString("title", "Goal"),
                            category = g.optString("category", "All"),
                            targetType = g.optString("targetType", "Drills"),
                            targetCount = g.optInt("targetCount", 5),
                            currentCount = g.optInt("currentCount", 0),
                            frequency = g.optString("frequency", "Daily"),
                            rewardCoins = g.optInt("rewardCoins", 50),
                            rewardXp = g.optInt("rewardXp", 100),
                            iconEmoji = g.optString("iconEmoji", "🎯"),
                            difficulty = g.optString("difficulty", "Normal"),
                            note = g.optString("note", ""),
                            isCompleted = g.optBoolean("isCompleted", false),
                            isRewardClaimed = g.optBoolean("isRewardClaimed", false)
                        )
                    )
                }
                if (restoredGoals.isNotEmpty()) {
                    _targetGoals.value = restoredGoals
                }
            }
            SoundManager.playSuccess()
            true
        } catch (_: Exception) {
            SoundManager.playError()
            false
        }
    }

    fun createRestoreCheckpoint() {
        val json = exportBackupJson()
        updateSetting(PreferencesManager.LOCAL_BACKUP_SNAPSHOT.name, json)
        SoundManager.playSuccess()
    }

    fun restoreFromCheckpoint(): Boolean {
        val snapshot = userPreferences.value.localBackupSnapshot
        return if (snapshot.isNotBlank()) {
            importBackupJson(snapshot)
        } else {
            false
        }
    }

    fun resetSettingsOnly() {
        viewModelScope.launch {
            prefsManager.resetSettingsOnly()
            SoundManager.playSuccess()
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            prefsManager.resetAllData()
            _targetGoals.value = listOf(
                UserTargetGoal(
                    id = 1L,
                    title = "Complete 5 Memory Drills",
                    category = "Memory",
                    targetType = "Drills",
                    targetCount = 5,
                    currentCount = 0,
                    frequency = "Daily",
                    iconEmoji = "🧠",
                    rewardCoins = 60,
                    rewardXp = 120
                ),
                UserTargetGoal(
                    id = 2L,
                    title = "Master 3 Logic Puzzles",
                    category = "Logic",
                    targetType = "Drills",
                    targetCount = 3,
                    currentCount = 0,
                    frequency = "Daily",
                    iconEmoji = "🧩",
                    rewardCoins = 80,
                    rewardXp = 150
                )
            )
        }
    }

    // Unlimited Progressive & Dynamic Achievements Engine
    val achievements: StateFlow<List<AchievementItem>> = combine(
        userPreferences,
        totalGamesPlayed,
        gameScores,
        targetGoals
    ) { prefs, playedCount, scores, goals ->
        AchievementEngine.generateUnlimitedAchievements(
            prefs = prefs,
            totalDrillsPlayed = playedCount,
            scores = scores,
            targetGoals = goals
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
