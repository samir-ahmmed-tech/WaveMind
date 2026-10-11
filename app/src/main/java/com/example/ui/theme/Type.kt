package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class AppFontItem(
    val name: String,
    val category: String,
    val description: String,
    val fontFamily: FontFamily,
    val letterSpacingDelta: Float = 0f
)

object AppFontCatalog {
    val fonts: List<AppFontItem> = listOf(
        AppFontItem("Sans-Serif Standard", "Sans", "Standard balanced Android system typeface", FontFamily.SansSerif),
        AppFontItem("Cyber Monospace", "Mono", "Monospaced terminal cyber code font", FontFamily.Monospace, 0.5f),
        AppFontItem("Classic Editorial Serif", "Serif", "Refined traditional serif typography", FontFamily.Serif),
        AppFontItem("Cursive Script", "Script", "Flowing elegant handwriting aesthetic", FontFamily.Cursive),
        AppFontItem("Roboto Tech Clean", "Sans", "Clean modern tech aesthetic", FontFamily.SansSerif, 0.2f),
        AppFontItem("Orbitron Mono Code", "Mono", "Fixed-width futuristic sci-fi terminal", FontFamily.Monospace, 0.8f),
        AppFontItem("Neon Cyber Display", "Sans", "High-contrast geometric cyber look", FontFamily.SansSerif, 0.4f),
        AppFontItem("Matrix Terminal", "Mono", "Hacker command prompt typography", FontFamily.Monospace, 0.6f),
        AppFontItem("Quantum Geometric", "Sans", "Modern Swiss design system", FontFamily.SansSerif, -0.2f),
        AppFontItem("Minimalist Obsidian", "Sans", "Ultra-sleek stealth typography", FontFamily.SansSerif, 0.3f),
        AppFontItem("Heavy Cyberpunk Black", "Sans", "Industrial heavy-duty weight typeface", FontFamily.SansSerif, 0.5f),
        AppFontItem("Literary Serif Pro", "Serif", "Bookish high-legibility publication style", FontFamily.Serif, 0.1f),
        AppFontItem("Gothic Tech Bold", "Serif", "Sharp mechanical serif construction", FontFamily.Serif, 0.4f),
        AppFontItem("Arcade 8-Bit Retro", "Mono", "Vintage gaming grid monospace", FontFamily.Monospace, 0.7f),
        AppFontItem("Titanium Sans", "Sans", "Strong structured industrial sans", FontFamily.SansSerif, 0.15f),
        AppFontItem("Silicon Valley Modern", "Sans", "Tech startup flagship clean font", FontFamily.SansSerif, 0.1f),
        AppFontItem("Neural Network Data", "Mono", "AI dataset and tensor stream mono", FontFamily.Monospace, 0.4f),
        AppFontItem("Hyper Grotesk Bold", "Sans", "Impactful geometric grotesque", FontFamily.SansSerif, -0.3f),
        AppFontItem("Obsidian Monolith", "Sans", "Solid architectural typography", FontFamily.SansSerif, 0.2f),
        AppFontItem("Space Mono Voyager", "Mono", "NASA telemetry & deep cosmos monospace", FontFamily.Monospace, 0.5f),
        AppFontItem("Bauhaus Modernist", "Sans", "Art deco meets mid-century constructivism", FontFamily.SansSerif, 0.1f),
        AppFontItem("Velocity Ultra", "Sans", "Speed-tuned energetic letterforms", FontFamily.SansSerif, -0.1f),
        AppFontItem("Stellar Cosmic Sans", "Sans", "Wide spaced ethereal space font", FontFamily.SansSerif, 0.6f),
        AppFontItem("Digital Glitch Stream", "Mono", "Low-level system debugging mono", FontFamily.Monospace, 0.3f),
        AppFontItem("Neon Synthwave 80s", "Sans", "Vibrant retro neon arcade vibe", FontFamily.SansSerif, 0.4f),
        AppFontItem("Executive Luxury Serif", "Serif", "High-end corporate serif authority", FontFamily.Serif, 0.2f),
        AppFontItem("Futuristic Condensed", "Sans", "Tight high-density interface font", FontFamily.SansSerif, -0.4f),
        AppFontItem("Ultra Clean Light", "Sans", "Airy modern high-transparency look", FontFamily.SansSerif, 0.3f),
        AppFontItem("Deep Matrix Terminal", "Mono", "Kernel level prompt font", FontFamily.Monospace, 0.5f),
        AppFontItem("Bio-Digital Synthetic", "Sans", "Bionic cyborg interface styling", FontFamily.SansSerif, 0.2f),
        AppFontItem("Aero Space Mono", "Mono", "Avionics cockpit head-up display", FontFamily.Monospace, 0.6f),
        AppFontItem("Plasma Glow Sans", "Sans", "Illuminated neon signage font", FontFamily.SansSerif, 0.35f)
    )

