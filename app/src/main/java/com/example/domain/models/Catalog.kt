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
        ),
        GameItem(
            id = "focus_flow",
            title = "Focus Flow",
            category = "Focus",
            difficulty = "Medium",
            description = "Track high-velocity bouncing neural orbs across physics boundaries.",
            iconEmoji = "🔮"
        ),
        GameItem(
            id = "odd_one_out",
            title = "Odd One Out",
            category = "Focus",
            difficulty = "Medium",
            description = "Detect subtle chromatic and rotational anomalies across cyber grids.",
            iconEmoji = "👁️"
        ),
        GameItem(
            id = "anagram_blitz",
            title = "Anagram Blitz",
            category = "Logic",
            difficulty = "Medium",
            description = "Unscramble encrypted cyber vocabulary against a high-speed countdown.",
            iconEmoji = "🔤"
        ),
        GameItem(
            id = "chrono_tap",
            title = "Chrono Tap",
            category = "Reflex",
            difficulty = "Easy",
            description = "Tap precisely when kinetic rings align within the resonance target window.",
            iconEmoji = "⏱️"
        ),
        GameItem(
            id = "word_morph",
            title = "Word Morph",
            category = "Logic",
            difficulty = "Medium",
            description = "Transform source words letter-by-letter to bridge lexicon targets.",
            iconEmoji = "🔡"
        ),
        GameItem(
            id = "math_matrix",
            title = "Math Matrix",
            category = "Logic",
            difficulty = "Hard",
            description = "Synthesize matrix cells to reach dynamic target arithmetic sums.",
            iconEmoji = "🔢"
        ),
        GameItem(
            id = "target_aim",
            title = "Target Aim",
            category = "Reflex",
            difficulty = "Medium",
            description = "Kinetic reflex targeting: eliminate dynamic orbital nodes before expiration.",
            iconEmoji = "🎯"
        ),
        GameItem(
            id = "tone_memory",
            title = "Tone Memory",
            category = "Memory",
            difficulty = "Medium",
            description = "Replay multi-chromatic frequency audio-visual sequences.",
            iconEmoji = "🎵"
        ),
        GameItem(
            id = "cipher_shift",
            title = "Cipher Shift",
            category = "Logic",
            difficulty = "Hard",
            description = "Decrypt Caesar rot-shifted cryptograms under neural clock countdown.",
            iconEmoji = "🗝️"
        ),
        GameItem(
            id = "silhouette_match",
            title = "Silhouette Match",
            category = "Focus",
            difficulty = "Medium",
            description = "Identify matching geometric silhouettes through mental spatial rotation.",
            iconEmoji = "👥"
        ),
        GameItem(
            id = "zen_balance",
            title = "Zen Balance",
            category = "Focus",
            difficulty = "Hard",
            description = "Maintain kinetic particle equilibrium within the cyber magnetic focal zone.",
            iconEmoji = "☯️"
        ),
        GameItem(
            id = "quick_sort",
            title = "Quick Sort",
            category = "Speed",
            difficulty = "Easy",
            description = "Rapid dual-category sorting: swipe or tap odd/even binary streams.",
            iconEmoji = "⚡"
        ),
        GameItem(
            id = "cyber_stacker",
            title = "Cyber Stacker",
            category = "Focus",
            difficulty = "Medium",
            description = "Stack oscillating cyber blocks with pixel-perfect alignment precision.",
            iconEmoji = "🧱"
        ),
        GameItem(
            id = "change_blindness",
            title = "Change Blindness",
            category = "Focus",
            difficulty = "Hard",
            description = "Detect subtle chromatic anomalies between flashing cyber frames.",
            iconEmoji = "👁️"
        ),
        GameItem(
            id = "constellation_connect",
            title = "Constellation",
            category = "Logic",
            difficulty = "Medium",
            description = "Topological pathfinding: link stellar nodes in exact numerical sequence.",
            iconEmoji = "✨"
        ),
        GameItem(
            id = "color_flood",
            title = "Color Flood",
            category = "Logic",
            difficulty = "Hard",
            description = "Algorithmic flood cascade: unify the chromatic neural matrix in minimal moves.",
            iconEmoji = "🌊"
        ),
        GameItem(
            id = "typing_blitz",
            title = "Typing Blitz",
            category = "Speed",
            difficulty = "Medium",
            description = "High-velocity cyber terminal command transcription under time pressure.",
            iconEmoji = "💻"
        ),
        GameItem(
            id = "number_grid_rush",
            title = "Grid Rush",
            category = "Speed",
            difficulty = "Easy",
            description = "Schulte numerical matrix scan: locate sequential numbers 1-16 at peak speed.",
            iconEmoji = "🏁"
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

