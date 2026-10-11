package com.example.domain.models

import com.example.data.local.GameScoreEntity
import com.example.data.local.UserPreferences

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val category: String, // "Mastery", "Streak", "Score", "XP", "Speed", "Ascension"
    val tierLabel: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val isUnlocked: Boolean,
    val progressText: String,
    val xpReward: Int
) {
    val progressFraction: Float
        get() = if (targetProgress <= 0) 1f else (currentProgress.toFloat() / targetProgress).coerceIn(0f, 1f)
}

object AchievementEngine {

    fun generateUnlimitedAchievements(
        prefs: UserPreferences,
        totalDrillsPlayed: Int,
        scores: List<GameScoreEntity>,
        targetGoals: List<UserTargetGoal>
    ): List<AchievementItem> {
        val list = mutableListOf<AchievementItem>()

        // 1. DRILL MASTERY TIERS (1, 3, 5, 10, 25, 50, 100, 250, 500, 1000)
        val drillMilestones = listOf(
            Triple(1, "Neural Genesis", "Complete your first cognitive training drill"),
            Triple(3, "Synaptic Ignition", "Complete 3 cognitive drills"),
            Triple(5, "Cognitive Apprentice", "Complete 5 neural drill sessions"),
            Triple(10, "Mental Veteran", "Complete 10 high-performance drills"),
            Triple(25, "Neocortex Ace", "Complete 25 cognitive drill sessions"),
            Triple(50, "Cerebral Centurion", "Complete 50 brain training sessions"),
            Triple(100, "Neural Overlord", "Complete 100 intensive neural drills"),
            Triple(250, "Cyber Paragon", "Complete 250 neural drill sessions"),
            Triple(500, "Quantum Immortal", "Complete 500 cognitive drill sessions"),
            Triple(1000, "Transcendent Mind", "Complete 1,000 elite cognitive sessions")
        )

        drillMilestones.forEachIndexed { index, (target, title, desc) ->
            val unlocked = totalDrillsPlayed >= target
            list.add(
                AchievementItem(
                    id = "drills_$target",
                    title = title,
                    description = desc,
                    iconEmoji = when {
                        target >= 500 -> "👑"
                        target >= 100 -> "🧠"
                        target >= 25 -> "⚡"
                        else -> "🌱"
                    },
                    category = "Mastery",
                    tierLabel = "Tier ${index + 1}",
                    currentProgress = totalDrillsPlayed.coerceAtMost(target),
                    targetProgress = target,
                    isUnlocked = unlocked,
                    progressText = "$totalDrillsPlayed / $target Drills",
                    xpReward = (index + 1) * 75
                )
            )
        }

        // 2. SYNAPTIC STREAKS (1, 3, 7, 14, 30, 60, 100, 365 Days)
        val streakMilestones = listOf(
            Triple(1, "Day One Protocol", "Train for your initial active day"),
            Triple(3, "Synaptic Flow", "Maintain a consecutive training streak of 3 days"),
            Triple(7, "Weekly Habit", "Maintain a continuous 7-day training streak"),
            Triple(14, "Bi-Weekly Master", "Train every single day for 14 straight days"),
            Triple(30, "Iron Habit", "30 consecutive days of unwavering neural training"),
            Triple(60, "Synaptic Dynasty", "60 consecutive days of brain optimization"),
            Triple(100, "Centurion Streak", "100 consecutive days of daily training"),
            Triple(365, "Eternal Neural Flame", "365 continuous days of neural conditioning")
        )

        streakMilestones.forEachIndexed { index, (target, title, desc) ->
            val unlocked = prefs.streak >= target
            list.add(
                AchievementItem(
                    id = "streak_$target",
                    title = title,
                    description = desc,
                    iconEmoji = when {
                        target >= 100 -> "🌟"
                        target >= 14 -> "🔥"
                        else -> "✨"
                    },
                    category = "Streak",
                    tierLabel = "Tier ${index + 1}",
                    currentProgress = prefs.streak.coerceAtMost(target),
                    targetProgress = target,
                    isUnlocked = unlocked,
                    progressText = "${prefs.streak} / $target Days",
                    xpReward = (index + 1) * 120
                )
            )
        }

        // 3. HIGH VOLTAGE SCORE MILESTONES (250, 500, 750, 1000, 1500, 2000, 3000, 5000)
        val scoreMilestones = listOf(
            Triple(250, "Voltage Spark", "Reach a high score of 250 points"),
            Triple(500, "High Voltage", "Reach a score over 500 points in any drill"),
            Triple(750, "Superconductor", "Surpass 750 points in a single drill session"),
            Triple(1000, "Gigawatt Intellect", "Break the 1,000 point barrier"),
            Triple(1500, "Terawatt Synapse", "Achieve 1,500 points in a neural drill"),
            Triple(2000, "Apex Score Master", "Reach 2,000 points in any cognitive game"),
            Triple(3000, "Quantum Supercluster", "Reach 3,000 points in cognitive training"),
            Triple(5000, "Singularity Highscore", "Score an astronomical 5,000 points")
        )

        val bestScore = prefs.bestScore
        scoreMilestones.forEachIndexed { index, (target, title, desc) ->
            val unlocked = bestScore >= target
            list.add(
                AchievementItem(
                    id = "score_$target",
                    title = title,
                    description = desc,
                    iconEmoji = "⚡",
                    category = "Score",
                    tierLabel = "Tier ${index + 1}",
                    currentProgress = bestScore.coerceAtMost(target),
                    targetProgress = target,
                    isUnlocked = unlocked,
                    progressText = "$bestScore / $target pts",
                    xpReward = (index + 1) * 100
                )
            )
        }

        // 4. QUANTUM XP ASCENSION (100, 300, 600, 1000, 2500, 5000, 10000, 25000, 50000)
        val xpMilestones = listOf(
            Triple(100, "Data Apprentice", "Accumulate 100 XP from training drills"),
            Triple(300, "Pattern Node", "Accumulate 300 XP"),
            Triple(600, "Synaptic Conduit", "Accumulate 600 XP"),
            Triple(1000, "Quantum Cortex", "Reach 1,000 lifetime XP in neural training"),
            Triple(2500, "Neural Sovereign", "Reach 2,500 lifetime XP"),
            Triple(5000, "Cognitive Luminary", "Reach 5,000 lifetime XP"),
            Triple(10000, "Hyper-Dimensional Mind", "Surpass 10,000 lifetime XP"),
            Triple(25000, "Universal Omniscient", "Reach 25,000 lifetime XP"),
            Triple(50000, "Cosmic Consciousness", "Attain 50,000 lifetime XP")
        )

        xpMilestones.forEachIndexed { index, (target, title, desc) ->
            val unlocked = prefs.xp >= target
            list.add(
                AchievementItem(
                    id = "xp_$target",
                    title = title,
                    description = desc,
                    iconEmoji = "🌌",
                    category = "XP",
                    tierLabel = "Tier ${index + 1}",
                    currentProgress = prefs.xp.coerceAtMost(target),
                    targetProgress = target,
                    isUnlocked = unlocked,
                    progressText = "${prefs.xp} / $target XP",
                    xpReward = (index + 1) * 90
                )
            )
        }

        // 5. REACTION VELOCITY & ACCURACY (Reaction time sub-milestones)
        val reactionScores = scores.filter { it.gameId == "reaction_time" }
        val bestReactionScore = reactionScores.maxOfOrNull { it.score } ?: 0

        val speedMilestones = listOf(
            Triple(300, "Quick Reflex", "Achieve a fast score in Reaction Time (sub-350ms equivalent)"),
            Triple(400, "Reflex Monarch", "Achieve elite reaction score (sub-250ms equivalent)"),
            Triple(500, "Light-Speed Synapse", "Achieve supersonic speed score (sub-200ms equivalent)"),
            Triple(600, "Tachyon Reflex", "Achieve godly sub-160ms reaction speed")
        )

        speedMilestones.forEachIndexed { index, (target, title, desc) ->
            val unlocked = bestReactionScore >= target
            list.add(
                AchievementItem(
                    id = "speed_$target",
                    title = title,
                    description = desc,
                    iconEmoji = "⏱️",
                    category = "Speed",
                    tierLabel = "Tier ${index + 1}",
                    currentProgress = bestReactionScore.coerceAtMost(target),
                    targetProgress = target,
                    isUnlocked = unlocked,
                    progressText = if (unlocked) "Unlocked" else "$bestReactionScore / $target pts",
                    xpReward = (index + 1) * 150
                )
            )
        }

        // 6. TARGET GOALS & MASTERY MILESTONES
        val completedGoalsCount = targetGoals.count { it.isCompleted }
        val goalMilestones = listOf(
            Triple(1, "Target Locked", "Complete your first daily custom goal"),
            Triple(3, "Routine Architect", "Complete 3 customized training goals"),
            Triple(5, "Objective Master", "Complete 5 daily target goals"),
            Triple(10, "Discipline Sovereign", "Complete 10 custom target goals")
        )

        goalMilestones.forEachIndexed { index, (target, title, desc) ->
            val unlocked = completedGoalsCount >= target
            list.add(
                AchievementItem(
                    id = "goals_$target",
                    title = title,
                    description = desc,
                    iconEmoji = "🎯",
                    category = "Mastery",
                    tierLabel = "Tier ${index + 1}",
                    currentProgress = completedGoalsCount.coerceAtMost(target),
                    targetProgress = target,
                    isUnlocked = unlocked,
                    progressText = "$completedGoalsCount / $target Goals",
                    xpReward = (index + 1) * 110
                )
            )
        }

        // 7. DYNAMIC INFINITE ASCENSION TIERS (Calculated infinitely ahead of the user!)
        // Whenever the user achieves high counts, infinite tiers generate with dynamic formulas!
        val highestDrillTier = maxOf(totalDrillsPlayed, 1000)
        val infiniteDrillLevels = listOf(
            (highestDrillTier + 50),
            (highestDrillTier + 150),
            (highestDrillTier + 300),
            (highestDrillTier + 600),
            (highestDrillTier + 1200)
        )

        infiniteDrillLevels.forEachIndexed { index, targetVal ->
            val levelRank = (highestDrillTier / 100) + index + 1
            val unlocked = totalDrillsPlayed >= targetVal
            list.add(
                AchievementItem(
                    id = "ascension_drill_$targetVal",
                    title = "Ascension Master Rank $levelRank",
                    description = "Reach infinite drill milestone of $targetVal completed sessions",
                    iconEmoji = "🔮",
                    category = "Ascension",
                    tierLabel = "Ascension $levelRank",
                    currentProgress = totalDrillsPlayed.coerceAtMost(targetVal),
                    targetProgress = targetVal,
                    isUnlocked = unlocked,
                    progressText = "$totalDrillsPlayed / $targetVal Drills",
                    xpReward = 500 + index * 200
                )
            )
        }

        val highestXpTier = maxOf(prefs.xp, 50000)
        val infiniteXpLevels = listOf(
            highestXpTier + 10000,
            highestXpTier + 25000,
            highestXpTier + 50000
        )

        infiniteXpLevels.forEachIndexed { index, targetVal ->
            val rankIndex = (highestXpTier / 10000) + index + 1
            val unlocked = prefs.xp >= targetVal
            list.add(
                AchievementItem(
                    id = "ascension_xp_$targetVal",
                    title = "Quantum Godmind Rank $rankIndex",
                    description = "Attain astronomical $targetVal lifetime neural XP",
                    iconEmoji = "🪐",
                    category = "Ascension",
                    tierLabel = "Ascension $rankIndex",
                    currentProgress = prefs.xp.coerceAtMost(targetVal),
                    targetProgress = targetVal,
                    isUnlocked = unlocked,
                    progressText = "${prefs.xp} / $targetVal XP",
                    xpReward = 1000 + index * 500
                )
            )
        }

        return list
    }
}
