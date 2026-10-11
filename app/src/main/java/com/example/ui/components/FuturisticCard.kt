package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ThemeRegistry
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Aura Size specification:
 * - XS: Extra small micro aura (spread 8dp)
 * - SM: Small compact aura (spread 16dp)
 * - MD: Standard balanced aura (spread 24dp)
 * - LG: Expansive vibrant aura (spread 36dp)
 * - XL: Ultra-wide dramatic aura (spread 50dp)
 */
enum class AuraSize(
    val code: String,
    val label: String,
    val spreadFactor: Float,
    val alphaFactor: Float
) {
    XS("aura-xs", "XS", 0.75f, 0.70f),
    SM("aura-sm", "SM", 0.90f, 0.85f),
    MD("aura", "MD", 1.05f, 1.0f),
    LG("aura-lg", "LG", 1.25f, 1.15f),
    XL("aura-xl", "XL", 1.50f, 1.25f);

    companion object {
        fun fromString(str: String): AuraSize = when (str.trim().uppercase()) {
            "XS", "AURA-XS" -> XS
            "SM", "AURA-SM" -> SM
            "LG", "AURA-LG" -> LG
            "XL", "AURA-XL" -> XL
            else -> MD
        }
    }
}

/**
 * Modern Card Animation Types:
 * 1. RADIAL_ORB_3D: Google / Firebase signature 3D radial orb emerging from behind the card
 * 2. FLOATING_3D: GitHub 3D spatial levitation with dynamic elevation & depth tilt
 * 3. COSMIC_PULSE: Rhythmic breathing radial glow radiating beneath the card
 * 4. HOLOGRAPHIC_WAVE: Cyber diagonal light beam with subtle glass sheen
 */
enum class CardAnimationType(val displayName: String, val description: String) {
    RADIAL_ORB_3D("Google / Firebase 3D Radial Nebula", "Circular glowing orb moving beneath the card, radiating out from behind with 3D depth"),
    FLOATING_3D("GitHub 3D Spatial Levitation", "Smooth physics-based vertical levitation & subtle 3D perspective tilt"),
    COSMIC_PULSE("Behind-Card Cosmic Pulse", "Rhythmic luminous expansion and organic breathing glow under the card"),
    HOLOGRAPHIC_WAVE("Holographic 3D Glass", "Diagonal cyber light beam sweeping across the card surface with specular rim");

    companion object {
        fun fromString(str: String): CardAnimationType = when (str.trim().lowercase()) {
            "google / firebase 3d radial nebula", "3d radial nebula", "radial nebula", "radial_orb_3d", "radial" -> RADIAL_ORB_3D
            "github 3d spatial levitation", "floating 3d levitation", "floating 3d", "floating", "levitation" -> FLOATING_3D
            "behind-card cosmic pulse", "cosmic pulse", "neon pulse", "pulse", "breathing" -> COSMIC_PULSE
            "holographic 3d glass", "holographic wave", "holographic", "shimmer", "wave" -> HOLOGRAPHIC_WAVE
            else -> RADIAL_ORB_3D
        }
    }
}

/**
 * Unified Card & Component Animation Configuration
 */
data class CardGlowConfig(
    val enabled: Boolean = true,
    val animationType: String = "Google / Firebase 3D Radial Nebula",
    val preset: String = "Cyber Glow",
    val speedSeconds: Float = 2.0f,
    val thicknessDp: Float = 1.8f,
    val auraIntensity: Float = 75f,
    // Layer options: "Behind Card (3D Underglow)", "Above Card", "Both (Dual 3D Dimension)"
    val layer: String = "Behind Card (3D Underglow)",
    val effectStyle: String = "Cyber Glow",
    val animationScale: Float = 1.15f,
    val depth3d: Float = 14f,
    val customColor: String = "#00F0FF",
    val customBgColor: String = "",
    val colorMode: String = "Preset Palette",
    val auraSize: String = "MD",
    val auraDurationMs: Int = 2000,
    val cardSizeScale: Float = 1.0f,
    val smoothPerformanceMode: Boolean = true
)

val LocalCardGlowConfig = compositionLocalOf { CardGlowConfig() }
val LocalCardAnimationClock = compositionLocalOf<() -> Float> { { 0f } }

fun parseHexColor(hex: String, fallback: Color = Color(0xFF00F0FF)): Color {
    return try {
        val clean = hex.removePrefix("#").trim()
        val longVal = clean.toLong(16)
        if (clean.length == 6) {
            Color(0xFF000000 or longVal)
        } else if (clean.length == 8) {
            Color(longVal)
        } else {
            fallback
        }
    } catch (_: Exception) {
        fallback
    }
}

