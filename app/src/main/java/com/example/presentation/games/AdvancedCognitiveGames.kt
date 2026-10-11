package com.example.presentation.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

// ==========================================
// 1. CHRONO TAP (Precision Timing & Motor Inhibition)
// ==========================================
@Composable
fun ChronoTapGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var perfectCount by remember { mutableIntStateOf(0) }
    val totalRounds = 5

    var waveRadiusRatio by remember { mutableFloatStateOf(0f) }
    var isExpanding by remember { mutableStateOf(true) }
    var roundDone by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("TAP when the neon ring hits the white line!") }
    var feedbackColor by remember { mutableStateOf(NeonCyan) }

    val speed = when (difficulty) {
        "Hard" -> 0.022f
        "Easy" -> 0.012f
        else -> 0.016f
    }

    LaunchedEffect(round, isExpanding) {
        if (!isExpanding) return@LaunchedEffect
        waveRadiusRatio = 0.1f
        roundDone = false
        while (isActive && isExpanding && waveRadiusRatio < 1.0f) {
            waveRadiusRatio += speed
            delay(16)
        }
        if (waveRadiusRatio >= 1.0f && !roundDone) {
            SoundManager.playError()
            feedbackText = "MISSED TIMING!"
            feedbackColor = Color(0xFFEF4444)
            delay(800)
            if (round >= totalRounds) {
                onGameOver(GameResult(score = score, roundsCompleted = round, accuracy = (perfectCount * 20f)))
            } else {
                round++
                isExpanding = true
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Round: $round/$totalRounds", fontSize = 14.sp, color = TextSecondary)
                Text("Perfects: $perfectCount", fontSize = 14.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
            }
        }

        Text(text = feedbackText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = feedbackColor)

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF090E17))
                .clickable(enabled = !roundDone) {
                    roundDone = true
                    isExpanding = false
                    val diff = abs(waveRadiusRatio - 0.75f)
                    if (diff < 0.06f) {
                        SoundManager.playSuccess()
                        score += 300
                        perfectCount++
                        feedbackText = "PERFECT TIMING! (+300)"
                        feedbackColor = NeonGreen
                    } else if (diff < 0.15f) {
                        SoundManager.playClick()
                        score += 150
                        feedbackText = "GOOD TIMING! (+150)"
                        feedbackColor = NeonCyan
                    } else {
                        SoundManager.playError()
                        feedbackText = "TOO EARLY/LATE!"
                        feedbackColor = Color(0xFFEF4444)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val targetRadius = (size.width / 2f) * 0.75f

                // Target threshold ring
                drawCircle(color = Color.White.copy(alpha = 0.85f), radius = targetRadius, center = center, style = Stroke(width = 3.dp.toPx()))

                // Expanding wave ring
                val currentRadius = (size.width / 2f) * waveRadiusRatio
                drawCircle(color = NeonCyan, radius = currentRadius, center = center, style = Stroke(width = 5.dp.toPx()))
                drawCircle(color = NeonCyan.copy(alpha = 0.25f), radius = currentRadius, center = center)
            }
        }

        if (roundDone) {
            CyberButton(
                text = if (round < totalRounds) "Next Round ➔" else "View Results",
                onClick = {
                    if (round < totalRounds) {
                        round++
                        feedbackText = "TAP when the neon ring hits the white line!"
                        feedbackColor = NeonCyan
                        isExpanding = true
                    } else {
                        onGameOver(GameResult(score = score, roundsCompleted = totalRounds, accuracy = (perfectCount * 20f), xpEarned = 75, coinsEarned = 25))
                    }
                }
            )
        }
    }
}

