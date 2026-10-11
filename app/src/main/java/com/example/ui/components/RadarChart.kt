package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import kotlin.math.cos
import kotlin.math.sin

/**
 * 6-Axis Hexagonal Cognitive Radar Chart:
 * 1. Focus
 * 2. Accuracy
 * 3. Speed
 * 4. Reflex
 * 5. Logic
 * 6. Memory
 */
@Composable
fun CognitiveRadarChart(
    focus: Float,       // 0.0 to 1.0
    accuracy: Float,    // 0.0 to 1.0
    speed: Float,       // 0.0 to 1.0
    reflex: Float = 0.84f,  // 0.0 to 1.0
    logic: Float = 0.88f,   // 0.0 to 1.0
    memory: Float = 0.79f,  // 0.0 to 1.0
    modifier: Modifier = Modifier,
    chartColor: Color = NeonCyan
) {
    val labels = listOf("Focus", "Accuracy", "Speed", "Reflex", "Logic", "Memory")
    val rawValues = listOf(focus, accuracy, speed, reflex, logic, memory)

    Box(
        modifier = modifier.padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val radius = (size.minDimension / 2f) - 24.dp.toPx()

            // 6 angles evenly spaced around the circle (60 degrees each, starting from top -90 deg)
            val angles = (0..5).map { i ->
                Math.toRadians(-90.0 + i * 60.0)
            }

            // Draw concentric guide hexagons (4 levels: 25%, 50%, 75%, 100%)
            val steps = listOf(0.25f, 0.5f, 0.75f, 1.0f)
            steps.forEach { stepRatio ->
                val r = radius * stepRatio
                val guidePath = Path().apply {
                    val p0 = Offset(
                        (centerX + r * cos(angles[0])).toFloat(),
                        (centerY + r * sin(angles[0])).toFloat()
                    )
                    moveTo(p0.x, p0.y)
                    for (i in 1..5) {
                        lineTo(
                            (centerX + r * cos(angles[i])).toFloat(),
                            (centerY + r * sin(angles[i])).toFloat()
                        )
                    }
                    close()
                }
                drawPath(
                    path = guidePath,
                    color = chartColor.copy(alpha = if (stepRatio == 1.0f) 0.35f else 0.15f),
                    style = Stroke(width = if (stepRatio == 1.0f) 1.2.dp.toPx() else 0.8.dp.toPx())
                )
            }

            // Draw 6 radial spoke lines from center to outer vertices
            angles.forEach { angle ->
                drawLine(
                    color = chartColor.copy(alpha = 0.25f),
                    start = Offset(centerX, centerY),
                    end = Offset(
                        (centerX + radius * cos(angle)).toFloat(),
                        (centerY + radius * sin(angle)).toFloat()
                    ),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Calculate actual performance data polygon
            val values = rawValues.map { it.coerceIn(0.25f, 1.0f) }

            val dataPoints = values.mapIndexed { i, v ->
                val r = radius * v
                Offset(
                    (centerX + r * cos(angles[i])).toFloat(),
                    (centerY + r * sin(angles[i])).toFloat()
                )
            }

            val dataPath = Path().apply {
                moveTo(dataPoints[0].x, dataPoints[0].y)
                for (i in 1..5) {
                    lineTo(dataPoints[i].x, dataPoints[i].y)
                }
                close()
            }

            // Fill area with radial gradient
            drawPath(
                path = dataPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        chartColor.copy(alpha = 0.50f),
                        chartColor.copy(alpha = 0.12f)
                    ),
                    center = Offset(centerX, centerY),
                    radius = radius
                ),
                style = Fill
            )

            // Draw glowing polygon boundary
            drawPath(
                path = dataPath,
                color = chartColor,
                style = Stroke(width = 2.dp.toPx())
            )

            // Draw vertex nodes
            dataPoints.forEach { pt ->
                drawCircle(
                    color = chartColor,
                    radius = 4.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = pt
                )
            }
        }
    }
}
