package com.advancedtictactoe.game.ui.game

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.advancedtictactoe.game.data.model.*
import com.advancedtictactoe.game.ui.game.components.*
import com.advancedtictactoe.game.ui.game.viewmodel.GameViewModel
import com.advancedtictactoe.game.ui.theme.*

@Composable
fun GameScreen(
    mode: String,
    boardSize: Int,
    difficulty: String,
    onBack: () -> Unit,
    vm: GameViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) { vm.initGame(mode, boardSize, difficulty) }

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val game = uiState.gameState

    val infiniteTransition = rememberInfiniteTransition(label = "game_bg")
    val bgGlow by infiniteTransition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.radialGradient(
                colors = listOf(
                    Color(0xFF001830).copy(alpha = 0.5f + 0.5f * bgGlow),
                    DarkBg
                ),
                radius = 900f + 300f * bgGlow
            )
        )
    ) {
        FloatingParticles(Modifier.fillMaxSize(), NeonCyan.copy(alpha = 0.3f), 12)

        // Win explosion
        if (game.result is GameResult.Winner) {
            val winColor = if ((game.result as GameResult.Winner).player == Player.X) NeonCyan else NeonPink
            ParticleExplosion(
                modifier = Modifier.fillMaxSize(),
                trigger = true,
                color = winColor,
                secondColor = NeonPurple,
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 56.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top bar
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Text("←", color = Color.White, fontSize = 24.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (mode) {
                            "SINGLE_PLAYER"      -> "vs AI • ${difficulty.lowercase().replaceFirstChar { it.uppercase() }}"
                            "LOCAL_MULTIPLAYER"  -> "Local 2-Player"
                            "TOURNAMENT"         -> "Tournament"
                            else -> mode
                        },
                        color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp,
                    )
                    Text("${boardSize}×${boardSize} Board", color = TextSecondary, fontSize = 11.sp)
                }
                IconButton(onClick = vm::restartGame) {
                    Text("↺", color = NeonCyan, fontSize = 22.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Turn indicator
            TurnIndicator(
                currentPlayer = game.currentPlayer,
                isAiThinking  = game.isAiThinking,
                result        = game.result,
            )

            Spacer(Modifier.height(24.dp))

            // Board
            GameBoard(
                board        = game.board,
                onCellClick  = vm::onCellClick,
                winningCells = uiState.winningCells,
                hintCell     = uiState.hintCell,
                isEnabled    = game.result is GameResult.Ongoing && !game.isAiThinking,
                modifier     = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            )

            Spacer(Modifier.weight(1f))

            // Controls
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ControlButton("↩ Undo",  NeonPurple, Modifier.weight(1f), game.moveHistory.isNotEmpty(), vm::undoMove)
                ControlButton("💡 Hint", NeonGreen,  Modifier.weight(1f), game.result is GameResult.Ongoing, vm::getHint)
                ControlButton("↺ New",   NeonCyan,   Modifier.weight(1f), true, vm::restartGame)
            }

            Spacer(Modifier.height(12.dp))

            // Move counter
            Text(
                "Moves: ${game.moveCount}",
                color = TextMuted, fontSize = 12.sp, letterSpacing = 2.sp,
            )
        }
    }

    // Result dialog
    if (uiState.showResultDialog) {
        ResultDialog(
            result      = game.result,
            xpEarned    = uiState.xpEarned,
            coinsEarned = uiState.coinsEarned,
            achievements = uiState.newAchievements,
            onPlayAgain = { vm.dismissResult(); vm.restartGame() },
            onGoHome    = onBack,
        )
    }
}

@Composable
private fun TurnIndicator(
    currentPlayer: Player,
    isAiThinking: Boolean,
    result: GameResult,
) {
    val color = when {
        result is GameResult.Winner -> if (result.player == Player.X) NeonCyan else NeonPink
        result is GameResult.Draw   -> NeonYellow
        currentPlayer == Player.X   -> NeonCyan
        else                        -> NeonPink
    }
    val text = when {
        isAiThinking               -> "AI is thinking…"
        result is GameResult.Winner -> "${result.player} Wins! 🎉"
        result is GameResult.Draw   -> "It's a Draw! 🤝"
        else                       -> "${currentPlayer.name}'s Turn"
    }
    val infiniteTransition = rememberInfiniteTransition(label = "turn")
    val pulse by infiniteTransition.animateFloat(
        0.7f, 1f,
        infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(40.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(40.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        if (isAiThinking) {
            CircularProgressIndicator(
                color = color, modifier = Modifier.size(16.dp), strokeWidth = 2.dp,
            )
            Spacer(Modifier.width(10.dp))
        } else {
            Box(
                Modifier.size(10.dp)
                    .scale(if (result is GameResult.Ongoing) pulse else 1f)
                    .background(color, androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(Modifier.width(10.dp))
        }
        Text(text, color = color, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ControlButton(
    label: String, color: Color, modifier: Modifier, enabled: Boolean, onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .background(
                if (enabled) color.copy(alpha = 0.12f) else Color.White.copy(0.04f),
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (enabled) color.copy(alpha = 0.3f) else Color.White.copy(0.05f),
                RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = if (enabled) color else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ResultDialog(
    result: GameResult,
    xpEarned: Int,
    coinsEarned: Int,
    achievements: List<Achievement>,
    onPlayAgain: () -> Unit,
    onGoHome: () -> Unit,
) {
    val (emoji, headline, color) = when {
        result is GameResult.Winner && result.player == Player.X ->
            Triple("🎉", "Victory!", NeonCyan)
        result is GameResult.Winner ->
            Triple("💀", "Defeat", NeonPink)
        else ->
            Triple("🤝", "Draw!", NeonYellow)
    }

    AlertDialog(
        onDismissRequest = {},
        containerColor = DarkCard,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(emoji, fontSize = 48.sp, textAlign = TextAlign.Center)
                Text(headline, color = color, fontSize = 28.sp, fontWeight = FontWeight.Black)
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Rewards
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    RewardBadge("⭐", "+$xpEarned XP",   NeonCyan)
                    RewardBadge("🪙", "+$coinsEarned",  RankGold)
                }
                // New achievements
                if (achievements.isNotEmpty()) {
                    Divider(color = Color.White.copy(0.1f))
                    Text("New Achievements!", color = NeonGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    achievements.take(3).forEach { ach ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(ach.iconEmoji, fontSize = 20.sp)
                            Column {
                                Text(ach.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(ach.description, color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NeonButton("Play Again ↺", onPlayAgain, Modifier.fillMaxWidth(), color)
                TextButton(onClick = onGoHome, modifier = Modifier.fillMaxWidth()) {
                    Text("← Back to Home", color = TextSecondary)
                }
            }
        }
    )
}

@Composable
private fun RewardBadge(icon: String, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(icon, fontSize = 22.sp)
        Text(label, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

private val NeonGreen  = Color(0xFF39FF14)
private val NeonYellow = Color(0xFFFFFF00)
private val NeonGold   = Color(0xFFFFD700)