// ==========================================
// 2. WORD MORPH (Verbal Flexibility & Lexical Chain)
// ==========================================
@Composable
fun WordMorphGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(45) }
    val totalRounds = 5

    // Word pairs: baseWord -> 1 valid 1-letter mutation, plus 3 decoys
    val chains = listOf(
        Triple("CORE", "CORD", listOf("CORN", "CURE", "CARD")),
        Triple("NODE", "MODE", listOf("NINE", "NOSE", "NOTE")),
        Triple("BYTE", "BITE", listOf("BORE", "BATH", "BAIT")),
        Triple("WAVE", "WANE", listOf("WAKE", "WARM", "WIPE")),
        Triple("MIND", "MEND", listOf("MINT", "MINE", "MAIL")),
        Triple("GRID", "GRIT", listOf("GLAD", "GOLD", "GRIM"))
    )

    val current = chains[(round - 1) % chains.size]
    val options = remember(round) {
        val list = mutableListOf(current.second)
        list.addAll(current.third.shuffled().take(3))
        list.shuffled()
    }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        onGameOver(GameResult(score = score, roundsCompleted = round, xpEarned = 60, coinsEarned = 20))
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("⏱️ ${timeLeft}s", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                Text("Chain: $round/$totalRounds", fontSize = 14.sp, color = TextSecondary)
            }
        }

        Text("Select the word that changes EXACTLY 1 letter:", fontSize = 13.sp, color = TextSecondary)

        // Root Word
        FuturisticCard(modifier = Modifier.fillMaxWidth(0.85f)) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                Text(text = current.first, fontSize = 36.sp, fontWeight = FontWeight.Black, color = NeonCyan, letterSpacing = 4.sp)
            }
        }

        // Choices
        Column(modifier = Modifier.fillMaxWidth(0.9f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { opt ->
                FuturisticCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (opt == current.second) {
                            SoundManager.playSuccess()
                            score += 150
                            if (round >= totalRounds) {
                                onGameOver(GameResult(score = score, roundsCompleted = totalRounds, accuracy = 100f, xpEarned = 80, coinsEarned = 30))
                            } else {
                                round++
                            }
                        } else {
                            SoundManager.playError()
                            score = (score - 40).coerceAtLeast(0)
                        }
                    }
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(text = opt, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. MATH MATRIX (Target Arithmetic & Combinatorics)
// ==========================================
@Composable
fun MathMatrixGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(50) }
    val totalRounds = 5

    val numbers = remember(round) { List(9) { Random.nextInt(2, 16) } }
    val targetSum = remember(round) {
        val i1 = Random.nextInt(9)
        var i2 = Random.nextInt(9)
        while (i2 == i1) i2 = Random.nextInt(9)
        numbers[i1] + numbers[i2]
    }
    val selectedIndices = remember(round) { mutableStateListOf<Int>() }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        onGameOver(GameResult(score = score, roundsCompleted = round, xpEarned = 65, coinsEarned = 20))
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Target Sum: $targetSum", fontSize = 16.sp, fontWeight = FontWeight.Black, color = NeonGreen)
                Text("⏱️ ${timeLeft}s", fontSize = 16.sp, color = NeonCyan)
            }
        }

        Text("Select 2 numbers that add up to $targetSum:", fontSize = 13.sp, color = TextSecondary)

        // 3x3 Number Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(9) { idx ->
                val isSelected = selectedIndices.contains(idx)
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NeonCyan.copy(alpha = 0.35f) else Color(0xFF0F172A))
                        .border(1.5.dp, if (isSelected) NeonCyan else Color(0x3300F0FF), RoundedCornerShape(12.dp))
                        .clickable {
                            if (isSelected) {
                                selectedIndices.remove(idx)
                            } else {
                                SoundManager.playClick()
                                selectedIndices.add(idx)
                                if (selectedIndices.size == 2) {
                                    val sum = numbers[selectedIndices[0]] + numbers[selectedIndices[1]]
                                    if (sum == targetSum) {
                                        SoundManager.playSuccess()
                                        score += 200
                                        if (round >= totalRounds) {
                                            onGameOver(GameResult(score = score, roundsCompleted = totalRounds, accuracy = 100f, xpEarned = 85, coinsEarned = 35))
                                        } else {
                                            round++
                                        }
                                    } else {
                                        SoundManager.playError()
                                        selectedIndices.clear()
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "${numbers[idx]}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }
    }
}

// ==========================================
// 4. TARGET AIM (Kinetic Reflex & Precision Tap)
// ==========================================
@Composable
fun TargetAimGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var score by remember { mutableIntStateOf(0) }
    var hits by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var targetPos by remember { mutableStateOf(Offset(0.5f, 0.5f)) }
    var isBomb by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        onGameOver(GameResult(score = score, roundsCompleted = hits, accuracy = 90f, xpEarned = 80, coinsEarned = 30))
    }

    LaunchedEffect(hits, timeLeft) {
        delay(Random.nextLong(600, 1100))
        targetPos = Offset(Random.nextFloat() * 0.75f + 0.12f, Random.nextFloat() * 0.75f + 0.12f)
        isBomb = Random.nextFloat() < 0.20f
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Hits: $hits", fontSize = 14.sp, color = NeonGreen)
                Text("⏱️ ${timeLeft}s", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            }
        }

        Text("Tap targets quickly, avoid red skulls! 🎯", fontSize = 13.sp, color = TextSecondary)

        // Kinetic Arena
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF070B12))
                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            // Target Node
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .align(Alignment.TopStart)
                    .padding(top = (targetPos.y * 300).dp, start = (targetPos.x * 260).dp)
                    .clip(CircleShape)
                    .background(if (isBomb) Color(0xFFEF4444).copy(alpha = 0.3f) else NeonCyan.copy(alpha = 0.25f))
                    .border(2.dp, if (isBomb) Color(0xFFEF4444) else NeonCyan, CircleShape)
                    .clickable {
                        if (isBomb) {
                            SoundManager.playBuzzer()
                            score = (score - 100).coerceAtLeast(0)
                        } else {
                            SoundManager.playSuccess()
                            score += 120
                            hits++
                        }
                        targetPos = Offset(Random.nextFloat() * 0.75f + 0.12f, Random.nextFloat() * 0.75f + 0.12f)
                        isBomb = Random.nextFloat() < 0.20f
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (isBomb) "💣" else "🎯", fontSize = 24.sp)
            }
        }
    }
}

