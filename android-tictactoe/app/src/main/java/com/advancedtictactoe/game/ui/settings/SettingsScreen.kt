package com.advancedtictactoe.game.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.advancedtictactoe.game.data.preferences.AppPreferences
import com.advancedtictactoe.game.ui.game.components.GlassCard
import com.advancedtictactoe.game.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: AppPreferences,
) : ViewModel() {
    val soundEnabled     = prefs.soundEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val musicEnabled     = prefs.musicEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val hapticEnabled    = prefs.hapticEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val darkMode         = prefs.darkMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val musicVolume      = prefs.musicVolume.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.5f)
    val sfxVolume        = prefs.sfxVolume.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.8f)
    val colorblindMode   = prefs.colorblindMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val largeText        = prefs.largeText.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val particlesEnabled = prefs.particlesEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val graphicsQuality  = prefs.graphicsQuality.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "HIGH")

    fun setSoundEnabled(v: Boolean) = viewModelScope.launch { prefs.setSoundEnabled(v) }
    fun setMusicEnabled(v: Boolean) = viewModelScope.launch { prefs.setMusicEnabled(v) }
    fun setHapticEnabled(v: Boolean) = viewModelScope.launch { prefs.setHapticEnabled(v) }
    fun setDarkMode(v: Boolean) = viewModelScope.launch { prefs.setDarkMode(v) }
    fun setMusicVolume(v: Float) = viewModelScope.launch { prefs.setMusicVolume(v) }
    fun setSfxVolume(v: Float) = viewModelScope.launch { prefs.setSfxVolume(v) }
    fun setColorblindMode(v: Boolean) = viewModelScope.launch { prefs.setColorblindMode(v) }
    fun setLargeText(v: Boolean) = viewModelScope.launch { prefs.setLargeText(v) }
    fun setParticlesEnabled(v: Boolean) = viewModelScope.launch { prefs.setParticlesEnabled(v) }
    fun setGraphicsQuality(v: String) = viewModelScope.launch { prefs.setGraphicsQuality(v) }
}

@Composable
fun SettingsScreen(onBack: () -> Unit, vm: SettingsViewModel = hiltViewModel()) {
    val soundEnabled     by vm.soundEnabled.collectAsStateWithLifecycle()
    val musicEnabled     by vm.musicEnabled.collectAsStateWithLifecycle()
    val hapticEnabled    by vm.hapticEnabled.collectAsStateWithLifecycle()
    val darkMode         by vm.darkMode.collectAsStateWithLifecycle()
    val musicVolume      by vm.musicVolume.collectAsStateWithLifecycle()
    val sfxVolume        by vm.sfxVolume.collectAsStateWithLifecycle()
    val colorblindMode   by vm.colorblindMode.collectAsStateWithLifecycle()
    val largeText        by vm.largeText.collectAsStateWithLifecycle()
    val particlesEnabled by vm.particlesEnabled.collectAsStateWithLifecycle()
    val graphicsQuality  by vm.graphicsQuality.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070B14), DarkBg)))) {
        Column(
            Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 32.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Text("←", color = Color.White, fontSize = 24.sp) }
                Text("SETTINGS", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                Spacer(Modifier.size(48.dp))
            }

            Spacer(Modifier.height(20.dp))

            SectionTitle("🔊 AUDIO")
            SettingsToggle("Sound Effects", soundEnabled, NeonCyan, vm::setSoundEnabled)
            SettingsToggle("Background Music", musicEnabled, NeonPurple, vm::setMusicEnabled)

            if (musicEnabled) {
                Spacer(Modifier.height(8.dp))
                GlassCard(Modifier.fillMaxWidth(), cornerRadius = 14.dp) {
                    Column {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Music Volume", color = Color.White, fontSize = 13.sp)
                            Text("${(musicVolume * 100).toInt()}%", color = NeonPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(value = musicVolume, onValueChange = vm::setMusicVolume,
                            colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple))
                    }
                }
            }
            if (soundEnabled) {
                Spacer(Modifier.height(8.dp))
                GlassCard(Modifier.fillMaxWidth(), cornerRadius = 14.dp) {
                    Column {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("SFX Volume", color = Color.White, fontSize = 13.sp)
                            Text("${(sfxVolume * 100).toInt()}%", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(value = sfxVolume, onValueChange = vm::setSfxVolume,
                            colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionTitle("🎮 CONTROLS")
            SettingsToggle("Haptic Feedback", hapticEnabled, NeonPink, vm::setHapticEnabled)

            Spacer(Modifier.height(16.dp))
            SectionTitle("🎨 DISPLAY")
            SettingsToggle("Dark Mode", darkMode, NeonCyan, vm::setDarkMode)
            SettingsToggle("Particle Effects", particlesEnabled, NeonPurple, vm::setParticlesEnabled)

            Spacer(Modifier.height(8.dp))
            GlassCard(Modifier.fillMaxWidth(), cornerRadius = 14.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Graphics Quality", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("LOW", "MEDIUM", "HIGH").forEach { q ->
                            FilterChip(
                                selected = graphicsQuality == q,
                                onClick = { vm.setGraphicsQuality(q) },
                                label = { Text(q, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonCyan.copy(0.2f),
                                    selectedLabelColor = NeonCyan,
                                    labelColor = TextSecondary,
                                )
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionTitle("♿ ACCESSIBILITY")
            SettingsToggle("Colorblind Mode", colorblindMode, NeonGreen, vm::setColorblindMode)
            SettingsToggle("Large Text", largeText, NeonYellow, vm::setLargeText)

            Spacer(Modifier.height(16.dp))
            SectionTitle("ℹ️ ABOUT")
            GlassCard(Modifier.fillMaxWidth(), cornerRadius = 14.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    InfoRow("App", "Advanced Tic-Tac-Toe 3D")
                    InfoRow("Version", "1.0.0 (2026 Edition)")
                    InfoRow("Engine", "Minimax + Alpha-Beta Pruning")
                    InfoRow("Build", "Native Kotlin + Jetpack Compose")
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = TextMuted, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
}

@Composable
private fun SettingsToggle(label: String, value: Boolean, color: Color, onToggle: (Boolean) -> Unit) {
    GlassCard(
        Modifier.fillMaxWidth().padding(bottom = 8.dp),
        accentColor = if (value) color else null,
        cornerRadius = 14.dp,
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = Color.White, fontSize = 14.sp)
            Switch(
                checked = value, onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = color,
                    checkedTrackColor = color.copy(alpha = 0.3f),
                )
            )
        }
    }
}

@Composable
private fun InfoRow(key: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(key, color = TextSecondary, fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

private val NeonGreen  = Color(0xFF39FF14)
private val NeonYellow = Color(0xFFFFFF00)
