package com.advancedtictactoe.game.ui.game.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00F5FF),
    secondaryColor: Color = Color(0xFFBF00FF),
    enabled: Boolean = true,
    height: Dp = 56.dp,
    icon: String? = null,
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "btn_scale"
    )
    val shape = RoundedCornerShape(16.dp)
    val gradient = Brush.horizontalGradient(
        colors = if (enabled)
            listOf(color, secondaryColor)
        else
            listOf(Color.Gray, Color.DarkGray)
    )
    Box(
        modifier = modifier
            .scale(scale)
            .height(height)
            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
                shape = shape,
                ambientColor = color.copy(alpha = 0.6f),
                spotColor = color.copy(alpha = 0.6f),
            )
            .clip(shape)
            .background(gradient)
            .border(1.dp, color.copy(alpha = 0.4f), shape)
            .clickable(enabled = enabled) {
                pressed = true
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            if (icon != null) {
                Text(icon, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
            }
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp,
            )
        }
    }
    LaunchedEffect(pressed) {
        if (pressed) { kotlinx.coroutines.delay(150); pressed = false }
    }
}