// ==========================================
// 5. TONE MEMORY / CYBER SIMON (Sequenced Memory)
// ==========================================
@Composable
fun ToneMemoryGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    val sequence = remember { mutableStateListOf<Int>().apply { add(Random.nextInt(4)) } }
    val userInputs = remember { mutableStateListOf<Int>() }
    var activeFlashIndex by remember { mutableIntStateOf(-1) }
    var isDemonstrating by remember { mutableStateOf(true) }

    LaunchedEffect(round) {
        isDemonstrating = true
        userInputs.clear()
        delay(600)
        for (item in sequence) {
            activeFlashIndex = item
            SoundManager.playClick()
            delay(500)
            activeFlashIndex = -1
            delay(250)
        }
        isDemonstrating = false
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(if (isDemonstrating) "Memorize Sequence..." else "Your Turn!", fontSize = 14.sp, color = if (isDemonstrating) NeonPurple else NeonGreen, fontWeight = FontWeight.Bold)
                Text("Round: $round", fontSize = 14.sp, color = TextSecondary)
            }
        }

        // 2x2 Cyber Simon Grid
        val colors = listOf(NeonCyan, NeonGreen, NeonPurple, Color(0xFFF59E0B))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(0.85f).aspectRatio(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(4) { idx ->
                val isLit = activeFlashIndex == idx
                val baseColor = colors[idx]
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isLit) baseColor else baseColor.copy(alpha = 0.20f))
                        .border(2.dp, if (isLit) Color.White else baseColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .clickable(enabled = !isDemonstrating) {
                            SoundManager.playClick()
                            userInputs.add(idx)
                            val checkIdx = userInputs.size - 1
                            if (userInputs[checkIdx] != sequence[checkIdx]) {
                                SoundManager.playBuzzer()
                                onGameOver(GameResult(score = score, roundsCompleted = round - 1, xpEarned = 70, coinsEarned = 25))
                            } else if (userInputs.size == sequence.size) {
                                SoundManager.playSuccess()
                                score += round * 150
                                if (round >= 8) {
                                    onGameOver(GameResult(score = score, roundsCompleted = round, xpEarned = 100, coinsEarned = 40))
                                } else {
                                    sequence.add(Random.nextInt(4))
                                    round++
                                }
                            }
                        }
                )
            }
        }
    }
}

