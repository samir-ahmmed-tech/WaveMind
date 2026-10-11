package com.example.domain.models

data class GameItem(
    val id: String,
    val title: String,
    val category: String, // "Memory", "Logic", "Focus", "Reflex", "Speed"
    val difficulty: String, // "Easy", "Medium", "Hard"
    val description: String,
    val iconEmoji: String,
    val isFavorite: Boolean = false
)

object AppCatalog {
    val games: List<GameItem> = listOf(
        GameItem(
            id = "memory_grid",
            title = "Memory Grid",
            category = "Memory",
            difficulty = "Medium",
            description = "Memorize and repeat neon tile patterns on an expanding cyber grid.",
            iconEmoji = "🧠"
        ),
        GameItem(
            id = "card_flipper",
            title = "Card Flipper",
            category = "Memory",
            difficulty = "Medium",
            description = "Uncover and match cybernetic symbols with minimal turns.",
            iconEmoji = "🃏"
        ),
        GameItem(
            id = "trail_blazer",
            title = "Trail Blazer",
            category = "Memory",
            difficulty = "Hard",
            description = "Memorize and retrace a neural pathway that lengthens each round.",
            iconEmoji = "👣"
        ),
        GameItem(
            id = "dual_n_back",
            title = "Dual N-Back",
            category = "Memory",
            difficulty = "Hard",
            description = "Clinical working memory exercise tracking spatial positions and auditory cues.",
            iconEmoji = "🧬"
        ),
        GameItem(
            id = "code_breaker",
            title = "Code Breaker",
            category = "Logic",
            difficulty = "Medium",
            description = "Deduce the encrypted color passcode using position and color feedback.",
            iconEmoji = "🔐"
        ),
        GameItem(
            id = "number_sequence",
            title = "Number Sequence",
            category = "Logic",
            difficulty = "Medium",
            description = "Calculate the missing number in complex mathematical series.",
            iconEmoji = "🔢"
        ),
        GameItem(
            id = "spatial_spin",
            title = "Spatial Spin",
            category = "Logic",
            difficulty = "Hard",
            description = "Mentally rotate 3D geometric shapes to identify matches under time pressure.",
            iconEmoji = "🔄"
        ),
        GameItem(
            id = "pattern_match",
            title = "Pattern Match",
            category = "Focus",
            difficulty = "Medium",
            description = "Spot target cyber patterns against high-speed distractor matrix.",
            iconEmoji = "🎨"
        ),
        GameItem(
            id = "speed_math",
            title = "Speed Math",
            category = "Focus",
            difficulty = "Medium",
            description = "Solve rapid mental arithmetic formulas before the clock expires.",
            iconEmoji = "🧮"
        ),
        GameItem(
            id = "color_clash",
            title = "Color Clash",
            category = "Focus",
            difficulty = "Medium",
            description = "Classic Stroop effect challenge: tap the text ink color, not the written word.",
            iconEmoji = "💥"
        ),
        GameItem(
            id = "reaction_time",
            title = "Reaction Time",
            category = "Focus",
            difficulty = "Easy",
            description = "Measure millisecond-level neural reflexes upon chromatic trigger shift.",
            iconEmoji = "⚡"
        )
    )
}

data class UserTargetGoal(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val category: String = "All", // "Memory", "Logic", "Focus", "Reflex", "Math", "All"
    val targetType: String = "Drills", // "Drills", "Score", "Streak", "Accuracy", "Speed"
    val targetCount: Int = 5,
    val currentCount: Int = 0,
    val frequency: String = "Daily", // "Daily", "Weekly", "Monthly", "Special Quest"
    val rewardCoins: Int = 50,
    val rewardXp: Int = 100,
    val iconEmoji: String = "🎯",
    val difficulty: String = "Normal", // "Normal", "Hard", "Elite"
    val note: String = "",
    val isCompleted: Boolean = false,
    val isRewardClaimed: Boolean = false
)

