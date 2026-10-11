package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore by preferencesDataStore(name = "brine_booster_prefs")

data class UserPreferences(
    // Initialized to 0 for a clean fresh start as requested
    val coins: Int = 0,
    val xp: Int = 0,
    val streak: Int = 0,
    val bestScore: Int = 0,
    val userName: String = "Cyber Neuralist",
    val userAvatar: String = "🧠 Neural Core",
    val userRankTitle: String = "Operative",
    val language: String = "English (US)",

    // Theme Engine Presets
    val themePreset: String = "Cyber Void Dark",
    val glassOpacity: Float = 85f, // 0% to 100%
    val glassBlurIntensity: Float = 70f, // 0% to 100%
    val cardCornerRadius: Float = 18f, // 0dp to 28dp
    val primaryColor: String = "#00F0FF",
    val cardGlowColor: String = "#00FF88",
    val backgroundGrid: Boolean = true,
    val gridLineIntensity: Float = 16f,
    val scanlineCrtEffect: Boolean = false,

    // UI Scale / App DP Multiplier (1% to 500%)
    val uiScalePercent: Float = 100f, // 1% to 500% dp scaling
    val fontScalePercent: Float = 100f, // 50% to 200% font size scaling
    val cardSizeScalePercent: Float = 100f, // 60% to 200% card size scaling
    val appFontFamily: String = "Sans-Serif Standard", // 30+ font choices

    // Animation Engine
    val enableAllAnimations: Boolean = true,
    val animationSpeedScale: Float = 1.0f,
    val motionBounces: Boolean = true,
    val borderAnimStyle: String = "Sweep",
    val borderBrightness: Float = 85f,
    val borderThickness: Float = 2.0f,
    val borderAnimSpeed: Float = 45f,
    val cardGlowEffect: Boolean = true,
    val glowIntensity: Float = 60f,

    // Rotating Card & 3D Atmospheric Animation System
    val cardRotatingGlowEnabled: Boolean = true,
    // 4 Modern Animation Types: "Rotating 3D Aura", "Floating 3D Levitation", "Neon Pulse", "Holographic Wave"
    val cardAnimationType: String = "Rotating 3D Aura",
    val cardGlowPreset: String = "Cyber Glow",
    val cardGlowSpeed: Float = 3.5f,
    val cardGlowThickness: Float = 2.0f,
    val cardGlowAura: Float = 75f,
    // User requested: ONLY Top / Above Card remains
    val cardGlowLayer: String = "Above Card",
    val cardGlowEffectStyle: String = "Cyber Glow",
    val cardAnimationScale: Float = 1.15f,
    val card3dDepth: Float = 14f,
    val cardCustomColor: String = "#00F0FF",
    val cardColorMode: String = "Preset Palette",
    val cardAuraSize: String = "MD",
    val cardAuraDuration: Int = 2000,
    val cardAuraBgColor: String = "",
    val smoothPerformanceMode: Boolean = false,

    // FPS & Smooth Engine (30, 60, 90, 120, 144, 160 FPS)
    val fpsTarget: Int = 60,
    val fpsPreset: String = "Standard (60 FPS)", // "Smooth (30 FPS)", "Standard (60 FPS)", "Ultra (90 FPS)", "Ultra Max (120 FPS)", "Ultra Pro (144 FPS)", "Ultra Pro Max (160 FPS)"
    val graphicsQuality: String = "Standard", // "Smooth", "Standard", "Ultra", "Ultra Max", "Ultra Pro"

    // Countdown Configuration (Default is 0 = OFF!)
    val countdownDuration: Int = 0,

    // Audio & Haptics Engine
    val masterSoundFx: Boolean = true,
    val soundEffects: Boolean = true,
    val soundVolume: Float = 100f,
    val bgMusicEnabled: Boolean = false,
    val bgMusicVolume: Float = 70f,
    val bgMusicTrack: String = "963Hz Solfeggio Cyber Zen", // "963Hz Solfeggio Cyber Zen", "Sports Rock Energizer", "Sci-Fi UI Ambient", "Neo Synthwave Groove"
    val tapSoundEnabled: Boolean = true,
    val uiClickSounds: Boolean = true,
    val tapSoundVolume: Float = 100f,
    val soundPack: String = "Sci-Fi UI", // "Sci-Fi UI", "Cyber Arcade", "Neo Synthwave", "Crystal Zen"
    val victoryFanfare: Boolean = true,
    val gameOverBuzzer: Boolean = true,
    val hapticFeedback: Boolean = true,
    val hapticStrength: String = "Medium",

    // Gameplay Mode Options (Round-based vs Unlimited)
    val gameModeType: String = "Rounds", // "Rounds", "Unlimited"
    val gameRoundsCount: Int = 5, // 5, 10, 15, 20
    val defaultDifficulty: String = "Medium",
    val gameTimerDuration: Int = 45,
    val autoSubmitAnswer: Boolean = true,
    val showHints: Boolean = true,
    val instantRetry: Boolean = true,

    // Settings Cards Visibility
    val showThemeCard: Boolean = true,
    val showCountdownCard: Boolean = true,
    val showAnimationCard: Boolean = true,
    val showColorCard: Boolean = true,
    val showAudioCard: Boolean = true,
    val showBackupCard: Boolean = true,
    val showIdentityCard: Boolean = true,
    val showEdgeBarCard: Boolean = true,
    val showCardGlowCard: Boolean = true,
    val showFpsCard: Boolean = true,
    val showTypographyCard: Boolean = true,

    // Edge Bar Customization
    val edgeBarColor: String = "#00F0FF",
    val edgeBarHeight: Float = 62f,
    val edgeBarCornerRadius: Float = 28f,
    val edgeBarBottomOffset: Float = 10f,
    val edgeBarHorizontalMargin: Float = 16f,
    val edgeBarGlassOpacity: Float = 90f,
    val edgeBarAutoHideOnScroll: Boolean = true,
    val topBarAutoHideOnScroll: Boolean = true,

    // Local Snapshot Backup
    val localBackupSnapshot: String = ""
)

