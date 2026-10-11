package com.example.presentation.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

// Game Report payload passed upon game completion
data class GameResult(
    val score: Int,
    val roundsCompleted: Int = 1,
    val accuracy: Float = 100f, // 0 to 100
    val avgReactionMs: Long = 450L,
    val focusMetric: Float = 0.9f, // 0.0 to 1.0
    val speedMetric: Float = 0.85f, // 0.0 to 1.0
    val accuracyMetric: Float = 0.95f, // 0.0 to 1.0
    val xpEarned: Int = 50,
    val coinsEarned: Int = 15
)

// ==========================================
// 1. MEMORY GRID
// ==========================================
@Composable
fun MemoryGridGame(
    difficulty: String,
    customGridSize: Int = 4,
    onGameOver: (GameResult) -> Unit
) {
    val gridSize = when (difficulty) {
        "Easy" -> 3
        "Hard" -> 5
        else -> customGridSize.coerceIn(3, 5)
    }
    val totalTiles = gridSize * gridSize
    val initialTargets = when (gridSize) {
        3 -> 3
        5 -> 5
        else -> 4
    }

    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var isMemorizing by remember { mutableStateOf(true) }
    var targetCount by remember { mutableIntStateOf(initialTargets) }
    val targetTiles = remember { mutableStateListOf<Int>() }
    val selectedTiles = remember { mutableStateListOf<Int>() }
    val mistakeTiles = remember { mutableStateListOf<Int>() }

    var totalAttempts by remember { mutableIntStateOf(0) }
    var correctAttempts by remember { mutableIntStateOf(0) }
    val startTime = remember { System.currentTimeMillis() }

    fun startNewRound() {
        targetTiles.clear()
        selectedTiles.clear()
        mistakeTiles.clear()
        val randomIndices = (0 until totalTiles).shuffled().take(targetCount)
        targetTiles.addAll(randomIndices)
        isMemorizing = true
    }

    LaunchedEffect(round) {
        startNewRound()
    }

    LaunchedEffect(isMemorizing, round) {
        if (isMemorizing) {
            delay(2000)
            isMemorizing = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(
            modifier = Modifier.fillMaxWidth(),
            borderThickness = 1.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Round", fontSize = 11.sp, color = TextSecondary)
                    Text(text = "$round/8", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Text(
                    text = if (isMemorizing) "Memorize Pattern!" else "Repeat The Nodes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isMemorizing) NeonPurple else NeonCyan
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Lives", fontSize = 11.sp, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(lives) {
                            Text(text = "❤️", fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(if (gridSize == 3) 0.85f else 0.95f)
                    .aspectRatio(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (row in 0 until gridSize) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0 until gridSize) {
                            val index = row * gridSize + col
                            val isTarget = targetTiles.contains(index)
                            val isSelected = selectedTiles.contains(index)
                            val isMistake = mistakeTiles.contains(index)

                            val tileColor = when {
                                isMemorizing && isTarget -> NeonCyan
                                isSelected -> NeonGreen
                                isMistake -> Color(0xFFFF3366)
                                else -> Color(0xFF0E1624)
                            }

                            val borderColor = when {
                                isMemorizing && isTarget -> NeonCyan
                                isSelected -> NeonGreen
                                isMistake -> Color(0xFFFF3366)
                                else -> Color(0x3300F0FF)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(tileColor)
                                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                    .clickable(enabled = !isMemorizing && !isSelected && !isMistake) {
                                        totalAttempts++
                                        if (isTarget) {
                                            SoundManager.playSuccess()
                                            selectedTiles.add(index)
                                            correctAttempts++
                                            score += 100

                                            if (selectedTiles.size == targetTiles.size) {
                                                SoundManager.playVictory()
                                                if (round >= 8) {
                                                    val accuracy = if (totalAttempts > 0) (correctAttempts.toFloat() / totalAttempts) * 100f else 100f
                                                    val avgReaction = (System.currentTimeMillis() - startTime) / totalAttempts.coerceAtLeast(1)
                                                    onGameOver(
                                                        GameResult(
                                                            score = score,
                                                            roundsCompleted = round,
                                                            accuracy = accuracy,
                                                            avgReactionMs = avgReaction,
                                                            focusMetric = 0.95f,
                                                            speedMetric = 0.85f,
                                                            accuracyMetric = accuracy / 100f,
                                                            xpEarned = 75,
                                                            coinsEarned = 25
                                                        )
                                                    )
                                                } else {
                                                    round++
                                                    if (round % 2 == 0 && targetCount < totalTiles - 2) {
                                                        targetCount++
                                                    }
                                                }
                                            }
                                        } else {
                                            SoundManager.playError()
                                            mistakeTiles.add(index)
                                            lives--
                                            if (lives <= 0) {
                                                SoundManager.playBuzzer()
                                                val accuracy = if (totalAttempts > 0) (correctAttempts.toFloat() / totalAttempts) * 100f else 0f
                                                val avgReaction = (System.currentTimeMillis() - startTime) / totalAttempts.coerceAtLeast(1)
                                                onGameOver(
                                                    GameResult(
                                                        score = score,
                                                        roundsCompleted = round - 1,
                                                        accuracy = accuracy,
                                                        avgReactionMs = avgReaction,
                                                        focusMetric = (round / 8f).coerceIn(0.2f, 1f),
                                                        speedMetric = 0.7f,
                                                        accuracyMetric = (accuracy / 100f).coerceIn(0.1f, 1f),
                                                        xpEarned = 35,
                                                        coinsEarned = 10
                                                    )
                                                )
                                            }
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. CARD FLIPPER
// ==========================================
@Composable
fun CardFlipperGame(
    difficulty: String,
    onGameOver: (GameResult) -> Unit
) {
    val pairCount = when (difficulty) {
        "Easy" -> 6
        "Hard" -> 10
        else -> 8
    }

    val emojis = remember {
        listOf("⚡", "🧠", "🔥", "💎", "🌌", "👁", "🚀", "🪐", "🧬", "🎯")
    }

    val cards = remember(pairCount) {
        val selectedEmojis = emojis.shuffled().take(pairCount)
        (selectedEmojis + selectedEmojis).shuffled().mapIndexed { id, emoji ->
            CardItem(id = id, emoji = emoji)
        }.toMutableList()
    }

    var flippedIndices by remember { mutableStateOf<List<Int>>(emptyList()) }
    val matchedIndices = remember { mutableStateListOf<Int>() }
    var moves by remember { mutableIntStateOf(0) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var isCheckingMatch by remember { mutableStateOf(false) }

    LaunchedEffect(matchedIndices.size) {
        if (matchedIndices.size == cards.size && cards.isNotEmpty()) {
            delay(500)
            SoundManager.playVictory()
            val score = (1000 - moves * 20 - elapsedSeconds * 5).coerceAtLeast(100)
            onGameOver(
                GameResult(
                    score = score,
                    roundsCompleted = pairCount,
                    accuracy = ((pairCount.toFloat() / moves.coerceAtLeast(pairCount)) * 100f).coerceIn(10f, 100f),
                    avgReactionMs = (elapsedSeconds * 1000L) / moves.coerceAtLeast(1),
                    focusMetric = 0.9f,
                    speedMetric = (60f / elapsedSeconds.coerceAtLeast(15)).coerceIn(0.2f, 1.0f),
                    accuracyMetric = 0.85f,
                    xpEarned = 80,
                    coinsEarned = 30
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        while (matchedIndices.size < cards.size) {
            delay(1000)
            elapsedSeconds++
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Moves", fontSize = 11.sp, color = TextSecondary)
                    Text("$moves", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pairs Left", fontSize = 11.sp, color = TextSecondary)
                    Text("${(cards.size - matchedIndices.size) / 2}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Time", fontSize = 11.sp, color = TextSecondary)
                    Text("${elapsedSeconds}s", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cards.size) { index ->
                val card = cards[index]
                val isMatched = matchedIndices.contains(index)
                val isFlipped = flippedIndices.contains(index) || isMatched

                val rotation by animateFloatAsState(
                    targetValue = if (isFlipped) 180f else 0f,
                    animationSpec = tween(300),
                    label = "flip"
                )

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 12f * density
                        }
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isMatched) NeonGreen.copy(alpha = 0.25f) else if (isFlipped) Color(0xFF131D2C) else Color(0xFF0C1320))
                        .border(
                            1.dp,
                            if (isMatched) NeonGreen else if (isFlipped) NeonCyan else Color(0x3300F0FF),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable(enabled = !isFlipped && !isCheckingMatch && flippedIndices.size < 2) {
                            SoundManager.playClick()
                            if (flippedIndices.size == 1) {
                                val firstIndex = flippedIndices[0]
                                flippedIndices = listOf(firstIndex, index)
                                moves++
                                isCheckingMatch = true
                            } else if (flippedIndices.isEmpty()) {
                                flippedIndices = listOf(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (rotation >= 90f) {
                        Text(
                            text = card.emoji,
                            fontSize = 28.sp,
                            modifier = Modifier.graphicsLayer { rotationY = 180f }
                        )
                    } else {
                        Text("?", fontSize = 20.sp, color = TextSecondary)
                    }
                }
            }
        }

        LaunchedEffect(flippedIndices, isCheckingMatch) {
            if (flippedIndices.size == 2 && isCheckingMatch) {
                val idx1 = flippedIndices[0]
                val idx2 = flippedIndices[1]
                if (cards[idx1].emoji == cards[idx2].emoji) {
                    delay(380)
                    SoundManager.playSuccess()
                    matchedIndices.add(idx1)
                    matchedIndices.add(idx2)
                    flippedIndices = emptyList()
                    isCheckingMatch = false
                } else {
                    delay(700)
                    SoundManager.playError()
                    flippedIndices = emptyList()
                    isCheckingMatch = false
                }
            }
        }
    }
}

private data class CardItem(val id: Int, val emoji: String)

// ==========================================
// 3. TRAIL BLAZER
// ==========================================
@Composable
fun TrailBlazerGame(
    difficulty: String,
    onGameOver: (GameResult) -> Unit
) {
    val totalRounds = 6
    var currentRound by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var strikes by remember { mutableIntStateOf(0) }

    val sequence = remember { mutableStateListOf<Int>() }
    val userSequence = remember { mutableStateListOf<Int>() }
    var isPlayingSequence by remember { mutableStateOf(true) }
    var activeNodeIndex by remember { mutableStateOf<Int?>(null) }

    fun generateNextStep() {
        userSequence.clear()
        sequence.add(Random.nextInt(9))
        isPlayingSequence = true
    }

    LaunchedEffect(Unit) {
        repeat(3) { sequence.add(Random.nextInt(9)) }
        isPlayingSequence = true
    }

    LaunchedEffect(isPlayingSequence, sequence.size) {
        if (isPlayingSequence) {
            delay(500)
            sequence.forEach { node ->
                activeNodeIndex = node
                SoundManager.playTick()
                delay(400)
                activeNodeIndex = null
                delay(200)
            }
            isPlayingSequence = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Trail Round", fontSize = 11.sp, color = TextSecondary)
                    Text("$currentRound/$totalRounds", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Text(
                    text = if (isPlayingSequence) "Observe Pattern..." else "Trace Order!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlayingSequence) NeonPurple else NeonGreen
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text("Strikes", fontSize = 11.sp, color = TextSecondary)
                    Text("$strikes/3", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (strikes > 0) Color(0xFFFF3366) else NeonGreen)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                for (row in 0..2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (col in 0..2) {
                            val nodeIndex = row * 3 + col
                            val isActive = activeNodeIndex == nodeIndex
                            val isUserSelected = userSequence.contains(nodeIndex) && !isPlayingSequence

                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isActive) NeonCyan
                                        else if (isUserSelected) NeonGreen.copy(alpha = 0.5f)
                                        else Color(0xFF0F1724)
                                    )
                                    .border(
                                        2.dp,
                                        if (isActive) NeonCyan else Color(0x3300F0FF),
                                        CircleShape
                                    )
                                    .clickable(enabled = !isPlayingSequence) {
                                        SoundManager.playClick()
                                        userSequence.add(nodeIndex)
                                        val expectedIndex = sequence[userSequence.size - 1]
                                        if (nodeIndex == expectedIndex) {
                                            SoundManager.playSuccess()
                                            score += 50
                                            if (userSequence.size == sequence.size) {
                                                SoundManager.playVictory()
                                                if (currentRound >= totalRounds) {
                                                    onGameOver(
                                                        GameResult(
                                                            score = score + 500,
                                                            roundsCompleted = currentRound,
                                                            accuracy = 95f,
                                                            avgReactionMs = 450L,
                                                            focusMetric = 0.95f,
                                                            speedMetric = 0.9f,
                                                            accuracyMetric = 0.9f,
                                                            xpEarned = 70,
                                                            coinsEarned = 25
                                                        )
                                                    )
                                                } else {
                                                    currentRound++
                                                    generateNextStep()
                                                }
                                            }
                                        } else {
                                            SoundManager.playError()
                                            strikes++
                                            if (strikes >= 3) {
                                                SoundManager.playBuzzer()
                                                onGameOver(
                                                    GameResult(
                                                        score = score,
                                                        roundsCompleted = currentRound,
                                                        accuracy = 65f,
                                                        avgReactionMs = 600L,
                                                        focusMetric = 0.7f,
                                                        speedMetric = 0.6f,
                                                        accuracyMetric = 0.65f,
                                                        xpEarned = 30,
                                                        coinsEarned = 10
                                                    )
                                                )
                                            } else {
                                                userSequence.clear()
                                                isPlayingSequence = true
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${nodeIndex + 1}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) Color.Black else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. DUAL N-BACK (Crash-Proof Index & Real-Time Sync)
// ==========================================
@Composable
fun DualNBackGame(
    difficulty: String,
    onGameOver: (GameResult) -> Unit
) {
    val n = when (difficulty) {
        "Hard" -> 2
        else -> 1
    }

    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    val maxRounds = 12

    val positionHistory = remember { mutableStateListOf<Int>() }
    val letterHistory = remember { mutableStateListOf<String>() }

    var currentPosition by remember { mutableIntStateOf(4) }
    var currentLetter by remember { mutableStateOf("A") }
    val letters = listOf("A", "C", "H", "K", "L", "O", "T")

    var positionMatchedByUser by remember { mutableStateOf(false) }
    var letterMatchedByUser by remember { mutableStateOf(false) }

    LaunchedEffect(round) {
        if (round > maxRounds) {
            SoundManager.playVictory()
            onGameOver(
                GameResult(
                    score = score,
                    roundsCompleted = maxRounds,
                    accuracy = ((score.toFloat() / (maxRounds * 100)) * 100f).coerceIn(20f, 100f),
                    avgReactionMs = 1200L,
                    focusMetric = 0.95f,
                    speedMetric = 0.85f,
                    accuracyMetric = 0.9f,
                    xpEarned = 85,
                    coinsEarned = 30
                )
            )
            return@LaunchedEffect
        }

        val targetIdx = positionHistory.size - n
        currentPosition = if (targetIdx >= 0 && Random.nextFloat() < 0.35f) {
            positionHistory[targetIdx]
        } else {
            Random.nextInt(9)
        }

        currentLetter = if (targetIdx >= 0 && Random.nextFloat() < 0.35f) {
            letterHistory[targetIdx]
        } else {
            letters.random()
        }

        positionHistory.add(currentPosition)
        letterHistory.add(currentLetter)
        positionMatchedByUser = false
        letterMatchedByUser = false

        delay(2500)
        round++
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Step: $round/$maxRounds", fontSize = 14.sp, color = TextPrimary)
                Text("Dual $n-Back", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Text("Score: $score", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
            }
        }

        Text(
            text = "Match if Position/Letter is same as $n step${if (n > 1) "s" else ""} ago",
            fontSize = 12.sp,
            color = TextSecondary
        )

        // Audio / Visual Letter Callout
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF131828))
                .border(1.dp, NeonPurple, RoundedCornerShape(10.dp))
                .padding(horizontal = 24.dp, vertical = 6.dp)
        ) {
            Text("Auditory Token: $currentLetter", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeonPurple)
        }

        // 3x3 Spatial Grid
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f)
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (r in 0..2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (c in 0..2) {
                            val pos = r * 3 + c
                            val isHighlighted = pos == currentPosition

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isHighlighted) NeonCyan else Color(0xFF0F1724))
                                    .border(1.dp, if (isHighlighted) NeonCyan else Color(0x3300F0FF), RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }
        }

        // Match Buttons with Safe Bounds Checks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CyberButton(
                text = if (positionMatchedByUser) "Position Confirmed ✓" else "Position Match",
                enabled = !positionMatchedByUser,
                onClick = {
                    val currentIdx = positionHistory.size - 1
                    val matchTargetIdx = currentIdx - n
                    if (!positionMatchedByUser && matchTargetIdx >= 0 && currentIdx < positionHistory.size) {
                        positionMatchedByUser = true
                        val isMatch = positionHistory[currentIdx] == positionHistory[matchTargetIdx]
                        if (isMatch) {
                            SoundManager.playSuccess()
                            score += 100
                        } else {
                            SoundManager.playError()
                        }
                    } else if (matchTargetIdx < 0) {
                        SoundManager.playError()
                    }
                },
                modifier = Modifier.weight(1f)
            )

            CyberButton(
                text = if (letterMatchedByUser) "Letter Confirmed ✓" else "Letter Match",
                enabled = !letterMatchedByUser,
                onClick = {
                    val currentIdx = letterHistory.size - 1
                    val matchTargetIdx = currentIdx - n
                    if (!letterMatchedByUser && matchTargetIdx >= 0 && currentIdx < letterHistory.size) {
                        letterMatchedByUser = true
                        val isMatch = letterHistory[currentIdx] == letterHistory[matchTargetIdx]
                        if (isMatch) {
                            SoundManager.playSuccess()
                            score += 100
                        } else {
                            SoundManager.playError()
                        }
                    } else if (matchTargetIdx < 0) {
                        SoundManager.playError()
                    }
                },
            )
        }
    }
}