    fun getFontItem(name: String): AppFontItem {
        return fonts.find { it.name.equals(name, ignoreCase = true) } ?: fonts.first()
    }
}

fun getAppTypography(fontFamilyName: String = "Sans-Serif Standard", scalePercent: Float = 100f): Typography {
    val factor = (scalePercent / 100f).coerceIn(0.6f, 2.0f)
    val fontItem = AppFontCatalog.getFontItem(fontFamilyName)
    val font = fontItem.fontFamily
    val delta = fontItem.letterSpacingDelta

    return Typography(
        headlineLarge = TextStyle(
            fontFamily = font,
            fontWeight = FontWeight.Bold,
            fontSize = (30f * factor).sp,
            lineHeight = (36f * factor).sp,
            letterSpacing = (-0.5f + delta).sp,
            color = TextPrimary
        ),
        headlineMedium = TextStyle(
            fontFamily = font,
            fontWeight = FontWeight.Bold,
            fontSize = (24f * factor).sp,
            lineHeight = (30f * factor).sp,
            letterSpacing = (0f + delta).sp,
            color = TextPrimary
        ),
        titleLarge = TextStyle(
            fontFamily = font,
            fontWeight = FontWeight.SemiBold,
            fontSize = (20f * factor).sp,
            lineHeight = (26f * factor).sp,
            letterSpacing = (0f + delta).sp,
            color = TextPrimary
        ),
        titleMedium = TextStyle(
            fontFamily = font,
            fontWeight = FontWeight.SemiBold,
            fontSize = (16f * factor).sp,
            lineHeight = (22f * factor).sp,
            letterSpacing = (0.15f + delta).sp,
            color = TextPrimary
        ),
        titleSmall = TextStyle(
            fontFamily = font,
            fontWeight = FontWeight.Medium,
            fontSize = (14f * factor).sp,
            lineHeight = (20f * factor).sp,
            letterSpacing = (0.1f + delta).sp,
            color = TextPrimary
        ),
        bodyLarge = TextStyle(
            fontFamily = font,
            fontWeight = FontWeight.Normal,
            fontSize = (15f * factor).sp,
            lineHeight = (22f * factor).sp,
            letterSpacing = (0.25f + delta).sp,
            color = TextPrimary
        ),
        bodyMedium = TextStyle(
            fontFamily = font,
            fontWeight = FontWeight.Normal,
            fontSize = (13f * factor).sp,
            lineHeight = (18f * factor).sp,
            letterSpacing = (0.25f + delta).sp,
            color = TextSecondary
        ),
        bodySmall = TextStyle(
            fontFamily = font,
            fontWeight = FontWeight.Normal,
            fontSize = (12f * factor).sp,
            lineHeight = (16f * factor).sp,
            letterSpacing = (0.4f + delta).sp,
            color = TextTertiary
        ),
        labelLarge = TextStyle(
            fontFamily = font,
            fontWeight = FontWeight.Bold,
            fontSize = (14f * factor).sp,
            lineHeight = (20f * factor).sp,
            letterSpacing = (0.1f + delta).sp,
            color = TextPrimary
        ),
        labelMedium = TextStyle(
            fontFamily = font,
            fontWeight = FontWeight.SemiBold,
            fontSize = (12f * factor).sp,
            lineHeight = (16f * factor).sp,
            letterSpacing = (0.5f + delta).sp,
            color = TextSecondary
        ),
        labelSmall = TextStyle(
            fontFamily = font,
            fontWeight = FontWeight.Medium,
            fontSize = (11f * factor).sp,
            lineHeight = (14f * factor).sp,
            letterSpacing = (0.5f + delta).sp,
            color = TextTertiary
        )
    )
}

val Typography = getAppTypography()