// ==========================================
// 6. CIPHER SHIFT (Caesar Deduction)
// ==========================================
@Composable
fun CipherShiftGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    val totalRounds = 5

    val words = listOf("BYTE", "CYBER", "ROBOT", "LOGIC", "NEXUS")
    val shift = remember(round) { Random.nextInt(1, 4) }
    val currentWord = words[(round - 1) % words.size]
    val cipherText = remember(round) {
        currentWord.map { ch -> (((ch - 'A' + shift) % 26) + 'A'.code).toChar() }.joinToString("")
    }

    val options = remember(round) {
        val decoys = listOf("DATA", "CORE", "PULSE", "MODEM", "CHIP").filter { it != currentWord }.shuffled().take(3)
        (decoys + currentWord).shuffled()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Shift: -$shift", fontSize = 14.sp, color = NeonPurple, fontWeight = FontWeight.Bold)
                Text("Round: $round/$totalRounds", fontSize = 14.sp, color = TextSecondary)
            }
        }

        Text("Decrypt the Encrypted Cipher Code:", fontSize = 13.sp, color = TextSecondary)

        FuturisticCard(modifier = Modifier.fillMaxWidth(0.85f)) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp), contentAlignment = Alignment.Center) {
                Text(text = cipherText, fontSize = 34.sp, fontWeight = FontWeight.Black, color = NeonCyan, letterSpacing = 4.sp)
            }
        }

        Column(modifier = Modifier.fillMaxWidth(0.9f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { opt ->
                FuturisticCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (opt == currentWord) {
                            SoundManager.playSuccess()
                            score += 200
                            if (round >= totalRounds) {
                                onGameOver(GameResult(score = score, roundsCompleted = totalRounds, xpEarned = 80, coinsEarned = 30))
                            } else {
                                round++
                            }
                        } else {
                            SoundManager.playError()
                        }
                    }
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(opt, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. SILHOUETTE MATCH (Visual Spatial Rotation)
// ==========================================
@Composable
fun SilhouetteMatchGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    val totalRounds = 5

    val shapes = listOf("▲", "◆", "⬡", "✦", "⯁")
    val currentShape = shapes[(round - 1) % shapes.size]
    val targetRotation = remember(round) { (Random.nextInt(1, 4) * 90) }

    val options = remember(round) {
        listOf(targetRotation, (targetRotation + 90) % 360, (targetRotation + 180) % 360).shuffled()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Round: $round/$totalRounds", fontSize = 14.sp, color = TextSecondary)
            }
        }

        Text("Match the Rotated Target Silhouette (+${targetRotation}°):", fontSize = 13.sp, color = TextSecondary)

        FuturisticCard(modifier = Modifier.size(120.dp)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = currentShape, fontSize = 48.sp, color = NeonCyan)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { rot ->
                FuturisticCard(
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                    onClick = {
                        if (rot == targetRotation) {
                            SoundManager.playSuccess()
                            score += 180
                            if (round >= totalRounds) {
                                onGameOver(GameResult(score = score, roundsCompleted = totalRounds, xpEarned = 75, coinsEarned = 25))
                            } else {
                                round++
                            }
                        } else {
                            SoundManager.playError()
                        }
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = currentShape, fontSize = 36.sp, color = if (rot == targetRotation) NeonGreen else TextPrimary)
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. ZEN BALANCE (Equilibrium Particle Physics)
// ==========================================
@Composable
fun ZenBalanceGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var holdSeconds by remember { mutableFloatStateOf(0f) }
    var particleOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    val targetRadius = 50f

    LaunchedEffect(Unit) {
        while (isActive && holdSeconds < 10f) {
            delay(50)
            // Gentle random drift force
            particleOffset = Offset(
                particleOffset.x + (Random.nextFloat() - 0.5f) * 4f,
                particleOffset.y + (Random.nextFloat() - 0.5f) * 4f
            )
            val dist = hypot(particleOffset.x, particleOffset.y)
            if (dist <= targetRadius) {
                holdSeconds += 0.05f
            }
        }
        if (holdSeconds >= 10f) {
            SoundManager.playVictory()
            onGameOver(GameResult(score = 800, roundsCompleted = 10, accuracy = 100f, xpEarned = 90, coinsEarned = 35))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Equilibrium Hold: ${String.format("%.1f", holdSeconds)}s / 10s", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
            }
        }

        Text("Drag the orb into the glowing center zone to balance:", fontSize = 13.sp, color = TextSecondary)

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(Color(0xFF060B12))
                .border(2.dp, NeonCyan.copy(alpha = 0.4f), CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        particleOffset = Offset(particleOffset.x + dragAmount.x, particleOffset.y + dragAmount.y)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                drawCircle(color = NeonGreen.copy(alpha = 0.25f), radius = targetRadius, center = center)
                drawCircle(color = NeonGreen, radius = targetRadius, center = center, style = Stroke(width = 2.dp.toPx()))

                // Particle
                val pPos = Offset(center.x + particleOffset.x, center.y + particleOffset.y)
                drawCircle(color = NeonCyan, radius = 16.dp.toPx(), center = pPos)
                drawCircle(color = Color.White, radius = 6.dp.toPx(), center = pPos)
            }
        }
    }
}

// ==========================================
// 9. QUICK SORT (Category Switching Speed)
// ==========================================
@Composable
fun QuickSortGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var score by remember { mutableIntStateOf(0) }
    var currentItem by remember { mutableIntStateOf(Random.nextInt(1, 100)) }
    var itemsProcessed by remember { mutableIntStateOf(0) }
    val totalItems = 15

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Processed: $itemsProcessed/$totalItems", fontSize = 14.sp, color = TextSecondary)
            }
        }

        Text("Sort: EVEN Numbers ➔ RIGHT | ODD Numbers ➔ LEFT", fontSize = 13.sp, color = TextSecondary)

        FuturisticCard(modifier = Modifier.size(140.dp)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "$currentItem", fontSize = 48.sp, fontWeight = FontWeight.Black, color = NeonCyan)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            CyberButton(
                text = "⬅️ ODD",
                modifier = Modifier.weight(1f),
                onClick = {
                    if (currentItem % 2 != 0) {
                        SoundManager.playSuccess()
                        score += 100
                    } else SoundManager.playError()
                    itemsProcessed++
                    if (itemsProcessed >= totalItems) {
                        onGameOver(GameResult(score = score, roundsCompleted = totalItems, xpEarned = 75, coinsEarned = 25))
                    } else {
                        currentItem = Random.nextInt(1, 100)
                    }
                }
            )

            CyberButton(
                text = "EVEN ➡️",
                modifier = Modifier.weight(1f),
                onClick = {
                    if (currentItem % 2 == 0) {
                        SoundManager.playSuccess()
                        score += 100
                    } else SoundManager.playError()
                    itemsProcessed++
                    if (itemsProcessed >= totalItems) {
                        onGameOver(GameResult(score = score, roundsCompleted = totalItems, xpEarned = 75, coinsEarned = 25))
                    } else {
                        currentItem = Random.nextInt(1, 100)
                    }
                }
            )
        }
    }
}

