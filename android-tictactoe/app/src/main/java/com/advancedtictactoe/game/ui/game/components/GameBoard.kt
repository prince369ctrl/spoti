package com.advancedtictactoe.game.ui.game.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.*
import com.advancedtictactoe.game.data.model.Player
import com.advancedtictactoe.game.ui.theme.*

@Composable
fun GameBoard(
    board: List<List<Player>>,
    onCellClick: (Int, Int) -> Unit,
    winningCells: List<Pair<Int, Int>>,
    hintCell: Pair<Int, Int>?,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    accentX: Color = NeonCyan,
    accentO: Color = NeonPink,
) {
    val size = board.size
    val haptic = LocalHapticFeedback.current

    // Board tilt animation
    val infiniteTransition = rememberInfiniteTransition(label = "board_tilt")
    val tiltX by infiniteTransition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = SinusoidalEasing), RepeatMode.Reverse),
        label = "tiltX"
    )

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer {
                rotationX = tiltX * 2f
                cameraDistance = 12f * density
            }
    ) {
        val cellSize = maxWidth / size

        // Grid lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBoardGrid(size, accentX.copy(alpha = 0.3f))
        }

        // Cells
        Column(modifier = Modifier.fillMaxSize()) {
            repeat(size) { row ->
                Row(modifier = Modifier.weight(1f)) {
                    repeat(size) { col ->
                        val player   = board[row][col]
                        val isWinning = winningCells.contains(row to col)
                        val isHint    = hintCell == (row to col)
                        GameCell(
                            player     = player,
                            isWinning  = isWinning,
                            isHint     = isHint,
                            accentX    = accentX,
                            accentO    = accentO,
                            modifier   = Modifier.weight(1f).fillMaxHeight(),
                            onClick    = {
                                if (isEnabled && player == Player.NONE) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onCellClick(row, col)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameCell(
    player: Player,
    isWinning: Boolean,
    isHint: Boolean,
    accentX: Color,
    accentO: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (isWinning) 1.1f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "cell_scale"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (isWinning) 1f else 0f,
        animationSpec = tween(300),
        label = "glow"
    )
    val cellColor = when (player) {
        Player.X -> accentX
        Player.O -> accentO
        Player.NONE -> Color.Transparent
    }

    Box(
        modifier = modifier
            .padding(4.dp)
            .scale(scale)
            .background(
                color = if (isHint) accentX.copy(alpha = 0.08f)
                        else Color.White.copy(alpha = 0.04f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isWinning) 2.dp else 0.5.dp,
                color = if (isWinning) cellColor else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when (player) {
            Player.X -> XMark(color = accentX, isWinning = isWinning)
            Player.O -> OMark(color = accentO, isWinning = isWinning)
            Player.NONE -> if (isHint) {
                Text("?", color = accentX.copy(alpha = 0.4f), fontSize = 24.sp)
            }
        }
    }
}

@Composable
private fun XMark(color: Color, isWinning: Boolean) {
    val progress by animateFloatAsState(
        targetValue = 1f, animationSpec = tween(300), label = "x_draw"
    )
    val glowRadius by animateFloatAsState(
        targetValue = if (isWinning) 20f else 0f,
        animationSpec = tween(300), label = "x_glow"
    )
    Canvas(modifier = Modifier.fillMaxSize(0.6f)) {
        val w = size.width; val h = size.height
        val strokeW = w / 6f
        val paint = Paint().apply {
            this.color = color
            strokeWidth = strokeW
            strokeCap = StrokeCap.Round
        }
        if (glowRadius > 0) {
            val nativeCanvas = drawContext.canvas.nativeCanvas
            val glowPaint = android.graphics.Paint().apply {
                this.color = color.copy(alpha = 0.4f).toArgb()
                this.maskFilter = android.graphics.BlurMaskFilter(
                    glowRadius, android.graphics.BlurMaskFilter.Blur.NORMAL
                )
            }
            nativeCanvas.drawLine(0f, 0f, w * progress, h * progress, glowPaint)
            nativeCanvas.drawLine(w, 0f, 0f, h * progress, glowPaint)
        }
        drawLine(color, Offset(0f, 0f), Offset(w * progress, h * progress), strokeW, StrokeCap.Round)
        drawLine(color, Offset(w, 0f), Offset(0f, h * progress), strokeW, StrokeCap.Round)
    }
}

@Composable
private fun OMark(color: Color, isWinning: Boolean) {
    val progress by animateFloatAsState(
        targetValue = 1f, animationSpec = tween(400), label = "o_draw"
    )
    Canvas(modifier = Modifier.fillMaxSize(0.6f)) {
        val radius = size.minDimension / 2f
        val strokeW = radius / 3f
        if (isWinning) {
            val nativeCanvas = drawContext.canvas.nativeCanvas
            val glowPaint = android.graphics.Paint().apply {
                this.color = color.copy(alpha = 0.35f).toArgb()
                this.style = android.graphics.Paint.Style.STROKE
                this.strokeWidth = strokeW * 2
                this.maskFilter = android.graphics.BlurMaskFilter(
                    20f, android.graphics.BlurMaskFilter.Blur.NORMAL
                )
            }
            nativeCanvas.drawCircle(center.x, center.y, radius, glowPaint)
        }
        drawArc(
            color       = color,
            startAngle  = -90f,
            sweepAngle  = 360f * progress,
            useCenter   = false,
            style       = Stroke(width = strokeW, cap = StrokeCap.Round),
        )
    }
}

private fun DrawScope.drawBoardGrid(size: Int, color: Color) {
    val cellW = this.size.width / size
    val cellH = this.size.height / size
    val stroke = Stroke(width = 1.5f)
    for (i in 1 until size) {
        drawLine(color, Offset(cellW * i, 0f), Offset(cellW * i, this.size.height), 1.5f)
        drawLine(color, Offset(0f, cellH * i), Offset(this.size.width, cellH * i), 1.5f)
    }
}

private val SinusoidalEasing = Easing { f ->
    (kotlin.math.sin(f * kotlin.math.PI.toFloat() - kotlin.math.PI.toFloat() / 2) + 1) / 2
}