class PreferencesManager(private val context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        val COINS = intPreferencesKey("coins")
        val XP = intPreferencesKey("xp")
        val STREAK = intPreferencesKey("streak")
        val BEST_SCORE = intPreferencesKey("best_score")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AVATAR = stringPreferencesKey("user_avatar")
        val USER_RANK_TITLE = stringPreferencesKey("user_rank_title")
        val LANGUAGE = stringPreferencesKey("language")

        val THEME_PRESET = stringPreferencesKey("theme_preset")
        val GLASS_OPACITY = floatPreferencesKey("glass_opacity")
        val GLASS_BLUR = floatPreferencesKey("glass_blur")
        val CARD_CORNER_RADIUS = floatPreferencesKey("card_corner_radius")
        val PRIMARY_COLOR = stringPreferencesKey("primary_color")
        val CARD_GLOW_COLOR = stringPreferencesKey("card_glow_color")
        val BG_GRID = booleanPreferencesKey("bg_grid")
        val GRID_INTENSITY = floatPreferencesKey("grid_intensity")
        val SCANLINE_CRT = booleanPreferencesKey("scanline_crt")

        val UI_SCALE_PERCENT = floatPreferencesKey("ui_scale_percent")
        val FONT_SCALE_PERCENT = floatPreferencesKey("font_scale_percent")
        val CARD_SIZE_SCALE_PERCENT = floatPreferencesKey("card_size_scale_percent")
        val APP_FONT_FAMILY = stringPreferencesKey("app_font_family")

        val ENABLE_ALL_ANIM = booleanPreferencesKey("enable_all_anim")
        val ANIMATION_SPEED_SCALE = floatPreferencesKey("animation_speed_scale")
        val MOTION_BOUNCES = booleanPreferencesKey("motion_bounces")
        val BORDER_STYLE = stringPreferencesKey("border_style")
        val BORDER_BRIGHTNESS = floatPreferencesKey("border_brightness")
        val BORDER_THICKNESS = floatPreferencesKey("border_thickness")
        val BORDER_SPEED = floatPreferencesKey("border_speed")
        val GLOW_EFFECT = booleanPreferencesKey("glow_effect")
        val GLOW_INTENSITY = floatPreferencesKey("glow_intensity")

        val CARD_ROTATING_GLOW_ENABLED = booleanPreferencesKey("card_rotating_glow_enabled")
        val CARD_ANIMATION_TYPE = stringPreferencesKey("card_animation_type")
        val CARD_GLOW_PRESET = stringPreferencesKey("card_glow_preset")
        val CARD_GLOW_SPEED = floatPreferencesKey("card_glow_speed")
        val CARD_GLOW_THICKNESS = floatPreferencesKey("card_glow_thickness")
        val CARD_GLOW_AURA = floatPreferencesKey("card_glow_aura")
        val CARD_GLOW_LAYER = stringPreferencesKey("card_glow_layer")
        val CARD_GLOW_EFFECT_STYLE = stringPreferencesKey("card_glow_effect_style")
        val CARD_ANIMATION_SCALE = floatPreferencesKey("card_anim_scale")
        val CARD_3D_DEPTH = floatPreferencesKey("card_3d_depth")
        val CARD_CUSTOM_COLOR = stringPreferencesKey("card_custom_color")
        val CARD_COLOR_MODE = stringPreferencesKey("card_color_mode")
        val CARD_AURA_SIZE = stringPreferencesKey("card_aura_size")
        val CARD_AURA_DURATION = intPreferencesKey("card_aura_duration")
        val CARD_AURA_BG_COLOR = stringPreferencesKey("card_aura_bg_color")
        val SMOOTH_PERF_MODE = booleanPreferencesKey("smooth_perf_mode")

        val FPS_TARGET = intPreferencesKey("fps_target")
        val FPS_PRESET = stringPreferencesKey("fps_preset")
        val GRAPHICS_QUALITY = stringPreferencesKey("graphics_quality")

        val COUNTDOWN_DURATION = intPreferencesKey("countdown_duration")

        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val MASTER_SOUND_FX = booleanPreferencesKey("master_sound_fx")
        val SOUND_VOLUME = floatPreferencesKey("sound_volume")
        val BG_MUSIC_ENABLED = booleanPreferencesKey("bg_music_enabled")
        val BG_MUSIC_VOLUME = floatPreferencesKey("bg_music_volume")
        val BG_MUSIC_TRACK = stringPreferencesKey("bg_music_track")
        val TAP_SOUND_ENABLED = booleanPreferencesKey("tap_sound_enabled")
        val UI_CLICK_SOUNDS = booleanPreferencesKey("ui_click_sounds")
        val TAP_SOUND_VOLUME = floatPreferencesKey("tap_sound_volume")
        val SOUND_PACK = stringPreferencesKey("sound_pack")
        val VICTORY_FANFARE = booleanPreferencesKey("victory_fanfare")
        val GAME_OVER_BUZZER = booleanPreferencesKey("game_over_buzzer")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val HAPTIC_STRENGTH = stringPreferencesKey("haptic_strength")

        val GAME_MODE_TYPE = stringPreferencesKey("game_mode_type")
        val GAME_ROUNDS_COUNT = intPreferencesKey("game_rounds_count")
        val DEFAULT_DIFFICULTY = stringPreferencesKey("default_difficulty")
        val GAME_TIMER_DURATION = intPreferencesKey("game_timer_duration")
        val AUTO_SUBMIT_ANSWER = booleanPreferencesKey("auto_submit_answer")
        val SHOW_HINTS = booleanPreferencesKey("show_hints")
        val INSTANT_RETRY = booleanPreferencesKey("instant_retry")

        val SHOW_THEME_CARD = booleanPreferencesKey("show_theme_card")
        val SHOW_COUNTDOWN_CARD = booleanPreferencesKey("show_countdown_card")
        val SHOW_ANIMATION_CARD = booleanPreferencesKey("show_animation_card")
        val SHOW_COLOR_CARD = booleanPreferencesKey("show_color_card")
        val SHOW_AUDIO_CARD = booleanPreferencesKey("show_audio_card")
        val SHOW_BACKUP_CARD = booleanPreferencesKey("show_backup_card")
        val SHOW_IDENTITY_CARD = booleanPreferencesKey("show_identity_card")
        val SHOW_EDGE_BAR_CARD = booleanPreferencesKey("show_edge_bar_card")
        val SHOW_CARD_GLOW_CARD = booleanPreferencesKey("show_card_glow_card")
        val SHOW_FPS_CARD = booleanPreferencesKey("show_fps_card")
        val SHOW_TYPOGRAPHY_CARD = booleanPreferencesKey("show_typography_card")

        val EDGE_BAR_COLOR = stringPreferencesKey("edge_bar_color")
        val EDGE_BAR_HEIGHT = floatPreferencesKey("edge_bar_height")
        val EDGE_BAR_CORNER_RADIUS = floatPreferencesKey("edge_bar_corner_radius")
        val EDGE_BAR_BOTTOM_OFFSET = floatPreferencesKey("edge_bar_bottom_offset")
        val EDGE_BAR_HORIZ_MARGIN = floatPreferencesKey("edge_bar_horiz_margin")
        val EDGE_BAR_GLASS_OPACITY = floatPreferencesKey("edge_bar_glass_opacity")
        val EDGE_BAR_AUTO_HIDE = booleanPreferencesKey("edge_bar_auto_hide")
        val TOP_BAR_AUTO_HIDE = booleanPreferencesKey("top_bar_auto_hide")

        val LOCAL_BACKUP_SNAPSHOT = stringPreferencesKey("local_backup_snapshot")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            UserPreferences(
                coins = prefs[COINS] ?: 0,
                xp = prefs[XP] ?: 0,
                streak = prefs[STREAK] ?: 0,
                bestScore = prefs[BEST_SCORE] ?: 0,
                userName = prefs[USER_NAME] ?: "Cyber Neuralist",
                userAvatar = prefs[USER_AVATAR] ?: "🧠 Neural Core",
                userRankTitle = prefs[USER_RANK_TITLE] ?: "Operative",
                language = "English (US)",
                themePreset = prefs[THEME_PRESET] ?: "Cyber Void Dark",
                glassOpacity = prefs[GLASS_OPACITY] ?: 85f,
                glassBlurIntensity = prefs[GLASS_BLUR] ?: 70f,
                cardCornerRadius = prefs[CARD_CORNER_RADIUS] ?: 18f,
                primaryColor = prefs[PRIMARY_COLOR] ?: "#00F0FF",
                cardGlowColor = prefs[CARD_GLOW_COLOR] ?: "#00FF88",
                backgroundGrid = prefs[BG_GRID] ?: true,
                gridLineIntensity = prefs[GRID_INTENSITY] ?: 16f,
                scanlineCrtEffect = prefs[SCANLINE_CRT] ?: false,
                uiScalePercent = prefs[UI_SCALE_PERCENT] ?: 100f,
                fontScalePercent = prefs[FONT_SCALE_PERCENT] ?: 100f,
                cardSizeScalePercent = prefs[CARD_SIZE_SCALE_PERCENT] ?: 100f,
                appFontFamily = prefs[APP_FONT_FAMILY] ?: "Sans-Serif Standard",
                enableAllAnimations = prefs[ENABLE_ALL_ANIM] ?: true,
                animationSpeedScale = prefs[ANIMATION_SPEED_SCALE] ?: 1.0f,
                motionBounces = prefs[MOTION_BOUNCES] ?: true,
                borderAnimStyle = prefs[BORDER_STYLE] ?: "Sweep",
                borderBrightness = prefs[BORDER_BRIGHTNESS] ?: 85f,
                borderThickness = prefs[BORDER_THICKNESS] ?: 2.0f,
                borderAnimSpeed = prefs[BORDER_SPEED] ?: 45f,
                cardGlowEffect = prefs[GLOW_EFFECT] ?: true,
                glowIntensity = prefs[GLOW_INTENSITY] ?: 60f,
                cardRotatingGlowEnabled = prefs[CARD_ROTATING_GLOW_ENABLED] ?: true,
                cardAnimationType = prefs[CARD_ANIMATION_TYPE] ?: "Rotating 3D Aura",
                cardGlowPreset = prefs[CARD_GLOW_PRESET] ?: "Cyber Glow",
                cardGlowSpeed = prefs[CARD_GLOW_SPEED] ?: 3.5f,
                cardGlowThickness = prefs[CARD_GLOW_THICKNESS] ?: 2.0f,
                cardGlowAura = prefs[CARD_GLOW_AURA] ?: 75f,
                cardGlowLayer = prefs[CARD_GLOW_LAYER] ?: "Above Card",
                cardGlowEffectStyle = prefs[CARD_GLOW_EFFECT_STYLE] ?: "Cyber Glow",
                cardAnimationScale = prefs[CARD_ANIMATION_SCALE] ?: 1.15f,
                card3dDepth = prefs[CARD_3D_DEPTH] ?: 14f,
                cardCustomColor = prefs[CARD_CUSTOM_COLOR] ?: "#00F0FF",
                cardColorMode = prefs[CARD_COLOR_MODE] ?: "Preset Palette",
                cardAuraSize = prefs[CARD_AURA_SIZE] ?: "MD",
                cardAuraDuration = prefs[CARD_AURA_DURATION] ?: 2000,
                cardAuraBgColor = prefs[CARD_AURA_BG_COLOR] ?: "",
                smoothPerformanceMode = prefs[SMOOTH_PERF_MODE] ?: false,
                fpsTarget = prefs[FPS_TARGET] ?: 60,
                fpsPreset = prefs[FPS_PRESET] ?: "Standard (60 FPS)",
                graphicsQuality = prefs[GRAPHICS_QUALITY] ?: "Standard",
                countdownDuration = prefs[COUNTDOWN_DURATION] ?: 0,
                soundEffects = prefs[SOUND_EFFECTS] ?: true,
                masterSoundFx = prefs[MASTER_SOUND_FX] ?: true,
                soundVolume = prefs[SOUND_VOLUME] ?: 100f,
                bgMusicEnabled = prefs[BG_MUSIC_ENABLED] ?: false,
                bgMusicVolume = prefs[BG_MUSIC_VOLUME] ?: 70f,
                bgMusicTrack = prefs[BG_MUSIC_TRACK] ?: "963Hz Solfeggio Cyber Zen",
                tapSoundEnabled = prefs[TAP_SOUND_ENABLED] ?: true,
                uiClickSounds = prefs[UI_CLICK_SOUNDS] ?: prefs[TAP_SOUND_ENABLED] ?: true,
                tapSoundVolume = prefs[TAP_SOUND_VOLUME] ?: 100f,
                soundPack = prefs[SOUND_PACK] ?: "Sci-Fi UI",
                victoryFanfare = prefs[VICTORY_FANFARE] ?: true,
                gameOverBuzzer = prefs[GAME_OVER_BUZZER] ?: true,
                hapticFeedback = prefs[HAPTIC_FEEDBACK] ?: true,
                hapticStrength = prefs[HAPTIC_STRENGTH] ?: "Medium",
                gameModeType = prefs[GAME_MODE_TYPE] ?: "Rounds",
                gameRoundsCount = prefs[GAME_ROUNDS_COUNT] ?: 5,
                defaultDifficulty = prefs[DEFAULT_DIFFICULTY] ?: "Medium",
                gameTimerDuration = prefs[GAME_TIMER_DURATION] ?: 45,
                autoSubmitAnswer = prefs[AUTO_SUBMIT_ANSWER] ?: true,
                showHints = prefs[SHOW_HINTS] ?: true,
                instantRetry = prefs[INSTANT_RETRY] ?: true,
                showThemeCard = prefs[SHOW_THEME_CARD] ?: true,
                showCountdownCard = prefs[SHOW_COUNTDOWN_CARD] ?: true,
                showAnimationCard = prefs[SHOW_ANIMATION_CARD] ?: true,
                showColorCard = prefs[SHOW_COLOR_CARD] ?: true,
                showAudioCard = prefs[SHOW_AUDIO_CARD] ?: true,
                showBackupCard = prefs[SHOW_BACKUP_CARD] ?: true,
                showIdentityCard = prefs[SHOW_IDENTITY_CARD] ?: true,
                showEdgeBarCard = prefs[SHOW_EDGE_BAR_CARD] ?: true,
                showCardGlowCard = prefs[SHOW_CARD_GLOW_CARD] ?: true,
                showFpsCard = prefs[SHOW_FPS_CARD] ?: true,
                showTypographyCard = prefs[SHOW_TYPOGRAPHY_CARD] ?: true,
                edgeBarColor = prefs[EDGE_BAR_COLOR] ?: "#00F0FF",
                edgeBarHeight = prefs[EDGE_BAR_HEIGHT] ?: 62f,
                edgeBarCornerRadius = prefs[EDGE_BAR_CORNER_RADIUS] ?: 28f,
                edgeBarBottomOffset = prefs[EDGE_BAR_BOTTOM_OFFSET] ?: 10f,
                edgeBarHorizontalMargin = prefs[EDGE_BAR_HORIZ_MARGIN] ?: 16f,
                edgeBarGlassOpacity = prefs[EDGE_BAR_GLASS_OPACITY] ?: 90f,
                edgeBarAutoHideOnScroll = prefs[EDGE_BAR_AUTO_HIDE] ?: true,
                topBarAutoHideOnScroll = prefs[TOP_BAR_AUTO_HIDE] ?: true,
                localBackupSnapshot = prefs[LOCAL_BACKUP_SNAPSHOT] ?: ""
            )
        }

    suspend fun addRewards(coinsEarned: Int, xpEarned: Int, newScore: Int? = null) {
        context.dataStore.edit { prefs ->
            val curCoins = prefs[COINS] ?: 0
            val curXp = prefs[XP] ?: 0
            val curBest = prefs[BEST_SCORE] ?: 0
            prefs[COINS] = curCoins + coinsEarned
            prefs[XP] = curXp + xpEarned
            if (newScore != null && newScore > curBest) {
                prefs[BEST_SCORE] = newScore
            }
        }
    }

    suspend fun incrementStreak() {
        context.dataStore.edit { prefs ->
            val cur = prefs[STREAK] ?: 0
            prefs[STREAK] = cur + 1
        }
    }

    suspend fun updatePreference(key: String, value: Any) {
        context.dataStore.edit { prefs ->
            when (value) {
                is Boolean -> prefs[booleanPreferencesKey(key)] = value
                is Float -> prefs[floatPreferencesKey(key)] = value
                is Int -> prefs[intPreferencesKey(key)] = value
                is String -> prefs[stringPreferencesKey(key)] = value
            }
        }
    }

    suspend fun resetSettingsOnly() {
        context.dataStore.edit { prefs ->
            val curCoins = prefs[COINS] ?: 0
            val curXp = prefs[XP] ?: 0
            val curStreak = prefs[STREAK] ?: 0
            val curBest = prefs[BEST_SCORE] ?: 0
            val curName = prefs[USER_NAME] ?: "Cyber Neuralist"
            val curAvatar = prefs[USER_AVATAR] ?: "🧠 Neural Core"

            prefs.clear()

            prefs[COINS] = curCoins
            prefs[XP] = curXp
            prefs[STREAK] = curStreak
            prefs[BEST_SCORE] = curBest
            prefs[USER_NAME] = curName
            prefs[USER_AVATAR] = curAvatar
        }
    }

    suspend fun resetAllData() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
