package com.advancedtictactoe.game.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.advancedtictactoe.game.data.model.*
import com.advancedtictactoe.game.data.preferences.AppPreferences
import com.advancedtictactoe.game.data.repository.GameRepository
import com.advancedtictactoe.game.ui.game.components.*
import com.advancedtictactoe.game.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: GameRepository,
    prefs: AppPreferences,
) : ViewModel() {
    val profile = repository.profile.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )
    val selectedTheme = prefs.selectedTheme.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "CYBERPUNK"
    )
}

data class GameModeItem(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val color: Color,
    val mode: String,
    val defaultDiff: String = "MEDIUM",
)

val gameModes = listOf(
    GameModeItem("🤖", "vs AI",         "5 difficulty levels", NeonCyan,   "SINGLE_PLAYER"),
    GameModeItem("👥", "Local 2P",      "Pass & play",        NeonPurple, "LOCAL_MULTIPLAYER"),
    GameModeItem("🌐", "Online",        "Real-time PvP",      NeonPink,   "ONLINE_MULTIPLAYER"),
    GameModeItem("🏆", "Tournament",    "Knockout bracket",   RankGold,   "TOURNAMENT"),
    GameModeItem("⚡", "Quick Match",  "1-click match",      NeonGreen,  "QUICK_MATCH"),
)

val boardSizes = listOf(3 to "3×3 Classic", 4 to "4×4", 5 to "5×5", 6 to "6×6 XL")
val difficulties = AIDifficulty.values().toList()

@Composable
fun HomeScreen(
    onNavigateToGame: (String, Int, String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTheme: () -> Unit,
    vm: HomeViewModel = hiltViewModel(),
) {
    val profile by vm.profile.collectAsStateWithLifecycle()
    var showModeDialog by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(gameModes[0]) }

    val infiniteTransition = rememberInfiniteTransition(label = "home")
    val glow by infiniteTransition.animateFloat(
        0.5f, 1f,
        infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF070B14), Color(0xFF0A0F1E))))
    ) {
        FloatingParticles(Modifier.fillMaxSize(), NeonCyan.copy(alpha = 0.5f), 15)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 32.dp),
        ) {
            // Top bar
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Welcome back,", color = TextSecondary, fontSize = 13.sp)
                    Text(
                        profile?.username ?: "Player",
                        color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconBubble("🎨", onClick = onNavigateToTheme, color = NeonPurple)
                    IconBubble("⚙️", onClick = onNavigateToSettings, color = NeonCyan)
                    IconBubble(
                        text = profile?.avatarIndex?.let { avatarEmojis.getOrElse(it) { "👤" } } ?: "👤",
                        onClick = onNavigateToProfile,
                        color = NeonPink,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Stats strip
            if (profile != null) {
                GlassCard(modifier = Modifier.fillMaxWidth(), accentColor = NeonCyan) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        StatChip("🏅", profile!!.rank.label, profile!!.rank.label)
                        StatChip("⭐", "${profile!!.xp} XP", "XP")
                        StatChip("🏆", "${profile!!.totalWins}", "Wins")
                        StatChip("🪙", "${profile!!.coins}", "Coins")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Title
            Text(
                "SELECT\nGAME MODE",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                lineHeight = 34.sp,
            )
            Text(
                "Choose your battlefield",
                color = NeonCyan.copy(alpha = glow),
                fontSize = 13.sp,
                letterSpacing = 3.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Mode cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(360.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false,
            ) {
                items(gameModes) { mode ->
                    ModeCard(mode) {
                        selectedMode = mode
                        showModeDialog = true
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Quick nav
            Text("QUICK ACCESS", color = TextMuted, fontSize = 11.sp, letterSpacing = 3.sp)
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                QuickNavCard("🏆", "Leaderboard", NeonGold, Modifier.weight(1f), onNavigateToLeaderboard)
                QuickNavCard("🎖️", "Achievements", NeonPurple, Modifier.weight(1f), onNavigateToAchievements)
            }
        }
    }

    if (showModeDialog) {
        ModeConfigDialog(
            mode = selectedMode,
            onDismiss = { showModeDialog = false },
            onConfirm = { boardSize, diff ->
                showModeDialog = false
                onNavigateToGame(selectedMode.mode, boardSize, diff)
            }
        )
    }
}

val avatarEmojis = listOf("👤","🦁","🐯","🦊","🐺","🦅","🐉","🤖","👾","🎭","🎩","👑")

@Composable
private fun StatChip(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 18.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextMuted, fontSize = 10.sp)
    }
}

@Composable
private fun ModeCard(mode: GameModeItem, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().height(100.dp).clickable(onClick = onClick),
        accentColor = mode.color,
        cornerRadius = 16.dp,
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Text(mode.emoji, fontSize = 28.sp)
            Spacer(Modifier.height(4.dp))
            Text(mode.title,    color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(mode.subtitle, color = TextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun QuickNavCard(emoji: String, title: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    GlassCard(modifier.height(72.dp).clickable(onClick = onClick), accentColor = color) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.width(10.dp))
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun IconBubble(text: String, onClick: () -> Unit, color: Color) {
    Box(
        modifier = Modifier.size(44.dp)
            .background(color.copy(alpha = 0.15f), CircleShape)
            .border(1.dp, color.copy(alpha = 0.3f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Text(text, fontSize = 18.sp) }
}

val NeonGold = Color(0xFFFFD700)

@Composable
private fun ModeConfigDialog(
    mode: GameModeItem,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit,
) {
    var selectedSize by remember { mutableIntStateOf(3) }
    var selectedDiff by remember { mutableStateOf(AIDifficulty.MEDIUM) }
    val isSinglePlayer = mode.mode == "SINGLE_PLAYER"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Text("${mode.emoji} ${mode.title}", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Board Size", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    boardSizes.forEach { (size, label) ->
                        FilterChip(
                            selected = selectedSize == size,
                            onClick = { selectedSize = size },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = mode.color.copy(alpha = 0.2f),
                                selectedLabelColor = mode.color,
                            )
                        )
                    }
                }
                if (isSinglePlayer) {
                    Text("AI Difficulty", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        difficulties.forEach { diff ->
                            FilterChip(
                                selected = selectedDiff == diff,
                                onClick = { selectedDiff = diff },
                                label = { Text(diff.label, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = mode.color.copy(alpha = 0.2f),
                                    selectedLabelColor = mode.color,
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            NeonButton(
                text = "Play Now",
                onClick = { onConfirm(selectedSize, selectedDiff.name) },
                color = mode.color,
                modifier = Modifier.fillMaxWidth(),
                height = 48.dp,
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