enum class BorderAnimStyle {
    SWEEP, PULSE, ROTATE, CHASE, METEOR, ELECTRIC, STATIC
}

@Composable
fun FuturisticCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    shape: Shape = RoundedCornerShape(cornerRadius),
    borderColor: Color = NeonCyan,
    glowColor: Color = NeonCyan,
    glowIntensity: Float = 0.65f,
    borderWidth: Dp = 1.2.dp,
    borderThickness: Dp = borderWidth,
    contentPadding: Dp = 14.dp,
    isAnimated: Boolean = true,
    animStyle: BorderAnimStyle = BorderAnimStyle.SWEEP,
    animSpeed: Float = 45f,
    themePreset: String = "Cyber Void Dark",
    glassOpacity: Float = 85f,
    testTag: String? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val themeConfig = remember(themePreset) { ThemeRegistry.getTheme(themePreset) }
    val glowConfig = LocalCardGlowConfig.current
    val clockProvider = LocalCardAnimationClock.current

    val effectiveCornerRadius = if (cornerRadius != 16.dp) cornerRadius else themeConfig.defaultCornerRadius
    val finalShape = if (effectiveCornerRadius != cornerRadius) RoundedCornerShape(effectiveCornerRadius) else shape

    val opacityRatio = (glassOpacity / 100f).coerceIn(0.1f, 1.0f)
    val cardBackground = remember(themePreset, opacityRatio) {
        themeConfig.getCardBackgroundColor(opacityRatio)
    }

    val customParsedColor = remember(glowConfig.customColor) {
        parseHexColor(glowConfig.customColor, borderColor)
    }

    val primaryAccent = when (glowConfig.colorMode) {
        "Custom Color" -> customParsedColor
        "Match Cyber Theme" -> borderColor
        else -> NeonCyan
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val clickScale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1.0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "click_scale"
    )

    val animType = remember(glowConfig.animationType) {
        CardAnimationType.fromString(glowConfig.animationType)
    }

    val auraSizeEnum = remember(glowConfig.auraSize) {
        AuraSize.fromString(glowConfig.auraSize)
    }

    val isAnimationActive = glowConfig.enabled && isAnimated
    val sizeScaleFactor = glowConfig.cardSizeScale.coerceIn(0.6f, 2.0f)

    val renderUnderglow = glowConfig.layer.contains("Behind", ignoreCase = true) || glowConfig.layer.contains("Both", ignoreCase = true)
    val renderTopBorder = glowConfig.layer.contains("Above", ignoreCase = true) || glowConfig.layer.contains("Both", ignoreCase = true)

    // Pre-allocate cached color lists to guarantee zero garbage-collector allocations during 120 FPS draw passes
    val underglowColors = remember(primaryAccent, glowConfig.auraIntensity, auraSizeEnum) {
        val intensityRatio = (glowConfig.auraIntensity / 100f) * auraSizeEnum.alphaFactor
        listOf(
            primaryAccent.copy(alpha = (0.50f * intensityRatio).coerceIn(0.12f, 0.85f)),
            primaryAccent.copy(alpha = (0.18f * intensityRatio).coerceIn(0.04f, 0.45f)),
            Color.Transparent
        )
    }

    val dualUnderglowColors = remember(primaryAccent, glowConfig.auraIntensity) {
        val intensityRatio = (glowConfig.auraIntensity / 100f)
        listOf(
            primaryAccent.copy(alpha = (0.45f * intensityRatio).coerceIn(0.10f, 0.75f)),
            Color(0xFF8B5CF6).copy(alpha = (0.22f * intensityRatio).coerceIn(0.05f, 0.40f)),
            Color.Transparent
        )
    }

    val topBorderColors = remember(primaryAccent) {
        listOf(
            primaryAccent,
            Color.White,
            primaryAccent.copy(alpha = 0.8f),
            Color(0xFF8B5CF6),
            primaryAccent.copy(alpha = 0.15f),
            Color.Transparent,
            primaryAccent
        )
    }

    // Outer Container
    Box(
        modifier = modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .graphicsLayer {
                val clockDeg = clockProvider()
                val clockRad = Math.toRadians(clockDeg.toDouble())

                scaleX = clickScale * sizeScaleFactor
                scaleY = clickScale * sizeScaleFactor

                if (isAnimationActive) {
                    when (animType) {
                        CardAnimationType.FLOATING_3D, CardAnimationType.RADIAL_ORB_3D -> {
                            // Smooth physics floating & perspective tilt (Zero recomposition, pure GPU transform)
                            val floatOffset = sin(clockRad * 1.4).toFloat() * 4.5f
                            val tiltX = cos(clockRad * 1.2).toFloat() * 1.5f
                            val tiltY = sin(clockRad * 1.1).toFloat() * 1.2f
                            translationY = floatOffset
                            rotationX = tiltX
                            rotationY = tiltY
                            cameraDistance = 16f * density
                        }
                        CardAnimationType.COSMIC_PULSE -> {
                            val pulseFactor = 1.0f + (sin(clockRad * 2.0).toFloat() * 0.008f)
                            scaleX *= pulseFactor
                            scaleY *= pulseFactor
                        }
                        else -> {
                            translationY = 0f
                            rotationX = 0f
                            rotationY = 0f
                        }
                    }
                }
                clip = false
            }
            .then(
                // BEHIND-THE-CARD 3D UNDERGLOW (Google / Firebase / GitHub landing page card effect)
                if (isAnimationActive && renderUnderglow) {
                    Modifier.drawBehind {
                        val clockDeg = clockProvider()
                        val clockRad = Math.toRadians(clockDeg.toDouble())
                        val crPx = effectiveCornerRadius.toPx()

                        when (animType) {
                            CardAnimationType.RADIAL_ORB_3D -> {
                                // Circular glowing orb moving smoothly beneath the card, radiating outwards
                                val cos = cos(clockRad).toFloat()
                                val sin = sin(clockRad).toFloat()
                                val cx = size.width * (0.50f + cos * 0.24f)
                                val cy = size.height * (0.50f + sin * 0.20f)
                                val baseRadius = hypot(size.width, size.height) * 0.52f * auraSizeEnum.spreadFactor

                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = dualUnderglowColors,
                                        center = Offset(cx, cy),
                                        radius = baseRadius
                                    ),
                                    center = Offset(cx, cy),
                                    radius = baseRadius
                                )
                            }

                            CardAnimationType.FLOATING_3D -> {
                                // Dynamic 3D elevation drop underglow that expands as card floats
                                val dynamicElevation = glowConfig.depth3d.dp.toPx() + (sin(clockRad * 1.4).toFloat() * 4.5f)
                                val shadowSpread = dynamicElevation * 0.65f * auraSizeEnum.spreadFactor
                                val shadowOffsetY = dynamicElevation * 0.85f

                                // Ambient black shadow base
                                drawRoundRect(
                                    color = Color.Black.copy(alpha = 0.55f),
                                    topLeft = Offset(-shadowSpread * 0.25f, shadowOffsetY),
                                    size = Size(size.width + shadowSpread * 0.5f, size.height + shadowSpread * 0.35f),
                                    cornerRadius = CornerRadius(crPx + shadowSpread * 0.2f)
                                )

                                // Soft colored glow halo under the bottom edge
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = underglowColors,
                                        center = Offset(size.width / 2f, size.height + shadowOffsetY * 0.5f),
                                        radius = size.width * 0.60f
                                    ),
                                    center = Offset(size.width / 2f, size.height + shadowOffsetY * 0.5f),
                                    radius = size.width * 0.60f
                                )
                            }

                            CardAnimationType.COSMIC_PULSE -> {
                                // Rhythmic breathing radial halo beneath card center
                                val pulseRadius = hypot(size.width, size.height) * 0.48f * auraSizeEnum.spreadFactor * (1.0f + (sin(clockRad * 2.2).toFloat() * 0.12f))
                                val cx = size.width / 2f
                                val cy = size.height / 2f

                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = underglowColors,
                                        center = Offset(cx, cy),
                                        radius = pulseRadius
                                    ),
                                    center = Offset(cx, cy),
                                    radius = pulseRadius
                                )
                            }

                            CardAnimationType.HOLOGRAPHIC_WAVE -> {
                                // Ambient wide soft underglow
                                val cx = size.width / 2f
                                val cy = size.height / 2f
                                val r = hypot(size.width, size.height) * 0.50f * auraSizeEnum.spreadFactor
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = underglowColors,
                                        center = Offset(cx, cy),
                                        radius = r
                                    ),
                                    center = Offset(cx, cy),
                                    radius = r
                                )
                            }
                        }
                    }
                } else Modifier
            )
    ) {
        // Card Surface Box (Clipped to shape)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(finalShape)
                .background(color = cardBackground, shape = finalShape)
                .drawBehind {
                    if (themeConfig.hasSpecularHighlight) {
                        val specularHeight = size.height * 0.32f
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.10f * opacityRatio.coerceAtMost(1f)),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = specularHeight
                            ),
                            topLeft = Offset(0f, 0f),
                            size = Size(size.width, specularHeight)
                        )
                    }
                }
                .drawWithContent {
                    drawContent()

                    val crPx = effectiveCornerRadius.toPx()
                    val thicknessPx = glowConfig.thicknessDp.dp.toPx()
                    val clockDeg = clockProvider()
                    val clockRad = Math.toRadians(clockDeg.toDouble())

                    // Top layer border drawing
                    if (!isAnimationActive || !renderTopBorder) {
                        // Clean, high-performance static cyber border with specular rim
                        drawRoundRect(
                            color = primaryAccent.copy(alpha = 0.38f),
                            topLeft = Offset(thicknessPx / 2f, thicknessPx / 2f),
                            size = Size(size.width - thicknessPx, size.height - thicknessPx),
                            cornerRadius = CornerRadius(crPx),
                            style = Stroke(width = thicknessPx)
                        )

                        // Top rim specular highlight line
                        drawLine(
                            color = Color.White.copy(alpha = 0.28f),
                            start = Offset(crPx, thicknessPx / 2f),
                            end = Offset(size.width - crPx, thicknessPx / 2f),
                            strokeWidth = thicknessPx * 1.1f
                        )
                        return@drawWithContent
                    }

                    // Selected top border animation
                    when (animType) {
                        CardAnimationType.RADIAL_ORB_3D, CardAnimationType.FLOATING_3D -> {
                            // Elevated cyber glowing outline with illuminated top crest
                            drawRoundRect(
                                color = primaryAccent.copy(alpha = 0.50f),
                                topLeft = Offset(thicknessPx / 2f, thicknessPx / 2f),
                                size = Size(size.width - thicknessPx, size.height - thicknessPx),
                                cornerRadius = CornerRadius(crPx),
                                style = Stroke(width = thicknessPx)
                            )

                            // Specular crest highlight on top rim
                            drawLine(
                                color = Color.White.copy(alpha = 0.70f),
                                start = Offset(crPx, thicknessPx / 2f),
                                end = Offset(size.width - crPx, thicknessPx / 2f),
                                strokeWidth = thicknessPx * 1.2f
                            )
                        }

                        CardAnimationType.COSMIC_PULSE -> {
                            val pulseAlpha = 0.35f + (sin(clockRad * 2.2).toFloat() * 0.30f).coerceIn(0f, 0.45f)
                            drawRoundRect(
                                color = primaryAccent.copy(alpha = pulseAlpha),
                                topLeft = Offset(thicknessPx / 2f, thicknessPx / 2f),
                                size = Size(size.width - thicknessPx, size.height - thicknessPx),
                                cornerRadius = CornerRadius(crPx),
                                style = Stroke(width = thicknessPx)
                            )
                        }

                        CardAnimationType.HOLOGRAPHIC_WAVE -> {
                            val waveProgress = ((clockDeg % 360f) / 360f)
                            val totalSpan = size.width + size.height
                            val waveCenter = waveProgress * totalSpan
                            val waveWidth = size.width * 0.40f

                            drawRoundRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        primaryAccent.copy(alpha = 0.12f),
                                        Color.White.copy(alpha = 0.40f),
                                        primaryAccent.copy(alpha = 0.25f),
                                        Color.Transparent
                                    ),
                                    start = Offset(waveCenter - waveWidth, 0f),
                                    end = Offset(waveCenter + waveWidth, size.height)
                                ),
                                topLeft = Offset(0f, 0f),
                                size = size,
                                cornerRadius = CornerRadius(crPx)
                            )

                            drawRoundRect(
                                color = primaryAccent.copy(alpha = 0.45f),
                                topLeft = Offset(thicknessPx / 2f, thicknessPx / 2f),
                                size = Size(size.width - thicknessPx, size.height - thicknessPx),
                                cornerRadius = CornerRadius(crPx),
                                style = Stroke(width = thicknessPx)
                            )
                        }
                    }
                }
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = ripple(color = primaryAccent),
                            onClick = onClick
                        )
                    } else Modifier
                )
                .padding(contentPadding)
        ) {
            content()
        }
    }
}