// ==========================================
// 10. CYBER STACKER (Spatial Stacking Precision)
// ==========================================
@Composable
fun CyberStackerGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var stackHeight by remember { mutableIntStateOf(0) }
    var blockX by remember { mutableFloatStateOf(0f) }
    var blockDir by remember { mutableFloatStateOf(1f) }
    val maxBlocks = 8

    LaunchedEffect(stackHeight) {
        while (isActive) {
            blockX += blockDir * 0.04f
            if (blockX > 0.7f) blockDir = -1f
            if (blockX < 0.0f) blockDir = 1f
            delay(16)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Stack Height: $stackHeight/$maxBlocks", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF090E17))
                .clickable {
                    SoundManager.playClick()
                    stackHeight++
                    if (stackHeight >= maxBlocks) {
                        SoundManager.playVictory()
                        onGameOver(GameResult(score = 800, roundsCompleted = maxBlocks, accuracy = 100f, xpEarned = 90, coinsEarned = 35))
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val bw = size.width * 0.35f
                val bh = 24.dp.toPx()
                // Placed blocks
                for (i in 0 until stackHeight) {
                    val y = size.height - (i + 1) * (bh + 4.dp.toPx())
                    drawRect(color = NeonCyan, topLeft = Offset(size.width * 0.35f, y), size = Size(bw, bh))
                }
                // Moving block
                val my = size.height - (stackHeight + 1) * (bh + 4.dp.toPx())
                drawRect(color = NeonPurple, topLeft = Offset(size.width * blockX, my), size = Size(bw, bh))
            }
        }

        Text("Tap anywhere to DROP the cyber block!", fontSize = 13.sp, color = TextSecondary)
    }
}

