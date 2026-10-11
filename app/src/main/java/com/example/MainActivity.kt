package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.presentation.screens.AnalyticsScreen
import com.example.presentation.screens.GamePlayerScreen
import com.example.presentation.screens.GamesScreen
import com.example.presentation.screens.HomeScreen
import com.example.presentation.screens.SettingsScreen
import com.example.presentation.viewmodel.MainViewModel
import com.example.ui.audio.SoundManager
import com.example.ui.components.CardGlowConfig
import com.example.ui.components.CyberBackground
import com.example.ui.components.FloatingBottomNavigationBar
import com.example.ui.components.LocalCardAnimationClock
import com.example.ui.components.LocalCardGlowConfig
import com.example.ui.components.NavTab
import com.example.ui.theme.BrineBoosterTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WaveMindTheme

fun parseCyberColor(hex: String, defaultColor: Color = NeonCyan): Color {
    return try {
        val clean = hex.removePrefix("#")
        when (clean.length) {
            6 -> Color((0xFF000000 or clean.toLong(16)).toInt())
            8 -> Color(clean.toLong(16).toInt())
            else -> defaultColor
        }
    } catch (_: Exception) {
        defaultColor
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Enable Hardware 120 FPS / Maximum Display Refresh Rate
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            try {
                val maxMode = display?.supportedModes?.maxByOrNull { it.refreshRate }
                if (maxMode != null) {
                    val lp = window.attributes
                    lp.preferredDisplayModeId = maxMode.modeId
                    window.attributes = lp
                }
            } catch (_: Exception) {
                // Fallback to standard refresh mode
            }
        }

        setContent {
            val viewModel: MainViewModel = viewModel()
            val prefs by viewModel.userPreferences.collectAsState()

            val activeTheme = com.example.ui.theme.ThemeRegistry.getTheme(prefs.themePreset)
            val primaryColor = parseCyberColor(prefs.primaryColor, activeTheme.primaryColor)
            val backgroundColor = activeTheme.backgroundColor

            val cardGlowConfig = remember(
                prefs.cardRotatingGlowEnabled,
                prefs.cardGlowPreset,
                prefs.cardGlowSpeed,
                prefs.cardGlowThickness,
                prefs.cardGlowAura,
                prefs.cardGlowLayer,
                prefs.cardGlowEffectStyle,
                prefs.cardAnimationScale,
                prefs.card3dDepth,
                prefs.cardCustomColor,
                prefs.cardColorMode,
                prefs.cardAuraSize,
                prefs.cardAuraDuration,
                prefs.cardAuraBgColor,
                prefs.smoothPerformanceMode
            ) {
                CardGlowConfig(
                    enabled = prefs.cardRotatingGlowEnabled,
                    preset = prefs.cardGlowPreset,
                    speedSeconds = (prefs.cardAuraDuration / 1000f).coerceAtLeast(0.8f),
                    thicknessDp = prefs.cardGlowThickness,
                    auraIntensity = prefs.cardGlowAura,
                    layer = prefs.cardGlowLayer,
                    effectStyle = prefs.cardGlowEffectStyle,
                    animationScale = prefs.cardAnimationScale,
                    depth3d = prefs.card3dDepth,
                    customColor = prefs.cardCustomColor,
                    customBgColor = prefs.cardAuraBgColor,
                    colorMode = prefs.cardColorMode,
                    auraSize = prefs.cardAuraSize,
                    auraDurationMs = prefs.cardAuraDuration,
                    smoothPerformanceMode = prefs.smoothPerformanceMode
                )
            }

            // Single shared animation clock for the whole app
            // Runs only when glow is enabled, reducing CPU/GPU overhead to practically zero
            val infiniteTransition = rememberInfiniteTransition(label = "global_glow_clock")
            val glowAngle = infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = prefs.cardAuraDuration.coerceIn(800, 10000),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "global_card_rotation_angle"
            )

            WaveMindTheme(
                primaryColor = primaryColor,
                backgroundColor = backgroundColor
            ) {
                val currentDensity = androidx.compose.ui.platform.LocalDensity.current
                val dpFactor = (prefs.uiScalePercent / 100f).coerceIn(0.65f, 2.0f)
                val scaledDensity = remember(currentDensity, dpFactor, prefs.fontScalePercent) {
                    androidx.compose.ui.unit.Density(
                        density = currentDensity.density * dpFactor,
                        fontScale = currentDensity.fontScale * (prefs.fontScalePercent / 100f).coerceIn(0.7f, 1.5f)
                    )
                }

                CompositionLocalProvider(
                    androidx.compose.ui.platform.LocalDensity provides scaledDensity,
                    LocalCardGlowConfig provides cardGlowConfig,
                    LocalCardAnimationClock provides { glowAngle.value }
                ) {
                    MainApp(viewModel = viewModel, primaryColor = primaryColor, backgroundColor = backgroundColor)
                }
            }
        }
    }
}

