package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ThemeConfig(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val iconEmoji: String,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val primaryColor: Color,
    val secondaryColor: Color,
    val defaultCornerRadius: Dp,
    val hasGrid: Boolean,
    val hasStars: Boolean,
    val isGlass: Boolean,
    val hasSpecularHighlight: Boolean
) {
    fun getCardBackgroundColor(opacityRatio: Float): Color {
        val clamped = opacityRatio.coerceIn(0.12f, 1.0f)
        return when (id) {
            "iOS Liquid Glass" -> Color(0xFFFFFFFF).copy(alpha = 0.12f * clamped + 0.04f)
            "Dark Liquid Glass" -> Color(0xFF1E2640).copy(alpha = 0.50f * clamped + 0.15f)
            "Minimalist Obsidian", "Minimal Sleek" -> Color(0xFF131A26)
            "iOS Clean Modern", "iOS Clean" -> Color(0xFF1C1C1E).copy(alpha = 0.92f * clamped + 0.08f)
            "Open Source Dev UI", "Open Source Minimal" -> Color(0xFF161B22)
            "Normal App UI" -> Color(0xFF222630)
            "Midnight AMOLED Purple" -> Color(0xFF140A26).copy(alpha = 0.90f * clamped + 0.10f)
            "Emerald Aurora Matrix" -> Color(0xFF092418).copy(alpha = 0.88f * clamped + 0.12f)
            "Solar Cyber Amber" -> Color(0xFF24140B).copy(alpha = 0.88f * clamped + 0.12f)
            "Deep Ocean Azure" -> Color(0xFF081C38).copy(alpha = 0.88f * clamped + 0.12f)
            else -> Color(0xFF0C1422).copy(alpha = 0.90f * clamped + 0.10f) // Cyber Void Dark
        }
    }

    fun getBorderBrush(accentColor: Color, glowColor: Color, animProgress: Float): Brush {
        return when (id) {
            "iOS Liquid Glass" -> Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.70f),
                    accentColor.copy(alpha = 0.40f),
                    Color.White.copy(alpha = 0.15f)
                )
            )
            "Dark Liquid Glass" -> Brush.linearGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.85f),
                    Color.White.copy(alpha = 0.35f),
                    glowColor.copy(alpha = 0.60f)
                )
            )
            "Minimalist Obsidian", "Minimal Sleek" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF334155),
                    Color(0xFF1E293B)
                )
            )
            "iOS Clean Modern", "iOS Clean" -> Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.22f),
                    Color.White.copy(alpha = 0.08f)
                )
            )
            "Open Source Dev UI", "Open Source Minimal" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF30363D),
                    accentColor.copy(alpha = 0.6f)
                )
            )
            "Normal App UI" -> Brush.linearGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.35f),
                    Color(0xFF2C3240)
                )
            )
            "Midnight AMOLED Purple" -> Brush.linearGradient(
                colors = listOf(
                    AmoledPurple.copy(alpha = 0.9f),
                    Color(0xFF6B21A8),
                    AmoledPurple.copy(alpha = 0.5f)
                )
            )
            "Emerald Aurora Matrix" -> Brush.linearGradient(
                colors = listOf(
                    MatrixGreen.copy(alpha = 0.9f),
                    Color(0xFF047857),
                    MatrixGreen.copy(alpha = 0.4f)
                )
            )
            "Solar Cyber Amber" -> Brush.linearGradient(
                colors = listOf(
                    SolarAmber.copy(alpha = 0.95f),
                    Color(0xFFB45309),
                    SolarAmber.copy(alpha = 0.5f)
                )
            )
            "Deep Ocean Azure" -> Brush.linearGradient(
                colors = listOf(
                    OceanAzure.copy(alpha = 0.95f),
                    Color(0xFF0284C7),
                    OceanAzure.copy(alpha = 0.5f)
                )
            )
            else -> Brush.linearGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.85f),
                    accentColor.copy(alpha = 0.35f),
                    glowColor.copy(alpha = 0.65f)
                )
            )
        }
    }
}

