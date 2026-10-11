package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.GameItem
import com.example.presentation.games.AnagramBlitzGame
import com.example.presentation.games.ChangeBlindnessGame
import com.example.presentation.games.ChronoTapGame
import com.example.presentation.games.CipherShiftGame
import com.example.presentation.games.ColorFloodGame
import com.example.presentation.games.ConstellationConnectGame
import com.example.presentation.games.CyberStackerGame
import com.example.presentation.games.FocusFlowGame
import com.example.presentation.games.MathMatrixGame
import com.example.presentation.games.NumberGridRushGame
import com.example.presentation.games.OddOneOutGame
import com.example.presentation.games.QuickSortGame
import com.example.presentation.games.SilhouetteMatchGame
import com.example.presentation.games.TargetAimGame
import com.example.presentation.games.ToneMemoryGame
import com.example.presentation.games.TypingBlitzGame
import com.example.presentation.games.WordMorphGame
import com.example.presentation.games.ZenBalanceGame
import com.example.presentation.games.CardFlipperGame
import com.example.presentation.games.CodeBreakerGame
import com.example.presentation.games.DualNBackGame
import com.example.presentation.games.GameResult
import com.example.presentation.games.MemoryGridGame
import com.example.presentation.games.NumberSequenceGame
import com.example.presentation.games.PatternMatchGame
import com.example.presentation.games.ReactionTimeGame
import com.example.presentation.games.SpatialSpinGame
import com.example.presentation.games.SpeedMathGame
import com.example.presentation.games.StroopEffectGame
import com.example.presentation.games.TrailBlazerGame
import com.example.presentation.viewmodel.MainViewModel
import com.example.ui.audio.SoundManager
import com.example.ui.components.CyberButton
import com.example.ui.components.FuturisticCard
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class GameState {
    START, COUNTDOWN, PLAYING, REPORT
}

