package com.advancedtictactoe.game.ui.achievements

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
import com.advancedtictactoe.game.data.model.*
import com.advancedtictactoe.game.data.repository.GameRepository
import com.advancedtictactoe.game.ui.game.components.GlassCard
import com.advancedtictactoe.game.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    repository: GameRepository,
) : ViewModel() {
    val unlockedIds = repository.unlockedAchievements
        .map { list -> list.map { it.achievementId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
}

@Composable
fun AchievementsScreen(onBack: () -> Unit, vm: AchievementsViewModel = hiltViewModel()) {
    val unlockedIds by vm.unlockedIds.collectAsStateWithLifecycle()
    val all = AchievementsData.all
    val unlocked = all.filter { it.id in unlockedIds }
    val locked   = all.filter { it.id !in unlockedIds }

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF070B14), DarkBg)))) {
        Column(
            Modifier.fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Text("←", color = Color.White, fontSize = 24.sp) }
                Text("ACHIEVEMENTS", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                Spacer(Modifier.size(48.dp))
            }

            Spacer(Modifier.height(8.dp))

            // Progress bar
            val progress = unlocked.size.toFloat() / all.size
            GlassCard(Modifier.fillMaxWidth(), accentColor = NeonCyan) {
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${unlocked.size} / ${all.size} Unlocked", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${(progress * 100).toInt()}%", color = NeonCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = NeonCyan, trackColor = NeonCyan.copy(0.15f),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (unlocked.isNotEmpty()) {
                    item {
                        Text("UNLOCKED", color = NeonCyan, fontSize = 11.sp, letterSpacing = 3.sp,
                            modifier = Modifier.padding(bottom = 4.dp))
                    }
                    items(unlocked) { ach ->
                        AchievementRow(ach, unlocked = true)
                    }
                }
                if (locked.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("LOCKED", color = TextMuted, fontSize = 11.sp, letterSpacing = 3.sp,
                            modifier = Modifier.padding(bottom = 4.dp))
                    }
                    items(locked) { ach ->
                        AchievementRow(ach, unlocked = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementRow(ach: Achievement, unlocked: Boolean) {
    val alpha = if (unlocked) 1f else 0.45f
    GlassCard(
        Modifier.fillMaxWidth().alpha(alpha),
        accentColor = if (unlocked) NeonCyan else null,
        cornerRadius = 14.dp,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.size(52.dp)
                    .background(if (unlocked) NeonCyan.copy(0.12f) else Color.White.copy(0.04f), RoundedCornerShape(12.dp))
                    .border(1.dp, if (unlocked) NeonCyan.copy(0.3f) else GlassBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(if (unlocked) ach.iconEmoji else "🔒", fontSize = 24.sp)
            }
            Column(Modifier.weight(1f)) {
                Text(ach.title, color = if (unlocked) Color.White else TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(ach.description, color = TextMuted, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("+${ach.xpReward} XP", color = if (unlocked) NeonCyan else TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text("🪙 ${ach.coinReward}", color = if (unlocked) RankGold else TextMuted, fontSize = 11.sp)
            }
        }
    }
}

private val RankGold = Color(0xFFFFD700)
