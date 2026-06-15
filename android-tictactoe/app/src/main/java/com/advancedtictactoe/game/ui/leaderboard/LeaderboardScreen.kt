package com.advancedtictactoe.game.ui.leaderboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
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
import com.advancedtictactoe.game.data.model.*
import com.advancedtictactoe.game.data.repository.GameRepository
import com.advancedtictactoe.game.ui.game.components.GlassCard
import com.advancedtictactoe.game.ui.profile.avatarList
import com.advancedtictactoe.game.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    repository: GameRepository,
) : ViewModel() {
    val localEntry = repository.profile.map { p ->
        p?.let {
            LeaderboardEntry(1, it.username, it.xp, it.totalWins, it.avatarIndex, it.rank)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Simulated leaderboard with local player + mock global entries
    val leaderboard = localEntry.map { local ->
        buildList {
            local?.let { add(it.copy(rank = 1)) }
            addAll(mockLeaderboard)
        }.sortedByDescending { it.xp }.mapIndexed { idx, e -> e.copy(rank = idx + 1) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), mockLeaderboard)
}

private val mockLeaderboard = listOf(
    LeaderboardEntry(1, "NeonMaster",    28500, 412, 6,  Rank.LEGEND),
    LeaderboardEntry(2, "CyberQueen",    22100, 334, 7,  Rank.GRANDMASTER),
    LeaderboardEntry(3, "DragonSlayer",  18900, 289, 4,  Rank.GRANDMASTER),
    LeaderboardEntry(4, "PixelKing",     15400, 241, 8,  Rank.MASTER),
    LeaderboardEntry(5, "GalaxyWolf",    12800, 198, 5,  Rank.MASTER),
    LeaderboardEntry(6, "NightOwl",      10100, 167, 9,  Rank.DIAMOND),
    LeaderboardEntry(7, "StarForge",     8400,  142, 2,  Rank.DIAMOND),
    LeaderboardEntry(8, "IronFist",      6200,  118, 3,  Rank.PLATINUM),
    LeaderboardEntry(9, "TactMaster",    4800,  89,  10, Rank.PLATINUM),
    LeaderboardEntry(10,"RookieRick",    2100,  43,  0,  Rank.GOLD),
)

@Composable
fun LeaderboardScreen(onBack: () -> Unit, vm: LeaderboardViewModel = hiltViewModel()) {
    val board by vm.leaderboard.collectAsStateWithLifecycle()
    val local by vm.localEntry.collectAsStateWithLifecycle()
    var tab by remember { mutableIntStateOf(0) }

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070B14), DarkBg)))) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 56.dp, bottom = 16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Text("←", color = Color.White, fontSize = 24.sp) }
                Text("LEADERBOARD", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                Spacer(Modifier.size(48.dp))
            }

            Spacer(Modifier.height(16.dp))

            // Tabs
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Global", "Country", "Friends").forEachIndexed { idx, label ->
                    FilterChip(
                        selected = tab == idx,
                        onClick = { tab = idx },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(0.2f),
                            selectedLabelColor = NeonCyan,
                            labelColor = TextSecondary,
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Podium (top 3)
            if (board.size >= 3) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                    PodiumCard(board[1], 2, 80.dp, Color(0xFFC0C0C0))
                    PodiumCard(board[0], 1, 100.dp, Color(0xFFFFD700))
                    PodiumCard(board[2], 3, 70.dp, Color(0xFFCD7F32))
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("RANKINGS", color = TextMuted, fontSize = 11.sp, letterSpacing = 3.sp)
            Spacer(Modifier.height(10.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(board) { entry ->
                    val isLocal = entry.username == local?.username
                    GlassCard(
                        Modifier.fillMaxWidth(),
                        accentColor = if (isLocal) NeonCyan else null,
                        cornerRadius = 14.dp,
                    ) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            // Rank
                            Text(
                                when (entry.rank) { 1 -> "🥇"; 2 -> "🥈"; 3 -> "🥉"; else -> "#${entry.rank}" },
                                color = if (entry.rank <= 3) Color.White else TextSecondary,
                                fontSize = if (entry.rank <= 3) 20.sp else 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(36.dp),
                            )
                            // Avatar
                            Text(avatarList.getOrElse(entry.avatarIndex) { "👤" }, fontSize = 22.sp, modifier = Modifier.padding(horizontal = 8.dp))
                            // Name + rank
                            Column(Modifier.weight(1f)) {
                                Text(
                                    entry.username + if (isLocal) " (You)" else "",
                                    color = if (isLocal) NeonCyan else Color.White,
                                    fontSize = 13.sp, fontWeight = FontWeight.Bold
                                )
                                Text(entry.rankTier.label, color = Color(entry.rankTier.color.toInt()), fontSize = 11.sp)
                            }
                            // Stats
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${entry.xp} XP", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("${entry.totalWins} wins", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumCard(entry: LeaderboardEntry, position: Int, height: Dp, medalColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(avatarList.getOrElse(entry.avatarIndex) { "👤" }, fontSize = 28.sp)
        Spacer(Modifier.height(4.dp))
        Text(entry.username.take(8), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text("${entry.xp} XP", color = NeonCyan, fontSize = 10.sp)
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier.width(70.dp).height(height)
                .background(
                    Brush.verticalGradient(listOf(medalColor.copy(0.3f), medalColor.copy(0.1f))),
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
                .border(1.dp, medalColor.copy(0.4f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text("$position", color = medalColor, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
    }
}
