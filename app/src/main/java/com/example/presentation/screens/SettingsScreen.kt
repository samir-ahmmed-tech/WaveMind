package com.example.presentation.screens

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PreferencesManager
import com.example.parseCyberColor
import com.example.presentation.viewmodel.MainViewModel
import com.example.ui.audio.SoundManager
import com.example.ui.components.BorderAnimStyle
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberSlider
import com.example.ui.components.CyberSwitch
import com.example.ui.components.FuturisticCard
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ThemeConfig
import com.example.ui.theme.ThemeRegistry

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val prefs by viewModel.userPreferences.collectAsState()
    val context = LocalContext.current
    val primaryCyberColor = parseCyberColor(prefs.primaryColor, NeonCyan)
    val cardGlowCyberColor = parseCyberColor(prefs.cardGlowColor, NeonGreen)

    var userNameInput by remember(prefs.userName) { mutableStateOf(prefs.userName) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }
    var showImportBackupDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
    var showExportInspectDialog by remember { mutableStateOf(false) }
    var exportedJsonText by remember { mutableStateOf("") }
    var showCardCustomizer by remember { mutableStateOf(false) }

    var previewToggleState by remember { mutableStateOf(true) }
    val cyberColors = listOf(
        "#00F0FF", "#00FF88", "#A855F7", "#EC4899",
        "#F59E0B", "#3B82F6", "#EF4444", "#14B8A6",
        "#8B5CF6", "#F97316", "#06B6D4", "#10B981",
        "#E11D48", "#D946EF", "#84CC16", "#F43F5E"
    )
    val animStyles = listOf("Sweep", "Pulse", "Rotate", "Electric")



    val animStyleEnum = when (prefs.borderAnimStyle) {
        "Pulse" -> BorderAnimStyle.PULSE
        "Rotate" -> BorderAnimStyle.ROTATE
        "Electric" -> BorderAnimStyle.ELECTRIC
        else -> BorderAnimStyle.SWEEP
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Settings & Customizer",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = primaryCyberColor
            )
            Text(
                text = "Personalize themes, liquid glass effects, animations, and countdown.",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // ==========================================
        // 1. ACTIVE THEME & LIVE UI ENGINE HUB (REQUESTED)
        // ==========================================
        FuturisticCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = primaryCyberColor,
            glowColor = cardGlowCyberColor,
            borderThickness = prefs.borderThickness.dp,
            glowIntensity = (prefs.glowIntensity / 100f),
            animStyle = animStyleEnum,
            isAnimated = prefs.enableAllAnimations,
            themePreset = prefs.themePreset,
            glassOpacity = prefs.glassOpacity,
            cornerRadius = prefs.cardCornerRadius.dp,
            onClick = {
                SoundManager.playSuccess()
                val allThemes = ThemeRegistry.themes
                val curIdx = allThemes.indexOfFirst { it.id == prefs.themePreset }.let { if (it >= 0) it else 0 }
                val nextTheme = allThemes[(curIdx + 1) % allThemes.size]
                viewModel.updateSetting(PreferencesManager.THEME_PRESET.name, nextTheme.id)
                viewModel.updateSetting(PreferencesManager.PRIMARY_COLOR.name, colorToHex(nextTheme.primaryColor))
                Toast.makeText(context, "Theme: ${nextTheme.displayName}", Toast.LENGTH_SHORT).show()
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(primaryCyberColor)
                        )
                        Column {
                            Text(
                                text = "Active Theme Architecture",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = prefs.themePreset,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Quick Cycle Badge Action
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(primaryCyberColor.copy(alpha = 0.18f))
                            .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = primaryCyberColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Tap to Cycle",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor
                            )
                        }
                    }
                }

                // Realistic Live UI Component Preview (Matching the App's Actual Aesthetic)
                val innerCornerRadius = (prefs.cardCornerRadius - 4f).coerceAtLeast(8f).dp
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(innerCornerRadius))
                        .background(Color(0xFF03070E).copy(alpha = 0.65f))
                        .border(1.dp, primaryCyberColor.copy(alpha = 0.25f), RoundedCornerShape(innerCornerRadius))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Mini Profile Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(primaryCyberColor.copy(alpha = 0.25f))
                                        .border(1.dp, primaryCyberColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = prefs.userAvatar.take(2),
                                        fontSize = 14.sp
                                    )
                                }
                                Column {
                                    Text(
                                        text = prefs.userName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = com.example.ui.theme.TextPrimary
                                    )
                                    Text(
                                        text = prefs.userRankTitle,
                                        fontSize = 10.sp,
                                        color = primaryCyberColor
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Ready to Train",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                )
                            }
                        }

                        // Mini 3-Pill Stat Matrix
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Focus" to "98%",
                                "Reflex" to "210ms",
                                "Streak" to "${prefs.streak}d"
                            ).forEach { (label, value) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(primaryCyberColor.copy(alpha = 0.08f))
                                        .border(0.8.dp, primaryCyberColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryCyberColor)
                                        Text(text = label, fontSize = 9.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }

                // Live Parameter Indicators Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(primaryCyberColor)
                        )
                        Text(
                            text = "Accent: ${prefs.primaryColor.uppercase()}",
                            fontSize = 11.sp,
                            color = com.example.ui.theme.TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F172A))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Glass: ${prefs.glassOpacity.toInt()}%",
                                fontSize = 10.sp,
                                color = primaryCyberColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F172A))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Radius: ${prefs.cardCornerRadius}dp",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. SETTINGS CARD CUSTOMIZER & SECTION TOGGLES (REQUESTED)
        // ==========================================
        FuturisticCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = primaryCyberColor.copy(alpha = 0.5f),
            themePreset = prefs.themePreset,
            glassOpacity = prefs.glassOpacity,
            cornerRadius = prefs.cardCornerRadius.dp
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(NeonGreen)
                        )
                        Column {
                            Text(
                                text = "Settings Card Customizer",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor
                            )
                            Text(
                                text = "Customize & toggle sections on or off",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Side Action Toggle Badge matching first card
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(primaryCyberColor.copy(alpha = 0.18f))
                            .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clickable {
                                SoundManager.playClick()
                                showCardCustomizer = !showCardCustomizer
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (showCardCustomizer) Icons.Default.ExpandLess else Icons.Default.Tune,
                                contentDescription = null,
                                tint = primaryCyberColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (showCardCustomizer) "Collapse" else "Customize",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor
                            )
                        }
                    }
                }

                if (showCardCustomizer) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SettingsToggleRow("Theme & Liquid Glass Card", prefs.showThemeCard) {
                            viewModel.updateSetting(PreferencesManager.SHOW_THEME_CARD.name, it)
                        }
                        SettingsToggleRow("Countdown Timer Card", prefs.showCountdownCard) {
                            viewModel.updateSetting(PreferencesManager.SHOW_COUNTDOWN_CARD.name, it)
                        }
                        SettingsToggleRow("Animation Dynamics Card", prefs.showAnimationCard) {
                            viewModel.updateSetting(PreferencesManager.SHOW_ANIMATION_CARD.name, it)
                        }
                        SettingsToggleRow("Color Accent Palettes Card", prefs.showColorCard) {
                            viewModel.updateSetting(PreferencesManager.SHOW_COLOR_CARD.name, it)
                        }
                        SettingsToggleRow("Audio & Sound System Card", prefs.showAudioCard) {
                            viewModel.updateSetting(PreferencesManager.SHOW_AUDIO_CARD.name, it)
                        }
                        SettingsToggleRow("iOS Edge Bar & Dock Card", prefs.showEdgeBarCard) {
                            viewModel.updateSetting(PreferencesManager.SHOW_EDGE_BAR_CARD.name, it)
                        }
                        SettingsToggleRow("Rotating Rainbow Card Glow Card", prefs.showCardGlowCard) {
                            viewModel.updateSetting(PreferencesManager.SHOW_CARD_GLOW_CARD.name, it)
                        }
                        SettingsToggleRow("Backup & Restore Data Card", prefs.showBackupCard) {
                            viewModel.updateSetting(PreferencesManager.SHOW_BACKUP_CARD.name, it)
                        }
                        SettingsToggleRow("Operative Identity Card", prefs.showIdentityCard) {
                            viewModel.updateSetting(PreferencesManager.SHOW_IDENTITY_CARD.name, it)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. THEME PRESETS & LIQUID GLASS CUSTOMIZER (REQUESTED)
        // ==========================================
        if (prefs.showThemeCard) {
            FuturisticCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = primaryCyberColor.copy(alpha = 0.6f),
                themePreset = prefs.themePreset,
                glassOpacity = prefs.glassOpacity,
                cornerRadius = prefs.cardCornerRadius.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = primaryCyberColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Theme & Liquid Glass Presets",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryCyberColor
                                )
                                Text(
                                    text = "11 distinct architectural design systems",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Side Action Badge like first card
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(primaryCyberColor.copy(alpha = 0.18f))
                                .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable {
                                    SoundManager.playClick()
                                    viewModel.updateSetting(PreferencesManager.SHOW_THEME_CARD.name, false)
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Hide Card",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor
                            )
                        }
                    }

                // Theme Presets List (All 11 Distinct Visual Paradigms)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeRegistry.themes.forEach { theme ->
                        val isSel = prefs.themePreset.equals(theme.id, ignoreCase = true)
                        ThemeCardItem(
                            theme = theme,
                            isSelected = isSel,
                            activeColor = primaryCyberColor,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                SoundManager.playSuccess()
                                viewModel.updateSetting(PreferencesManager.THEME_PRESET.name, theme.id)
                                viewModel.updateSetting(PreferencesManager.PRIMARY_COLOR.name, colorToHex(theme.primaryColor))
                                viewModel.updateSetting(PreferencesManager.CARD_CORNER_RADIUS.name, theme.defaultCornerRadius.value)
                                viewModel.updateSetting(PreferencesManager.BG_GRID.name, theme.hasGrid)
                            }
                        )
                    }
                }

                // Glass Surface Opacity Slider (REQUESTED: 1% to 500%)
                // App UI Scale / DP Size Slider (Requested: 70% to 200%, default 85%)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "App UI Scale (ডিপি সাইজ স্কেলিং)", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        Text(text = "${prefs.uiScalePercent.toInt()}%", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                    }
                    CyberSlider(
                        value = prefs.uiScalePercent,
                        onValueChange = { viewModel.updateSetting(PreferencesManager.UI_SCALE_PERCENT.name, it) },
                        valueRange = 70f..200f,
                        accentColor = primaryCyberColor
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Glass Surface Opacity", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${prefs.glassOpacity.toInt()}%", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                    }
                    CyberSlider(
                        value = prefs.glassOpacity.coerceIn(10f, 100f),
                        onValueChange = { viewModel.updateSetting(PreferencesManager.GLASS_OPACITY.name, it) },
                        valueRange = 10f..100f,
                        accentColor = primaryCyberColor
                    )
                }

                // Card Corner Squircle Radius Slider
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Card Corner Squircle Radius", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${prefs.cardCornerRadius.toInt()}dp", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                    }
                    CyberSlider(
                        value = prefs.cardCornerRadius,
                        onValueChange = { viewModel.updateSetting(PreferencesManager.CARD_CORNER_RADIUS.name, it) },
                        valueRange = 1f..32f,
                        accentColor = primaryCyberColor
                    )
                }

                // Border Outline Width Slider
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Border Outline Width", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${prefs.borderThickness.toInt()}px", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                    }
                    CyberSlider(
                        value = prefs.borderThickness,
                        onValueChange = { viewModel.updateSetting(PreferencesManager.BORDER_THICKNESS.name, it) },
                        valueRange = 1f..10f,
                        accentColor = primaryCyberColor
                    )
                }

                // Neon Glow Aura Slider
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Neon Glow Aura", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${prefs.glowIntensity.toInt()}%", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                    }
                    CyberSlider(
                        value = prefs.glowIntensity.coerceIn(10f, 100f),
                        onValueChange = { viewModel.updateSetting(PreferencesManager.GLOW_INTENSITY.name, it) },
                        valueRange = 10f..100f,
                        accentColor = primaryCyberColor
                    )
                }
            }
        }
    }

        // ==========================================
        // 4. COUNTDOWN CONFIGURATION (REQUESTED: 0s OFF DEFAULT, 3, 5, 10, 15)
        // ==========================================
        if (prefs.showCountdownCard) {
            FuturisticCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = primaryCyberColor.copy(alpha = 0.5f),
                themePreset = prefs.themePreset,
                glassOpacity = prefs.glassOpacity,
                cornerRadius = prefs.cardCornerRadius.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = primaryCyberColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Pre-Game Countdown Timer",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryCyberColor
                                )
                                Text(
                                    text = if (prefs.countdownDuration == 0) "Status: Instant Launch (0s)" else "Status: ${prefs.countdownDuration}s Delay",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Side Action Badge like first card
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(primaryCyberColor.copy(alpha = 0.18f))
                                .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clickable {
                                    SoundManager.playClick()
                                    viewModel.updateSetting(PreferencesManager.SHOW_COUNTDOWN_CARD.name, false)
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Hide Card",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor
                            )
                        }
                    }

                    Text(
                        text = "Configure start countdown delay before drills begin. (Default is OFF for instant launch).",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    // Countdown duration pills: 0 (Off), 3s, 5s, 10s, 15s
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val countdownOptions = listOf(
                            "Off (0s)" to 0,
                            "3s" to 3,
                            "5s" to 5,
                            "10s" to 10,
                            "15s" to 15
                        )

                        countdownOptions.forEach { (label, secs) ->
                            val isSel = prefs.countdownDuration == secs
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) primaryCyberColor.copy(alpha = 0.22f) else Color(0xFF090E17))
                                    .border(1.dp, if (isSel) primaryCyberColor else Color(0x3300F0FF), RoundedCornerShape(8.dp))
                                    .clickable {
                                        SoundManager.playClick()
                                        viewModel.updateSetting(PreferencesManager.COUNTDOWN_DURATION.name, secs)
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) primaryCyberColor else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 4. ANIMATION CUSTOMIZATION ENGINE (REQUESTED)
        // ==========================================
        if (prefs.showAnimationCard) {
            FuturisticCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = primaryCyberColor.copy(alpha = 0.5f),
                themePreset = prefs.themePreset,
                glassOpacity = prefs.glassOpacity,
                cornerRadius = prefs.cardCornerRadius.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = primaryCyberColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "✨ Animation Dynamics & Speed",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryCyberColor
                                )
                                Text(
                                    text = "Configure FPS velocity, bounce & borders",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Side Action Badge matching first card
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(primaryCyberColor.copy(alpha = 0.18f))
                                .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable {
                                    SoundManager.playClick()
                                    viewModel.updateSetting(PreferencesManager.SHOW_ANIMATION_CARD.name, false)
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Hide Card",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor
                            )
                        }
                    }

                    SettingsToggleRow("Enable Master Animations", prefs.enableAllAnimations) {
                        viewModel.updateSetting(PreferencesManager.ENABLE_ALL_ANIM.name, it)
                    }

                    // Speed scale
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "Motion Velocity Scale", fontSize = 12.sp, color = TextPrimary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Fast (0.5x)" to 0.5f,
                                "Normal (1.0x)" to 1.0f,
                                "Cinematic (1.5x)" to 1.5f,
                                "Snappy (0x)" to 0.0f
                            ).forEach { (label, scale) ->
                                val isSel = prefs.animationSpeedScale == scale
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) primaryCyberColor.copy(alpha = 0.2f) else Color(0xFF090E17))
                                        .border(1.dp, if (isSel) primaryCyberColor else Color(0x3300F0FF), RoundedCornerShape(8.dp))
                                        .clickable {
                                            SoundManager.playClick()
                                            viewModel.updateSetting(PreferencesManager.ANIMATION_SPEED_SCALE.name, scale)
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) primaryCyberColor else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    SettingsToggleRow("Spring Scale Bounce On Touch", prefs.motionBounces) {
                        viewModel.updateSetting(PreferencesManager.MOTION_BOUNCES.name, it)
                    }

                    // Border animation style selection
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "Border Dynamics Engine", fontSize = 12.sp, color = TextPrimary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            animStyles.forEach { style ->
                                val isSel = prefs.borderAnimStyle == style
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) primaryCyberColor.copy(alpha = 0.2f) else Color(0xFF090E17))
                                        .border(1.dp, if (isSel) primaryCyberColor else Color(0x3300F0FF), RoundedCornerShape(8.dp))
                                        .clickable {
                                            SoundManager.playClick()
                                            viewModel.updateSetting(PreferencesManager.BORDER_STYLE.name, style)
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = style,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) primaryCyberColor else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 4B. iOS EDGE BAR & NAVIGATION DOCK CUSTOMIZER (USER REQUESTED)
        // ==========================================
        if (prefs.showEdgeBarCard) {
            FuturisticCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = primaryCyberColor.copy(alpha = 0.55f),
                themePreset = prefs.themePreset,
                glassOpacity = prefs.glassOpacity,
                cornerRadius = prefs.cardCornerRadius.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = primaryCyberColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "📱 iOS Edge Bar & Navigation Dock",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryCyberColor
                                )
                                Text(
                                    text = "Customize dock roundness, height, offset, color & scroll auto-hide",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(primaryCyberColor.copy(alpha = 0.18f))
                                .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable {
                                    SoundManager.playClick()
                                    viewModel.updateSetting(PreferencesManager.SHOW_EDGE_BAR_CARD.name, false)
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Hide Card",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor
                            )
                        }
                    }

                    // Live Interactive Preview of the Dock
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF040812))
                            .border(1.dp, Color(0x3300F0FF), RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LIVE DOCK PREVIEW",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )

                        val previewDockColor = parseCyberColor(prefs.edgeBarColor, primaryCyberColor)
                        val previewDockShape = RoundedCornerShape(prefs.edgeBarCornerRadius.dp)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .height(prefs.edgeBarHeight.dp)
                                .clip(previewDockShape)
                                .background(Color(0xFF070D18).copy(alpha = prefs.edgeBarGlassOpacity / 100f))
                                .border(1.2.dp, previewDockColor.copy(alpha = 0.8f), previewDockShape)
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf("Home", "Games", "Analytics", "Settings").forEachIndexed { idx, label ->
                                    val isCur = idx == 0
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape((prefs.edgeBarCornerRadius * 0.6f).dp.coerceAtLeast(8.dp)))
                                            .background(if (isCur) previewDockColor.copy(alpha = 0.2f) else Color.Transparent)
                                            .padding(vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCur) previewDockColor else Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Dock Color Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "Edge Bar Accent Color", fontSize = 12.sp, color = TextPrimary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "#00F0FF", "#00FF88", "#A855F7", "#EC4899",
                                "#F59E0B", "#3B82F6", "#FFFFFF"
                            ).forEach { hex ->
                                val isSel = prefs.edgeBarColor.equals(hex, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(parseCyberColor(hex))
                                        .border(
                                            width = if (isSel) 2.5.dp else 1.dp,
                                            color = if (isSel) Color.White else Color(0x55000000),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            SoundManager.playClick()
                                            viewModel.updateSetting(PreferencesManager.EDGE_BAR_COLOR.name, hex)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSel) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (hex == "#FFFFFF") Color.Black else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Sliders for Roundness, Height, Offset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Dock Roundness", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${prefs.edgeBarCornerRadius.toInt()}dp", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                    }
                    CyberSlider(
                        value = prefs.edgeBarCornerRadius,
                        onValueChange = {
                            viewModel.updateSetting(PreferencesManager.EDGE_BAR_CORNER_RADIUS.name, it)
                        },
                        valueRange = 8f..38f,
                        accentColor = primaryCyberColor
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Dock Height / Size", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${prefs.edgeBarHeight.toInt()}dp", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                    }
                    CyberSlider(
                        value = prefs.edgeBarHeight,
                        onValueChange = {
                            viewModel.updateSetting(PreferencesManager.EDGE_BAR_HEIGHT.name, it)
                        },
                        valueRange = 52f..78f,
                        accentColor = primaryCyberColor
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Bottom Elevation", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${prefs.edgeBarBottomOffset.toInt()}dp", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                    }
                    CyberSlider(
                        value = prefs.edgeBarBottomOffset,
                        onValueChange = {
                            viewModel.updateSetting(PreferencesManager.EDGE_BAR_BOTTOM_OFFSET.name, it)
                        },
                        valueRange = 0f..32f,
                        accentColor = primaryCyberColor
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Dock Side Margin / Width", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${prefs.edgeBarHorizontalMargin.toInt()}dp", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                    }
                    CyberSlider(
                        value = prefs.edgeBarHorizontalMargin,
                        onValueChange = {
                            viewModel.updateSetting(PreferencesManager.EDGE_BAR_HORIZ_MARGIN.name, it)
                        },
                        valueRange = 6f..32f,
                        accentColor = primaryCyberColor
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Glass Frosted Opacity", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${prefs.edgeBarGlassOpacity.toInt()}%", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                    }
                    CyberSlider(
                        value = prefs.edgeBarGlassOpacity,
                        onValueChange = {
                            viewModel.updateSetting(PreferencesManager.EDGE_BAR_GLASS_OPACITY.name, it)
                        },
                        valueRange = 30f..100f,
                        accentColor = primaryCyberColor
                    )

                    SettingsToggleRow("Auto-Hide Edge Bar On Scroll Down", prefs.edgeBarAutoHideOnScroll) {
                        viewModel.updateSetting(PreferencesManager.EDGE_BAR_AUTO_HIDE.name, it)
                    }
                }
            }
        }

        // ==========================================
        // 4C. ROTATING RAINBOW CARD GLOW (AS IN SCREENSHOT)
        // ==========================================
        if (prefs.showCardGlowCard) {
            FuturisticCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = primaryCyberColor.copy(alpha = 0.55f),
                themePreset = prefs.themePreset,
                glassOpacity = prefs.glassOpacity,
                cornerRadius = prefs.cardCornerRadius.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = primaryCyberColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "🌈 Rotating Rainbow Card Glow",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryCyberColor
                                )
                                Text(
                                    text = "GitHub modal style rotating 6-color gradient halo & border",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(primaryCyberColor.copy(alpha = 0.18f))
                                .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable {
                                    SoundManager.playClick()
                                    viewModel.updateSetting(PreferencesManager.SHOW_CARD_GLOW_CARD.name, false)
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Hide Card",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor
                            )
                        }
                    }

                    // 1. Live Interactive 3D Card Preview of Selected Effect & Aura
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        com.example.ui.components.FuturisticCard(
                            modifier = Modifier.fillMaxWidth(0.92f),
                            cornerRadius = 18.dp,
                            themePreset = prefs.themePreset,
                            glassOpacity = prefs.glassOpacity
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp, horizontal = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val animEmoji = when (prefs.cardAnimationType) {
                                    "Floating 3D Levitation" -> "🛸"
                                    "Neon Pulse" -> "💓"
                                    "Holographic Wave" -> "🌊"
                                    else -> "🌀"
                                }
                                Text(text = animEmoji, fontSize = 36.sp)
                                Text(
                                    text = "${prefs.cardAnimationType} • Active",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Size: ${prefs.cardAuraSize} • Duration: ${prefs.cardAuraDuration}ms • Top Layer",
                                    fontSize = 11.sp,
                                    color = primaryCyberColor,
                                    fontWeight = FontWeight.SemiBold
                                )

                                // Perfectly Uniform & Balanced Action Buttons inside Rotating Card
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(primaryCyberColor.copy(alpha = 0.22f))
                                            .border(1.dp, primaryCyberColor, RoundedCornerShape(10.dp))
                                            .clickable { SoundManager.playClick() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "⚡ Engage Effect",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryCyberColor
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF0F172A))
                                            .border(1.dp, primaryCyberColor.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                                            .clickable { SoundManager.playSuccess() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "🎯 Test Motion",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // MASTER TOGGLES
                    SettingsToggleRow("Enable Aura Animations & 3D Atmospheric Underglow", prefs.cardRotatingGlowEnabled) {
                        viewModel.updateSetting(PreferencesManager.CARD_ROTATING_GLOW_ENABLED.name, it)
                    }

                    SettingsToggleRow("⚡ Hardware RenderNode Acceleration & 120 FPS Mode", prefs.smoothPerformanceMode) {
                        viewModel.updateSetting(PreferencesManager.SMOOTH_PERF_MODE.name, it)
                    }

                    // 2. AURA SIZES (XS, SM, MD, LG, XL) - Uniform 42.dp Height Buttons
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Card Aura Sizes",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                com.example.ui.components.AuraSize.XS,
                                com.example.ui.components.AuraSize.SM,
                                com.example.ui.components.AuraSize.MD,
                                com.example.ui.components.AuraSize.LG,
                                com.example.ui.components.AuraSize.XL
                            ).forEach { aSize ->
                                val isSel = prefs.cardAuraSize.equals(aSize.name, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSel) primaryCyberColor else Color(0xFF0F172A))
                                        .border(
                                            1.dp,
                                            if (isSel) Color.White else primaryCyberColor.copy(alpha = 0.45f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            SoundManager.playClick()
                                            viewModel.updateSetting(PreferencesManager.CARD_AURA_SIZE.name, aSize.name)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = aSize.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }

                    // 3. AURA DURATION & SPEED SELECTOR (1000ms, 2000ms, 3000ms, 4000ms) - Uniform 42.dp Height Buttons
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Aura Duration & Cycle Speed",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${prefs.cardAuraDuration}ms Cycle",
                                fontSize = 12.sp,
                                color = primaryCyberColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                1000 to "1000ms Fast",
                                2000 to "2000ms Standard",
                                3000 to "3000ms Smooth",
                                4000 to "4000ms Ambient"
                            ).forEach { (durMs, lbl) ->
                                val isSel = prefs.cardAuraDuration == durMs
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) primaryCyberColor.copy(alpha = 0.22f) else Color(0xFF090E17))
                                        .border(1.dp, if (isSel) primaryCyberColor else Color(0x3300F0FF), RoundedCornerShape(8.dp))
                                        .clickable {
                                            SoundManager.playClick()
                                            viewModel.updateSetting(PreferencesManager.CARD_AURA_DURATION.name, durMs)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = lbl,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) primaryCyberColor else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // 4. LAYER PLACEMENT (Behind Card 3D Underglow / Top Layer / Both)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Animation Layer Placement (এনিমেশন লেয়ার অবস্থান)",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Behind Card (3D Underglow)" to "✨ Behind (3D Underglow)",
                                "Above Card" to "💫 Top Border",
                                "Both (Dual 3D Dimension)" to "🌟 Both Layers"
                            ).forEach { (layerKey, label) ->
                                val isSel = prefs.cardGlowLayer == layerKey
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) primaryCyberColor.copy(alpha = 0.25f) else Color(0xFF090E17))
                                        .border(1.dp, if (isSel) primaryCyberColor else Color(0x3300F0FF), RoundedCornerShape(8.dp))
                                        .clickable {
                                            SoundManager.playClick()
                                            viewModel.updateSetting(PreferencesManager.CARD_GLOW_LAYER.name, layerKey)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) primaryCyberColor else TextSecondary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // 5. 3D FLOATING ELEVATION DEPTH
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "3D Floating Depth",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${prefs.card3dDepth.toInt()}dp Float",
                                fontSize = 12.sp,
                                color = primaryCyberColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        CyberSlider(
                            value = prefs.card3dDepth,
                            onValueChange = {
                                viewModel.updateSetting(PreferencesManager.CARD_3D_DEPTH.name, it)
                            },
                            valueRange = 0f..24f,
                            accentColor = primaryCyberColor
                        )
                    }

                    // 6. ANIMATION SCALE / SPREAD
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Animation Scale / Spread",
                                fontSize = 12.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${String.format("%.1fx", prefs.cardAnimationScale)} Scale",
                                fontSize = 12.sp,
                                color = primaryCyberColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        CyberSlider(
                            value = prefs.cardAnimationScale,
                            onValueChange = {
                                viewModel.updateSetting(PreferencesManager.CARD_ANIMATION_SCALE.name, it)
                            },
                            valueRange = 0.5f..2.2f,
                            accentColor = primaryCyberColor
                        )
                    }

                    // 4 CARD ANIMATION TYPES
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Card Animation Type",
                            fontSize = 13.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Choose your preferred motion and illumination aesthetic:",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        listOf(
                            Triple("Google / Firebase 3D Radial Nebula", "🔮", "Circular glowing orb moving beneath the card with 3D glass depth"),
                            Triple("GitHub 3D Spatial Levitation", "🛸", "Smooth vertical levitation & subtle 3D tilt with depth underglow"),
                            Triple("Behind-Card Cosmic Pulse", "💓", "Rhythmic luminous expansion and organic breathing glow under the card"),
                            Triple("Holographic 3D Glass", "🌊", "Diagonal cyber light beam sweeping across the surface with specular rim")
                        ).forEach { (animKey, icon, desc) ->
                            val isSel = prefs.cardAnimationType == animKey
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) primaryCyberColor.copy(alpha = 0.20f) else Color(0xFF090E17))
                                    .border(
                                        1.dp,
                                        if (isSel) primaryCyberColor else Color(0x3300F0FF),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        SoundManager.playClick()
                                        viewModel.updateSetting(PreferencesManager.CARD_ANIMATION_TYPE.name, animKey)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "$icon $animKey",
                                            fontSize = 13.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSel) primaryCyberColor else TextPrimary
                                        )
                                        Text(
                                            text = desc,
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    if (isSel) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(primaryCyberColor)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 8. COLOR CUSTOMIZATION MODE & PALETTES
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Color Customization Mode (রং নির্বাচন ও কাস্টমাইজেশন)",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "Preset Palette" to "রেডিমেড প্যালেট",
                                "Custom Color" to "কাস্টম রঙ",
                                "Match Cyber Theme" to "অ্যাপ থিম ম্যাচ"
                            ).forEach { (modeKey, modeLbl) ->
                                val isSel = prefs.cardColorMode == modeKey
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) primaryCyberColor.copy(alpha = 0.20f) else Color(0xFF090E17))
                                        .border(1.dp, if (isSel) primaryCyberColor else Color(0x3300F0FF), RoundedCornerShape(8.dp))
                                        .clickable {
                                            SoundManager.playClick()
                                            viewModel.updateSetting(PreferencesManager.CARD_COLOR_MODE.name, modeKey)
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = modeLbl,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) primaryCyberColor else TextSecondary
                                    )
                                }
                            }
                        }

                        // CUSTOM COLOR SWATCHES (matching text-orange-600)
                        if (prefs.cardColorMode == "Custom Color") {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Choose Custom Aura Tint (text-orange-600 ইত্যাদি):",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    listOf(
                                        "#FF6600" to Color(0xFFFF6600), // Orange (text-orange-600)
                                        "#00F0FF" to Color(0xFF00F0FF),
                                        "#FF007F" to Color(0xFFFF007F),
                                        "#00FF88" to Color(0xFF00FF88),
                                        "#FFD700" to Color(0xFFFFD700),
                                        "#9D00FF" to Color(0xFF9D00FF),
                                        "#00A3FF" to Color(0xFF00A3FF),
                                        "#FFFFFF" to Color(0xFFFFFFFF)
                                    ).forEach { (hexStr, c) ->
                                        val isChosen = prefs.cardCustomColor.equals(hexStr, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(c)
                                                .border(
                                                    if (isChosen) 2.5.dp else 1.dp,
                                                    if (isChosen) Color.White else Color.Black.copy(alpha = 0.5f),
                                                    CircleShape
                                                )
                                                .clickable {
                                                    SoundManager.playClick()
                                                    viewModel.updateSetting(PreferencesManager.CARD_CUSTOM_COLOR.name, hexStr)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isChosen) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = if (hexStr == "#FFFFFF" || hexStr == "#FFD700") Color.Black else Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // BACKGROUND TINT AURA (matching bg-yellow-200)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Background Aura Underglow (bg-yellow-200 ইত্যাদি ব্যাকগ্রাউন্ড আভা):",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                listOf(
                                    "" to ("None" to Color(0xFF1E293B)),
                                    "#40FFE082" to ("Yellow" to Color(0xFFFFE082)), // bg-yellow-200
                                    "#4000F0FF" to ("Cyan" to Color(0xFF00F0FF)),
                                    "#40FF007F" to ("Pink" to Color(0xFFFF007F)),
                                    "#40FFA500" to ("Orange" to Color(0xFFFFA500)),
                                    "#407000FF" to ("Purple" to Color(0xFF7000FF))
                                ).forEach { (bgHex, pair) ->
                                    val (name, col) = pair
                                    val isChosen = prefs.cardAuraBgColor == bgHex
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isChosen) primaryCyberColor.copy(alpha = 0.25f) else Color(0xFF090E17))
                                            .border(1.dp, if (isChosen) primaryCyberColor else Color(0x3300F0FF), RoundedCornerShape(8.dp))
                                            .clickable {
                                                SoundManager.playClick()
                                                viewModel.updateSetting(PreferencesManager.CARD_AURA_BG_COLOR.name, bgHex)
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(col)
                                            )
                                            Text(
                                                text = name,
                                                fontSize = 10.sp,
                                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isChosen) primaryCyberColor else TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 9. FINE-TUNING SLIDERS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Glow Border Thickness", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${prefs.cardGlowThickness.toInt()}dp", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                    }
                    CyberSlider(
                        value = prefs.cardGlowThickness,
                        onValueChange = {
                            viewModel.updateSetting(PreferencesManager.CARD_GLOW_THICKNESS.name, it)
                        },
                        valueRange = 1.0f..5.0f,
                        accentColor = primaryCyberColor
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Outer Glowing Aura Halo Intensity", fontSize = 12.sp, color = TextPrimary)
                        Text(text = "${prefs.cardGlowAura.toInt()}%", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                    }
                    CyberSlider(
                        value = prefs.cardGlowAura,
                        onValueChange = {
                            viewModel.updateSetting(PreferencesManager.CARD_GLOW_AURA.name, it)
                        },
                        valueRange = 0f..100f,
                        accentColor = primaryCyberColor
                    )
                }
            }
        }

        // ==========================================
        // 5. COLOR PALETTES (REQUESTED)
        // ==========================================
        if (prefs.showColorCard) {
            FuturisticCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = primaryCyberColor.copy(alpha = 0.45f),
                themePreset = prefs.themePreset,
                glassOpacity = prefs.glassOpacity,
                cornerRadius = prefs.cardCornerRadius.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = primaryCyberColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "🎨 Primary Accent Color",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryCyberColor
                                )
                                Text(
                                    text = "16 vibrant neon cyber spectral tones",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Side Action Badge matching first card
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(primaryCyberColor.copy(alpha = 0.18f))
                                .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable {
                                    SoundManager.playClick()
                                    viewModel.updateSetting(PreferencesManager.SHOW_COLOR_CARD.name, false)
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Hide Card",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor
                            )
                        }
                    }

                    val colors = listOf(
                        "#00F0FF" to "Cyan",
                        "#00FF88" to "Emerald",
                        "#A855F7" to "Purple",
                        "#EC4899" to "Hot Pink",
                        "#F59E0B" to "Amber",
                        "#3B82F6" to "Electric",
                        "#EF4444" to "Crimson",
                        "#14B8A6" to "Teal",
                        "#8B5CF6" to "Violet",
                        "#F97316" to "Blaze",
                        "#06B6D4" to "Ice Frost",
                        "#10B981" to "Bio Green",
                        "#E11D48" to "Rose",
                        "#D946EF" to "Fuchsia",
                        "#84CC16" to "Lime",
                        "#F43F5E" to "Coral"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        colors.chunked(4).forEach { rowColors ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowColors.forEach { (hex, name) ->
                                    val isSel = prefs.primaryColor.equals(hex, ignoreCase = true)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                SoundManager.playClick()
                                                viewModel.updateSetting(PreferencesManager.PRIMARY_COLOR.name, hex)
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(parseCyberColor(hex))
                                                .border(
                                                    width = if (isSel) 2.5.dp else 1.dp,
                                                    color = if (isSel) Color.White else parseCyberColor(hex).copy(alpha = 0.35f),
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSel) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = name,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) primaryCyberColor else TextSecondary,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    SettingsToggleRow("Background Cyber Grid", prefs.backgroundGrid) {
                        viewModel.updateSetting(PreferencesManager.BG_GRID.name, it)
                    }

                    SettingsToggleRow("Retro Scanline Filter", prefs.scanlineCrtEffect) {
                        viewModel.updateSetting(PreferencesManager.SCANLINE_CRT.name, it)
                    }
                }
            }
        }

        // ==========================================
        // 6. AUDIO & HAPTICS (EXPANDED ENGINE)
        // ==========================================
        if (prefs.showAudioCard) {
            FuturisticCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = primaryCyberColor.copy(alpha = 0.45f),
                themePreset = prefs.themePreset,
                glassOpacity = prefs.glassOpacity,
                cornerRadius = prefs.cardCornerRadius.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = primaryCyberColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "🔊 Audio & Haptic System",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryCyberColor
                                )
                                Text(
                                    text = "Synthesized dynamic sounds & tactile response",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Side Action Badge matching first card
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(primaryCyberColor.copy(alpha = 0.18f))
                                .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable {
                                    SoundManager.playClick()
                                    viewModel.updateSetting(PreferencesManager.SHOW_AUDIO_CARD.name, false)
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Hide Card",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor
                            )
                        }
                    }

                    SettingsToggleRow("Master Sound FX", prefs.masterSoundFx) {
                        viewModel.updateSetting(PreferencesManager.MASTER_SOUND_FX.name, it)
                    }

                    // Sound Volume Slider (0% - 100%)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Master Audio Volume Scale", fontSize = 12.sp, color = TextPrimary)
                            Text(text = "${prefs.soundVolume.toInt()}%", fontSize = 12.sp, color = primaryCyberColor, fontWeight = FontWeight.Bold)
                        }
                        CyberSlider(
                            value = prefs.soundVolume,
                            onValueChange = {
                                viewModel.updateSetting(PreferencesManager.SOUND_VOLUME.name, it)
                            },
                            valueRange = 0f..100f,
                            accentColor = primaryCyberColor
                        )
                    }

                    // Sound Pack Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "Acoustic Sound Pack", fontSize = 12.sp, color = TextPrimary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Cyber Arcade", "Neo Synthwave", "Crystal Zen").forEach { pack ->
                                val isSel = prefs.soundPack == pack
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) primaryCyberColor.copy(alpha = 0.2f) else Color(0xFF090E17))
                                        .border(1.dp, if (isSel) primaryCyberColor else Color(0x3300F0FF), RoundedCornerShape(8.dp))
                                        .clickable {
                                            SoundManager.playSuccess()
                                            viewModel.updateSetting(PreferencesManager.SOUND_PACK.name, pack)
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = pack,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) primaryCyberColor else TextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // Live Audio Test Bench Chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "🎧 Live Sound Test Bench (Tap to Preview)", fontSize = 11.sp, color = TextSecondary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "🪙 Coin" to { SoundManager.playCoin() },
                                "🔥 Streak" to { SoundManager.playStreak() },
                                "🏆 LevelUp" to { SoundManager.playLevelUp() },
                                "🎯 Goal" to { SoundManager.playTargetCompleted() }
                            ).forEach { (label, action) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF0F172A))
                                        .border(1.dp, primaryCyberColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                        .clickable { action() }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = label, fontSize = 10.sp, color = primaryCyberColor, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    SettingsToggleRow("UI Click SFX", prefs.uiClickSounds) {
                        viewModel.updateSetting(PreferencesManager.UI_CLICK_SOUNDS.name, it)
                    }
                    SettingsToggleRow("Victory Celebration Fanfare", prefs.victoryFanfare) {
                        viewModel.updateSetting(PreferencesManager.VICTORY_FANFARE.name, it)
                    }
                    SettingsToggleRow("Haptic Vibration Feedback", prefs.hapticFeedback) {
                        viewModel.updateSetting(PreferencesManager.HAPTIC_FEEDBACK.name, it)
                    }
                }
            }
        }

        // ==========================================
        // 7. BACKUP & RESTORE DATA ENGINE (REQUESTED)
        // ==========================================
        if (prefs.showBackupCard) {
            FuturisticCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = primaryCyberColor.copy(alpha = 0.5f),
                themePreset = prefs.themePreset,
                glassOpacity = prefs.glassOpacity,
                cornerRadius = prefs.cardCornerRadius.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backup,
                                contentDescription = null,
                                tint = primaryCyberColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "💾 Cloud & Local Backup / Restore",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryCyberColor
                                )
                                Text(
                                    text = "Export snapshots, quick checkpoints, restore",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Side Action Badge matching first card
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(primaryCyberColor.copy(alpha = 0.18f))
                                .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable {
                                    SoundManager.playClick()
                                    viewModel.updateSetting(PreferencesManager.SHOW_BACKUP_CARD.name, false)
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Hide Card",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor
                            )
                        }
                    }

                    Text(
                        text = "Protect your neural telemetry, scores, streak, and custom target goals with instant local checkpoints and full JSON snapshots.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    // Quick Checkpoint Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CyberButton(
                            text = "⚡ Save Checkpoint",
                            onClick = {
                                SoundManager.playSuccess()
                                viewModel.createRestoreCheckpoint()
                                Toast.makeText(context, "Checkpoint Snapshot Created!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        CyberButton(
                            text = "🔄 Restore Checkpoint",
                            onClick = {
                                SoundManager.playSuccess()
                                val ok = viewModel.restoreFromCheckpoint()
                                if (ok) {
                                    Toast.makeText(context, "Restored from Last Checkpoint!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "No saved checkpoint found", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // JSON Export / Import Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CyberButton(
                            text = "📋 Copy JSON Data",
                            onClick = {
                                SoundManager.playClick()
                                val json = viewModel.exportBackupJson()
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Neural Backup", json)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Backup JSON Copied to Clipboard!", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        CyberButton(
                            text = "📥 Import JSON Data",
                            onClick = {
                                SoundManager.playClick()
                                showImportBackupDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Reset Settings Only button
                    CyberButton(
                        text = "🎨 Reset Themes & Visuals Only",
                        onClick = {
                            SoundManager.playClick()
                            showResetSettingsDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // ==========================================
        // 8. LANGUAGE (LOCKED ENGLISH ONLY)
        // ==========================================
        FuturisticCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = primaryCyberColor.copy(alpha = 0.45f),
            themePreset = prefs.themePreset,
            glassOpacity = prefs.glassOpacity,
            cornerRadius = prefs.cardCornerRadius.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🌐 Language: English (US)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Configured exclusively in English.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonGreen.copy(alpha = 0.15f))
                        .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Locked",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                }
            }
        }

        // ==========================================
        // 9. PROFILE & RESET DATA (REQUESTED)
        // ==========================================
        if (prefs.showIdentityCard) {
            FuturisticCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = primaryCyberColor.copy(alpha = 0.45f),
                themePreset = prefs.themePreset,
                glassOpacity = prefs.glassOpacity,
                cornerRadius = prefs.cardCornerRadius.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = primaryCyberColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "👤 Operative Identity",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryCyberColor
                                )
                                Text(
                                    text = "Customize profile callsign & master reset",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Side Action Badge matching first card
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(primaryCyberColor.copy(alpha = 0.18f))
                                .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable {
                                    SoundManager.playClick()
                                    viewModel.updateSetting(PreferencesManager.SHOW_IDENTITY_CARD.name, false)
                                }
                                .padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Hide Card",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryCyberColor
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, primaryCyberColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .background(Color(0xFF090F18))
                                .padding(horizontal = 14.dp, vertical = 11.dp)
                        ) {
                            BasicTextField(
                                value = userNameInput,
                                onValueChange = { userNameInput = it },
                                singleLine = true,
                                textStyle = TextStyle(color = TextPrimary, fontSize = 14.sp),
                                cursorBrush = SolidColor(primaryCyberColor),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        CyberButton(
                            text = "Save",
                            onClick = {
                                if (userNameInput.isNotBlank()) {
                                    SoundManager.playClick()
                                    viewModel.updateSetting(PreferencesManager.USER_NAME.name, userNameInput.trim())
                                    Toast.makeText(context, "Operative Name Saved!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    CyberButton(
                        text = "⚠️ Reset All Training Progress",
                        onClick = {
                            SoundManager.playBuzzer()
                            showResetConfirmDialog = true
                        },
                        backgroundColor = Color(0xFFDC2626),
                        textColor = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // ==========================================
        // 9. OFFICIAL WAVEMIND APP SUITE INFO CARD
        // ==========================================
        FuturisticCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = primaryCyberColor.copy(alpha = 0.5f),
            themePreset = prefs.themePreset,
            glassOpacity = prefs.glassOpacity,
            cornerRadius = prefs.cardCornerRadius.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.app_logo),
                    contentDescription = "WaveMind Official Logo",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, primaryCyberColor.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                )

                Text(
                    text = "WaveMind",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryCyberColor,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "v1.0.0 (Official Release)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Text(
                    text = "29 Calibrated Cognitive Drills • 120 FPS High Refresh Rate Engine\nZero-Lag Render Architecture • 3D Behind-Card Nebula Underglow",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 16.sp
                )

                Text(
                    text = "Crafted for Peak Neural Acuity",
                    fontSize = 10.sp,
                    color = primaryCyberColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(96.dp))
    }

    // Import Backup JSON Dialog
    if (showImportBackupDialog) {
        AlertDialog(
            onDismissRequest = { showImportBackupDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = null,
                    tint = primaryCyberColor,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "Import Backup JSON",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Paste your exported JSON telemetry backup below:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF090E17))
                            .border(1.dp, primaryCyberColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        if (importJsonText.isEmpty()) {
                            Text(
                                text = "Paste JSON here...",
                                color = Color(0xFF475569),
                                fontSize = 12.sp
                            )
                        }
                        BasicTextField(
                            value = importJsonText,
                            onValueChange = { importJsonText = it },
                            textStyle = TextStyle(color = TextPrimary, fontSize = 12.sp),
                            cursorBrush = SolidColor(primaryCyberColor),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (importJsonText.isNotBlank()) {
                            val success = viewModel.importBackupJson(importJsonText.trim())
                            if (success) {
                                SoundManager.playSuccess()
                                Toast.makeText(context, "Data Successfully Restored!", Toast.LENGTH_SHORT).show()
                                showImportBackupDialog = false
                                importJsonText = ""
                            } else {
                                SoundManager.playError()
                                Toast.makeText(context, "Invalid JSON structure", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("Restore Now", color = primaryCyberColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportBackupDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF0C1420),
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Reset Visual Settings Only Confirmation Dialog
    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = {
                Text(
                    text = "Reset Visual Settings?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "This will restore default theme, opacity, corners, and sounds. Your XP, Credits, Streak, and high scores will be preserved.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSettingsOnly()
                        showResetSettingsDialog = false
                        Toast.makeText(context, "Visual settings reset to default", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Reset Visuals", color = primaryCyberColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF0C1420),
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Factory Reset Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF4444),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Reset All Training Progress?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "This action will reset your XP, Credits, Streak, and target goals.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        SoundManager.playError()
                        viewModel.resetAllData()
                        showResetConfirmDialog = false
                        Toast.makeText(context, "Progress Reset to Initial State", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Confirm Reset", color = Color(0xFFFF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF0C1420),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

private fun colorToHex(color: Color): String {
    val r = (color.red * 255).toInt().coerceIn(0, 255)
    val g = (color.green * 255).toInt().coerceIn(0, 255)
    val b = (color.blue * 255).toInt().coerceIn(0, 255)
    return String.format("#%02X%02X%02X", r, g, b)
}

@Composable
private fun ThemeCardItem(
    theme: ThemeConfig,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) theme.primaryColor.copy(alpha = 0.20f) else Color(0xFF090F1B))
            .border(
                width = if (isSelected) 1.5.dp else 0.8.dp,
                color = if (isSelected) theme.primaryColor else Color(0x3300F0FF),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.backgroundColor)
                    .border(1.dp, theme.primaryColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = theme.iconEmoji, fontSize = 20.sp)
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = theme.displayName,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) theme.primaryColor else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = theme.subtitle,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(theme.primaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Active Theme",
                        tint = Color(0xFF04070D),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 13.sp, color = TextPrimary)
        CyberSwitch(
            checked = checked,
            onCheckedChange = {
                SoundManager.playClick()
                onCheckedChange(it)
            }
        )
    }
}