object ThemeRegistry {
    val themes = listOf(
        ThemeConfig(
            id = "Cyber Void Dark",
            displayName = "Cyber Void Dark",
            subtitle = "High-tech cyberpunk with glowing neon matrix",
            iconEmoji = "⚡",
            backgroundColor = Color(0xFF04070D),
            surfaceColor = Color(0xFF0C1420),
            primaryColor = NeonCyan,
            secondaryColor = NeonGreen,
            defaultCornerRadius = 16.dp,
            hasGrid = true,
            hasStars = true,
            isGlass = false,
            hasSpecularHighlight = false
        ),
        ThemeConfig(
            id = "Minimalist Obsidian",
            displayName = "Minimalist Obsidian",
            subtitle = "Pure stark slate, clean borders, zero distraction",
            iconEmoji = "⬛",
            backgroundColor = Color(0xFF0E131C),
            surfaceColor = Color(0xFF151C28),
            primaryColor = Color(0xFF38BDF8),
            secondaryColor = Color(0xFF94A3B8),
            defaultCornerRadius = 14.dp,
            hasGrid = false,
            hasStars = false,
            isGlass = false,
            hasSpecularHighlight = false
        ),
        ThemeConfig(
            id = "iOS Liquid Glass",
            displayName = "iOS Liquid Glass",
            subtitle = "Frosted acrylic glass with glossy top specular shine",
            iconEmoji = "🪟",
            backgroundColor = Color(0xFF060B1E),
            surfaceColor = Color(0xFF101935),
            primaryColor = AppleBlue,
            secondaryColor = AppleTeal,
            defaultCornerRadius = 20.dp,
            hasGrid = false,
            hasStars = true,
            isGlass = true,
            hasSpecularHighlight = true
        ),
        ThemeConfig(
            id = "Dark Liquid Glass",
            displayName = "Dark Liquid Glass",
            subtitle = "Smoked translucent glass with luminous neon accents",
            iconEmoji = "🌌",
            backgroundColor = Color(0xFF050814),
            surfaceColor = Color(0xFF0F1B2F),
            primaryColor = NeonCyan,
            secondaryColor = NeonPurple,
            defaultCornerRadius = 18.dp,
            hasGrid = true,
            hasStars = true,
            isGlass = true,
            hasSpecularHighlight = true
        ),
        ThemeConfig(
            id = "iOS Clean Modern",
            displayName = "iOS Clean Modern",
            subtitle = "Apple dark mode system aesthetic with squircle cards",
            iconEmoji = "📱",
            backgroundColor = Color(0xFF000000),
            surfaceColor = AppleSystemGray,
            primaryColor = AppleBlue,
            secondaryColor = AppleTeal,
            defaultCornerRadius = 18.dp,
            hasGrid = false,
            hasStars = false,
            isGlass = false,
            hasSpecularHighlight = false
        ),
        ThemeConfig(
            id = "Open Source Dev UI",
            displayName = "Open Source Dev UI",
            subtitle = "Clean GitHub Dracula matte dark with git emerald",
            iconEmoji = "💻",
            backgroundColor = OpenSourceDarkBg,
            surfaceColor = Color(0xFF161B22),
            primaryColor = OpenSourceGreen,
            secondaryColor = Color(0xFF58A6FF),
            defaultCornerRadius = 12.dp,
            hasGrid = false,
            hasStars = false,
            isGlass = false,
            hasSpecularHighlight = false
        ),
        ThemeConfig(
            id = "Normal App UI",
            displayName = "Normal App UI",
            subtitle = "Standard native Material 3 container design",
            iconEmoji = "✨",
            backgroundColor = Color(0xFF151820),
            surfaceColor = Color(0xFF222630),
            primaryColor = Material3Primary,
            secondaryColor = Color(0xFFC4E7FF),
            defaultCornerRadius = 16.dp,
            hasGrid = false,
            hasStars = false,
            isGlass = false,
            hasSpecularHighlight = false
        ),
        ThemeConfig(
            id = "Midnight AMOLED Purple",
            displayName = "Midnight AMOLED",
            subtitle = "100% true pitch black with electric ultraviolet glow",
            iconEmoji = "🟣",
            backgroundColor = AmoledBlack,
            surfaceColor = Color(0xFF140A26),
            primaryColor = AmoledPurple,
            secondaryColor = NeonPink,
            defaultCornerRadius = 16.dp,
            hasGrid = false,
            hasStars = true,
            isGlass = false,
            hasSpecularHighlight = false
        ),
        ThemeConfig(
            id = "Emerald Aurora Matrix",
            displayName = "Emerald Aurora",
            subtitle = "Deep dark jade forest with luminous matrix mint",
            iconEmoji = "🟢",
            backgroundColor = Color(0xFF03140C),
            surfaceColor = Color(0xFF082618),
            primaryColor = MatrixGreen,
            secondaryColor = NeonCyan,
            defaultCornerRadius = 16.dp,
            hasGrid = true,
            hasStars = false,
            isGlass = false,
            hasSpecularHighlight = false
        ),
        ThemeConfig(
            id = "Solar Cyber Amber",
            displayName = "Solar Cyber Amber",
            subtitle = "Warm dark espresso base with blazing gold and amber",
            iconEmoji = "🟠",
            backgroundColor = Color(0xFF140904),
            surfaceColor = Color(0xFF24130A),
            primaryColor = SolarAmber,
            secondaryColor = Color(0xFFFF6600),
            defaultCornerRadius = 16.dp,
            hasGrid = true,
            hasStars = true,
            isGlass = false,
            hasSpecularHighlight = false
        ),
        ThemeConfig(
            id = "Deep Ocean Azure",
            displayName = "Deep Ocean Azure",
            subtitle = "Bioluminescent sapphire and cyan deep oceanic trench",
            iconEmoji = "🌊",
            backgroundColor = Color(0xFF020C1C),
            surfaceColor = Color(0xFF061834),
            primaryColor = OceanAzure,
            secondaryColor = NeonBlue,
            defaultCornerRadius = 16.dp,
            hasGrid = true,
            hasStars = true,
            isGlass = true,
            hasSpecularHighlight = true
        )
    )

