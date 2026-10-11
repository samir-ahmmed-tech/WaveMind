package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ThemeRegistry
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Aura Size specification:
 * - XS: Extra small micro aura (spread 5dp)
 * - SM: Small compact aura (spread 10dp)
 * - MD: Standard balanced aura (spread 16dp)
 * - LG: Expansive vibrant aura (spread 24dp)
 * - XL: Ultra-wide dramatic aura (spread 36dp)
 */
enum class AuraSize(
    val code: String,
    val label: String,
    val spreadDp: Float,
    val blurDp: Float,
    val elevationDp: Float,
    val alphaFactor: Float
) {
    XS("aura-xs", "XS", 5f, 10f, 4f, 0.65f),
    SM("aura-sm", "SM", 10f, 16f, 8f, 0.78f),
    MD("aura", "MD", 16f, 24f, 14f, 0.90f),
    LG("aura-lg", "LG", 24f, 34f, 18f, 1.0f),
    XL("aura-xl", "XL", 36f, 48f, 24f, 1.10f);

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
 * 4 Modern Card Animation Types:
 * 1. Rotating 3D Aura: Orbital perimeter gradient sweep around the top border
 * 2. Floating 3D Levitation: Smooth 3D depth tilt & vertical levitation floating with soft drop shadow
 * 3. Neon Pulse: Rhythmic organic expansion and luminosity pulsing
 * 4. Holographic Wave: Diagonal sci-fi light beam sweeping across the card
 */
enum class CardAnimationType(val displayName: String, val description: String) {
    ROTATING_AURA("Rotating 3D Aura", "Orbital gradient beam circulating along the card border"),
    FLOATING_3D("Floating 3D Levitation", "Smooth physics-based vertical levitation & subtle 3D perspective tilt"),
    NEON_PULSE("Neon Pulse", "Rhythmic luminous expansion and organic breathing glow"),
    HOLOGRAPHIC_WAVE("Holographic Wave", "Diagonal cyber light beam sweeping across the card surface");

    companion object {
        fun fromString(str: String): CardAnimationType = when (str.trim().lowercase()) {
            "floating 3d levitation", "floating 3d", "floating", "levitation" -> FLOATING_3D
            "neon pulse", "pulse", "breathing" -> NEON_PULSE
            "holographic wave", "holographic", "shimmer", "wave" -> HOLOGRAPHIC_WAVE
            else -> ROTATING_AURA
        }
    }
}

/**
 * Unified Card & Component Animation Configuration
 */
data class CardGlowConfig(
    val enabled: Boolean = true,
    val animationType: String = "Rotating 3D Aura",
    val preset: String = "Cyber Glow",
    val speedSeconds: Float = 2.0f,
    val thicknessDp: Float = 2.0f,
    val auraIntensity: Float = 75f,
    // Only "Above Card" is supported as per user requirement (Top layer only)
    val layer: String = "Above Card",
    val effectStyle: String = "Cyber Glow",
    val animationScale: Float = 1.15f,
    val depth3d: Float = 14f,
    val customColor: String = "#00F0FF",
    val customBgColor: String = "",
    val colorMode: String = "Preset Palette",
    val auraSize: String = "MD",
    val auraDurationMs: Int = 2000,
    val cardSizeScale: Float = 1.0f,
    val smoothPerformanceMode: Boolean = false
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
    glowIntensity: Float = 0.6f,
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

    val isAnimationActive = glowConfig.enabled && isAnimated
    val sizeScaleFactor = glowConfig.cardSizeScale.coerceIn(0.6f, 2.0f)

    // Outer Container
    Box(
        modifier = modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .graphicsLayer {
                val clockDeg = clockProvider()
                val clockRad = Math.toRadians(clockDeg.toDouble())

                // Scale factor from settings
                scaleX = clickScale * sizeScaleFactor
                scaleY = clickScale * sizeScaleFactor

                if (isAnimationActive) {
                    when (animType) {
                        CardAnimationType.FLOATING_3D -> {
                            // Smooth floating bobbing and subtle 3D perspective tilt
                            val floatOffset = sin(clockRad * 1.5).toFloat() * 6.5f
                            val tiltAngle = cos(clockRad * 1.2).toFloat() * 1.8f
                            translationY = floatOffset
                            rotationZ = tiltAngle * 0.4f
                            rotationX = cos(clockRad * 1.5).toFloat() * 2.2f
                            cameraDistance = 16f * density
                        }
                        CardAnimationType.NEON_PULSE -> {
                            // Rhythmic gentle scale expansion
                            val pulseFactor = 1.0f + (sin(clockRad * 2.0).toFloat() * 0.012f)
                            scaleX *= pulseFactor
                            scaleY *= pulseFactor
                        }
                        else -> {
                            // ROTATING_AURA & HOLOGRAPHIC_WAVE: handled in draw layer
                            translationY = 0f
                            rotationZ = 0f
                            rotationX = 0f
                        }
                    }
                }
                clip = false
            }
            .then(
                if (isAnimationActive && animType == CardAnimationType.FLOATING_3D && glowConfig.depth3d > 0f) {
                    Modifier.drawBehind {
                        val clockDeg = clockProvider()
                        val clockRad = Math.toRadians(clockDeg.toDouble())
                        val dynamicElevation = glowConfig.depth3d.dp.toPx() + (sin(clockRad * 1.5).toFloat() * 4f)
                        val shadowOffsetY = dynamicElevation * 0.75f
                        val shadowSpread = dynamicElevation * 0.40f
                        val crPx = effectiveCornerRadius.toPx()

                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.60f),
                            topLeft = Offset(-shadowSpread * 0.2f, shadowOffsetY),
                            size = Size(size.width + shadowSpread * 0.4f, size.height + shadowSpread * 0.25f),
                            cornerRadius = CornerRadius(crPx + shadowSpread * 0.2f)
                        )
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
                        val specularHeight = size.height * 0.35f
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.12f * opacityRatio.coerceAtMost(1f)),
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

                    if (!isAnimationActive) {
                        // Static standard border
                        drawRoundRect(
                            color = primaryAccent.copy(alpha = 0.35f),
                            topLeft = Offset(thicknessPx / 2f, thicknessPx / 2f),
                            size = Size(size.width - thicknessPx, size.height - thicknessPx),
                            cornerRadius = CornerRadius(crPx),
                            style = Stroke(width = thicknessPx)
                        )
                        return@drawWithContent
                    }

                    // Render selected top border animation
                    when (animType) {
                        CardAnimationType.ROTATING_AURA -> {
                            // Orbital perimeter gradient beam circulating along the card border
                            val cos = cos(clockRad).toFloat()
                            val sin = sin(clockRad).toFloat()
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val r = hypot(size.width, size.height) / 2f

                            val p1 = Offset(cx - cos * r, cy - sin * r)
                            val p2 = Offset(cx + cos * r, cy + sin * r)

                            val borderBrush = Brush.linearGradient(
                                colors = listOf(
                                    primaryAccent,
                                    Color.White,
                                    primaryAccent.copy(alpha = 0.8f),
                                    Color(0xFF8B5CF6),
                                    primaryAccent.copy(alpha = 0.15f),
                                    Color.Transparent,
                                    primaryAccent
                                ),
                                start = p1,
                                end = p2
                            )

                            drawRoundRect(
                                brush = borderBrush,
                                topLeft = Offset(thicknessPx / 2f, thicknessPx / 2f),
                                size = Size(size.width - thicknessPx, size.height - thicknessPx),
                                cornerRadius = CornerRadius(crPx),
                                style = Stroke(width = thicknessPx)
                            )
                        }

                        CardAnimationType.NEON_PULSE -> {
                            // Rhythmic breathing luminous border
                            val pulseAlpha = 0.40f + (sin(clockRad * 2.5).toFloat() * 0.40f).coerceIn(0f, 0.55f)
                            val dynamicThickness = thicknessPx * (1.0f + (sin(clockRad * 2.5).toFloat() * 0.3f).coerceIn(0f, 0.4f))

                            drawRoundRect(
                                color = primaryAccent.copy(alpha = pulseAlpha),
                                topLeft = Offset(dynamicThickness / 2f, dynamicThickness / 2f),
                                size = Size(size.width - dynamicThickness, size.height - dynamicThickness),
                                cornerRadius = CornerRadius(crPx),
                                style = Stroke(width = dynamicThickness)
                            )

                            // Inner soft neon line
                            drawRoundRect(
                                color = Color.White.copy(alpha = pulseAlpha * 0.7f),
                                topLeft = Offset(dynamicThickness, dynamicThickness),
                                size = Size(size.width - dynamicThickness * 2f, size.height - dynamicThickness * 2f),
                                cornerRadius = CornerRadius((crPx - dynamicThickness).coerceAtLeast(0f)),
                                style = Stroke(width = 1.dp.toPx())
                            )
                        }

                        CardAnimationType.HOLOGRAPHIC_WAVE -> {
                            // Diagonal cyber light sweep across top surface
                            val waveProgress = ((clockDeg % 360f) / 360f)
                            val totalSpan = size.width + size.height
                            val waveCenter = waveProgress * totalSpan
                            val waveWidth = size.width * 0.45f

                            val sweepBrush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    primaryAccent.copy(alpha = 0.15f),
                                    Color.White.copy(alpha = 0.45f),
                                    primaryAccent.copy(alpha = 0.35f),
                                    Color.Transparent
                                ),
                                start = Offset(waveCenter - waveWidth, 0f),
                                end = Offset(waveCenter + waveWidth, size.height)
                            )

                            // Draw light beam sweep overlay
                            drawRoundRect(
                                brush = sweepBrush,
                                topLeft = Offset(0f, 0f),
                                size = size,
                                cornerRadius = CornerRadius(crPx)
                            )

                            // Clean cyber outline
                            drawRoundRect(
                                color = primaryAccent.copy(alpha = 0.50f),
                                topLeft = Offset(thicknessPx / 2f, thicknessPx / 2f),
                                size = Size(size.width - thicknessPx, size.height - thicknessPx),
                                cornerRadius = CornerRadius(crPx),
                                style = Stroke(width = thicknessPx)
                            )
                        }

                        CardAnimationType.FLOATING_3D -> {
                            // Elevated cyber glowing outline with illuminated top edge
                            drawRoundRect(
                                color = primaryAccent.copy(alpha = 0.60f),
                                topLeft = Offset(thicknessPx / 2f, thicknessPx / 2f),
                                size = Size(size.width - thicknessPx, size.height - thicknessPx),
                                cornerRadius = CornerRadius(crPx),
                                style = Stroke(width = thicknessPx)
                            )

                            // Specular crest highlight on top rim
                            drawLine(
                                color = Color.White.copy(alpha = 0.75f),
                                start = Offset(crPx, thicknessPx / 2f),
                                end = Offset(size.width - crPx, thicknessPx / 2f),
                                strokeWidth = thicknessPx * 1.2f
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