@Composable
fun GamePlayerScreen(
    game: GameItem,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prefs by viewModel.userPreferences.collectAsState()
    var currentState by remember(game.id) { mutableStateOf(GameState.START) }
    var selectedDifficulty by remember(game.id, prefs.defaultDifficulty) { mutableStateOf(prefs.defaultDifficulty) }
    var selectedGameMode by remember(game.id, prefs.gameModeType) { mutableStateOf(prefs.gameModeType) }
    var selectedRoundsCount by remember(game.id, prefs.gameRoundsCount) { mutableIntStateOf(prefs.gameRoundsCount) }
    var finalResult by remember(game.id) { mutableStateOf<GameResult?>(null) }
    var isGameOverHandled by remember(game.id) { mutableStateOf(false) }
    var countdownValue by remember(game.id, prefs.countdownDuration) { mutableIntStateOf(prefs.countdownDuration) }
    var playSessionId by remember(game.id) { mutableIntStateOf(0) }

    val handleGameOver: (GameResult) -> Unit = { res ->
        if (!isGameOverHandled && currentState == GameState.PLAYING) {
            isGameOverHandled = true
            finalResult = res
            viewModel.recordGameFinished(game.id, game.title, res.score, res.xpEarned, res.coinsEarned)
            currentState = GameState.REPORT
        }
    }

    val primaryCyberColor = com.example.parseCyberColor(prefs.primaryColor, NeonCyan)

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 20.dp, bottom = 14.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Sleek Floating Back Button & Prominent Game Name Header
            // Positioned safely with statusBarsPadding + top spacing so it never collides with status bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF09111D).copy(alpha = 0.9f))
                            .border(1.2.dp, primaryCyberColor.copy(alpha = 0.5f), CircleShape)
                            .clickable {
                                SoundManager.playClick()
                                onBack()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Exit Drill",
                            tint = primaryCyberColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            text = game.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${game.category} • $selectedDifficulty",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryCyberColor
                        )
                    }
                }

                // In-game subtle status tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF070E1A).copy(alpha = 0.75f))
                        .border(1.dp, primaryCyberColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "100% Offline",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                }
            }

            when (currentState) {
                // ==========================================
                // STAGE 1: BRIEFING & PRE-GAME
                // ==========================================
                GameState.START -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 76.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Hero Drill Briefing Card
                            FuturisticCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = primaryCyberColor,
                                glowColor = primaryCyberColor,
                                glowIntensity = 0.8f,
                                themePreset = prefs.themePreset,
                                glassOpacity = prefs.glassOpacity,
                                cornerRadius = prefs.cardCornerRadius.dp,
                                onClick = {
                                    SoundManager.playClick()
                                    if (prefs.countdownDuration > 0) {
                                        currentState = GameState.COUNTDOWN
                                    } else {
                                        currentState = GameState.PLAYING
                                    }
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 18.dp, horizontal = 14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(text = game.iconEmoji, fontSize = 58.sp)

                                    Text(
                                        text = game.title,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(primaryCyberColor.copy(alpha = 0.15f))
                                                .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = game.category,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryCyberColor
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(NeonPurple.copy(alpha = 0.15f))
                                                .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "$selectedDifficulty Protocol",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NeonPurple
                                            )
                                        }
                                    }

                                    Text(
                                        text = game.description,
                                        fontSize = 14.sp,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 20.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(NeonGreen.copy(alpha = 0.12f))
                                            .border(1.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                            .padding(horizontal = 14.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = if (prefs.countdownDuration == 0) "⚡ Instant Launch Active • Zero Delay" else "⚡ Countdown: ${prefs.countdownDuration}s Enabled",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NeonGreen
                                        )
                                    }
                                }
                            }

                            // Difficulty Calibration Card
                            FuturisticCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = primaryCyberColor.copy(alpha = 0.4f),
                                themePreset = prefs.themePreset,
                                glassOpacity = prefs.glassOpacity,
                                cornerRadius = prefs.cardCornerRadius.dp
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "Difficulty Calibration",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryCyberColor
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf("Easy", "Medium", "Hard").forEach { diff ->
                                            val isSel = selectedDifficulty == diff
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        if (isSel) primaryCyberColor.copy(alpha = 0.2f)
                                                        else Color(0xFF090E17)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isSel) primaryCyberColor else Color(0x3300F0FF),
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    .clickable {
                                                        SoundManager.playClick()
                                                        selectedDifficulty = diff
                                                    }
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = diff,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSel) primaryCyberColor else TextSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Game Mode Calibration Card (Rounds vs Unlimited)
                            FuturisticCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = primaryCyberColor.copy(alpha = 0.4f),
                                themePreset = prefs.themePreset,
                                glassOpacity = prefs.glassOpacity,
                                cornerRadius = prefs.cardCornerRadius.dp
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "Game Mode Calibration",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryCyberColor
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf(
                                            "Rounds" to "🎯 Rounds Mode",
                                            "Unlimited" to "♾️ Unlimited Mode"
                                        ).forEach { (mKey, mLbl) ->
                                            val isSel = selectedGameMode == mKey
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        if (isSel) primaryCyberColor.copy(alpha = 0.2f)
                                                        else Color(0xFF090E17)
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isSel) primaryCyberColor else Color(0x3300F0FF),
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    .clickable {
                                                        SoundManager.playClick()
                                                        selectedGameMode = mKey
                                                    }
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = mLbl,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSel) primaryCyberColor else TextSecondary
                                                )
                                            }
                                        }
                                    }

                                    if (selectedGameMode == "Rounds") {
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = "Target Rounds: $selectedRoundsCount",
                                                fontSize = 12.sp,
                                                color = TextSecondary,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                listOf(5, 10, 15, 20).forEach { rCount ->
                                                    val isSel = selectedRoundsCount == rCount
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(
                                                                if (isSel) primaryCyberColor.copy(alpha = 0.25f)
                                                                else Color(0xFF060B12)
                                                            )
                                                            .border(
                                                                1.dp,
                                                                if (isSel) primaryCyberColor else Color(0x2200F0FF),
                                                                RoundedCornerShape(8.dp)
                                                            )
                                                            .clickable {
                                                                SoundManager.playClick()
                                                                selectedRoundsCount = rCount
                                                            }
                                                            .padding(vertical = 8.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = "$rCount",
                                                            fontSize = 12.sp,
                                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                                            color = if (isSel) primaryCyberColor else TextSecondary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = "Endless survival mode: play as long as you want, accumulating streak and high score!",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // Launch Drill Action Button
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color(0xFF04070D).copy(alpha = 0.95f))
                                    )
                                )
                                .padding(vertical = 8.dp)
                        ) {
                            CyberButton(
                                text = "🚀 START DRILL NOW",
                                onClick = {
                                    SoundManager.playClick()
                                    if (prefs.countdownDuration > 0) {
                                        currentState = GameState.COUNTDOWN
                                    } else {
                                        // Default: 0s / Off -> Instant play!
                                        currentState = GameState.PLAYING
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                backgroundColor = primaryCyberColor,
                                textColor = Color(0xFF04070D)
                            )
                        }
                    }
                }

                // ==========================================
                // STAGE 1.5: CUSTOMIZABLE COUNTDOWN (SKIPPED IF OFF/0s)
                // ==========================================
                GameState.COUNTDOWN -> {
                    val initialSeconds = prefs.countdownDuration.coerceAtLeast(1)

                    LaunchedEffect(Unit) {
                        countdownValue = initialSeconds
                        for (sec in initialSeconds downTo 1) {
                            countdownValue = sec
                            SoundManager.playClick()
                            delay(1000)
                        }
                        SoundManager.playSuccess()
                        currentState = GameState.PLAYING
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "CALIBRATING CORTEX",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 2.sp
                            )

                            Text(
                                text = "$countdownValue",
                                fontSize = 90.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = primaryCyberColor
                            )

                            Text(
                                text = "Focus your visual attention...",
                                fontSize = 14.sp,
                                color = NeonGreen
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F1826))
                                    .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        SoundManager.playClick()
                                        currentState = GameState.PLAYING
                                    }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Skip Countdown ⏭️",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryCyberColor
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // STAGE 2: ACTIVE GAMEPLAY
                // ==========================================
                GameState.PLAYING -> {
                    Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                        key(game.id, playSessionId) {
                            when (game.id) {
                            "memory_grid" -> MemoryGridGame(
                                difficulty = selectedDifficulty,
                                customGridSize = 4,
                                onGameOver = handleGameOver
                            )

                            "card_flipper" -> CardFlipperGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "trail_blazer" -> TrailBlazerGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "dual_n_back" -> DualNBackGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "code_breaker" -> CodeBreakerGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "number_sequence" -> NumberSequenceGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "spatial_spin" -> SpatialSpinGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "pattern_match" -> PatternMatchGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "speed_math" -> SpeedMathGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "color_clash" -> StroopEffectGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "reaction_time" -> ReactionTimeGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "focus_flow" -> FocusFlowGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "odd_one_out" -> OddOneOutGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "anagram_blitz" -> AnagramBlitzGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "chrono_tap" -> ChronoTapGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "word_morph" -> WordMorphGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "math_matrix" -> MathMatrixGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "target_aim" -> TargetAimGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "tone_memory" -> ToneMemoryGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "cipher_shift" -> CipherShiftGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "silhouette_match" -> SilhouetteMatchGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "zen_balance" -> ZenBalanceGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "quick_sort" -> QuickSortGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "cyber_stacker" -> CyberStackerGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "change_blindness" -> ChangeBlindnessGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "constellation_connect" -> ConstellationConnectGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "color_flood" -> ColorFloodGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "typing_blitz" -> TypingBlitzGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            "number_grid_rush" -> NumberGridRushGame(
                                difficulty = selectedDifficulty,
                                onGameOver = handleGameOver
                            )

                            else -> MemoryGridGame(
                                difficulty = selectedDifficulty,
                                customGridSize = 4,
                                onGameOver = handleGameOver
                            )
                        }
                    }
                }
                }

                // ==========================================
                // STAGE 3: CELEBRATION & PERFORMANCE REPORT
                // ==========================================
                GameState.REPORT -> {
                    val res = finalResult ?: GameResult(score = 250, roundsCompleted = 3, xpEarned = 50, coinsEarned = 15)

                    LaunchedEffect(Unit) {
                        SoundManager.playVictory()
                    }

                    Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                        CelebrationParticles()

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(bottom = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Celebration,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(54.dp)
                            )

                            Text(
                                text = "DRILL COMPLETE!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = primaryCyberColor,
                                letterSpacing = 1.sp
                            )

                            // Score Summary Card
                            FuturisticCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = primaryCyberColor,
                                glowColor = NeonGreen,
                                themePreset = prefs.themePreset,
                                glassOpacity = prefs.glassOpacity,
                                cornerRadius = prefs.cardCornerRadius.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "Final Neural Score",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "${res.score}",
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(NeonGreen.copy(alpha = 0.15f))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "+${res.xpEarned} XP",
                                                color = NeonGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "+${res.coinsEarned} 🪙 Credits",
                                                color = Color(0xFFFFD700),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // Metrics Breakdown Card
                            FuturisticCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = Color(0x3300F0FF),
                                themePreset = prefs.themePreset,
                                glassOpacity = prefs.glassOpacity,
                                cornerRadius = prefs.cardCornerRadius.dp
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Cognitive Metrics",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryCyberColor
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Accuracy", fontSize = 13.sp, color = TextSecondary)
                                        Text("${res.accuracy.toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Avg Reaction Speed", fontSize = 13.sp, color = TextSecondary)
                                        Text("${res.avgReactionMs} ms", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Rounds Completed", fontSize = 13.sp, color = TextSecondary)
                                        Text("${res.roundsCompleted}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                }
                            }

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CyberButton(
                                    text = "🔄 Play Again",
                                    onClick = {
                                        SoundManager.playClick()
                                        isGameOverHandled = false
                                        finalResult = null
                                        playSessionId++
                                        if (prefs.countdownDuration > 0) {
                                            currentState = GameState.COUNTDOWN
                                        } else {
                                            currentState = GameState.PLAYING
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                CyberButton(
                                    text = "Done",
                                    onClick = {
                                        SoundManager.playClick()
                                        onBack()
                                    },
                                    backgroundColor = primaryCyberColor,
                                    textColor = Color(0xFF04070D),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CelebrationParticles() {
    val particles = remember {
        List(35) {
            Offset(
                x = Random.nextFloat(),
                y = Random.nextFloat()
            ) to Color(
                red = Random.nextFloat(),
                green = 0.8f + Random.nextFloat() * 0.2f,
                blue = 0.8f + Random.nextFloat() * 0.2f,
                alpha = 0.7f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { (offset, color) ->
            drawCircle(
                color = color,
                radius = 3.dp.toPx(),
                center = Offset(offset.x * size.width, offset.y * size.height)
            )
        }
    }
}
