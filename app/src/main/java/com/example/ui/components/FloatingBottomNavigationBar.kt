package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan

enum class NavTab(val title: String, val icon: ImageVector, val tag: String) {
    HOME("Home", Icons.Default.Home, "nav_tab_home"),
    GAMES("Games", Icons.Default.SportsEsports, "nav_tab_games"),
    ANALYTICS("Analytics", Icons.Default.Insights, "nav_tab_analytics"),
    SETTINGS("Settings", Icons.Default.Settings, "nav_tab_settings")
}

/**
 * iOS-Style Floating Edge Bar / Dock
 * High-performance, silky smooth 60fps pill dock with full user customization:
 * - Corner Radius (roundness / কতটা গোল)
 * - Vertical Offset (position from bottom / কতটা উপরে নিচে)
 * - Dock Height & Horizontal Margin
 * - Color & Glass Opacity
 * - Auto-Hide on Scroll Animation
 */
@Composable
fun FloatingBottomNavigationBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = NeonCyan,
    motionEnabled: Boolean = true,
    height: Dp = 62.dp,
    cornerRadius: Dp = 28.dp,
    bottomOffset: Dp = 10.dp,
    horizontalMargin: Dp = 16.dp,
    glassOpacity: Float = 90f,
    isVisible: Boolean = true
) {
    val inactiveTextColor = Color(0xFFCBD5E1) // High contrast light slate
    val inactiveIconColor = Color(0xFF94A3B8) // Crisp slate

    val opacityFraction = (glassOpacity / 100f).coerceIn(0.3f, 1.0f)
    val dockShape = RoundedCornerShape(cornerRadius)

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f)
        ) + fadeIn(animationSpec = tween(200)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f)
        ) + fadeOut(animationSpec = tween(150)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = bottomOffset, start = horizontalMargin, end = horizontalMargin),
            contentAlignment = Alignment.Center
        ) {
            // Floating Dock Container with iOS Glassmorphism and Glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .shadow(
                        elevation = 14.dp,
                        shape = dockShape,
                        spotColor = activeColor.copy(alpha = 0.28f),
                        ambientColor = Color.Black.copy(alpha = 0.6f)
                    )
                    .clip(dockShape)
                    .background(Color(0xFF070D18).copy(alpha = opacityFraction))
                    .border(
                        width = 1.2.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                activeColor.copy(alpha = 0.35f),
                                activeColor.copy(alpha = 0.85f),
                                activeColor.copy(alpha = 0.35f)
                            )
                        ),
                        shape = dockShape
                    )
                    .drawBehind {
                        // iOS Frosted Glass Specular Top Highlight
                        val topHighlightHeight = size.height * 0.4f
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.12f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = topHighlightHeight
                            )
                        )
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                        .padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab

                        val tabTextColor by animateColorAsState(
                            targetValue = if (isSelected) activeColor else inactiveTextColor,
                            animationSpec = tween(180),
                            label = "tab_text_color"
                        )
                        val tabIconColor by animateColorAsState(
                            targetValue = if (isSelected) activeColor else inactiveIconColor,
                            animationSpec = tween(180),
                            label = "tab_icon_color"
                        )

                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed && motionEnabled) 0.90f else if (isSelected) 1.05f else 1.0f,
                            animationSpec = spring(dampingRatio = 0.65f, stiffness = 550f),
                            label = "tab_scale"
                        )

                        val itemShape = RoundedCornerShape((cornerRadius.value * 0.65f).dp.coerceAtLeast(10.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height((height.value - 10f).dp.coerceAtLeast(44.dp))
                                .testTag(tab.tag)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clip(itemShape)
                                .then(
                                    if (isSelected) {
                                        Modifier
                                            .background(activeColor.copy(alpha = 0.18f))
                                            .border(
                                                width = 1.dp,
                                                color = activeColor.copy(alpha = 0.45f),
                                                shape = itemShape
                                            )
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onTabSelected(tab) }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = tabIconColor,
                                    modifier = Modifier.size(if (height > 60.dp) 24.dp else 22.dp)
                                )
                                Text(
                                    text = tab.title,
                                    fontSize = if (height > 60.dp) 11.sp else 10.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = tabTextColor,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
