package com.example.presentation.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.isActive
import kotlin.math.hypot
import kotlin.random.Random

// ==========================================
// 1. FOCUS FLOW (Ball Tracking with Real Physics & Accurate Tap Detection)
// ==========================================
@Composable
fun FocusFlowGame(
    difficulty: String,
    onGameOver: (GameResult) -> Unit
) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }

    // Phase: 0 = Highlighting target, 1 = Moving/Tracking, 2 = Pick target
    var phase by remember { mutableIntStateOf(0) }

    val ballCount = when (difficulty) {
        "Easy" -> 6
        "Hard" -> 10
        else -> 8
    }

    val targetIndex = remember(round) { Random.nextInt(ballCount) }
    val balls = remember(round) {
        mutableStateListOf<Ball>().apply {
            addAll(
                List(ballCount) { i ->
                    val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                    val speed = when (difficulty) {
                        "Easy" -> 0.007f
                        "Hard" -> 0.015f
                        else -> 0.011f
                    }
                    Ball(
                        id = i,
                        x = 0.15f + (i % 3) * 0.3f + Random.nextFloat() * 0.05f,
                        y = 0.15f + (i / 3) * 0.25f + Random.nextFloat() * 0.05f,
                        vx = kotlin.math.cos(angle) * speed,
                        vy = kotlin.math.sin(angle) * speed
                    )
                }
            )
        }
    }

    // Phase transition & Physics update loop
    LaunchedEffect(round) {
        phase = 0 // Reveal target orb
        delay(2200)
        phase = 1 // Smooth shuffle movement

        val startTime = System.currentTimeMillis()
        val durationMs = when (difficulty) {
            "Easy" -> 3500L
            "Hard" -> 5000L
            else -> 4200L
        }

        while (isActive && System.currentTimeMillis() - startTime < durationMs) {
            // Update physics on frame tick
            balls.forEach { ball ->
                ball.x += ball.vx
                ball.y += ball.vy

                // Bounce off boundaries
                if (ball.x <= 0.08f) {
                    ball.x = 0.08f
                    ball.vx = kotlin.math.abs(ball.vx)
                } else if (ball.x >= 0.92f) {
                    ball.x = 0.92f
                    ball.vx = -kotlin.math.abs(ball.vx)
                }

                if (ball.y <= 0.08f) {
                    ball.y = 0.08f
                    ball.vy = kotlin.math.abs(ball.vy)
                } else if (ball.y >= 0.92f) {
                    ball.y = 0.92f
                    ball.vy = -kotlin.math.abs(ball.vy)
                }
            }
            delay(16) // ~60fps movement
        }

        phase = 2 // Tap the target
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top HUD Bar
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Score", fontSize = 11.sp, color = TextSecondary)
                    Text("$score", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Text(
                    text = when (phase) {
                        0 -> "Memorize Target Orb!"
                        1 -> "Tracking Movement..."
                        else -> "TAP the Target Orb!"
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (phase) {
                        0 -> NeonCyan
                        1 -> NeonPurple
                        else -> NeonGreen
                    }
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text("Lives", fontSize = 11.sp, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(lives) { Text("❤️", fontSize = 14.sp) }
                    }
                }
            }
        }

        // Arena with Canvas & Accurate Hit Testing
        FuturisticCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(phase, round) {
                        if (phase == 2) {
                            detectTapGestures { tapOffset ->
                                val w = size.width.toFloat()
                                val h = size.height.toFloat()
                                val hitRadius = 36.dp.toPx()

                                // Find closest tapped ball
                                val tappedBall = balls.minByOrNull { ball ->
                                    val ballPxX = ball.x * w
                                    val ballPxY = ball.y * h
                                    hypot(tapOffset.x - ballPxX, tapOffset.y - ballPxY)
                                }

                                if (tappedBall != null) {
                                    val dist = hypot(tapOffset.x - tappedBall.x * w, tapOffset.y - tappedBall.y * h)
                                    if (dist <= hitRadius * 1.5f) {
                                        if (tappedBall.id == targetIndex) {
                                            SoundManager.playSuccess()
                                            score += 150
                                            if (round >= 5) {
                                                SoundManager.playVictory()
                                                onGameOver(
                                                    GameResult(
                                                        score = score,
                                                        roundsCompleted = round,
                                                        accuracy = 100f,
                                                        avgReactionMs = 1100L,
                                                        focusMetric = 0.98f,
                                                        speedMetric = 0.92f,
                                                        accuracyMetric = 1.0f,
                                                        xpEarned = 80,
                                                        coinsEarned = 30
                                                    )
                                                )
                                            } else {
                                                round++
                                            }
                                        } else {
                                            SoundManager.playError()
                                            lives--
                                            if (lives <= 0) {
                                                SoundManager.playBuzzer()
                                                onGameOver(
                                                    GameResult(
                                                        score = score,
                                                        roundsCompleted = round - 1,
                                                        accuracy = 60f,
                                                        avgReactionMs = 1400L,
                                                        focusMetric = 0.7f,
                                                        speedMetric = 0.65f,
                                                        accuracyMetric = 0.6f,
                                                        xpEarned = 35,
                                                        coinsEarned = 10
                                                    )
                                                )
                                            } else {
                                                round++
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    balls.forEach { ball ->
                        val isTarget = ball.id == targetIndex
                        val color = when {
                            phase == 0 && isTarget -> NeonCyan
                            phase == 2 && isTarget -> NeonGreen.copy(alpha = 0.85f)
                            else -> Color(0xFF384A65)
                        }

                        val ballCenter = Offset(ball.x * w, ball.y * h)
                        val radiusPx = 18.dp.toPx()

                        // Outer glowing aura for target during phase 0
                        if (phase == 0 && isTarget) {
                            drawCircle(
                                color = NeonCyan.copy(alpha = 0.35f),
                                radius = radiusPx * 1.8f,
                                center = ballCenter
                            )
                        }

                        // Ball body
                        drawCircle(
                            color = color,
                            radius = radiusPx,
                            center = ballCenter
                        )

                        // Highlight dot
                        drawCircle(
                            color = Color.White.copy(alpha = 0.6f),
                            radius = radiusPx * 0.3f,
                            center = Offset(ballCenter.x - radiusPx * 0.3f, ballCenter.y - radiusPx * 0.3f)
                        )
                    }
                }
            }
        }
    }
}

private class Ball(
    val id: Int,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float
)

// ==========================================
// 2. PATTERN MATCH (Dedicated Visual Comparison Game)
// ==========================================
@Composable
fun PatternMatchGame(
    difficulty: String,
    onGameOver: (GameResult) -> Unit
) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(40) }
    var streak by remember { mutableIntStateOf(0) }

    // 3x3 binary pattern: list of 9 booleans
    val targetPattern = remember(round) {
        List(9) { Random.nextBoolean() }
    }

    val options = remember(round) {
        val correct = targetPattern
        val decoy1 = targetPattern.mapIndexed { idx, v -> if (idx == (round % 9)) !v else v }
        val decoy2 = targetPattern.mapIndexed { idx, v -> if (idx == ((round + 3) % 9)) !v else v }
        listOf(correct, decoy1, decoy2).shuffled()
    }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        SoundManager.playVictory()
        onGameOver(
            GameResult(
                score = score,
                roundsCompleted = round,
                accuracy = 92f,
                avgReactionMs = 1200L,
                focusMetric = 0.95f,
                speedMetric = 0.92f,
                accuracyMetric = 0.94f,
                xpEarned = 75,
                coinsEarned = 25
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top HUD
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Score", fontSize = 11.sp, color = TextSecondary)
                    Text("$score", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Time", fontSize = 11.sp, color = TextSecondary)
                    Text("${timeLeft}s", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Streak", fontSize = 11.sp, color = TextSecondary)
                    Text("🔥 $streak", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                }
            }
        }

        Text("Identify the Identical Target Grid:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonCyan)

        // Target Matrix Box
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0C1422))
                .border(2.dp, NeonCyan, RoundedCornerShape(12.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            PatternGrid(pattern = targetPattern, cellSize = 26.dp)
        }

        // Candidate Options Row
        Text("Candidate Matrices:", fontSize = 12.sp, color = TextSecondary)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            options.forEachIndexed { optIndex, patternOption ->
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF090F1A))
                        .border(1.dp, Color(0x4400F0FF), RoundedCornerShape(12.dp))
                        .clickable {
                            if (patternOption == targetPattern) {
                                SoundManager.playSuccess()
                                score += 100 + streak * 20
                                streak++
                                timeLeft += 2
                            } else {
                                SoundManager.playError()
                                streak = 0
                                timeLeft = (timeLeft - 3).coerceAtLeast(0)
                            }
                            round++
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PatternGrid(pattern = patternOption, cellSize = 24.dp)
                }
            }
        }
    }
}

