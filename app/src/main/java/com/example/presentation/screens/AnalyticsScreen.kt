package com.example.presentation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.viewmodel.MainViewModel
import com.example.ui.components.BorderAnimStyle
import com.example.ui.components.CognitiveRadarChart
import com.example.ui.components.FuturisticCard
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val prefs by viewModel.userPreferences.collectAsState()
    val totalDrills by viewModel.totalGamesPlayed.collectAsState()
    val bestScore by viewModel.bestScore.collectAsState()
    val scores by viewModel.gameScores.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val targetGoals by viewModel.targetGoals.collectAsState()

    val primaryCyberColor = com.example.parseCyberColor(prefs.primaryColor, NeonCyan)

    val focusScore = if (scores.isEmpty()) 0f else (scores.filter { it.gameId in listOf("pattern_match", "speed_math") }.map { it.score }.average() / 800.0).toFloat().coerceIn(0f, 1.0f)
    val accuracyScore = if (scores.isEmpty()) 0f else (scores.map { it.score }.average() / 750.0).toFloat().coerceIn(0f, 1.0f)
    val speedScore = if (scores.isEmpty()) 0f else (scores.filter { it.gameId in listOf("reaction_time", "color_clash") }.map { it.score }.average() / 700.0).toFloat().coerceIn(0f, 1.0f)
    val reflexScore = if (scores.isEmpty()) 0f else (scores.filter { it.gameId in listOf("reaction_time", "trail_blazer") }.map { it.score }.average() / 720.0).toFloat().coerceIn(0f, 1.0f)
    val logicScore = if (scores.isEmpty()) 0f else (scores.filter { it.gameId in listOf("code_breaker", "number_sequence", "spatial_spin") }.map { it.score }.average() / 720.0).toFloat().coerceIn(0f, 1.0f)
    val memoryScore = if (scores.isEmpty()) 0f else (scores.filter { it.gameId in listOf("memory_grid", "card_flipper", "trail_blazer", "dual_n_back") }.map { it.score }.average() / 650.0).toFloat().coerceIn(0f, 1.0f)

    val animatedXpProgress by animateFloatAsState(
        targetValue = ((prefs.xp % 100) / 100f).coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "xp_progress"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Header (No leaderboard!)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Cognitive Analytics",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = primaryCyberColor
            )
            Text(
                text = "6-Dimensional Neuroplasticity Telemetry & Records",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // 6-Axis Radar Chart Hero Card
        FuturisticCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = primaryCyberColor,
            glowColor = primaryCyberColor,
            animStyle = BorderAnimStyle.SWEEP,
            isAnimated = prefs.enableAllAnimations,
            themePreset = prefs.themePreset,
            glassOpacity = prefs.glassOpacity,
            cornerRadius = prefs.cardCornerRadius.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "6-Axis Neural Radar Profile",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryCyberColor
                )

                CognitiveRadarChart(
                    focus = focusScore,
                    accuracy = accuracyScore,
                    speed = speedScore,
                    reflex = reflexScore,
                    logic = logicScore,
                    memory = memoryScore,
                    chartColor = primaryCyberColor,
                    modifier = Modifier.size(240.dp)
                )

                // 6 Metrics in 2 balanced rows so badges never overflow!
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ScoreBadge("Focus", "${(focusScore * 100).toInt()}%", primaryCyberColor)
                    ScoreBadge("Accuracy", "${(accuracyScore * 100).toInt()}%", NeonGreen)
                    ScoreBadge("Speed", "${(speedScore * 100).toInt()}%", Color(0xFFFFCC00))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ScoreBadge("Reflex", "${(reflexScore * 100).toInt()}%", Color(0xFFFF6600))
                    ScoreBadge("Logic", "${(logicScore * 100).toInt()}%", NeonPurple)
                    ScoreBadge("Memory", "${(memoryScore * 100).toInt()}%", Color(0xFF38BDF8))
                }
            }
        }

        // Level & Rank Progression Card
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Level ${(prefs.xp / 100) + 1} • ${prefs.userRankTitle}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${prefs.xp % 100} / 100 XP to next tier",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(primaryCyberColor.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${prefs.xp} Total XP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryCyberColor
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { animatedXpProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = primaryCyberColor,
                    trackColor = Color(0xFF101C2B)
                )
            }
        }

        // Drills & High Scores Overview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "Total Sessions",
                value = "$totalDrills",
                sublabel = "Drills completed",
                color = primaryCyberColor,
                themePreset = prefs.themePreset,
                glassOpacity = prefs.glassOpacity,
                cornerRadius = prefs.cardCornerRadius.dp,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "All-Time Best",
                value = "${bestScore ?: 0}",
                sublabel = "Highest score",
                color = NeonGreen,
                themePreset = prefs.themePreset,
                glassOpacity = prefs.glassOpacity,
                cornerRadius = prefs.cardCornerRadius.dp,
                modifier = Modifier.weight(1f)
            )
        }

        // Active Personal Targets Overview
        FuturisticCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = Color(0x3300F0FF),
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
                        text = "🎯 Active Target Goals",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "${targetGoals.count { it.isCompleted }} / ${targetGoals.size} Achieved",
                        fontSize = 12.sp,
                        color = NeonGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                targetGoals.take(3).forEach { goal ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = goal.title,
                            fontSize = 13.sp,
                            color = if (goal.isCompleted) NeonGreen else TextSecondary
                        )
                        Text(
                            text = "${goal.currentCount}/${goal.targetCount}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (goal.isCompleted) NeonGreen else primaryCyberColor
                        )
                    }
                }
            }
        }

        // Unlimited Achievements & Milestones Section
        var achievementFilter by remember { mutableStateOf("All") }
        val displayedAchievements = remember(achievements, achievementFilter) {
            when (achievementFilter) {
                "Unlocked" -> achievements.filter { it.isUnlocked }
                "In Progress" -> achievements.filter { !it.isUnlocked }
                "Ascension" -> achievements.filter { it.category == "Ascension" }
                "Mastery" -> achievements.filter { it.category == "Mastery" }
                "Streak" -> achievements.filter { it.category == "Streak" }
                "Score" -> achievements.filter { it.category == "Score" }
                "XP" -> achievements.filter { it.category == "XP" }
                else -> achievements
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🏆 Unlimited Achievements",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryCyberColor
                    )
                    Text(
                        text = "Dynamic infinite tier progression",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(primaryCyberColor.copy(alpha = 0.15f))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${achievements.count { it.isUnlocked }} / ${achievements.size} Unlocked",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryCyberColor
                    )
                }
            }

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filters = listOf(
                    "All" to achievements.size,
                    "Unlocked" to achievements.count { it.isUnlocked },
                    "In Progress" to achievements.count { !it.isUnlocked },
                    "Ascension" to achievements.count { it.category == "Ascension" },
                    "Mastery" to achievements.count { it.category == "Mastery" },
                    "Streak" to achievements.count { it.category == "Streak" },
                    "Score" to achievements.count { it.category == "Score" },
                    "XP" to achievements.count { it.category == "XP" }
                )

                filters.forEach { (cat, count) ->
                    val isSel = achievementFilter == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) primaryCyberColor.copy(alpha = 0.25f) else Color(0xFF0A101D))
                            .border(1.dp, if (isSel) primaryCyberColor else Color(0x2500F0FF), RoundedCornerShape(8.dp))
                            .clickable { achievementFilter = cat }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$cat ($count)",
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) primaryCyberColor else TextSecondary
                        )
                    }
                }
            }

            displayedAchievements.forEach { achievement ->
                FuturisticCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (achievement.isUnlocked) primaryCyberColor.copy(alpha = 0.7f) else Color(0x2200F0FF),
                    themePreset = prefs.themePreset,
                    glassOpacity = prefs.glassOpacity,
                    cornerRadius = prefs.cardCornerRadius.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (achievement.isUnlocked) primaryCyberColor.copy(alpha = 0.18f) else Color(0xFF0B1320))
                                    .border(1.dp, if (achievement.isUnlocked) primaryCyberColor else Color(0x3300F0FF), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = achievement.iconEmoji, fontSize = 24.sp)
                            }

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (achievement.isUnlocked) NeonGreen.copy(alpha = 0.2f) else Color(0xFF1E293B))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = achievement.tierLabel,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (achievement.isUnlocked) NeonGreen else Color(0xFF94A3B8)
                                        )
                                    }

                                    Text(
                                        text = achievement.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (achievement.isUnlocked) TextPrimary else Color(0xFF94A3B8)
                                    )

                                    if (achievement.isUnlocked) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = NeonGreen,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = achievement.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 14.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (achievement.isUnlocked) NeonGreen.copy(alpha = 0.15f) else Color(0xFF101926))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (achievement.isUnlocked) "UNLOCKED" else achievement.progressText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (achievement.isUnlocked) NeonGreen else Color(0xFF94A3B8)
                                    )
                                }
                                Text(
                                    text = "+${achievement.xpReward} XP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryCyberColor
                                )
                            }
                        }

                        // Progress Bar (if not unlocked)
                        if (!achievement.isUnlocked) {
                            LinearProgressIndicator(
                                progress = { achievement.progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = primaryCyberColor,
                                trackColor = Color(0xFF0F172A)
                            )
                        }
                    }
                }
            }

            // Infinite Ascension Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF07111E))
                    .border(1.dp, primaryCyberColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "♾️", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "Infinite Ascension Architecture Active",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryCyberColor
                        )
                        Text(
                            text = "Milestones scale indefinitely with your neural progress with zero cap.",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(96.dp))
    }
}

@Composable
private fun ScoreBadge(title: String, score: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = score, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = color)
        Text(text = title, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    sublabel: String,
    color: Color,
    themePreset: String,
    glassOpacity: Float,
    cornerRadius: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    FuturisticCard(
        modifier = modifier,
        borderColor = color.copy(alpha = 0.35f),
        themePreset = themePreset,
        glassOpacity = glassOpacity,
        cornerRadius = cornerRadius
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, fontSize = 11.sp, color = TextSecondary)
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(text = sublabel, fontSize = 10.sp, color = Color(0xFF64748B))
        }
    }
}
