package com.advancedtictactoe.game.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.advancedtictactoe.game.data.preferences.AppPreferences
import com.advancedtictactoe.game.ui.game.components.GlassCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeSelectorViewModel @Inject constructor(
    private val prefs: AppPreferences,
) : ViewModel() {
    val selectedTheme = prefs.selectedTheme
        .map { name -> try { AppTheme.valueOf(name) } catch (e: Exception) { AppTheme.CYBERPUNK } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.CYBERPUNK)

    fun selectTheme(theme: AppTheme) = viewModelScope.launch {
        prefs.setSelectedTheme(theme.name)
    }
}

@Composable
fun ThemeSelectorScreen(onBack: () -> Unit, vm: ThemeSelectorViewModel = hiltViewModel()) {
    val selected by vm.selectedTheme.collectAsStateWithLifecycle()

    Box(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(selected.bgStart, selected.bgEnd)))
    ) {
        Column(
            Modifier.fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 32.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) { Text("←", color = Color.White, fontSize = 24.sp) }
                Text("THEMES", color = selected.accent, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                Spacer(Modifier.size(48.dp))
            }

            Spacer(Modifier.height(8.dp))

            // Preview
            Box(
                Modifier.fillMaxWidth().height(160.dp)
                    .background(
                        Brush.horizontalGradient(listOf(selected.bgStart, selected.bgEnd)),
                        RoundedCornerShape(20.dp)
                    )
                    .border(2.dp, selected.accent.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✕ ⊙ ✕", color = selected.accent, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(8.dp))
                    Text(selected.displayName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                    Text("Active Theme", color = selected.accent.copy(0.6f), fontSize = 11.sp, letterSpacing = 2.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("SELECT THEME", color = NeonCyan.copy(0.6f), fontSize = 11.sp, letterSpacing = 3.sp)
            Spacer(Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(AppTheme.values()) { theme ->
                    ThemeCard(
                        theme = theme,
                        isSelected = theme == selected,
                        onClick = { vm.selectTheme(theme) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeCard(theme: AppTheme, isSelected: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "theme")
    val glow by infiniteTransition.animateFloat(
        0.5f, 1f,
        infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )
    val borderAlpha = if (isSelected) glow * 0.8f else 0.2f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(
                Brush.horizontalGradient(listOf(theme.bgStart, theme.bgEnd, theme.accent.copy(0.1f))),
                RoundedCornerShape(16.dp)
            )
            .border(
                if (isSelected) 2.dp else 1.dp,
                theme.accent.copy(alpha = borderAlpha),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    Modifier.size(36.dp)
                        .background(theme.accent.copy(0.2f), RoundedCornerShape(8.dp))
                        .border(1.dp, theme.accent.copy(0.4f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✕", color = theme.accent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(theme.displayName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Tap to apply", color = Color.White.copy(0.4f), fontSize = 10.sp, letterSpacing = 1.sp)
                }
            }
            if (isSelected) {
                Box(
                    Modifier.size(24.dp)
                        .background(theme.accent, androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("✓", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