@Composable
fun MainApp(
    viewModel: MainViewModel,
    primaryColor: Color,
    backgroundColor: Color
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val prefs by viewModel.userPreferences.collectAsState()
    val activeGame by viewModel.activeGame.collectAsState()

    var isEdgeBarVisible by remember { mutableStateOf(true) }

    val edgeBarNestedScrollConnection = remember(prefs.edgeBarAutoHideOnScroll) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (prefs.edgeBarAutoHideOnScroll) {
                    if (available.y < -12f && isEdgeBarVisible) {
                        isEdgeBarVisible = false
                    } else if (available.y > 12f && !isEdgeBarVisible) {
                        isEdgeBarVisible = true
                    }
                }
                return Offset.Zero
            }
        }
    }

    // Android System Back Navigation handling
    BackHandler(enabled = activeGame != null || currentTab != NavTab.HOME) {
        when {
            activeGame != null -> viewModel.closeGame()
            currentTab != NavTab.HOME -> {
                isEdgeBarVisible = true
                viewModel.selectTab(NavTab.HOME)
            }
        }
    }

    LaunchedEffect(
        prefs.soundEffects,
        prefs.masterSoundFx,
        prefs.uiClickSounds,
        prefs.victoryFanfare,
        prefs.gameOverBuzzer,
        prefs.soundVolume,
        prefs.soundPack
    ) {
        SoundManager.updateConfig(
            master = prefs.soundEffects && prefs.masterSoundFx,
            click = prefs.uiClickSounds,
            victory = prefs.victoryFanfare,
            buzzer = prefs.gameOverBuzzer,
            volume = prefs.soundVolume,
            pack = prefs.soundPack
        )
    }

    val edgeBarColor = parseCyberColor(prefs.edgeBarColor, primaryColor)

    CyberBackground(
        backgroundColor = backgroundColor,
        themePreset = prefs.themePreset,
        showGrid = prefs.backgroundGrid,
        gridIntensity = prefs.gridLineIntensity / 100f,
        gridColor = primaryColor,
        scanlineCrtEffect = prefs.scanlineCrtEffect && !prefs.smoothPerformanceMode
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(edgeBarNestedScrollConnection)
        ) {
            // Main content fills the viewport with zero jarring height recalculations on scroll
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(top = 6.dp)
            ) {
                when (currentTab) {
                    NavTab.HOME -> HomeScreen(viewModel = viewModel)
                    NavTab.GAMES -> GamesScreen(viewModel = viewModel)
                    NavTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                    NavTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }

            // iOS-Style Floating Edge Bar Dock (Overlaid cleanly at the bottom, zero content relayout!)
            FloatingBottomNavigationBar(
                currentTab = currentTab,
                activeColor = edgeBarColor,
                motionEnabled = prefs.motionBounces,
                height = prefs.edgeBarHeight.dp,
                cornerRadius = prefs.edgeBarCornerRadius.dp,
                bottomOffset = prefs.edgeBarBottomOffset.dp,
                horizontalMargin = prefs.edgeBarHorizontalMargin.dp,
                glassOpacity = prefs.edgeBarGlassOpacity,
                isVisible = if (prefs.edgeBarAutoHideOnScroll) isEdgeBarVisible else true,
                onTabSelected = {
                    SoundManager.playClick()
                    isEdgeBarVisible = true
                    viewModel.selectTab(it)
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Active Game Screen (Keyed to game.id to ensure zero state bleeding or accidental game jumping)
        activeGame?.let { game ->
            key(game.id) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                ) {
                    GamePlayerScreen(
                        game = game,
                        viewModel = viewModel,
                        onBack = {
                            isEdgeBarVisible = true
                            viewModel.closeGame()
                        }
                    )
                }
            }
        }
    }
}
