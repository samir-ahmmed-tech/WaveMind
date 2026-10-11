package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberVoidBlack
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ThemeRegistry
import kotlin.random.Random

data class Star(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val baseAlpha: Float
)

@Composable
fun CyberBackground(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CyberVoidBlack,
    themePreset: String = "Cyber Void Dark",
    showGrid: Boolean = true,
    gridIntensity: Float = 0.15f,
    showStars: Boolean = true,
    gridColor: Color = NeonCyan,
    scanlineCrtEffect: Boolean = false,
    content: @Composable () -> Unit
) {
    val themeConfig = remember(themePreset) { ThemeRegistry.getTheme(themePreset) }

    // Pre-calculate deterministic starfield once
    val stars = remember {
        val random = Random(42)
        List(24) {
            Star(
                xRatio = random.nextFloat(),
                yRatio = random.nextFloat(),
                radius = random.nextFloat() * 1.2f + 0.8f,
                baseAlpha = random.nextFloat() * 0.30f + 0.20f
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .drawWithCache {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val isGlassTheme = themeConfig.isGlass || themePreset.contains("Glass") || themePreset.contains("Ocean")

                val topOrbBrush = if (isGlassTheme && canvasWidth > 0f) {
                    Brush.radialGradient(
                        colors = listOf(
                            themeConfig.primaryColor.copy(alpha = 0.16f),
                            Color.Transparent
                        ),
                        center = Offset(canvasWidth * 0.85f, canvasHeight * 0.15f),
                        radius = canvasWidth * 0.65f
                    )
                } else null

                val bottomOrbBrush = if (isGlassTheme && canvasWidth > 0f) {
                    Brush.radialGradient(
                        colors = listOf(
                            themeConfig.secondaryColor.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(canvasWidth * 0.15f, canvasHeight * 0.75f),
                        radius = canvasWidth * 0.75f
                    )
                } else null

                val effectiveAlpha = (gridIntensity * 0.8f).coerceIn(0.02f, 0.30f)
                val lineColor = gridColor.copy(alpha = effectiveAlpha)
                val strokeWidth = 1.dp.toPx().coerceAtLeast(1f)
                val gridSizePx = 48.dp.toPx().coerceAtLeast(36f)

                onDrawBehind {
                    if (canvasWidth <= 0f || canvasHeight <= 0f) return@onDrawBehind

                    // 1. Ambient Diffuse Orbs
                    if (topOrbBrush != null) {
                        drawCircle(
                            brush = topOrbBrush,
                            center = Offset(canvasWidth * 0.85f, canvasHeight * 0.15f),
                            radius = canvasWidth * 0.65f
                        )
                    }
                    if (bottomOrbBrush != null) {
                        drawCircle(
                            brush = bottomOrbBrush,
                            center = Offset(canvasWidth * 0.15f, canvasHeight * 0.75f),
                            radius = canvasWidth * 0.75f
                        )
                    }

                    // 2. Cached Starfield
                    if (showStars && themeConfig.hasStars) {
                        stars.forEach { star ->
                            val x = star.xRatio * canvasWidth
                            val y = star.yRatio * canvasHeight
                            drawCircle(
                                color = Color.White.copy(alpha = star.baseAlpha),
                                radius = star.radius.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                    }

                    // 3. Cyber Grid (GPU optimized line stride)
                    if (showGrid && themeConfig.hasGrid && gridIntensity > 0f) {
                        var curX = 0f
                        var vCount = 0
                        while (curX <= canvasWidth && vCount < 60) {
                            drawLine(
                                color = lineColor,
                                start = Offset(curX, 0f),
                                end = Offset(curX, canvasHeight),
                                strokeWidth = strokeWidth
                            )
                            curX += gridSizePx
                            vCount++
                        }

                        var curY = 0f
                        var hCount = 0
                        while (curY <= canvasHeight && hCount < 60) {
                            drawLine(
                                color = lineColor,
                                start = Offset(0f, curY),
                                end = Offset(canvasWidth, curY),
                                strokeWidth = strokeWidth
                            )
                            curY += gridSizePx
                            hCount++
                        }
                    }

                    // 4. Subtle CRT Scanlines
                    if (scanlineCrtEffect) {
                        var scanY = 0f
                        val scanStep = 12.dp.toPx().coerceAtLeast(10f)
                        val scanColor = Color.Black.copy(alpha = 0.08f)
                        var sCount = 0
                        while (scanY <= canvasHeight && sCount < 120) {
                            drawLine(
                                color = scanColor,
                                start = Offset(0f, scanY),
                                end = Offset(canvasWidth, scanY),
                                strokeWidth = 1.dp.toPx()
                            )
                            scanY += scanStep
                            sCount++
                        }
                    }
                }
            }
    ) {
        content()
    }
}