// ==========================================
// 11. CHANGE BLINDNESS (Flicker Mask Perception)
// ==========================================
@Composable
fun ChangeBlindnessGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    val totalRounds = 5

    val gridColors = remember(round) { List(9) { NeonCyan } }
    val changedIdx = remember(round) { Random.nextInt(9) }
    var isMasked by remember { mutableStateOf(false) }

    LaunchedEffect(round) {
        while (isActive) {
            isMasked = false
            delay(900)
            isMasked = true
            delay(120)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Round: $round/$totalRounds", fontSize = 14.sp, color = TextSecondary)
            }
        }

        Text("Spot the tile that changes during the flicker:", fontSize = 13.sp, color = TextSecondary)

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(0.85f).aspectRatio(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(9) { idx ->
                val col = if (idx == changedIdx && isMasked) NeonPurple else gridColors[idx]
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(col.copy(alpha = 0.35f))
                        .border(1.5.dp, col, RoundedCornerShape(12.dp))
                        .clickable {
                            if (idx == changedIdx) {
                                SoundManager.playSuccess()
                                score += 200
                                if (round >= totalRounds) {
                                    onGameOver(GameResult(score = score, roundsCompleted = totalRounds, xpEarned = 80, coinsEarned = 30))
                                } else round++
                            } else SoundManager.playError()
                        }
                )
            }
        }
    }
}

// ==========================================
// 12. CONSTELLATION CONNECT (Numerical Pathfinding)
// ==========================================
@Composable
fun ConstellationConnectGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var nextExpected by remember { mutableIntStateOf(1) }
    val count = 8
    val points = remember {
        List(count) { i ->
            Triple(i + 1, Random.nextFloat() * 0.7f + 0.15f, Random.nextFloat() * 0.7f + 0.15f)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Connect Node: $nextExpected / $count", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
            }
        }

        Text("Tap star nodes in exact numerical order (1 ➔ $count):", fontSize = 13.sp, color = TextSecondary)

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF070B14))
                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
        ) {
            points.forEach { (num, x, y) ->
                val isCompleted = num < nextExpected
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.TopStart)
                        .padding(top = (y * 280).dp, start = (x * 250).dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) NeonGreen.copy(alpha = 0.3f) else NeonCyan.copy(alpha = 0.2f))
                        .border(1.5.dp, if (isCompleted) NeonGreen else NeonCyan, CircleShape)
                        .clickable {
                            if (num == nextExpected) {
                                SoundManager.playClick()
                                nextExpected++
                                if (nextExpected > count) {
                                    SoundManager.playVictory()
                                    onGameOver(GameResult(score = 750, roundsCompleted = count, xpEarned = 80, coinsEarned = 30))
                                }
                            } else SoundManager.playError()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$num", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (isCompleted) NeonGreen else TextPrimary)
                }
            }
        }
    }
}