    fun getTheme(preset: String): ThemeConfig {
        return themes.find { it.id.equals(preset, ignoreCase = true) }
            ?: themes.find { it.id.contains(preset, ignoreCase = true) }
            ?: themes.first()
    }
}

fun getCyberColorScheme(
    primaryColor: Color = NeonCyan,
    backgroundColor: Color = CyberVoidBlack
) = darkColorScheme(
    primary = primaryColor,
    onPrimary = CyberVoidBlack,
    primaryContainer = CyberSurfaceVariant,
    onPrimaryContainer = primaryColor,
    secondary = NeonGreen,
    onSecondary = CyberVoidBlack,
    secondaryContainer = CyberSurfaceVariant,
    onSecondaryContainer = NeonGreen,
    tertiary = NeonPurple,
    onTertiary = Color.White,
    tertiaryContainer = CyberSurfaceVariant,
    onTertiaryContainer = NeonPurple,
    background = backgroundColor,
    onBackground = TextPrimary,
    surface = CyberSurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = primaryColor.copy(alpha = 0.25f),
    outlineVariant = primaryColor.copy(alpha = 0.12f)
)

@Composable
fun BrineBoosterTheme(
    primaryColor: Color = NeonCyan,
    backgroundColor: Color = CyberVoidBlack,
    typography: Typography = com.example.ui.theme.Typography,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = getCyberColorScheme(primaryColor, backgroundColor),
        typography = typography,
        content = content
    )
}