@Composable
private fun PatternGrid(pattern: List<Boolean>, cellSize: androidx.compose.ui.unit.Dp) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        for (r in 0..2) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                for (c in 0..2) {
                    val isActive = pattern[r * 3 + c]
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isActive) NeonCyan else Color(0xFF131D2A))
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. ODD ONE OUT (Diverse cognitive variants per round)
// ==========================================
@Composable
fun OddOneOutGame(
    difficulty: String,
    onGameOver: (GameResult) -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(45) }
    var streak by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(1) }

    val oddPosition = remember(round) { Random.nextInt(9) }
    val variantType = remember(round) { (round - 1) % 4 } // 0: rotation, 1: color, 2: symbol, 3: scale

    val symbols = listOf("⚡", "💎", "🎯", "✦", "▲", "⦿")
    val baseSymbol = remember(round) { symbols[(round - 1) % symbols.size] }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        SoundManager.playVictory()
        onGameOver(
            GameResult(
                score = score,
                roundsCompleted = round,
                accuracy = 92f,
                avgReactionMs = 950L,
                focusMetric = 0.94f,
                speedMetric = 0.91f,
                accuracyMetric = 0.92f,
                xpEarned = 70,
                coinsEarned = 25
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top HUD
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Score", fontSize = 11.sp, color = TextSecondary)
                    Text("$score", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Time", fontSize = 11.sp, color = TextSecondary)
                    Text("${timeLeft}s", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Streak", fontSize = 11.sp, color = TextSecondary)
                    Text("🔥 $streak", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                }
            }
        }

        Text(
            text = "Spot the Anomaly Grid Cell:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan
        )

        // Center 3x3 Card Grid
        FuturisticCard(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items(9) { index ->
                    val isOdd = index == oddPosition
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F1724))
                            .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(12.dp))
                            .clickable {
                                if (isOdd) {
                                    SoundManager.playSuccess()
                                    score += 25 + streak * 5
                                    streak++
                                    timeLeft += 2
                                    round++
                                } else {
                                    SoundManager.playError()
                                    streak = 0
                                    score = (score - 10).coerceAtLeast(0)
                                    timeLeft = (timeLeft - 2).coerceAtLeast(0)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val symbolToRender = if (isOdd && variantType == 2) "★" else baseSymbol
                        Text(
                            text = symbolToRender,
                            fontSize = 32.sp,
                            color = if (isOdd && variantType == 1) Color(0xFFFF5577) else Color.White,
                            modifier = Modifier.graphicsLayer {
                                if (isOdd && variantType == 0) rotationZ = 180f
                                if (isOdd && variantType == 3) {
                                    scaleX = 0.72f
                                    scaleY = 0.72f
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. SPEED MATH (Guaranteed Distinct Choices & Dynamic Clock)
// ==========================================
@Composable
fun SpeedMathGame(
    difficulty: String,
    onGameOver: (GameResult) -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(45) }
    var streak by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(1) }

    val numA = remember(round) { Random.nextInt(10, 50) }
    val numB = remember(round) { Random.nextInt(5, 30) }
    val isAdd = remember(round) { Random.nextBoolean() }
    val correctAns = remember(round) { if (isAdd) numA + numB else numA - numB }

    val options = remember(round) {
        val set = mutableSetOf(correctAns)
        while (set.size < 4) {
            val delta = listOf(-10, -5, -2, -1, 1, 2, 5, 10).random()
            set.add(correctAns + delta)
        }
        set.toList().shuffled()
    }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        SoundManager.playVictory()
        onGameOver(
            GameResult(
                score = score,
                roundsCompleted = round,
                accuracy = 90f,
                avgReactionMs = 1100L,
                focusMetric = 0.95f,
                speedMetric = 0.95f,
                accuracyMetric = 0.9f,
                xpEarned = 70,
                coinsEarned = 25
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top HUD
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Time: ${timeLeft}s", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Text("Streak: x${streak + 1}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
            }
        }

        // Equation Card
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$numA ${if (isAdd) "+" else "-"} $numB = ?",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // 4 Multiple Choice buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (i in 0..1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (j in 0..1) {
                        val opt = options[i * 2 + j]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0E1724))
                                .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                                .clickable {
                                    if (opt == correctAns) {
                                        SoundManager.playSuccess()
                                        score += 100 + streak * 20
                                        streak++
                                        timeLeft += 2
                                    } else {
                                        SoundManager.playError()
                                        streak = 0
                                        timeLeft = (timeLeft - 3).coerceAtLeast(0)
                                    }
                                    round++
                                }
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "$opt", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. REACTION TIME (Reflex Radar with Precise History)
// ==========================================
@Composable
fun ReactionTimeGame(
    difficulty: String,
    onGameOver: (GameResult) -> Unit
) {
    // 0 = Waiting (Red), 1 = Trigger (Green), 2 = Clicked (Cyan), 3 = False Start
    var state by remember { mutableIntStateOf(0) }
    var reactionTimeMs by remember { mutableLongStateOf(0L) }
    var roundsDone by remember { mutableIntStateOf(0) }
    val trialTimes = remember { mutableStateListOf<Long>() }
    var triggerTimestamp by remember { mutableLongStateOf(0L) }

    LaunchedEffect(roundsDone, state) {
        if (state == 0) {
            delay(Random.nextLong(1500, 3800))
            triggerTimestamp = System.currentTimeMillis()
            state = 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(
                when (state) {
                    0 -> Color(0xFFDC2626) // Red wait
                    1 -> NeonGreen        // Green TAP NOW!
                    2 -> NeonCyan         // Result
                    else -> Color(0xFF2563EB) // False Start Blue
                }
            )
            .clickable(enabled = state == 0 || state == 1) {
                when (state) {
                    0 -> {
                        SoundManager.playError()
                        state = 3 // False start
                    }
                    1 -> {
                        SoundManager.playSuccess()
                        val diff = System.currentTimeMillis() - triggerTimestamp
                        reactionTimeMs = diff
                        trialTimes.add(diff)
                        roundsDone++
                        state = 2
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            when (state) {
                0 -> {
                    Text("Hold Steady...", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Wait for the green signal!", fontSize = 14.sp, color = Color(0xFFFFDDDD))
                }
                1 -> {
                    Text("⚡ TAP NOW! ⚡", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                }
                2 -> {
                    Text("${reactionTimeMs} ms", fontSize = 52.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF04070D))
                    Text("Round ${roundsDone}/3 complete", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF04070D))
                    Spacer(modifier = Modifier.height(8.dp))
                    CyberButton(
                        text = if (roundsDone >= 3) "View Performance Report ➔" else "Next Reflex Trial ➔",
                        onClick = {
                            SoundManager.playClick()
                            if (roundsDone >= 3) {
                                val avg = if (trialTimes.isNotEmpty()) trialTimes.average().toLong() else 250L
                                SoundManager.playVictory()
                                onGameOver(
                                    GameResult(
                                        score = (1000 - avg).toInt().coerceAtLeast(100),
                                        roundsCompleted = roundsDone,
                                        accuracy = 95f,
                                        avgReactionMs = avg,
                                        focusMetric = 0.95f,
                                        speedMetric = (500f / avg.coerceAtLeast(100)).coerceIn(0.2f, 1f),
                                        accuracyMetric = 0.9f,
                                        xpEarned = 70,
                                        coinsEarned = 25
                                    )
                                )
                            } else {
                                state = 0
                            }
                        },
                        backgroundColor = Color(0xFF04070D),
                        textColor = NeonCyan
                    )
                }
                3 -> {
                    Text("⚠️ Too Early!", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Trigger Shift detected before signal. Wait for green!", fontSize = 14.sp, color = Color(0xFFFFDDDD), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    CyberButton(
                        text = "Try Again 🔄",
                        onClick = {
                            SoundManager.playClick()
                            state = 0
                        },
                        backgroundColor = Color(0xFF0A1222),
                        textColor = Color.White
                    )
                }
            }

            if (trialTimes.isNotEmpty() && state != 1) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Recorded Trials: ${trialTimes.joinToString(" • ") { "${it}ms" }}",
                    fontSize = 12.sp,
                    color = if (state == 2) Color(0xFF04070D).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ==========================================
// 6. STROOP EFFECT (COLOR CLASH)
// ==========================================
@Composable
fun StroopEffectGame(
    difficulty: String,
    onGameOver: (GameResult) -> Unit
) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    val maxRounds = 15

    val colorOptions = listOf(
        "RED" to Color(0xFFFF3344),
        "BLUE" to Color(0xFF3388FF),
        "GREEN" to Color(0xFF22DD55),
        "YELLOW" to Color(0xFFFFCC00)
    )

    val currentWord = remember(round) { colorOptions.random().first }
    val currentInk = remember(round) { colorOptions.random().second }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Round: $round/$maxRounds", fontSize = 14.sp, color = TextPrimary)
                Text("Score: $score", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
            }
        }

        Text(
            text = "SELECT INK COLOR • IGNORE THE TEXT!",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan
        )

        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentWord,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = currentInk
                )
            }
        }

        // 4 color buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colorOptions.forEach { (name, color) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color)
                        .clickable {
                            if (color == currentInk) {
                                SoundManager.playSuccess()
                                score += 100
                            } else {
                                SoundManager.playError()
                            }
                            if (round >= maxRounds) {
                                SoundManager.playVictory()
                                onGameOver(
                                    GameResult(
                                        score = score,
                                        roundsCompleted = maxRounds,
                                        accuracy = (score.toFloat() / (maxRounds * 100)) * 100f,
                                        avgReactionMs = 850L,
                                        focusMetric = 0.98f,
                                        speedMetric = 0.94f,
                                        accuracyMetric = 0.92f,
                                        xpEarned = 75,
                                        coinsEarned = 25
                                    )
                                )
                            } else {
                                round++
                            }
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}