// ==========================================
// 13. COLOR FLOOD (Algorithmic Matrix Assimilation)
// ==========================================
@Composable
fun ColorFloodGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var movesLeft by remember { mutableIntStateOf(12) }
    val colors = listOf(NeonCyan, NeonGreen, NeonPurple, Color(0xFFF59E0B))
    val board = remember { mutableStateListOf<Color>().apply { addAll(List(25) { colors[Random.nextInt(4)] }) } }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Moves Left: $movesLeft", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (movesLeft > 3) NeonGreen else Color(0xFFEF4444))
            }
        }

        Text("Flood-fill the matrix from the top-left corner:", fontSize = 13.sp, color = TextSecondary)

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.fillMaxWidth(0.85f).aspectRatio(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(25) { idx ->
                Box(modifier = Modifier.aspectRatio(1f).clip(RoundedCornerShape(6.dp)).background(board[idx]))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(0.85f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            colors.forEach { c ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(c)
                        .clickable {
                            SoundManager.playClick()
                            movesLeft--
                            // Simple cascade
                            board[0] = c
                            board[1] = c
                            board[5] = c
                            if (movesLeft <= 0) {
                                onGameOver(GameResult(score = 500, roundsCompleted = 12, xpEarned = 70, coinsEarned = 25))
                            }
                        }
                )
            }
        }
    }
}

// ==========================================
// 14. TYPING BLITZ (High-Speed Command Spelling)
// ==========================================
@Composable
fun TypingBlitzGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    val totalRounds = 5

    val words = listOf("BYTE", "SYNC", "NODE", "PORT", "HASH")
    val currentWord = words[(round - 1) % words.size]
    var typed by remember(round) { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Score: $score", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Round: $round/$totalRounds", fontSize = 14.sp, color = TextSecondary)
            }
        }

        Text("Type the command terminal word:", fontSize = 13.sp, color = TextSecondary)

        FuturisticCard(modifier = Modifier.fillMaxWidth(0.85f)) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = currentWord, fontSize = 32.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "> $typed _", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
            }
        }

        // On-screen keyboard for letters in word + distractors
        val keys = remember(round) { (currentWord.toSet() + listOf('A', 'E', 'I', 'O', 'U', 'R', 'T')).shuffled().take(8) }
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(keys.size) { idx ->
                val ch = keys[idx]
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable {
                            SoundManager.playClick()
                            typed += ch
                            if (typed == currentWord) {
                                SoundManager.playSuccess()
                                score += 200
                                if (round >= totalRounds) {
                                    onGameOver(GameResult(score = score, roundsCompleted = totalRounds, xpEarned = 80, coinsEarned = 30))
                                } else round++
                            } else if (!currentWord.startsWith(typed)) {
                                SoundManager.playError()
                                typed = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$ch", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }
    }
}

// ==========================================
// 15. NUMBER GRID RUSH (Schulte Matrix Speed)
// ==========================================
@Composable
fun NumberGridRushGame(difficulty: String, onGameOver: (GameResult) -> Unit) {
    var nextNum by remember { mutableIntStateOf(1) }
    var secondsElapsed by remember { mutableIntStateOf(0) }
    val numbers = remember { (1..16).shuffled() }

    LaunchedEffect(Unit) {
        while (isActive && nextNum <= 16) {
            delay(1000)
            secondsElapsed++
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FuturisticCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tap Next: $nextNum", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NeonGreen)
                Text("⏱️ ${secondsElapsed}s", fontSize = 16.sp, color = NeonCyan)
            }
        }

        Text("Tap numbers in ascending order from 1 to 16 as fast as possible!", fontSize = 12.sp, color = TextSecondary)

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(16) { idx ->
                val num = numbers[idx]
                val isCompleted = num < nextNum
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isCompleted) NeonGreen.copy(alpha = 0.25f) else Color(0xFF0F172A))
                        .border(1.dp, if (isCompleted) NeonGreen else Color(0x3300F0FF), RoundedCornerShape(10.dp))
                        .clickable(enabled = !isCompleted) {
                            if (num == nextNum) {
                                SoundManager.playClick()
                                nextNum++
                                if (nextNum > 16) {
                                    SoundManager.playVictory()
                                    val finalScore = (1000 - secondsElapsed * 20).coerceAtLeast(200)
                                    onGameOver(GameResult(score = finalScore, roundsCompleted = 16, avgReactionMs = (secondsElapsed * 62L), xpEarned = 90, coinsEarned = 35))
                                }
                            } else SoundManager.playError()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$num", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isCompleted) NeonGreen else TextPrimary)
                }
            }
        }
    }
}
