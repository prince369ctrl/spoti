package com.advancedtictactoe.game.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.advancedtictactoe.game.ui.theme.GlassBorder
import com.advancedtictactoe.game.ui.theme.GlassWhite

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    borderWidth: Dp = 1.dp,
    bgAlpha: Float = 0.12f,
    borderColor: Color = GlassBorder,
    accentColor: Color? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val bg = if (accentColor != null)
        Brush.linearGradient(
            colors = listOf(
                accentColor.copy(alpha = bgAlpha),
                GlassWhite.copy(alpha = bgAlpha * 0.5f),
            )
        )
    else Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = bgAlpha),
            Color.White.copy(alpha = bgAlpha * 0.5f),
        )
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(bg)
            .border(borderWidth, borderColor, shape)
            .padding(16.dp),
        content = content,
    )
}
