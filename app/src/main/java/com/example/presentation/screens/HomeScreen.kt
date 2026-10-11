package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.models.AppCatalog
import com.example.presentation.viewmodel.MainViewModel
import com.example.domain.models.UserTargetGoal
import com.example.ui.audio.SoundManager
import com.example.ui.components.BorderAnimStyle
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberSlider
import com.example.ui.components.FuturisticCard
import com.example.ui.components.NavTab
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val prefs by viewModel.userPreferences.collectAsState()
    val gamesPlayedCount by viewModel.totalGamesPlayed.collectAsState()
    val bestScoreVal by viewModel.bestScore.collectAsState()
    val targetGoals by viewModel.targetGoals.collectAsState()
    val primaryCyberColor = com.example.parseCyberColor(prefs.primaryColor, NeonCyan)

    var showAddGoalDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP WAVEMIND BRAND HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.app_logo),
                    contentDescription = "WaveMind Logo",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.2.dp, primaryCyberColor.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                )
                Column {
                    Text(
                        text = "WaveMind",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryCyberColor,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "120 FPS NEURAL COGNITION SUITE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(primaryCyberColor.copy(alpha = 0.15f))
                    .border(1.dp, primaryCyberColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "29 DRILLS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryCyberColor,
                    letterSpacing = 1.sp
                )
            }
        }

        // TOP OPERATIVE PROFILE CARD
        FuturisticCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = primaryCyberColor.copy(alpha = 0.5f),
            themePreset = prefs.themePreset,
            glassOpacity = prefs.glassOpacity,
            cornerRadius = prefs.cardCornerRadius.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(primaryCyberColor.copy(alpha = 0.2f))
                                .border(1.5.dp, primaryCyberColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = prefs.userAvatar.take(2),
                                fontSize = 22.sp
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = prefs.userName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = prefs.userRankTitle,
                                fontSize = 11.sp,
                                color = primaryCyberColor,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Level Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(primaryCyberColor.copy(alpha = 0.15f))
                            .border(1.dp, primaryCyberColor.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LVL ${(prefs.xp / 100) + 1}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryCyberColor
                        )
                    }
                }

                // Stats Pills Row (Streak, XP, Credits)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatPill(
                        icon = "🔥",
                        value = "${prefs.streak}d",
                        label = "Streak",
                        color = Color(0xFFFF8800),
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        icon = "⚡",
                        value = "${prefs.xp}",
                        label = "XP",
                        color = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        icon = "🪙",
                        value = "${prefs.coins}",
                        label = "Credits",
                        color = Color(0xFFFFD700),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ==========================================
        // PERSONAL TARGETS & TRAINING GOALS
    var selectedGoalFilter by remember { mutableStateOf("All") }

        // ==========================================
        // 4. PERSONAL TARGET GOALS (EXPANDED ENGINE)
        // ==========================================
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🎯 Personal Target Goals",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryCyberColor
                    )
                    Text(
                        text = "${targetGoals.count { it.isCompleted }}/${targetGoals.size} targets mastered",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(primaryCyberColor.copy(alpha = 0.18f))
                        .border(1.dp, primaryCyberColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .clickable {
                            SoundManager.playClick()
                            showAddGoalDialog = true
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Goal",
                            tint = primaryCyberColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "+ New Target",
                            color = primaryCyberColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Filter Tabs: All, Active, Completed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "All (${targetGoals.size})" to "All",
                    "Active (${targetGoals.count { !it.isCompleted }})" to "Active",
                    "Completed (${targetGoals.count { it.isCompleted }})" to "Completed"
                ).forEach { (label, key) ->
                    val isSel = selectedGoalFilter == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) primaryCyberColor.copy(alpha = 0.2f) else Color(0xFF090E17))
                            .border(1.dp, if (isSel) primaryCyberColor else Color(0x2200F0FF), RoundedCornerShape(6.dp))
                            .clickable {
                                SoundManager.playClick()
                                selectedGoalFilter = key
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) primaryCyberColor else TextSecondary
                        )
                    }
                }
            }

            val displayedGoals = remember(targetGoals, selectedGoalFilter) {
                when (selectedGoalFilter) {
                    "Active" -> targetGoals.filter { !it.isCompleted }
                    "Completed" -> targetGoals.filter { it.isCompleted }
                    else -> targetGoals
                }
            }

            if (displayedGoals.isEmpty()) {
                FuturisticCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = primaryCyberColor.copy(alpha = 0.25f),
                    themePreset = prefs.themePreset,
                    glassOpacity = prefs.glassOpacity,
                    cornerRadius = prefs.cardCornerRadius.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🎯 No goals in this view.", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            text = "Tap '+ New Target' above to challenge your focus!",
                            color = primaryCyberColor,
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    displayedGoals.forEach { goal ->
                        TargetGoalItem(
                            goal = goal,
                            primaryColor = primaryCyberColor,
                            themePreset = prefs.themePreset,
                            glassOpacity = prefs.glassOpacity,
                            cornerRadius = prefs.cardCornerRadius.dp,
                            onPlayDrill = {
                                SoundManager.playClick()
                                val drillGame = AppCatalog.games.find { it.category.equals(goal.category, ignoreCase = true) } ?: AppCatalog.games.first()
                                viewModel.openGame(drillGame)
                            },
                            onIncrement = {
                                viewModel.incrementGoalProgress(goal.id)
                            },
                            onClaimReward = {
                                viewModel.claimTargetReward(goal.id)
                            },
                            onDelete = {
                                viewModel.deleteTargetGoal(goal.id)
                            }
                        )
                    }
                }
            }
        }

        // DAILY NEURAL WORKOUT (FEATURED DRILL)
        val dailyGame = remember {
            AppCatalog.games.find { it.id == "speed_math" } ?: AppCatalog.games.first()
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Neural Challenge",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryCyberColor
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+100 XP Bonus",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                }
            }

            FuturisticCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = primaryCyberColor,
                glowColor = primaryCyberColor,
                animStyle = BorderAnimStyle.SWEEP,
                isAnimated = prefs.enableAllAnimations,
                themePreset = prefs.themePreset,
                glassOpacity = prefs.glassOpacity,
                cornerRadius = prefs.cardCornerRadius.dp,
                onClick = {
                    SoundManager.playClick()
                    viewModel.openGame(dailyGame)
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(primaryCyberColor.copy(alpha = 0.15f))
                            .border(1.dp, primaryCyberColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = dailyGame.iconEmoji, fontSize = 24.sp)
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = dailyGame.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = dailyGame.description,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(primaryCyberColor)
                            .clickable {
                                SoundManager.playClick()
                                viewModel.openGame(dailyGame)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Drill",
                            tint = Color(0xFF04070D),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // QUICK PLAY DRILLS CAROUSEL
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡ Quick Play Drills",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryCyberColor
                )
                Text(
                    text = "See All (${AppCatalog.games.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryCyberColor,
                    modifier = Modifier.clickable {
                        SoundManager.playClick()
                        viewModel.selectTab(NavTab.GAMES)
                    }
                )
            }

            val quickGames = listOf("memory_grid", "reaction_time", "card_flipper", "trail_blazer", "pattern_match", "code_breaker")
                .mapNotNull { id -> AppCatalog.games.find { it.id == id } }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                quickGames.forEach { game ->
                    FuturisticCard(
                        modifier = Modifier.width(125.dp),
                        contentPadding = 10.dp,
                        borderColor = primaryCyberColor.copy(alpha = 0.35f),
                        themePreset = prefs.themePreset,
                        glassOpacity = prefs.glassOpacity,
                        cornerRadius = prefs.cardCornerRadius.dp,
                        onClick = {
                            SoundManager.playClick()
                            viewModel.openGame(game)
                        }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = game.iconEmoji, fontSize = 28.sp)
                            Text(
                                text = game.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = game.category,
                                fontSize = 10.sp,
                                color = primaryCyberColor,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // TELEMETRY SUMMARY CARD
        FuturisticCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = primaryCyberColor.copy(alpha = 0.35f),
            themePreset = prefs.themePreset,
            glassOpacity = prefs.glassOpacity,
            cornerRadius = prefs.cardCornerRadius.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Training Telemetry",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Analytics →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryCyberColor,
                        modifier = Modifier.clickable {
                            SoundManager.playClick()
                            viewModel.selectTab(NavTab.ANALYTICS)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricBox(
                        title = "Completed",
                        value = "$gamesPlayedCount",
                        color = primaryCyberColor,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Peak Score",
                        value = "${bestScoreVal ?: 0}",
                        color = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Daily Target",
                        value = "${(gamesPlayedCount % 5)}/5",
                        color = NeonPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(96.dp))
    }

    // ==========================================
    // EXPANDED TARGET GOAL ARCHITECTURE DIALOG
    // ==========================================
    if (showAddGoalDialog) {
        var goalTitle by remember { mutableStateOf("") }
        var goalEmoji by remember { mutableStateOf("🎯") }
        var goalCategory by remember { mutableStateOf("All") }
        var targetType by remember { mutableStateOf("Drills") }
        var targetCount by remember { mutableFloatStateOf(5f) }
        var frequency by remember { mutableStateOf("Daily") }
        var difficulty by remember { mutableStateOf("Normal") }
        var rewardCoins by remember { mutableFloatStateOf(60f) }
        var rewardXp by remember { mutableFloatStateOf(120f) }
        var goalNote by remember { mutableStateOf("") }

        val emojiChoices = listOf("🎯", "🧠", "⚡", "🔥", "🏆", "🚀", "💎", "⚔️")
        val categoryChoices = listOf("All", "Memory", "Logic", "Focus", "Reflex", "Math")
        val typeChoices = listOf("Drills", "Score", "Streak", "Speed")
        val frequencyChoices = listOf("Daily", "Weekly", "Monthly", "Quest")
        val difficultyChoices = listOf("Normal", "Hard", "Elite")

        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = goalEmoji, fontSize = 22.sp)
                    Column {
                        Text(
                            text = "New Target Goal Hub",
                            color = primaryCyberColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Define your custom neural challenge & unlock rewards",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Quick Preset Chips
                    Text(text = "⚡ Quick Preset Blueprints", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("🧠 Memory Master", "Memory", 5),
                            Triple("⚡ Reflex Rush", "Focus", 4),
                            Triple("🔥 Daily Streak", "All", 7),
                            Triple("🏆 Logic Pioneer", "Logic", 3),
                            Triple("🎯 Precision Sniper", "All", 8)
                        ).forEach { (pTitle, pCat, pCount) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, primaryCyberColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        SoundManager.playClick()
                                        goalTitle = pTitle
                                        goalCategory = pCat
                                        targetCount = pCount.toFloat()
                                        goalEmoji = pTitle.take(2).trim()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(text = pTitle, fontSize = 10.sp, color = primaryCyberColor, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // Emoji Icon Picker
                    Text(text = "Choose Target Icon", fontSize = 11.sp, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        emojiChoices.forEach { em ->
                            val isSel = goalEmoji == em
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (isSel) primaryCyberColor.copy(alpha = 0.25f) else Color(0xFF0A1220))
                                    .border(if (isSel) 1.5.dp else 1.dp, if (isSel) primaryCyberColor else Color(0x3300F0FF), CircleShape)
                                    .clickable {
                                        SoundManager.playClick()
                                        goalEmoji = em
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = em, fontSize = 16.sp)
                            }
                        }
                    }

                    // Goal Title input
                    Text(text = "Goal Name / Challenge", fontSize = 11.sp, color = TextSecondary)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF090E17))
                            .border(1.dp, primaryCyberColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        if (goalTitle.isEmpty()) {
                            Text(text = "e.g., Master 5 Logic Drills", color = Color(0xFF64748B), fontSize = 12.sp)
                        }
                        BasicTextField(
                            value = goalTitle,
                            onValueChange = { goalTitle = it },
                            singleLine = true,
                            textStyle = TextStyle(color = TextPrimary, fontSize = 13.sp),
                            cursorBrush = SolidColor(primaryCyberColor),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Category Pill selector
                    Text(text = "Target Domain", fontSize = 11.sp, color = TextSecondary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categoryChoices.forEach { cat ->
                            val isSel = goalCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) primaryCyberColor.copy(alpha = 0.2f) else Color(0xFF0A1220))
                                    .border(1.dp, if (isSel) primaryCyberColor else Color(0x3300F0FF), RoundedCornerShape(6.dp))
                                    .clickable {
                                        SoundManager.playClick()
                                        goalCategory = cat
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) primaryCyberColor else TextSecondary
                                )
                            }
                        }
                    }

                    // Target Type selector
                    Text(text = "Target Type", fontSize = 11.sp, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        typeChoices.forEach { t ->
                            val isSel = targetType == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) NeonGreen.copy(alpha = 0.2f) else Color(0xFF0A1220))
                                    .border(1.dp, if (isSel) NeonGreen else Color(0x3300FF88), RoundedCornerShape(6.dp))
                                    .clickable {
                                        SoundManager.playClick()
                                        targetType = t
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = t,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) NeonGreen else TextSecondary
                                )
                            }
                        }
                    }

                    // Target Count Slider
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Target Milestones", fontSize = 11.sp, color = TextSecondary)
                            Text(text = "${targetCount.toInt()} $targetType", fontSize = 11.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                        }
                        CyberSlider(
                            value = targetCount,
                            onValueChange = { targetCount = it },
                            valueRange = 1f..25f,
                            accentColor = primaryCyberColor
                        )
                    }

                    // Frequency & Difficulty Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Frequency
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Schedule", fontSize = 11.sp, color = TextSecondary)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                frequencyChoices.take(2).forEach { f ->
                                    val isSel = frequency == f
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSel) primaryCyberColor.copy(alpha = 0.2f) else Color(0xFF0A1220))
                                            .border(1.dp, if (isSel) primaryCyberColor else Color(0x3300F0FF), RoundedCornerShape(6.dp))
                                            .clickable { frequency = f }
                                            .padding(vertical = 5.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = f, fontSize = 10.sp, color = if (isSel) primaryCyberColor else TextSecondary)
                                    }
                                }
                            }
                        }

                        // Difficulty
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Tier", fontSize = 11.sp, color = TextSecondary)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                difficultyChoices.take(2).forEach { d ->
                                    val isSel = difficulty == d
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSel) NeonPurple.copy(alpha = 0.2f) else Color(0xFF0A1220))
                                            .border(1.dp, if (isSel) NeonPurple else Color(0x33A855F7), RoundedCornerShape(6.dp))
                                            .clickable { difficulty = d }
                                            .padding(vertical = 5.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = d, fontSize = 10.sp, color = if (isSel) NeonPurple else TextSecondary)
                                    }
                                }
                            }
                        }
                    }

                    // Reward Incentive
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Completion Reward:", fontSize = 11.sp, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "+${rewardCoins.toInt()} 🪙", fontSize = 11.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                            Text(text = "+${rewardXp.toInt()} XP", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Optional Strategy Note
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF090E17))
                            .border(1.dp, Color(0x2200F0FF), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        if (goalNote.isEmpty()) {
                            Text(text = "Strategy note (optional)", color = Color(0xFF475569), fontSize = 11.sp)
                        }
                        BasicTextField(
                            value = goalNote,
                            onValueChange = { goalNote = it },
                            singleLine = true,
                            textStyle = TextStyle(color = TextSecondary, fontSize = 11.sp),
                            cursorBrush = SolidColor(primaryCyberColor),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val finalTitle = goalTitle.ifBlank { "Personal $goalCategory Target" }
                        viewModel.addTargetGoal(
                            title = finalTitle.trim(),
                            category = goalCategory,
                            targetCount = targetCount.toInt(),
                            frequency = frequency,
                            targetType = targetType,
                            rewardCoins = rewardCoins.toInt(),
                            rewardXp = rewardXp.toInt(),
                            iconEmoji = goalEmoji,
                            difficulty = difficulty,
                            note = goalNote.trim()
                        )
                        showAddGoalDialog = false
                    }
                ) {
                    Text("Save & Activate", color = primaryCyberColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGoalDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF0C1420),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun TargetGoalItem(
    goal: UserTargetGoal,
    primaryColor: Color,
    themePreset: String,
    glassOpacity: Float,
    cornerRadius: androidx.compose.ui.unit.Dp,
    onPlayDrill: () -> Unit,
    onIncrement: () -> Unit,
    onClaimReward: () -> Unit,
    onDelete: () -> Unit
) {
    FuturisticCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 12.dp,
        borderColor = if (goal.isCompleted) NeonGreen.copy(alpha = 0.7f) else primaryColor.copy(alpha = 0.35f),
        themePreset = themePreset,
        glassOpacity = glassOpacity,
        cornerRadius = cornerRadius
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Emoji Icon Badge
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (goal.isCompleted) NeonGreen.copy(alpha = 0.18f) else primaryColor.copy(alpha = 0.14f))
                            .border(1.dp, if (goal.isCompleted) NeonGreen else primaryColor.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = goal.iconEmoji, fontSize = 16.sp)
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = goal.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (goal.isCompleted) NeonGreen else TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (goal.isCompleted) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(NeonGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(text = "Achieved!", fontSize = 9.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${goal.frequency} • ${goal.category} • ${goal.currentCount}/${goal.targetCount} ${goal.targetType}",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            // Reward Chip
                            Text(
                                text = "+${goal.rewardCoins}🪙 +${goal.rewardXp}XP",
                                fontSize = 9.sp,
                                color = Color(0xFFF59E0B),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Play drill button
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.2f))
                            .border(1.dp, primaryColor, CircleShape)
                            .clickable(onClick = onPlayDrill),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Target Drill",
                            tint = primaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (!goal.isCompleted) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(primaryColor.copy(alpha = 0.15f))
                                .border(1.dp, primaryColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .clickable(onClick = onIncrement)
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Text(text = "+1", fontSize = 11.sp, color = primaryColor, fontWeight = FontWeight.Bold)
                        }
                    } else if (!goal.isRewardClaimed) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonGreen.copy(alpha = 0.25f))
                                .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                                .clickable(onClick = onClaimReward)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "🎁 Claim", fontSize = 10.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(text = "✅ Done", fontSize = 9.sp, color = Color(0xFF94A3B8))
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Goal",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            val progressRatio = (goal.currentCount.toFloat() / goal.targetCount.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (goal.isCompleted) NeonGreen else primaryColor,
                trackColor = Color(0xFF0F1A28)
            )
        }
    }
}

@Composable
private fun StatPill(
    icon: String,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0A1220))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(vertical = 7.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = icon, fontSize = 13.sp)
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(text = title, fontSize = 10.sp, color = TextSecondary)
    }
}
