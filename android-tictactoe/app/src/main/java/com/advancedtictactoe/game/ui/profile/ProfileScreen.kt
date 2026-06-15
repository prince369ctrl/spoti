package com.advancedtictactoe.game.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
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
import com.advancedtictactoe.game.data.model.*
import com.advancedtictactoe.game.data.repository.GameRepository
import com.advancedtictactoe.game.ui.game.components.GlassCard
import com.advancedtictactoe.game.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GameRepository,
) : ViewModel() {
    val profile      = repository.profile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val matchHistory = repository.matchHistory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateUsername(name: String) = viewModelScope.launch {
        val p = profile.value ?: return@launch
        repository.updateProfile(p.copy(username = name.take(20)))
    }
    fun updateAvatar(idx: Int) = viewModelScope.launch {
        val p = profile.value ?: return@launch
        repository.updateProfile(p.copy(avatarIndex = idx))
    }
}

val avatarList = listOf("👤","🦁","🐯","🦊","🐺","🦅","🐉","🤖","👾","🎭","🎩","👑","⚡","🔥","💎")

@Composable
fun ProfileScreen(onBack: () -> Unit, vm: ProfileViewModel = hiltViewModel()) {
    val profile by vm.profile.collectAsStateWithLifecycle()
    val history by vm.matchHistory.collectAsStateWithLifecycle()
    var editingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }

    Box(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF070B14), Color(0xFF0D0F1E))))
    ) {
        Column(
            Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 32.dp)
        ) {
            // Top bar
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Text("←", color = Color.White, fontSize = 24.sp) }
                Text("PROFILE", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                Spacer(Modifier.size(48.dp))
            }

            Spacer(Modifier.height(20.dp))

            profile?.let { p ->
                // Avatar
                Box(Modifier.align(Alignment.CenterHorizontally)) {
                    Box(
                        Modifier.size(100.dp)
                            .background(NeonCyan.copy(0.12f), CircleShape)
                            .border(2.dp, NeonCyan.copy(0.4f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) { Text(avatarList.getOrElse(p.avatarIndex) { "👤" }, fontSize = 48.sp) }
                }

                Spacer(Modifier.height(12.dp))

                // Name + edit
                if (editingName) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nameInput, onValueChange = { nameInput = it },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan, unfocusedBorderColor = GlassBorder,
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            ),
                            singleLine = true,
                        )
                        Button(onClick = { vm.updateUsername(nameInput); editingName = false },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)) {
                            Text("Save", color = DarkBg, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Text(p.username, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = { nameInput = p.username; editingName = true }) {
                            Text("✏️", fontSize = 16.sp)
                        }
                    }
                }

                Text(p.rank.label, color = Color(p.rank.color.toInt()), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.CenterHorizontally))

                Spacer(Modifier.height(20.dp))

                // XP bar
                GlassCard(Modifier.fillMaxWidth(), accentColor = NeonCyan) {
                    Column {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${p.xp} XP", color = NeonCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            val nextRank = Rank.values().firstOrNull { it.minXp > p.xp }
                            if (nextRank != null)
                                Text("${nextRank.label}: ${nextRank.minXp} XP", color = TextSecondary, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        val progress = if (p.rank == Rank.LEGEND) 1f else {
                            val next = Rank.values().firstOrNull { it.minXp > p.xp }
                            if (next != null) (p.xp - p.rank.minXp).toFloat() / (next.minXp - p.rank.minXp)
                            else 1f
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = NeonCyan,
                            trackColor = NeonCyan.copy(0.15f),
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Stats
                Text("STATISTICS", color = TextMuted, fontSize = 11.sp, letterSpacing = 3.sp)
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("🏆", "Wins", "${p.totalWins}", NeonCyan, Modifier.weight(1f))
                    StatCard("💀", "Losses", "${p.totalLosses}", NeonPink, Modifier.weight(1f))
                    StatCard("🤝", "Draws", "${p.totalDraws}", NeonYellow, Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("📊", "Win Rate", "${(p.winRate * 100).toInt()}%", NeonGreen, Modifier.weight(1f))
                    StatCard("🔥", "Best Streak", "${p.bestWinStreak}", NeonOrange, Modifier.weight(1f))
                    StatCard("🪙", "Coins", "${p.coins}", RankGold, Modifier.weight(1f))
                }

                Spacer(Modifier.height(20.dp))

                // Avatar selector
                Text("AVATAR", color = TextMuted, fontSize = 11.sp, letterSpacing = 3.sp)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    itemsIndexed(avatarList) { idx, emoji ->
                        val isSelected = idx == p.avatarIndex
                        Box(
                            Modifier.size(52.dp)
                                .background(
                                    if (isSelected) NeonCyan.copy(0.2f) else Color.White.copy(0.05f),
                                    CircleShape
                                )
                                .border(if (isSelected) 2.dp else 1.dp, if (isSelected) NeonCyan else GlassBorder, CircleShape)
                                .clickable { vm.updateAvatar(idx) },
                            contentAlignment = Alignment.Center,
                        ) { Text(emoji, fontSize = 24.sp) }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Recent matches
                Text("RECENT MATCHES", color = TextMuted, fontSize = 11.sp, letterSpacing = 3.sp)
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    history.take(10).forEach { match ->
                        MatchRow(match)
                    }
                    if (history.isEmpty()) {
                        Text("No matches yet. Start playing!", color = TextMuted, fontSize = 13.sp)
                    }
                }
            } ?: CircularProgressIndicator(color = NeonCyan, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
private fun StatCard(icon: String, label: String, value: String, color: Color, modifier: Modifier) {
    GlassCard(modifier.height(80.dp), accentColor = color, cornerRadius = 14.dp) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
            Text(icon, fontSize = 18.sp)
            Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun MatchRow(match: MatchRecord) {
    val (emoji, color) = when (match.result) {
        "WIN"  -> "🏆" to NeonCyan
        "LOSS" -> "💀" to NeonPink
        else   -> "🤝" to NeonYellow
    }
    GlassCard(Modifier.fillMaxWidth(), cornerRadius = 12.dp) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(emoji, fontSize = 20.sp)
                Column {
                    Text(match.result, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("${match.boardSize}×${match.boardSize} • ${match.difficulty.lowercase().replaceFirstChar { it.uppercase() }}", color = TextSecondary, fontSize = 11.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("+${match.xpEarned} XP", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("${match.movesCount} moves", color = TextMuted, fontSize = 10.sp)
            }
        }
    }
}

private val NeonYellow = Color(0xFFFFFF00)
private val NeonGreen  = Color(0xFF39FF14)
private val NeonOrange = Color(0xFFFF6B00)
private val RankGold   = Color(0xFFFFD700)
